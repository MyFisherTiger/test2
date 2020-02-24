package com.kjs.medialibrary.video;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.SurfaceHolder;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.sound.AudioRecorder;
import com.kjs.medialibrary.sound.encoder.AACEncoder;
import com.kjs.medialibrary.sound.encoder.WAVEncoder;
import com.kjs.medialibrary.video.camera.CameraUtil;
import com.kjs.medialibrary.video.encoder.BaseVideoEncoder;
import com.kjs.medialibrary.video.mux.MuxerHelper;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 作者：柯嘉少 on 2019/11/28
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class VideoRecorder {
    private CameraUtil cameraUtil;
    private AudioRecorder audioRecorder;
    private BaseVideoEncoder videoEncoder = null;
    private boolean record = false;
    private int width = 1280;//1280
    private int height = 720;//720
    private int FPS = 24;

    /**
     * 请先初始化（调用init方法），再设置解码器(如果先设置解码器再init，解码器会阻塞)
     *
     * @param videoEncoder
     */
    public void setVideoEncoder(BaseVideoEncoder videoEncoder) {
        this.videoEncoder = videoEncoder;
        //这个方法因为只需调用一次
        this.videoEncoder.init(FPS, FPS * width * height / 1000, width, height);//30帧，256kb的码率（固定的帧率，码率硬解码）
    }

    /**
     * 初始化
     * 1.cameraUtil打开摄像头提供预览，及预览数据监听回调
     * 2.audioRecord准备录制音频
     *
     * @param context
     * @param surfaceHolder
     */
    public void init(Activity context, SurfaceHolder surfaceHolder) {
        cameraUtil = new CameraUtil(context, width, height);
        cameraUtil.open(0);
        cameraUtil.initRecordVideo();
        cameraUtil.startPreview(surfaceHolder);

        cameraUtil.setVideoCallBack(new CameraUtil.VideoCallBack() {
            @Override
            public void result(byte[] data) {
                if (record) {
                    encodeVideo(data);

                }
            }
        });




        audioRecorder = new AudioRecorder();
        audioRecorder.setEncoder(new AACEncoder());
        //audioRecorder.setEncoder(new AMREncoder());
        //audioRecorder.setEncoder(new WAVEncoder());

    }

    private MediaMuxer muxer;
    private void encodeVideo(byte[] data) {
        if (videoEncoder == null) {
            return;
        }
        LogMedia.info("去编码");
        videoEncoder.encode(data);
        //编码后回调合成
        videoEncoder.setCallBackEncodeData(new BaseVideoEncoder.CallBackEncodeData() {
            @Override
            public void callBack(ByteBuffer outputBuffer, MediaFormat outPutFormat, MediaCodec.BufferInfo bufferInfo) {
                try {
                    String desFile=VideoFileUtil.getMP4FileAbsolutePath("desFile");
                    muxer = new MediaMuxer(desFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    int currentTrackIndex=muxer.addTrack(outPutFormat);
                    //boolean finished = false;
                    //boolean isAudioSample=false;//是否来自音频
                    muxer.start();
                    muxer.writeSampleData(currentTrackIndex,outputBuffer,bufferInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    /**
     * 开始录制视频
     */
    public void startRecordVideo() {
        LogMedia.info("开始录制视频");
        videoEncoder.setFinishedEncoder(false);
        record = true;

        audioRecorder.start();
    }

    /**
     * 暂停录制视频
     */
    public void pauseRecordVideo() {
        record = false;
        videoEncoder.setFinishedEncoder(true);
        LogMedia.info("暂停录制视频");

        audioRecorder.pause();
    }

    /**
     * 停止录制视频
     */
    public void StopRecordVideo() {
        LogMedia.info("停止录制视频");
        videoEncoder.setFinishedEncoder(true);
        record = false;
        videoEncoder.release();
        //String str=VideoFileUtil.getOriginFileAbsolutePath("input");
        //MuxerHelper.muxerVideo(str);

        audioRecorder.stop();

        //muxer.stop();
        //muxer.release();
    }

    /**
     * 停止录像与释放摄像头是两回事
     */
    public void releaseCamera() {
        cameraUtil.stopPreview();
        cameraUtil.releaseCamera();
    }

}
