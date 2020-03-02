package com.kjs.medialibrary.video.hw2.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.kjs.medialibrary.BaseEncoder;

/**
 * 作者：柯嘉少 on 2019/11/20
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public abstract class BaseVideoEncoder extends BaseEncoder {
    protected int FPS = 30;//默认采样率30帧
    protected int BIT_RATE = 64;//默认码率64bit
    protected int width = 640;//视频宽度1080
    protected int height = 480;//视频高度720
    protected String destinationFile;//转码后的文件，未转码时为空
    protected MediaCodec encoder;





    /**
     * @return 转码后的文件，未转码时为空
     */
    public String getDestFile() {
        return destinationFile;
    }

    /**
     * @param fps
     * @param bitRate width * height * FPS * 3 这里计算公式选择一个中等码率，把3改为更大的值可以开启更高码率，通常不建议超过5
     */
    public void init(int fps, int bitRate, int width, int height) {
        this.FPS = fps;
        this.BIT_RATE = bitRate;
        this.width = width;
        this.height = height;
    }

    public abstract void encode(byte[] data);

    /**
     * 获取当前编码的format
     *
     * @return
     */
    public abstract MediaFormat getOutPutFormat();

    public abstract void release();

}
