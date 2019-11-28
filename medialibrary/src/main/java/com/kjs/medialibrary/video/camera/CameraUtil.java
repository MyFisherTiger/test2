package com.kjs.medialibrary.video.camera;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.kjs.medialibrary.LogMedia;
import com.kjs.medialibrary.video.encoder.BaseVideoEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：柯嘉少 on 2019/11/11
 * 邮箱：2449926649@qq.com
 * 说明：摄像头工具类，由于官方的cameraX（貌似也是基于camera2的）还没稳定仍为alpha版本
 * ， camera2部分厂商还没适配且api较复杂（但是功能也多）
 * ，还是先使用camera1的api稳定，后面看情况更换（看cameraX的发展趋势，优先选用）
 * 修订者：
 * 版本：1.0
 */
public class CameraUtil {
    private Activity mContext;
    private boolean hasACamera = false;
    private Camera camera;
    private int cameraId = 0;
    private PackageManager pm;
    private PhotoCallBack photoCallBack = null;
    private VideoCallBack videoCallBack = null;
    private int width;
    private int height;

    public static interface PhotoCallBack {
        void result(byte[] data);
    }

    public static interface VideoCallBack {
        void result(byte[] data);
    }

    public CameraUtil(Activity mContext,int width,int height) {
        this.mContext = mContext;
        this.width=width;
        this.height=height;
    }

    public void setPhotoCallBack(PhotoCallBack photoCallBack) {
        if (!hasACamera) {
            return;
        }
        this.photoCallBack = photoCallBack;
    }

    public void setVideoCallBack(VideoCallBack videoCallBack) {
        if (!hasACamera) {
            return;
        }
        this.videoCallBack = videoCallBack;
    }

    /**
     * @param tag 0后置摄像头，1前置摄像头
     */
    public void open(int tag) {
        cameraId = tag;
        pm = mContext.getPackageManager();

        //FEATURE_CAMERA ： 后置相机;FEATURE_CAMERA_FRONT ： 前置相机
        if (cameraId == 0) {
            hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } else {
            hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        }

        if (!hasACamera) {
            return;
        }
        //打开摄像机。默认打开后置，0后置摄像机，1前置摄像机
        try {
            camera = Camera.open(cameraId);
            camera.setPreviewCallback(null);
            camera.stopPreview();
        } catch (Exception e) {
            Log.e("Camera Error", "打开摄像头错误" + e.toString());
            hasACamera = false;
            return;
        }

        //设置相机参数
        Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
        parameters.setJpegQuality(100);//设置照片的质量
        //是否支持预览格式NV21
        List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
        if (supportedPreviewFormats.contains(ImageFormat.NV21)) {//NV21
            //如果不需要对视频帧进行处理，可以把图像格式设置为YUV格式，可以直接给编码器进行编码，否则还需要进行格式转换
            parameters.setPreviewFormat(ImageFormat.NV21);
        } else {
            Log.e("Camera Error", "视频参数设置错误:设置预览图像格式异常,将使用默认格式");
        }
        //如果需要对视频帧添加滤镜等渲染操作，那么就必须把图像格式设置为RGB格式
        //parameters.setPictureFormat(ImageFormat.JPEG);
        //是否支持分辨率尺寸
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        boolean isSupportPreviewSize = isSupportPreviewSize(supportedPreviewSizes, width, height);
        if (isSupportPreviewSize) {
            parameters.setPreviewSize(width, height);//设置预览尺寸
        } else {
            Log.e("Camera Error", "视频参数设置错误:设置预览的尺寸异常，按摄像头支持的最大尺寸预览");
        }
        parameters.setPictureSize(width, height);//设置照片尺寸
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        //Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;// 连续对焦模式
        //Camera.Parameters.FOCUS_MODE_AUTO; //自动聚焦模式
        //Camera.Parameters.FOCUS_MODE_INFINITY;//无穷远
        //Camera.Parameters.FOCUS_MODE_MACRO;//微距
        //Camera.Parameters.FOCUS_MODE_FIXED;//固定焦距

        //parameters.setExposureCompensation(0);//设置固定的曝光补偿为0
        //parameters.setAutoExposureLock(true);//关闭自动调节曝光补偿
        //parameters.setAutoWhiteBalanceLock(true);//关闭自动调节黑白平衡
        /*if (parameters.isVideoStabilizationSupported()) {//不是所有的照相机设备都支持图像稳定化,判断一下
            parameters.setVideoStabilization(true);//设置视频稳定输出
        }*/
       /* List<Integer> frameRates = parameters.getSupportedPreviewFrameRates();
        int fpsMax = 0;
        for (Integer n : frameRates) {
            LogMedia.info("摄像头支持的采集帧率: " + n);
            if (fpsMax < n) {
                fpsMax = n;
            }
        }
        parameters.setPreviewFrameRate(fpsMax);*///这里选用最大采集帧率
        //parameters.setPreviewFpsRange(50,60);//设置预览帧率范围50fps~60fps
        //parameters.setRecordingHint(true);//提高到最大帧率预览，其实没什么作用，设置还是无改变
        camera.setParameters(parameters);


        //有些手机上面会出现旋转90度的情况（Android兼容性问题）所以我们要在这里适配一下
        Camera.CameraInfo info = new Camera.CameraInfo();
        //获取摄像头信息
        Camera.getCameraInfo(cameraId, info);
        int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
        //获取摄像头当前的角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            // 后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * 判断是否包含该分辨率的尺寸
     *
     * @param supportedPreviewSizes 支持的分辨率尺寸
     * @param width                 设置的宽
     * @param height                设置的高
     * @return true支持，false不支持
     */
    private boolean isSupportPreviewSize(List<Camera.Size> supportedPreviewSizes, int width, int height) {
        for (Camera.Size item : supportedPreviewSizes) {
            Log.e("支持的分辨率尺寸:", "宽" + item.width + "高" + item.height);
            if (item.height == height && item.width == width) {
                return true;
            }
        }
        return false;
    }


    /**
     * 开始预览
     *
     * @param surfaceHolder
     */
    public void startPreview(SurfaceHolder surfaceHolder) {
        if (!hasACamera) {
            return;
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);//通过SurfaceView显示取景画面
            camera.startPreview();//开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (!hasACamera) {
            return;
        }
        camera.setPreviewCallback(null);
        camera.stopPreview();
    }

