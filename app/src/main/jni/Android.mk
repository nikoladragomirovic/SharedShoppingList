LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := Check
LOCAL_SRC_FILES := nikola_dragomirovic_shoppinglist_Check.cpp

NDK_TOOLCHAIN_VERSION := clang
APP_STL := c++_shared
LOCAL_CPPFLAGS := -std=c++11
APP_PLATFORM := android-<minSdkVersion>

include $(BUILD_SHARED_LIBRARY)
