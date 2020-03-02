package com.kjs.medialibrary.video.hw2.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 作者：柯嘉少 on 2019/11/12
 * 邮箱：2449926649@qq.com
 * 说明：首先在Activity的界面层构造一个SurfaceView用于显
 * 示渲染结果；然后在Native层用EGL和OpenGL ES构造一个渲染线程用
 * 于渲染该SurfaceView，同时在该渲染线程中生成一个纹理ID并传递到
 * Java层；Java层利用该纹理ID构造出一个Surface-Texture，之后再将该
 * SurfaceTexture作为Camera的预览目标。最终调用Camera的开始预览方
 * 法，这样就可以将摄像头采集到的视频帧渲染到设备屏幕上了
 * 修订者：
 * 版本：1.0
 */
public class CameraPreview extends SurfaceView {
    private SurfaceHolder mSurfaceHolder = null;
    private Context mContext;
    //private CameraUtil cameraUtil;
    private String Tag = this.getClass().getSimpleName();

    public CameraPreview(Context context) {
        super(context);
        mContext = context;
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }
}
