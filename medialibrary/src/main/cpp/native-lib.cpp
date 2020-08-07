#include <jni.h>
#include <string>
#include <libavcodec/jni.h>
#include <ffmpeg.h>

#include "android/log.h"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "native-lib.c", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "native-lib.c", __VA_ARGS__)

extern "C" {
#include <libavcodec/avcodec.h> //引入ffmpeg的包
}
extern "C" JNIEXPORT jstring JNICALL Java_com_kjs_medialibrary_nactive_NativeFFMPEG_stringFromJNI(JNIEnv *env,jobject /* this */) {
      std::string hello = "Hello from C++";
      return env->NewStringUTF(avcodec_configuration());
}

/**
 * 使用命令驱动ffmpeg
 */
extern "C" JNIEXPORT jint JNICALL Java_com_kjs_medialibrary_nactive_NativeFFMPEG_run(JNIEnv *env, jclass clazz, jint cmdLen, jobjectArray cmd) {
      //set java vm
      JavaVM *jvm = NULL;
      env->GetJavaVM(&jvm);
      //av_jni_set_java_vm(jvm, NULL);

      char *argCmd[cmdLen] ;
      jstring buf[cmdLen];

      for (int i = 0; i < cmdLen; ++i) {
            buf[i] = static_cast<jstring>(env->GetObjectArrayElement(cmd, i));
            char *string = const_cast<char *>(env->GetStringUTFChars(buf[i], JNI_FALSE));
            argCmd[i] = string;
            LOGD("argCmd=%s",argCmd[i]);
      }

      int retCode = 0;//ffmpeg_exec(cmdLen, argCmd);
      LOGD("ffmpeg-invoke: retCode=%d",retCode);

      return retCode;
}

/**
 * 获取命令执行进度
 */
extern "C" JNIEXPORT jint JNICALL Java_com_kjs_medialibrary_nactive_NativeFFMPEG_getProgress(JNIEnv *env, jclass clazz) {
      int i=0;//get_progress();
      return i;
}
