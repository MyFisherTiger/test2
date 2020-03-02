package com.kjs.medialibrary;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

public abstract class BaseEncoder {
    protected CallBackEncodeData listener = null;
    protected boolean finishEncoder = false;//是否结束编码，true结束编码；false未结束编码
    protected CallBackFinishEncode callBackFinishEncode=null;

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

    public interface CallBackFinishEncode {
        /**
         * 所有的帧数据编码都编码完毕后回调
         * @param tag 0表示所有的音频段数据都编码完毕，1表示所有的视频帧都编码完毕，2表示所有音频段和视频帧都被muxer合成好了时回调
         **/
        void callBackFinish(int tag);

    }

    public void setCallBackFinishEncode(CallBackFinishEncode callBackFinishEncode) {
        this.callBackFinishEncode = callBackFinishEncode;
    }
}
