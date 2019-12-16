package com.kjs.medialibrary;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：柯嘉少 on 2019/12/4
 * 邮箱：2449926649@qq.com
 * 说明：超时阻断工具类（定时器+线程+内部计数器实现，控制论中的主动触发理论），使用的不是单例，注意不要创建大量对象
 * 修订者：
 * 版本：1.0
 */
public class TimeOutUtil {
    private QuitTime quitTime;
    private long timeOut = 1000 * 1000;//默认允许的超时容忍值（1000000微秒=1秒），单位微秒
    private boolean tag=false;//doInTime是否及时跑完
    private Thread counterThread;
    private Timer timer=new Timer();

    public static interface QuitTime {
        void doInTime();//容忍时间内能执行就执行
        void release();//容忍时间一到，不管了做释放操作，停止执行
    }

    public void setQuitTime(QuitTime quitTime) {
        this.quitTime = quitTime;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public void start() {
        if (quitTime != null) {
            //这里应当使用线程池
            counterThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    quitTime.doInTime();
                    tag=true;
                }
            });
            counterThread.start();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(counterThread!=null&&!tag){
                        counterThread.interrupt();
                        quitTime.release();
                    }
                }
            },timeOut/1000);

        }
    }
}
