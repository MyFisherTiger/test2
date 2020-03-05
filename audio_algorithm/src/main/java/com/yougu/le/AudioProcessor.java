/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yougu.le;

/**
 * Created by Piasy{github.com/Piasy} on 13/09/2016.
 */

public class AudioProcessor {

    static {
        System.loadLibrary("audio-processor");
    }

    private final int mBufferSize;
    private final byte[] mOutBuffer;
    private final float[] mFloatInput;
    private final float[] mFloatOutput;
    boolean inited;

    public AudioProcessor(int bufferSize) {
        mBufferSize = bufferSize;
        mOutBuffer = new byte[mBufferSize];
        // in native code, two bytes is converted into one float
        mFloatInput = new float[mBufferSize / 2];
        mFloatOutput = new float[mBufferSize / 2];
        audioProcessorInit();
        audioProcessorSetNoiseReductionLevel(0);
        audioProcessorSetTargetLoudness(0);
        inited = true;
    }

    public void destroy() {
        audioProcessorDestroy();
        inited = false;
    }

    protected void finalize() {
        if (inited) {
            audioProcessorDestroy();
        }
    }

    public synchronized byte[] process(byte[] input) {
        audioProcessorProcess(mBufferSize, input, mOutBuffer, mFloatInput, mFloatOutput);
        return mOutBuffer;
    }

    //声音增益 范围:-20~0,loudness在版面的取值范围为0-20
    public void setTargetLoudness(int loudness) {
        audioProcessorSetTargetLoudness((loudness - 20));
    }

    //声音降噪 范围:0~24
    public void setNoiseReductionLevel(int level) {
        audioProcessorSetNoiseReductionLevel(level);
    }

    //降噪前的增益 范围:0~56
    public void setPreGain(int preGain) {
        audioProcessorSetPreGain(preGain);
    }

    //降噪后的增益 范围:0~40
    public void setPostGain(int postGain) {
        audioProcessorSetPostGain(postGain);
    }

    public float getInLevel() {
        return audioProcessorGetInLevel();
    }

    private static native void audioProcessorProcess(int size, byte[] in, byte[] out, float[] floatInput, float[] floatOutput);

    private static native void audioProcessorInit();

    private static native void audioProcessorDestroy();

    private static native void audioProcessorSetTargetLoudness(int loudness);

    private static native void audioProcessorSetNoiseReductionLevel(int level);

    private static native void audioProcessorSetPreGain(int preGain);

    private static native void audioProcessorSetPostGain(int postGain);

    private static native float audioProcessorGetInLevel();

    public static native String getString();
}
