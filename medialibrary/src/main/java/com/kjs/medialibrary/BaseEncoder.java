package com.kjs.medialibrary;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

public abstract class BaseEncoder {
    protected CallBackEncodeData listener = null;
    protected boolean finishEncoder = false;//是否结束编码，true结束编码；false未结束编码

    /**
     * 设置是否结束编码
     *
     * @param finishEncoder 是否结束编码，true结束编码；false未结束编码
     */
    public void setFinishEncoder(boolean finishEncoder) {
        this.finishEncoder = finishEncoder;
    }

    public interface CallBackEncodeData {
        void callBack(boolean isAudio,ByteBuffer outputBuffer, MediaFormat outPutFormat, MediaCodec.BufferInfo bufferInfo);

    }

    public void setCallBackEncodeData(CallBackEncodeData callBackEncodeData) {
        this.listener = callBackEncodeData;
    }
}
