package com.kjs.medialibrary.sound.utils;

/**
 * ProjectName：v2.23_bgm_comp_based_chinese_pre
 * Describe：分离左右声道类
 * Author：Icex
 * CreationTime：2019/6/5
 */
public class RawData {

    private short[] rawDataLeft;
    private short[] rawDataRight;

    public RawData() {
    }

    public RawData(short[] rawDataLeft, short[] rawDataRight) {
        this.rawDataLeft = rawDataLeft;
        this.rawDataRight = rawDataRight;
    }

    public short[] getRawDataLeft() {
        return rawDataLeft;
    }

    public void setRawDataLeft(short[] rawDataLeft) {
        this.rawDataLeft = rawDataLeft;
    }

    public short[] getRawDataRight() {
        return rawDataRight;
    }

    public void setRawDataRight(short[] rawDataRight) {
        this.rawDataRight = rawDataRight;
    }
}
