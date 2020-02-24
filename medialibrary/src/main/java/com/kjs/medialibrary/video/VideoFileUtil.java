package com.kjs.medialibrary.video;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 作者：柯嘉少 on 2019/11/20
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class VideoFileUtil {
    private static String rootPath = "vediorecord";
    private final static String AUDIO_ORIGIN_BASEPATH = "/" + rootPath + "/origin/";//刚编完码字节流
    private final static String AUDIO_MP4_BASEPATH = "/" + rootPath + "/mp4/";
    private final static String AUDIO_MKV_BASEPATH = "/" + rootPath + "/mkv/";
    private final static String AUDIO_AVI_BASEPATH = "/" + rootPath + "/avi/";

    public static void setRootPath(String rootPath) {
        VideoFileUtil.rootPath = rootPath;
    }

    public static String getOriginFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        String mAudioRawPath = "";
        if (!fileName.endsWith(".mp4")) {
            fileName = fileName + ".mp4";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/video/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioRawPath = fileBasePath + fileName;

        return mAudioRawPath;
    }

    public static String getMP4FileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        String mAudioRawPath = "";
        if (!fileName.endsWith(".mp4")) {
            fileName = fileName + ".mp4";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/mp4/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioRawPath = fileBasePath + fileName;

        return mAudioRawPath;
    }

}
