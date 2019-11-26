package com.kjs.medialibrary;
import android.util.Log;

import com.kjs.medialibrary.BuildConfig;

/**
 * 作者：柯嘉少 on 2019/11/13
 * 邮箱：2449926649@qq.com
 * 说明：
 * 修订者：
 * 版本：1.0
 */
public class LogMedia {
    //手动设置是否显示该模块日志,默认发布版本时不显示，调试版本时也不显示，调试的时候请打开
    public static boolean display = BuildConfig.DEBUG? true: false;//false: false

    private static final int MAX_LENGTH = 3 * 1024;//单次输出最长日志
    private static final int ASSERT = Log.ASSERT;
    private static final int DEBUG = Log.DEBUG;
    private static final int ERROR = Log.ERROR;
    private static final int INFO = Log.INFO;
    private static final int VERBOSE = Log.VERBOSE;
    private static final int WARN = Log.WARN;


    public static void verbose(String str) {
        printLong(VERBOSE, str);
    }


    public static void debug(String str) {
        printLong(DEBUG, str);
    }

    public static void info(String str) {
        printLong(INFO, str);
    }

    public static void warn(String str) {
        printLong(WARN, str);
    }

    public static void error(String str) {
        printLong(ERROR, str);
    }

    private static void printLong(int level, Object obj) {
        if (!display) {
            return;
        }
        String origin = obj == null ? "null" : obj.toString();
        int strLen = origin.length();
        if (strLen < MAX_LENGTH) {
            printLog(level, origin);
        } else {
            StringBuilder builder = new StringBuilder(origin);
            int start = 0;
            int end = MAX_LENGTH;
            String msg;
            do {
                msg = builder.substring(start, end);
                printLog(level, msg);
                start = end;
                end += MAX_LENGTH;
            } while (end < strLen);
            msg = builder.substring(start, strLen);
            printLog(level, msg);
        }

    }

    private static void printLog(int level, String msg) {
        msg = ">>>>>>" + msg;
        switch (level) {
            case VERBOSE:
                Log.v(generateTag(), msg);
                break;
            case DEBUG:
                Log.d(generateTag(), msg);
                break;
            case INFO:
                Log.i(generateTag(), msg);
                break;
            case WARN:
                Log.w(generateTag(), msg);
                break;
            case ERROR:
                Log.e(generateTag(), msg);
                break;
            case ASSERT:
                Log.e(generateTag(), msg);
                break;
            default:
                Log.w("LogTips", "没有预设的输出等级" + msg);
                break;
        }

    }

    /**
     * 获取输出日志的类，方法，输出行数
     *
     * @return
     */
    public static String generateTag() {
        String tag = "%s.%s(L:%d)";
        StackTraceElement caller = Thread.currentThread().getStackTrace()[6];
        tag = String.format(tag, caller.getFileName(), caller.getMethodName(), caller.getLineNumber());
        return tag;
    }
}

