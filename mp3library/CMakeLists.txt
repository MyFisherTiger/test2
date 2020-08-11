# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

#头文件路径
include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/include)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#添加共享库搜索路径
#LINK_DIRECTORIES(${PROJECT_SOURCE_DIR}/libs/armeabi-v7a)

# 指定源文件目录
AUX_SOURCE_DIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp/extra SRC_LIST)

add_library( # Sets the name of the library.
            lamemp3
            # Sets the library as a shared library.
            SHARED
            # Provides a relative path to your source file(s).
            ${CMAKE_SOURCE_DIR}/src/main/cpp/native-lib.cpp
            ${SRC_LIST}
        )

find_library( log-lib log )

target_link_libraries(
                    lamemp3
                     android
                     ${log-lib})