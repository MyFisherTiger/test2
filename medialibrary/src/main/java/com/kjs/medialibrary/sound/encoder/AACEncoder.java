package com.kjs.medialibrary.sound.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.sound.AudioFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：柯嘉少 on 2019/11/14
 * 邮箱：2449926649@qq.com
 * 说明：AAC格式编码封装
 *      todo：优化应当参考多点下载的设计，实现多点编解码，边编边解，提高效率
 * 修订者：
 * 版本：1.0
 */
public class AACEncoder extends BaseAudioEncoder {
    int index = 0;//当index等于-1表示解码前的buffer读取完毕
    int outputId = 0;//outputIndex等于-1表示解码后的buffer读取完毕
    Timer timer = null;
    TimerTask task = null;
    FileInputStream fis = null;
    FileOutputStream fos = null;
    ByteBuffer[] encodeInputBuffers;
    ByteBuffer[] encodeOutputBuffers;
    MediaCodec.BufferInfo encodeBufferInfo;
    int CodecProfileLevel = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    int MAX_INPUT = 10 * 1024;
    MediaFormat encodeFormat;

    @Override
    public void init(int SAMPLE_RATE, int BIT_RATE, int CHANNEL_COUNT) {
        super.init(SAMPLE_RATE, BIT_RATE, CHANNEL_COUNT);
        LogMedia.info("初始化");
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        destinationFile = AudioFileUtils.getAACFileAbsolutePath(fileName);
        File aacFIle = new File(destinationFile);
        try {
            fos = new FileOutputStream(aacFIle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initAACMediaEncode();
    }

    /**
     * 配置AAC编码器
     */
    private void initAACMediaEncode() {
        try {
            LogMedia.info("配置AAC编码器,pcm原始码率" + BIT_RATE + "声道数" + CHANNEL_COUNT + "采样率" + SAMPLE_RATE);
            encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,
                    SAMPLE_RATE, CHANNEL_COUNT);//参数对应-> mime type、采样率、声道数
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//比特率
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, CodecProfileLevel);
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_INPUT);
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            encoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (encoder == null) {
            LogMedia.error("创建编码器失败");
            return;
        }
        encoder.start();


        encodeInputBuffers = encoder.getInputBuffers();
        encodeOutputBuffers = encoder.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();
    }

