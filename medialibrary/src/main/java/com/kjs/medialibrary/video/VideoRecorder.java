package com.kjs.medialibrary.video;

import android.app.Activity;
import android.media.AudioRecord;
import android.view.SurfaceHolder;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.video.encoder.BaseVideoEncoder;

/**
 * 作者：柯嘉少 on 2019/11/28
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class VideoRecorder {
    private CameraUtil cameraUtil;
    private AudioRecord audioRecord;
    private BaseVideoEncoder videoEncoder = null;
    private boolean record = false;
    private int width = 1280;//1280
    private int height = 720;//720
    private int FPS = 24;

    /**
     * 请先初始化（调用init方法），再设置解码器(如果先设置解码器再init，解码器会阻塞)
     * @param videoEncoder
     */
    public void setVideoEncoder(BaseVideoEncoder videoEncoder) {
        this.videoEncoder = videoEncoder;
        //这个方法因为只需调用一次
        this.videoEncoder.init(FPS, FPS * width * height / 1000, width, height);//30帧，256kb的码率（固定的帧率，码率硬解码）
    }

    /**
     * @param context
     * @param surfaceHolder
     */
    public void init(Activity context, SurfaceHolder surfaceHolder) {
        cameraUtil = new CameraUtil(context,width,height);
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


    }

    private void encodeVideo(byte[] data) {
        if (videoEncoder == null) {
            return;
        }
        LogMedia.info("去编码");
        videoEncoder.encode(data);

    }


    /**
     * 开始录制视频
     */
    public void startRecordVideo() {
        LogMedia.info("开始录制视频");
        videoEncoder.setFinishedEncoder(false);
        record = true;
    }

    /**
     * 暂停录制视频
     */
    public void pauseRecordVideo() {
        record = false;
        videoEncoder.setFinishedEncoder(true);
        LogMedia.info("暂停录制视频");
    }

    /**
     * 停止录制视频
     */
    public void StopRecordVideo() {
        LogMedia.info("停止录制视频");
        videoEncoder.setFinishedEncoder(true);
        record = false;
        videoEncoder.release();
    }

    /**
     * 停止录像与释放摄像头是两回事
     */
    public void releaseCamera() {
        cameraUtil.stopPreview();
        cameraUtil.releaseCamera();
    }

}