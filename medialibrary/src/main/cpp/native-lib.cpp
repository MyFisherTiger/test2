#include <jni.h>
#include <string>
extern "C" {
#include <libavcodec/avcodec.h> //引入ffmpeg的包
}
extern "C" JNIEXPORT jstring JNICALL

Java_com_kjs_medialibrary_nactive_NativeFFMPEG_stringFromJNI(JNIEnv *env,jobject /* this */) {
      std::string hello = "Hello from C++";
      return env->NewStringUTF(avcodec_configuration());
}