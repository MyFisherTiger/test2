package com.kjs.medialibrary;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

public abstract class BaseEncoder {
    protected CallBackEncodeData listener = null;


    public interface CallBackEncodeData {
        void callBack(boolean isAudio,ByteBuffer outputBuffer, MediaFormat outPutFormat, MediaCodec.BufferInfo bufferInfo);

    }

    public void setCallBackEncodeData(CallBackEncodeData callBackEncodeData) {
        this.listener = callBackEncodeData;
    }
}
