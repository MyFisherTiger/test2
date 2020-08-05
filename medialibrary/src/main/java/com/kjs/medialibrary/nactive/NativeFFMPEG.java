package com.kjs.medialibrary.nactive;

import android.os.AsyncTask;

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

    public static void loadFFMEPGLib(){
        if(!hasLoad){
            System.loadLibrary("native-lib");
        }
        hasLoad=true;

    }


    /*public static int excute(String[] commands){
        return run(commands);
    }

    public static void excute(String[] commands, final CallBack callBack){
        new AsyncTask<String[],Integer,Integer>(){

            @Override
            protected void onPreExecute() {
                if(callBack!=null){
                    callBack.onStart();
                }
            }

            @Override
            protected Integer doInBackground(String[]... strings) {
                return run(strings[0]);
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

    public native static int run(String[] commands);*/

    public native String stringFromJNI() throws IllegalStateException;
}
