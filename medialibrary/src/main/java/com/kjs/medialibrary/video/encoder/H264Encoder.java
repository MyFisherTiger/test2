package com.kjs.medialibrary.video.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.NonNull;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.video.VideoFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
    private MediaCodec.BufferInfo encodeBufferInfo;
    private int MAX_INPUT = 100 * 1024;//460800;//
    private MediaFormat outPutFormat;

    private boolean wait = false;//是否进行阻塞
    private byte[] data;
    private int tag = 0;
    private int tagCount = 0;
    private long startPts = 0;

    @Override
    public void init(int fps, int bitRate, int width, int height) {
        super.init(fps, bitRate, width, height);
        LogMedia.info("初始化");
        if (fos == null) {
            String fileName = "input";//new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            destinationFile = VideoFileUtil.getOriginFileAbsolutePath(fileName);
            File originFIle = new File(destinationFile);
            try {
                fos = new FileOutputStream(originFIle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        startPts = System.nanoTime() / 1000;
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
        } catch (IOException e) {
            e.printStackTrace();
            LogMedia.error("创建编码器失败");
        }

    }

    private MediaFormat createMediaFormat() {
        getSupportColorFormat();
        outPutFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);

        //设置视频码率
        outPutFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        //设置视频fps
        outPutFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FPS);
        /*这里需要注意，为了简单这里是写了个固定的ColorFormat
        实际上，并不是所有的手机都支持COLOR_FormatYUV420Planar颜色空间
        所以正确的做法应该是，获取当前设备支持的颜色空间，并从中选取*/

        outPutFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        //设置视频关键帧间隔，这里设置两秒一个关键帧
        outPutFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        outPutFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_INPUT);//设置缓冲池的最大值

        //设置sps和pps 如果设置不正确会导致合成的mp4视频作为文件预览的时候，预览图片是黑色的
        //视频进度条拖拽画面会出现绿色，以及块状现象

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         *//**
         * 可选配置，设置码率模式
         * BITRATE_MODE_VBR：恒定质量
         * BITRATE_MODE_VBR：可变码率
         * BITRATE_MODE_CBR：恒定码率
         *//*
            outPutFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
            *//**
         * 可选配置，设置H264 Profile
         * 需要做兼容性检查
         *//*
            outPutFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
            *//**
         * 可选配置，设置H264 Level
         * 需要做兼容性检查
         *//*
            outPutFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel31);
        }*/

        return outPutFormat;
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


    //MediaFormat mOutputFormat;

    @Override
    public void encode(final byte[] encoderData) {
        data = encoderData;
        LogMedia.error("需要编码的数据一帧的长度为：" + data.length);
        /*if (data.length > MAX_INPUT) {
            LogMedia.error("编码器缓冲区长度过短，请重新设置缓冲区大小");
            return;
        }*/
        if (encoder == null) {
            LogMedia.error("创建编码器");
            try {
                encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        tagCount += 1;
        if (wait) {//处理完一帧数据的编码后，才能接收下一帧数据
            tag += 1;
            LogMedia.error("遗漏了多少帧没编码：" + tag);
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
                    if(finishEncoder){//该结束编码了
                        encoder.queueInputBuffer(i, 0, data.length, pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }else {
                        encoder.queueInputBuffer(i, 0, data.length, pts, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);

                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int i, @NonNull MediaCodec.BufferInfo bufferInfo) {
                    ByteBuffer outputBuffer = encoder.getOutputBuffer(i);//outputBuffer is ready to be processed or rendered.
                    bufferInfo.flags=MediaCodec.BUFFER_FLAG_KEY_FRAME;
                    bufferInfo.presentationTimeUs=System.nanoTime()/1000-startPts;
                    LogMedia.info("本帧的编码:" + i + "bufferInfo.size:" + bufferInfo.size + "~~" + outputBuffer.toString());

                    //MediaFormat bufferFormat = encoder.getOutputFormat(i);//bufferFormat is equivalent to mOutputFormat
                    //编码后的数据存到本地
                    /*int outBitSize = bufferInfo.size;
                    byte[] outByte = new byte[outBitSize];
                    outputBuffer.position(bufferInfo.offset);
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                    outputBuffer.get(outByte, 0, bufferInfo.size);
                    outputBuffer.position(bufferInfo.offset);
                    //Io的写入是较耗时的，最差的情况下甚至会阻塞较长时间导致很多帧没有被编码，看看怎么优化
                    LogMedia.info("写到本地的文件:" + i + "bufferInfo.size:" + bufferInfo.size + "~~" + outputBuffer.toString());

                    try {
                        fos.write(outByte, 0, outBitSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    //给合成器（muxer）喂一帧编码后的数据
                    if (listener != null) {
                        listener.callBack(false, outputBuffer, outPutFormat, bufferInfo);
                    }

                    //释放输出缓冲区
                    encoder.releaseOutputBuffer(i, false);
                    encoder.stop();
                    wait = false;
                }

                @Override
                public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
                    LogMedia.error("本帧编码出错");
                    //释放输出缓冲区
                    encoder.stop();
                    wait = false;
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
                    outPutFormat = mediaFormat;
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
    public MediaFormat getOutPutFormat() {
        return outPutFormat;
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
        encodeBufferInfo = null;
    }
}
