package com.kjs.medialibrary.nactive;

/**
 * 作者：柯嘉少 on 2020/8/3
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class NativeFFMPEG {
    public static boolean hasLoad=false;

    public static void loadFFMEPGLib(){
        if(!hasLoad){
            System.loadLibrary("native-lib");
        }
        hasLoad=true;

    }
    public native String stringFromJNI() throws IllegalStateException;
}
