package com.kjs.medialibrary.sound.encoder;

import android.media.MediaCodec;

/**
 * 作者：柯嘉少 on 2019/11/13
 * 邮箱：2449926649@qq.com
 * 说明：对文件进行转码的抽象父类，具体实现看子类的
 * 修订者：
 * 版本：1.0
 */
public abstract class BaseAudioEncoder {
    protected int SAMPLE_RATE = 44100;//默认采样率44100
    protected int BIT_RATE = 16;//默认码率16bit
    protected int CHANNEL_COUNT = 1;//声道数，默认1
    protected String destinationFile;//转码后的文件，未转码时为空
    protected MediaCodec encoder;

    /**
     * @param SAMPLE_RATE   默认采样率8000
     * @param BIT_RATE      默认码率64bit
     * @param CHANNEL_COUNT 声道数，默认1
     */
    public void init(int SAMPLE_RATE, int BIT_RATE, int CHANNEL_COUNT) {
        this.SAMPLE_RATE = SAMPLE_RATE;
        this.BIT_RATE = BIT_RATE;
        this.CHANNEL_COUNT = CHANNEL_COUNT;
    }

    /**
     * @return 转码后的文件，未转码时为空
     */
    public String getDestFile() {
        return destinationFile;
    }

    /**
     * 进行转码
     *
     * @param sourceFile
     */
    public abstract void encode(String sourceFile);
}
