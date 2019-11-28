package com.kjs.medialibrary.video.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.video.VideoFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 作者：柯嘉少 on 2019/11/20
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class H264Encoder extends BaseVideoEncoder {
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private int outputLength = 0;//outputLength等于-1表示解码后的buffer读取完毕
    private MediaCodec.BufferInfo encodeBufferInfo;
    private int MAX_INPUT = 100 * 1024;
    private MediaFormat mediaFormat;

    private boolean wait = false;//是否进行阻塞
    private byte[] readByte = new byte[MAX_INPUT];
    private long startTime = 0L;//编码开始时间
    private long endTime = 0L;//编码结束时间
    private int inputLength = 0;
    private ByteBuffer inputBuffer;
    private byte[] data;
    private int tag = 0;
    private int tagCount = 0;

    @Override
    public void init(int fps, int bitRate, int width, int height) {
        super.init(fps, bitRate, width, height);
        LogMedia.info("初始化");
        if (fos == null) {
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            destinationFile = VideoFileUtil.getOriginFileAbsolutePath(fileName);
            File originFIle = new File(destinationFile);
            try {
                fos = new FileOutputStream(originFIle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initH264MediaEncode();
    }

    private void initH264MediaEncode() {
        try {
            if (encoder == null) {
                LogMedia.error("创建编码器");
                encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            } else {
                return;
            }
            MediaFormat mediaFormat = createMediaFormat();
            encoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            LogMedia.info("编码器开始工作");
            encoder.start();

            encodeInputBuffers = encoder.getInputBuffers();
            encodeOutputBuffers = encoder.getOutputBuffers();
            encodeBufferInfo = new MediaCodec.BufferInfo();
        } catch (IOException e) {
            e.printStackTrace();
            LogMedia.error("创建编码器失败");
        }

    }

    private MediaFormat createMediaFormat() {
        getSupportColorFormat();
        mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
       /* //使用H264编码
        mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_VIDEO_AVC);
        //设置视频宽度
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, width);
        //设置视频高度
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, height);*/

        //设置视频码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        //设置视频fps
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FPS);
        /*这里需要注意，为了简单这里是写了个固定的ColorFormat
        实际上，并不是所有的手机都支持COLOR_FormatYUV420Planar颜色空间
        所以正确的做法应该是，获取当前设备支持的颜色空间，并从中选取*/

        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        //设置视频关键帧间隔，这里设置两秒一个关键帧
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_INPUT);//设置缓冲池的最大值
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         *//**
         * 可选配置，设置码率模式
         * BITRATE_MODE_VBR：恒定质量
         * BITRATE_MODE_VBR：可变码率
         * BITRATE_MODE_CBR：恒定码率
         *//*
            mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
            *//**
         * 可选配置，设置H264 Profile
         * 需要做兼容性检查
         *//*
            mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
            *//**
         * 可选配置，设置H264 Level
         * 需要做兼容性检查
         *//*
            mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel31);
        }*/

        return mediaFormat;
    }

    private int getSupportColorFormat() {

        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals("video/avc")) {
                    System.out.println("found");
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
        }
        LogMedia.info("AvcEncoder,Found " + codecInfo.getName() + " supporting " + "video/avc");
        // Find a color profile that the codec supports
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType("video/avc");
        LogMedia.info("AvcEncoder,length-" + capabilities.colorFormats.length + "==" + Arrays.toString(capabilities.colorFormats));
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            switch (capabilities.colorFormats[i]) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible:
                    LogMedia.info("AvcEncoder,supported color format::" + capabilities.colorFormats[i]);
                    break;

                default:
                    LogMedia.info("AvcEncoder,other color format " + capabilities.colorFormats[i]);
                    break;
            }
        }
        //return capabilities.colorFormats[i];
        return 0;
    }


    @Override
    public void encode(final byte[] encoderData) {
        tagCount += 1;
        if (wait) {//处理完一帧数据的编码后，才能接收下一帧数据
            tag += 1;
            LogMedia.error("遗漏了多少帧没编码：" + tag);
            return;
        }
        wait = true;
        data = Arrays.copyOf(encoderData, encoderData.length);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (encoder == null) {
                    LogMedia.error("码器未创建或者已关闭");
                    return;
                }

                //需要进行编码的buffer队列，生产者
                LogMedia.info("需要进行编码的buffer队列，生产者");
                int restLength = data.length;
                int start = 0;
                int end = 0;
                while (restLength > 0 && !finishedEncoder) {
                    startTime = System.nanoTime();
                    inputLength = encoder.dequeueInputBuffer(0);

                    if (inputLength != -1) {
                        inputBuffer = encoder.getInputBuffers()[inputLength];//编码成功的buffer
                    } else {
                        break;
                    }
                    if (restLength < MAX_INPUT) {
                        start = end;
                        end = data.length;
                    } else {
                        start = end;
                        end = start + MAX_INPUT;
                    }
                    restLength = restLength - MAX_INPUT;
                    readByte = Arrays.copyOfRange(data, start, end);//包括下标from，不包括上标to
                    inputBuffer.clear();//清除之前进入编码队列编码成功的buffer
                    inputBuffer.limit(MAX_INPUT);//限定buffer长度
                    inputBuffer.put(readByte);//视频数据填充给inputBuffer
                    LogMedia.info("填充编码数据" + readByte.length);
                    encoder.queueInputBuffer(inputLength, 0, readByte.length, 0, 0);//通知编码器 编码
                    saveEncoderData();
                }

            }
        }).start();
    }

    @Override
    public MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    private void saveEncoderData() {
        //成功编码后输出的buffer队列，消费者
        LogMedia.info("成功编码后输出的buffer队列，消费者");
        byte[] outByteBuffer;
        int outBitSize;
        outputLength = encoder.dequeueOutputBuffer(encodeBufferInfo, 0);
        while (outputLength >= 0 && !finishedEncoder) {
            try {
                if (outputLength < 0) {
                    outputLength = 0;
                    LogMedia.info("目前编码后的队列buffer处理完毕，等待输入数据进行编码");
                }

                outBitSize = encodeBufferInfo.size;
                outByteBuffer = new byte[outBitSize];

                ByteBuffer byteBuffer = encoder.getOutputBuffers()[outputLength];
                byteBuffer.position(encodeBufferInfo.offset);
                byteBuffer.limit(encodeBufferInfo.offset + outBitSize);

                byteBuffer.get(outByteBuffer, 0, outBitSize);
                byteBuffer.position(encodeBufferInfo.offset);

                LogMedia.info("写到本地的文件长度" + outByteBuffer.length);
                fos.write(outByteBuffer, 0, outBitSize);
                encoder.releaseOutputBuffer(outputLength, false);
                outputLength = encoder.dequeueOutputBuffer(encodeBufferInfo, 0);
                outByteBuffer = null;
                endTime = System.nanoTime();

                LogMedia.info("编码阻塞的时间（微秒）：" + (endTime - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wait = false;//在输出编码后的数据后，允许新加入数据进行编码
    }

    @Override
    public void release() {
        LogMedia.error("摄像头总共采集要编码的帧数：" + tagCount);
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
}
