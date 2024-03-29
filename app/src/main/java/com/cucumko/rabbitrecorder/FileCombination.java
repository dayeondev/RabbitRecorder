package com.cucumko.rabbitrecorder;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileCombination {

    Activity activity;

    private final int RECORDER_SAMPLERATE = 44100;
    private final int RECORDER_BPP = 16;
    private final int bufferSize = 256;

    // CombineAllFiles에서 사용하는 인스턴스
    private File beforeFileName;
    private File afterFileName;


    public FileCombination(Activity activity){
        this.activity = activity;
    }


    void Check(String downPath, int dirNum,  int numberOfFiles){
        Toast.makeText(activity.getApplicationContext(),Integer.toString(CheckRecordedFile(downPath, dirNum, numberOfFiles)), Toast.LENGTH_SHORT).show();
    }

    // 파일 체크
    int CheckRecordedFile(String downPath, int dirNum,  int numberOfFiles){
        File checking;
        for(int i = 0; i < numberOfFiles; i++){
            checking = new File(
                    downPath + "/"
                            + String.format("%03d", dirNum)
                            + "/record"
                            + String.format("%02d", i)
                            +  ".aac"
            );
            if(!checking.exists()){  // 파일 없으면
                return i - 1;
            }
        }
        return 19; // 전부 있으므로 19를 리턴
    }


    // CombineWaveFile로 여러 파일 합치기
    // downloadPath + "/" + dirNum + "/" + "record" + (file-th) + ".aac"
    void CombineAllFiles(String downPath, int dirNum, int startNum, int numberOfFiles){
        Log.d(this.getClass().getName(), "CombineAllFiles 실행됨");
        if(CheckRecordedFile(downPath, dirNum, numberOfFiles) == numberOfFiles - 1) // copy protection
        for(int i = 0; i < numberOfFiles - 1; i++){
//        for(int i = 0; i < 2 - 1; i++){
            if(i == 0){ // 첫번째 파일일 경우 파일 명을 combine01로 변경한다.
                beforeFileName = new File(downPath + "/" + String.format("$03d", dirNum) + "/record" + String.format("%02d", startNum));
                afterFileName = new File(downPath + "/" + String.format("$03d", dirNum) + "/combine00.acc");
            }

                CombineWaveFile(
                        downPath + "/"
                                + String.format("%03d", dirNum)
                                + "/record"
                                + String.format("%02d", i)
                                +  ".aac",
                        downPath + "/"
                                + String.format("%03d", dirNum)
                                + "/record"
                                + String.format("%02d", (startNum + i + 1) % numberOfFiles)
                                +  ".aac",
                        downPath + "/"
                                + String.format("%03d", dirNum)
                                + "/combine"
                                + String.format("%02d", i + 1)
                                +  ".aac"
                );
//            Toast.makeText(activity.getApplicationContext(), "[테스트] i: " + Integer.toString(i), Toast.LENGTH_SHORT).show();
            Toast.makeText(activity.getApplicationContext(), "Done!!", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(activity.getApplicationContext(), "잠시 뒤에 시도하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    // 파일 합치기
    void CombineWaveFile(String file1, String file2, String outputPath) {
        FileInputStream in1 = null, in2 = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in1 = new FileInputStream(file1);
            in2 = new FileInputStream(file2);
            out = new FileOutputStream(outputPath);

            totalAudioLen = in1.getChannel().size() + in2.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in1.read(data) != -1) {

                out.write(data);

            }
            while (in2.read(data) != -1) {

                out.write(data);
            }

            out.close();
            in1.close();
            in2.close();

            Toast.makeText(activity.getApplicationContext(), "Done!!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
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
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        header[34] = RECORDER_BPP;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }


//    public ArrayList LoadFiles(int pathNum){
//
//    }
//


}
