cmake_minimum_required(VERSION 3.10.2)

add_library( cgffmpeg
             SHARED
             src/main/cpp/CGFFmpeg.cpp
             src/main/cpp/CGFFmpegVideoDecoder.cpp
             src/main/cpp/CGGlobal.cpp
             )
#头文件路径
include_directories(libs/include)

add_library( avcodec SHARED IMPORTED)
set_target_properties( avcodec PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavcodec.so)

add_library( avfilter SHARED IMPORTED)
set_target_properties( avfilter PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavfilter.so)

add_library( avformat SHARED IMPORTED)
set_target_properties( avformat PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavformat.so)

add_library( avutil SHARED IMPORTED)
set_target_properties( avutil PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavutil.so)

add_library( swresample SHARED IMPORTED)
set_target_properties( swresample PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswresample.so)

add_library( swscale SHARED IMPORTED)
set_target_properties( swscale PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswscale.so)

find_library( log-lib log )
target_link_libraries( cgffmpeg
                     avfilter
                     avcodec
                     avformat
                     avutil
                     swresample
                     swscale
                     android
                     ${log-lib} )