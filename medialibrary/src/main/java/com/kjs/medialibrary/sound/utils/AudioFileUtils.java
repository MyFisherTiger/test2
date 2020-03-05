package com.kjs.medialibrary.sound.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者：柯嘉少 on 2019/11/13
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class AudioFileUtils {
    private static String rootPath = "audiorecord";
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath + "/pcm/";
    private final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";
    private final static String AUDIO_AMR_BASEPATH = "/" + rootPath + "/amr/";
    private final static String AUDIO_TEMP_BASEPATH = "/" + rootPath + "/temp/";//临时文件

    public static void setRootPath(String rootPath) {
        AudioFileUtils.rootPath = rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        String mAudioRawPath = "";
        if (!fileName.endsWith(".pcm")) {
            fileName = fileName + ".pcm";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/pcm/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioRawPath = fileBasePath + fileName;

        return mAudioRawPath;
    }

    public static String getAACFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }

        String mAudioPath = "";
        if (!fileName.endsWith(".aac")) {
            fileName = fileName + ".aac";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/aac/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioPath = fileBasePath + fileName;
        return mAudioPath;
    }


    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }

        String mAudioWavPath = "";
        if (!fileName.endsWith(".wav")) {
            fileName = fileName + ".wav";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/wav/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioWavPath = fileBasePath + fileName;
        return mAudioWavPath;
    }

    public static String getAmrFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }

        String mAudioWavPath = "";
        if (!fileName.endsWith(".amr")) {
            fileName = fileName + ".amr";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/amr/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioWavPath = fileBasePath + fileName;
        return mAudioWavPath;
    }

    //获取临时文件的方法名字
    public static String getTempFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }

        String mAudioWavPath = "";
        if (!fileName.endsWith(".amr")) {
            fileName = fileName + ".amr";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/temp/";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioWavPath = fileBasePath + fileName;
        return mAudioWavPath;
    }


    /**
     * 获取全部amr文件列表
     *
     * @return
     */
    public static List<File> getAmrFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootPath + "/amr/";

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        Collections.reverse(list); //倒序排列
        return list;
    }
}
