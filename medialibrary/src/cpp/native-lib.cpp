#include <jni.h>
    #include <string>
    extern "C" {
    #include <libavcodec/avcodec.h> //引入ffmpeg的包
    }
    extern "C" JNIEXPORT jstring JNICALL
    Java_com_goodboy_myplay_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
        std::string hello = "Hello from C++";
        return env->NewStringUTF(av_version_info());//返回ffmpeg的版本
    }