package com.kjs.medialibrary.sound.utils;

import com.yougu.le.AudioProcessor;

public class AudioAlgorithmUtil {
    //音频算法文件
    private AudioProcessor audioProcessor;
    //音量增益-20到0
    private int loudness = 18;
    //降噪参数 0-24
    private int mRatio = 24;//9
    //降噪前增益 0到56
    private int preGain = 7;
    //降噪前增益 0到40
    private int postGain = 9;
    //是否开启音频算法
    private boolean isAlgorithm = false;

    public AudioAlgorithmUtil(int mBufferSize) {
        this.audioProcessor = new AudioProcessor(mBufferSize);
        this.audioProcessor.setNoiseReductionLevel(mRatio);
        this.audioProcessor.setTargetLoudness(loudness);
        this.audioProcessor.setPreGain(preGain);
        this.audioProcessor.setPostGain(postGain);


    }

    public AudioAlgorithmUtil(int mBufferSize, int loudness, int mRatio, int preGain, int postGain) {

        this.audioProcessor = new AudioProcessor(mBufferSize);
        this.loudness = loudness;
        this.mRatio = mRatio;
        this.preGain = preGain;
        this.postGain = postGain;
    }

    /**
     * 使用音频算法进行增益降噪等（降噪前需要先开启训练2秒,后面可以直接使用了）
     */
    public byte[] processAudio(byte[] input) {
        return audioProcessor.process(input);
    }


    /**
     * 混合背景音轨与录制的音轨为一个音轨
     */
    public void mixBGMWithRecord() {

    }
}
