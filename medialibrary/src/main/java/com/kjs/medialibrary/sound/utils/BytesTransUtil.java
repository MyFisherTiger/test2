package com.kjs.medialibrary.sound.utils;

import android.util.Log;

import java.nio.ByteOrder;


/**
 *
 * 作者：Icex
 * 邮箱：未知
 * 日期: 2018-12-06
 * 修改人: xxx（des）
 * 说明: 音频字节转换工具类
 * 版本: 1.0
 */
public class BytesTransUtil {

    public static boolean testCPU() {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] getBytes(short s, boolean bBigEnding) {
        byte[] buf = new byte[2];
        if (bBigEnding)
            for (int i = buf.length - 1; i >= 0; i--) {
                buf[i] = (byte) (s & 0x00ff);
                s >>= 8;
            }
        else
            for (int i = 0; i < buf.length; i++) {
                buf[i] = (byte) (s & 0x00ff);
                s >>= 8;
            }
        return buf;
    }

    public static byte[] getBytes(int s, boolean bBigEnding) {
        byte[] buf = new byte[4];
        if (bBigEnding) {
            for (int i = buf.length - 1; i >= 0; i--) {
                buf[i] = (byte) (s & 0x000000ff);
                s >>= 8;
            }
        } else {
            for (int i = 0; i < buf.length; i++) {
                buf[i] = (byte) (s & 0x000000ff);
                s >>= 8;
            }
        }
        return buf;
    }

    public static byte[] getBytes(long s, boolean bBigEnding) {
        byte[] buf = new byte[8];
        if (bBigEnding)
            for (int i = buf.length - 1; i >= 0; i--) {
                buf[i] = (byte) (s & 0x00000000000000ff);
                s >>= 8;
            }
        else
            for (int i = 0; i < buf.length; i++) {
                buf[i] = (byte) (s & 0x00000000000000ff);
                s >>= 8;
            }
        return buf;
    }

    public static short getShort(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        }

        return r;
    }

    public static int getInt(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 4) {
            throw new IllegalArgumentException("byte array size > 4 !");
        }
        int r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        }
        return r;
    }

    public static long getLong(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 8) {
            throw new IllegalArgumentException("byte array size > 8 !");
        }
        long r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00000000000000ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00000000000000ff);
            }
        }
        return r;
    }


    public static byte[] getBytes(int i) {
        return getBytes(i, testCPU());
    }

    public static byte[] getBytes(short s) {
        return getBytes(s, testCPU());
    }

    public static byte[] getBytes(long l) {
        return getBytes(l, testCPU());
    }

    public static int getInt(byte[] buf) {
        return getInt(buf, testCPU());
    }

    public static short getShort(byte[] buf) {
        return getShort(buf, testCPU());
    }

    public static long getLong(byte[] buf) {
        return getLong(buf, testCPU());
    }

    public static short[] Bytes2Shorts(byte[] buf) {
        byte bLength = 2;
        short[] s = new short[buf.length / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = new byte[bLength];
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getShort(temp);
        }
        return s;
    }

    public static byte[] Shorts2Bytes(short[] s) {
        byte bLength = 2;
        byte[] buf = new byte[s.length * bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = getBytes(s[iLoop]);
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                buf[iLoop * bLength + jLoop] = temp[jLoop];
            }
        }
        return buf;
    }

    public static int[] Bytes2Ints(byte[] buf) {
        byte bLength = 4;
        int[] s = new int[buf.length / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = new byte[bLength];
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getInt(temp);
        }
        return s;
    }

    public static byte[] Ints2Bytes(int[] s) {
        byte bLength = 4;
        byte[] buf = new byte[s.length * bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = getBytes(s[iLoop]);
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                buf[iLoop * bLength + jLoop] = temp[jLoop];
            }
        }
        return buf;
    }

    public static long[] Bytes2Longs(byte[] buf) {
        byte bLength = 8;
        long[] s = new long[buf.length / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = new byte[bLength];
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getLong(temp);
        }
        return s;
    }

    public static byte[] Longs2Bytes(long[] s) {
        byte bLength = 8;
        byte[] buf = new byte[s.length * bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = getBytes(s[iLoop]);
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                buf[iLoop * bLength + jLoop] = temp[jLoop];
            }
        }
        return buf;
    }

    public static byte[] short2byte(short[] sData, int size) {
        byte[] result = new byte[size * 2];
        if (size > sData.length) {
            Log.w("AudioRecorder", "short2byte: too long short data array");
        }
        for (int i = 0; i < size; ++i) {
            result[i * 2] = (byte) (sData[i] & 255);
            result[i * 2 + 1] = (byte) (sData[i] >> 8);
        }
        return result;
    }

    public static short[] byte2short(byte[] sData, int size) {
        short[] retVal = new short[size];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((sData[i * 2] & 0xff) | (sData[i * 2 + 1] & 0xff) << 8);
        return retVal;
    }

    /**
     * 单声道转双声道
     * 8bit
     */
    public static byte[] byte8bitMerger(byte[] byte_1) {
        byte[] byte_2 = new byte[byte_1.length * 2];
        for (int i = 0; i < byte_1.length; i++) {
            byte_2[2 * i] = byte_1[i];
            byte_2[2 * i + 1] = byte_1[i];
        }
        return byte_2;
    }

    /**
     * 单声道转双声道
     * 16bit
     */
    public static byte[] byte16bitMerger(byte[] byte_1) {
        byte[] byte_2 = new byte[byte_1.length * 2];
        for (int i = 0; i < byte_1.length; i++) {
            if (i % 2 == 0) {
                byte_2[2 * i] = byte_1[i];
                byte_2[2 * i + 1] = byte_1[i + 1];
            } else {
                byte_2[2 * i] = byte_1[i - 1];
                byte_2[2 * i + 1] = byte_1[i];
            }
        }
        return byte_2;
    }

    /**
     * 左右声道进行反转
     */
    public static byte[] getReversedData(byte[] data) {
        byte[] reversed = new byte[data.length];
        for (int i = 0; i < data.length - 3; i = i + 4) {
            reversed[i] = data[i + 2];
            reversed[i + 1] = data[i + 3];
            reversed[i + 2] = data[i];
            reversed[i + 3] = data[i + 1];
        }
        return reversed;
    }

    /**
     * 双声道处理
     * 使用leftData、rightData 进行其他处理
     */
    public static RawData splitStereoPcm(byte[] data) {
        RawData resultBuff = new RawData();
        int monoLength = data.length / 2;
        byte[] leftData = new byte[monoLength];
        byte[] rightData = new byte[monoLength];
        for (int i = 0; i < monoLength; i++) {
            if (i % 2 == 0) {
                System.arraycopy(data, i * 2, leftData, i, 2);
            } else {
                System.arraycopy(data, i * 2, rightData, i - 1, 2);
            }
        }
        resultBuff.setRawDataLeft(byte2short(leftData, monoLength / 2));
        resultBuff.setRawDataRight(byte2short(rightData, monoLength / 2));
        return resultBuff;
    }


}