    /**
     * 结束预览，释放相机
     */
    public void releaseCamera() {
        if (!hasACamera) {
            return;
        }
        camera.setPreviewCallback(null);
        camera.stopPreview();//结束预览
        camera.release();
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        if (!hasACamera) {
            return;
        }
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //拍照 byte数据即是照取的图片 可以直接转换成bitmap
                //此方法只会调用一次 建议写在点击事件中
                //Bitmap mbitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (photoCallBack != null) {
                    photoCallBack.result(data);
                }
            }
        });
    }

    /**
     * 初始化，获取录制回调数据
     * <p>
     * <p>
     * 摄像头的数据回调时间并不是确定的，就算你设置了摄像头FPS范围为30-30帧，它也不会每秒就一定给你30帧数据。
     * Android摄像头的数据回调，受光线的影响非常严重，这是由HAL层的3A算法决定的，
     * 你可以将自动曝光补偿、自动白平光等等给关掉，这样你才有可能得到稳定的帧率。
     * <p>
     * 稳定帧率的办法：按照固定时间编码，如果没有新的摄像头数据回调来就用上一帧的数据
     */
    public void initRecordVideo() {
        if (!hasACamera) {
            return;
        }

        //final byte[] temp = new byte[5];
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //System.arraycopy(data, 0, temp, 0, 4);
                LogMedia.error("当前配置的帧率为：" + camera.getParameters().getPreviewFrameRate());
                //LogMedia.error("有在回调码？" + Arrays.toString(temp));

                //surfaceholder或textureview消耗了才会填充新数据
                //这里byte数据即是监听surfaceholder或textureview消耗的byte获取的帧数据 只要相机正在预览就会一直回调此方法
                //需要注意的是 这里的byte数据不能够直接使用 需要转换下格式
                /*try {
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, 640, 480, null);
                    if (image != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, 640, 480), 80, stream);
                        mbitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        stream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/

                if (videoCallBack != null) {
                    videoCallBack.result(data);
                }
            }
        });
    }


}
