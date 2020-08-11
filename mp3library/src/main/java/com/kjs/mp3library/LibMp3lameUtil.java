package com.kjs.mp3library;

/**
 * 作者：柯嘉少 on 2020/8/11
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class LibMp3lameUtil {
    public native static String stringFromJNI();

    public native static void close();

    public native static int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    public native static int flush(byte[] mp3buf);

    public native static void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate, int quality);
}