    @Override
    public void encode(final String sourceFile) {
        if (encoder == null) {
            LogMedia.error("创建编码器失败");
            return;
        }
        LogMedia.info("编码器开始工作");
        task = new TimerTask() {
            @Override
            public void run() {
                LogMedia.info("请稍侯。。。。");

                if (index == -1 && outputId == -1) {//解码完毕，结束任务
                    LogMedia.info("编码完毕，结束任务");
                    //release();
                }
                putPCMData(0, sourceFile);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0);
    }

    @Override
    public void encode(byte[] encoderData) {

    }

    @Override
    public void pause() {

    }

    @Override
    public MediaFormat getMediaFormat() {
        return encodeFormat;
    }

    @Override
    public void release() {
        LogMedia.info("关闭文件流释放资源");
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }

        if (encoder != null) {
            encoder.stop();
            encoder.release();
            encoder = null;
        }
        if (fos != null) {
            try {
                fos.close();
                fos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fis != null) {
            try {
                fis.close();
                fis = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        encodeInputBuffers = null;
        encodeOutputBuffers = null;
        encodeBufferInfo = null;
    }

    /**
     * 根据tag获取的pcm流有两种情况
     * 0.只有编码过程：直接获取pcm原文件文件的字节流数据
     * 1.先解码后编码、转码的过程：获取解码器所在线程输出的数据
     *
     * @param tag 0直接编码,1先解码后编码、转码
     * @return
     */
    private void putPCMData(int tag, String sourceFile) {
        LogMedia.info("根据tag获取的pcm流有两种情况,tag=" + tag);
        int inputId = 0;
        ByteBuffer inputBuffer;
        byte[] readByte = new byte[MAX_INPUT];
        if (tag == 0) {
            File pcmFile = new File(sourceFile);
            try {
                fis = new FileInputStream(pcmFile);

                while (index >= 0) {
                    //从输入流中读取一定数量的字节，并将其存储在缓冲区数组readByte中。以整数形式返回实际读取的字节数,-1表示读完了
                    index = fis.read(readByte);
                    inputId = encoder.dequeueInputBuffer(0);
                    if(inputId!=-1){
                        inputBuffer = encoder.getInputBuffers()[inputId];//编码成功的buffer
                    }else {
                        break;
                    }
                    inputBuffer.clear();//清除之前进入编码队列编码成功的buffer
                    inputBuffer.limit(readByte.length);//限定buffer长度
                    inputBuffer.put(readByte);//PCM数据填充给inputBuffer
                    encoder.queueInputBuffer(inputId, 0, readByte.length, 0, 0);//通知编码器 编码
                    dstAudioFormatFromPCM();
                    LogMedia.info("pcmFile读取buffer的index" + index);
                }
                LogMedia.info("pcmFile读完了");

                LogMedia.info("解码完毕，结束任务");

                if(callBackFinishEncode!=null){
                    LogMedia.info("请回调callBackFinishEncode，释放AudioRecorder的内存");
                    callBackFinishEncode.callBackFinish(0);
                }

                release();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //todo
        }
    }

    /**
     * 编码PCM数据 得到MediaFormat.MIMETYPE_AUDIO_AAC格式的音频文件，并保存为aac数据流（注意与AAC文件的区别）
     */
    private void dstAudioFormatFromPCM() {
        LogMedia.info("编码PCM数据，并保存为aac数据流");
        ByteBuffer outputBuffer;
        byte[] chunkAudio;
        int outBitSize;
        //int outPacketSize;
        byte[] outByteBuffer;

        outputId = encoder.dequeueOutputBuffer(encodeBufferInfo, 0);
        while (outputId >= 0) {
            try {
                outBitSize = encodeBufferInfo.size + 7;
                outByteBuffer = new byte[outBitSize];

                ByteBuffer byteBuffer = encoder.getOutputBuffers()[outputId];
                byteBuffer.position(encodeBufferInfo.offset);
                byteBuffer.limit(encodeBufferInfo.offset + encodeBufferInfo.size);

                addADTStoPacket(outByteBuffer, outBitSize);

                byteBuffer.get(outByteBuffer, 7, encodeBufferInfo.size);
                byteBuffer.position(encodeBufferInfo.offset);
                fos.write(outByteBuffer, 0, outBitSize);

                encoder.releaseOutputBuffer(outputId, false);
                outputId = encoder.dequeueOutputBuffer(encodeBufferInfo, 0);
                outByteBuffer = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogMedia.info("读完了buffer");
    }

    /**
     * 添加ADTS头
     * <p>
     * AAC原始码流无法直接播放，一般需要封装为ADTS格式才能再次使用，在android中用MediaCodec编码得到的AAC就是raw格式，
     * 为了保存为.aac格式，需要增加ADTS头
     *
     * @param packet    aac数据包字节流
     * @param packetLen 为raw aac Packet Len + 7
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        LogMedia.info("添加ADTS头");
        int profile = CodecProfileLevel;
        int freqIdx = getADTSSampleRate(SAMPLE_RATE); // 44.1KHz
        int chanCfg = CHANNEL_COUNT;

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    /**
     * 获取采样率对应数组下标（默认44100采样率，下标为4）
     *
     * @param sampleRate
     * @return
     */
    private int getADTSSampleRate(int sampleRate) {
        int rate = 4;
        switch (sampleRate) {
            case 96000:
                rate = 0;
                break;
            case 88200:
                rate = 1;
                break;
            case 64000:
                rate = 2;
                break;
            case 48000:
                rate = 3;
                break;
            case 44100:
                rate = 4;
                break;
            case 32000:
                rate = 5;
                break;
            case 24000:
                rate = 6;
                break;
            case 22050:
                rate = 7;
                break;
            case 16000:
                rate = 8;
                break;
            case 12000:
                rate = 9;
                break;
            case 11025:
                rate = 10;
                break;
            case 8000:
                rate = 11;
                break;
            case 7350:
                rate = 12;
                break;
        }
        return rate;
    }


}
