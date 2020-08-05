package com.kjs.medialibrary.nactive;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * 作者：柯嘉少 on 2020/8/3
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class NativeFFMPEG {
    public static boolean hasLoad=false;

    public interface CallBack{
        void onStart();
        void onProgress(int progress);
        void onEnd();
    }

    //执行FFmpeg命令
    private static native int run(int cmdLen, String[] cmd);

    //获取命令执行进度
    public static native int getProgress();

    //测试jni，显示ffmpeg的信息
    public native String stringFromJNI() throws IllegalStateException;


    public static void loadFFMEPGLib(){
        if(!hasLoad){
            System.loadLibrary("native-lib");
        }
        hasLoad=true;

    }

    /**
     * 同步执行命令
     * @param cmd
     * @return
     */
    public static int excute(@NonNull String[] cmd) {
        if(!hasLoad){
            Log.e("NativeFFMPEG(同步执行命令1)：","ffmpeg尚未加载");
            return -1;
        }
        Log.d("NativeFFMPEG执行命令-->", "run: " + cmd.toString());
        return run(cmd.length, cmd);
    }

    /**
     * 同步执行命令
     * @param cmd
     * @return
     */
    public static int excute(ArrayList<String> cmd) {
        if(!hasLoad){
            Log.e("NativeFFMPEG(同步执行命令2)：","ffmpeg尚未加载");
            return -1;
        }
        String[] cmdArr = new String[cmd.size()];
        Log.d("NativeFFMPEG执行命令-->", "run: " + cmd.toString());
        return run(cmd.size(), cmd.toArray(cmdArr));
    }

    /**
     * 异步执行命令
     * @param commands
     * @param callBack
     */
    public static void excute(String[] commands, final CallBack callBack){
        if(!hasLoad){
            Log.e("NativeFFMPEG(异步执行命令)：","ffmpeg尚未加载");
            return;
        }
        new AsyncTask<String[],Integer,Integer>(){

            @Override
            protected void onPreExecute() {
                if(callBack!=null){
                    callBack.onStart();
                }
            }

            @Override
            protected Integer doInBackground(String[]... strings) {
                return excute(strings[0]);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if(callBack!=null){
                    callBack.onProgress(values[0]);
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if(callBack!=null){
                    callBack.onEnd();
                }
            }
        }.execute(commands);
    }

}
