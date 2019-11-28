package com.kjs.medialibrary.video.mux;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.nio.ByteBuffer;

/**
 * 作者：柯嘉少 on 2019/11/21
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class MuxerHelper {
    private MediaMuxer muxer;
    private MediaFormat audioFormat;
    private MediaFormat videoFormat;
    ByteBuffer inputBuffer;
    MediaCodec.BufferInfo bufferInfo;



    public void init(){

    }

    /*MediaMuxer muxer = new MediaMuxer("temp.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    // More often, the MediaFormat will be retrieved from MediaCodec.getOutputFormat()
    // or MediaExtractor.getTrackFormat().
    MediaFormat audioFormat = new MediaFormat(...);
    MediaFormat videoFormat = new MediaFormat(...);
    int audioTrackIndex = muxer.addTrack(audioFormat);
    int videoTrackIndex = muxer.addTrack(videoFormat);
    ByteBuffer inputBuffer = ByteBuffer.allocate(bufferSize);
    boolean finished = false;
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    muxer.start();
    while(!finished) {
            // getInputBuffer() will fill the inputBuffer with one frame of encoded
            // sample from either MediaCodec or MediaExtractor, set isAudioSample to
            // true when the sample is audio data, set up all the fields of bufferInfo,
            // and return true if there are no more samples.
            finished = getInputBuffer(inputBuffer, isAudioSample, bufferInfo);
            if (!finished) {
                int currentTrackIndex = isAudioSample ? audioTrackIndex : videoTrackIndex;
                muxer.writeSampleData(currentTrackIndex, inputBuffer, bufferInfo);
            }
    }
    muxer.stop();
    muxer.release();*/
}
