# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

#头文件路径
include_directories(${PROJECT_SOURCE_DIR}/include)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#添加共享库搜索路径
LINK_DIRECTORIES(${PROJECT_SOURCE_DIR}/armeabi-v7a)

# 指定源文件目录
#AUX_SOURCE_DIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp SRC_LIST)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        native-lib.cpp)

add_library( avcodec SHARED IMPORTED)
set_target_properties( avcodec PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libavcodec.so)

add_library( avfilter SHARED IMPORTED)
set_target_properties( avfilter PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libavfilter.so)

add_library( avformat SHARED IMPORTED)
set_target_properties( avformat PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libavformat.so)

add_library( avutil SHARED IMPORTED)
set_target_properties( avutil PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libavutil.so)

add_library( swresample SHARED IMPORTED)
set_target_properties( swresample PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libswresample.so)

add_library( swscale SHARED IMPORTED)
set_target_properties( swscale PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/${ANDROID_ABI}/libswscale.so)

#find_library( log-lib log )

target_link_libraries(
                     native-lib
                     avfilter
                     avcodec
                     avformat
                     avutil
                     swresample
                     swscale
                     android
                     ${log-lib})