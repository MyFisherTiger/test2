package com.kjs.medialibrary.sound.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.NonNull;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.sound.utils.AudioFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AACEncoder2 extends BaseAudioEncoder {
    FileOutputStream fos = null;
    int CodecProfileLevel = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    int MAX_INPUT = 100 * 1024;
    MediaFormat encodeFormat;
    private int tag = 0;
    private byte[] data;
    private int tagCount = 0;
    private long startPts = 0;


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
        startPts = System.nanoTime() / 1000;
        initAACMediaEncode();
    }

    /**
     * 配置AAC编码器
     */
    private void initAACMediaEncode() {
        try {
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (encoder == null) {
            LogMedia.error("创建编码器失败");
            return;
        }
    }

    private MediaFormat createMediaFormat() {
        LogMedia.info("配置AAC编码器,pcm原始码率" + BIT_RATE + "声道数" + CHANNEL_COUNT + "采样率" + SAMPLE_RATE);
        encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,
                SAMPLE_RATE, CHANNEL_COUNT);//参数对应-> mime type、采样率、声道数
        encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, CodecProfileLevel);
        //encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//比特率
        encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_INPUT);
        return encodeFormat;
    }


    @Override
    public void encode(final String sourceFile) {

    }


    @Override
    public void encode(byte[] encoderData) {
        data = encoderData;
        LogMedia.error("需要编码的音频数据一段的长度为：" + data.length);
        if (encoder == null) {
            LogMedia.error("创建编码器失败");
            return;
        }

        tagCount += 1;
        if (wait) {//处理完一帧数据的编码后，才能接收下一帧数据
            tag += 1;
            LogMedia.error("遗漏了多少段音频没编码：" + tag);
            return;
        }
        wait = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            encoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int i) {
                    ByteBuffer inputBuffer = encoder.getInputBuffer(i);
                    //填充编码数据
                    LogMedia.info("填充编码数据" + i + "~~" + inputBuffer.toString());
                    inputBuffer.put(data);
                    long pts=System.nanoTime()/1000-startPts;
                    if(finishEncoder){
                        encoder.queueInputBuffer(i,0,data.length,pts,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }else {
                        encoder.queueInputBuffer(i, 0, data.length, pts, 0);
                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int i, @NonNull MediaCodec.BufferInfo bufferInfo) {
                    ByteBuffer outputBuffer = encoder.getOutputBuffer(i);//outputBuffer is ready to be processed or rendered.
                    bufferInfo.presentationTimeUs=System.nanoTime()/1000-startPts;
                    LogMedia.info("本帧的编码:" + i + "bufferInfo.size:" + bufferInfo.size + "~~" + outputBuffer.toString());

                    int outBitSize = bufferInfo.size + 7;
                    byte[] outByteBuffer = new byte[outBitSize];

                    outputBuffer.position(bufferInfo.offset);
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

                    addADTStoPacket(outByteBuffer, outBitSize);
                    if(listener!=null){
                        listener.callBack(true,outputBuffer,encodeFormat,bufferInfo);
                    }

                    outputBuffer.get(outByteBuffer, 7, bufferInfo.size);
                    outputBuffer.position(bufferInfo.offset);

                    try {
                        fos.write(outByteBuffer, 0, outBitSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    //释放输出缓冲区
                    encoder.releaseOutputBuffer(i, false);
                    encoder.stop();

                    wait = false;
                }

                @Override
                public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
                    LogMedia.error("本段音频编码出错");
                    //释放输出缓冲区
                    encoder.stop();
                    wait = false;
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
                    encodeFormat = mediaFormat;
                }
            });
        } else {
            LogMedia.error("系统版本过低，需要android系统至少为5.0");
        }

        encoder.configure(createMediaFormat(), null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        LogMedia.info("编码器开始工作");
        encoder.start();
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
        LogMedia.error("音频总共采集要编码的段数：" + tagCount);
        LogMedia.info("关闭文件流释放资源");

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
