package com.kjs.medialibrary.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.sound.encoder.BaseAudioEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：柯嘉少 on 2019/11/12
 * 邮箱：2449926649@qq.com
 * 说明：音频录制工具类
 * 修订者：
 * 版本：1.0
 */
public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private int audioInput = MediaRecorder.AudioSource.MIC;//来源（麦克风）
    private int audioSampleRate = 44100;//频率（4000到192000的范围内取值）
    private int audioChannel = AudioFormat.CHANNEL_IN_MONO;//声道(单声道)
    private int audioEncode = AudioFormat.ENCODING_PCM_16BIT;//编码样式

    private int bufferSizeInBytes = 0;
    private AudioRecord audioRecord;
    private Status status = Status.STATUS_NO_READY;
    protected String pcmFileName;

    private Timer timer;

    private TimerTask timerTask;
    private int currentPosition = 0;
    private CallBack callBack;
    private int lastVolume = 0;//录音的分贝
    private BaseAudioEncoder encoder;//编码格式
    private Thread saveFileThread;

    /**
     * 录音的状态
     */
    public enum Status {
        STATUS_NO_READY,
        STATUS_READY,
        STATUS_START,
        STATUS_PAUSE,
        STATUS_STOP
    }

    public interface CallBack {
        void recordProgress(int progress);

        void volume(int volume);
    }

    public AudioRecorder() {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        pcmFileName = AudioFileUtils.getPcmFileAbsolutePath(fileName);
        File file = new File(pcmFileName);
        if (file.exists()) {
            file.delete();
        }
        status = Status.STATUS_READY;
    }

    /**
     * @param audioInput
     */
    public void setAudioInput(int audioInput) {
        this.audioInput = audioInput;
    }

    /**
     * @param audioSampleRate
     */
    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    /**
     * @param audioChannel
     */
    public void setAudioChannel(int audioChannel) {
        this.audioChannel = audioChannel;
    }


    /**
     * @param encoder
     */
    public void setEncoder(BaseAudioEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * @param callBack
     */
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 开始
     */
    public void start() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(audioSampleRate,
                audioChannel, audioEncode);
        audioRecord = new AudioRecord(audioInput, audioSampleRate, audioChannel, audioEncode, bufferSizeInBytes);
        if (status == Status.STATUS_NO_READY) {
            return;
        }
        if (status == Status.STATUS_START) {
            return;
        }
        LogMedia.info("开始录制音频" + audioRecord.getState());
        audioRecord.startRecording();

        recordToFile();

        startTimer();
    }

    /**
     * 暂停
     */
    public void pause() {
        LogMedia.info("暂停");
        if (status != Status.STATUS_START) {
            return;
        } else {
            stopRecorder();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止
     */
    public void stop() {
        currentPosition = 0;
        if (status != Status.STATUS_START && status != Status.STATUS_PAUSE) {
            return;
        } else {
            LogMedia.info("停止录音，并编码保存为带格式的音频文件");
            stopRecorder();
            //makeDestFile();
            status = Status.STATUS_READY;
        }
    }


    /**
     * 文件进行转码，格式封装
     */
    private void makeDestFile() {
        if (encoder == null || audioRecord == null)
            return;
        LogMedia.error("开始转码");
        new Thread() {
            @Override
            public void run() {
                //releaseRecorder();
                int bitRate = audioSampleRate * 16 * audioRecord.getChannelCount();//264600
                encoder.init(audioSampleRate, bitRate, audioRecord.getChannelCount());
                encoder.encode(pcmFileName);
                release();//转完码再释放
            }
        }.run();
    }

    /**
     * 释放录音资源
     */
    public void release() {
        LogMedia.info("释放录音资源");
        stopRecorder();
        releaseRecorder();
        status = Status.STATUS_READY;
        //clearFiles();
    }

    //释放资源
    private void releaseRecorder() {

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    //停止录音
    private void stopRecorder() {
        LogMedia.info("停止录音");
        stopTimer();
        if (audioRecord != null) {
            try {
                audioRecord.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startTimer() {
        if (timer == null)
            timer = new Timer();
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                currentPosition++;
                if (callBack != null && status == Status.STATUS_START) {
                    callBack.recordProgress(currentPosition);
                    callBack.volume(lastVolume);
                }

            }
        };
        timer.schedule(timerTask, 0, 10);//1000毫秒=1秒
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 清除文件
     */
    private void clearFiles() {
        LogMedia.info("清除pcm及转码的文件");
        try {
            File pcmfile = new File(pcmFileName);
            if (pcmfile.exists())
                pcmfile.delete();

            if (encoder != null && !TextUtils.isEmpty(encoder.getDestFile())) {
                File file = new File(encoder.getDestFile());
                if (file.exists())
                    file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将音频写入文件
    private void recordToFile() {
        LogMedia.info("采集的音频数据写入pcm文件");
        saveFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] audioData = new byte[bufferSizeInBytes];
                FileOutputStream fos = null;
                int readsize = 0;
                try {
                    fos = new FileOutputStream(pcmFileName, true);
                } catch (FileNotFoundException e) {
                    LogMedia.error(e.getMessage());
                }
                status = Status.STATUS_START;
                while (status == Status.STATUS_START && audioRecord != null) {
                    readsize = audioRecord.read(audioData, 0, bufferSizeInBytes);
                    if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                        try {
                            //get the volume  1--10
                    /*int sum = 0;
                    for (int i = 0; i < readsize; i++) {
                        sum += Math.abs(audioData[i]);
                    }

                    if (readsize > 0) {
                        int raw = sum / readsize;
                        lastVolume = raw > 10 ? raw - 10 : 0;
                        //Log.i(TAG, "writeDataTOFile: volume -- " + raw + " / lastvolumn -- " + lastVolume);
                    }*/
                            if (readsize > 0 && readsize <= audioData.length)
                                fos.write(audioData, 0, readsize);
                        } catch (IOException e) {
                            Log.e("AudioRecorder", e.getMessage());
                        }
                    }
                }
                try {
                    if (fos != null) {
                        fos.close();
                        LogMedia.info("pcm文件写入完毕");
                        if (status == Status.STATUS_READY) {
                            makeDestFile();
                        }

                    }
                } catch (IOException e) {
                    Log.e("AudioRecorder", e.getMessage());
                }
            }
        });
        saveFileThread.start();

    }


    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * 获取当前的录音状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取当前的录音文件的位置
     *
     * @return
     */
    public String getVoiceFilePath() {
        return encoder == null ? pcmFileName : encoder.getDestFile();
    }
}
