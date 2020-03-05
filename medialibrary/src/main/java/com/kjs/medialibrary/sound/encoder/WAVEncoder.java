package com.kjs.medialibrary.sound.encoder;

import android.media.MediaFormat;

import com.kjs.medialibrary.sound.utils.AudioFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：柯嘉少 on 2019/11/19
 * 邮箱：2449926649@qq.com
 * 说明：pcm转wave实际上是不需要转码的，只需要添加wave的格式封装头
 * 修订者：
 * 版本：1.0
 */
public class WAVEncoder extends BaseAudioEncoder {
    String fileName;
    File wavFIle;
    File pcmFile;
    byte[] readByte;
    int readSize = 10 * 1024;
    FileInputStream fis;
    FileOutputStream fos;

    @Override
    public void init(int SAMPLE_RATE, int BIT_RATE, int CHANNEL_COUNT) {
        super.init(SAMPLE_RATE, BIT_RATE, CHANNEL_COUNT);

    }

    @Override
    public void encode(String sourceFile) {
        fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        destinationFile = AudioFileUtils.getWavFileAbsolutePath(fileName);
        wavFIle = new File(destinationFile);
        pcmFile = new File(sourceFile);
        readByte = new byte[readSize];
        try {
            fis = new FileInputStream(pcmFile);
            fos = new FileOutputStream(wavFIle);
            //PCM文件大小
            long totalAudioLen = fis.getChannel().size();

            //总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalDataLen = totalAudioLen + 36;
            addHead(fos, totalAudioLen, totalDataLen, SAMPLE_RATE, CHANNEL_COUNT, BIT_RATE);
            int length = 0;
            while ((length = fis.read(readByte)) > 0) {
                fos.write(readByte, 0, length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            readByte = null;
        }

    }

    @Override
    public void encode(byte[] encoderData) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void release() {

    }

    /**
     * pcm转wave实际上是不需要转码的,所以也没有MediaFormat
     * @return
     */
    @Override
    public MediaFormat getMediaFormat() {
        return null;
    }

    private void addHead(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';  //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;   // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
