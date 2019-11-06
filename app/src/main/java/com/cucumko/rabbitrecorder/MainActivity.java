package com.cucumko.rabbitrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";
    private final int RECORDER_SAMPLERATE = 44100;
    private final int RECORDER_BPP = 16;
    private final int bufferSize = 256;


    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String mediaPath;

    boolean isRecording = false;
    boolean isPlaying = false;

    Button buttonRecord;
    Button buttonPlay;

    public Record record;

    FileCombination fileCombination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        record = new Record(this);
        permissionCheck();

        callStartRecorder();
        fileCombination = new FileCombination();


    }

//    https://stackoverflow.com/questions/11452464/merging-two-or-more-wav-files-in-android/11452687#11452687
//    https://stackoverflow.com/questions/18750892/merging-two-wave-files-on-android-concatenate
    private void CombineWaveFile(String file1, String file2) {
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

//            out = new FileOutputStream(getFilename3());
            out = new FileOutputStream(record.downloadPath + "/test_taeyang.aac");

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

            Toast.makeText(this, "Done!!", Toast.LENGTH_LONG).show();
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




        void callStartRecorder(){
//        핸들러 참고
//        Timer 사용시 문제가 생겨서 핸들러로 변경하였다.
//        https://wowon.tistory.com/95
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                record.startRecord();

            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);
                    try{
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }

    public void onButtonCopyClicked(View view){
//        record.nextDirectory();
//        fileCombination.copyDirectory(new File(record.downloadPath), new File(record.downloadPath + "/../copy"));
//        fileCombination.copyDirectory(new File(record.downloadPath), new File(record.downloadPath + "/../copy"));
        CombineWaveFile(record.downloadPath + "/000/record00.aac", record.downloadPath + "/000/record01.aac");
    }



    public void onButtonRecordClicked(View view) {
//        callStartRecorder();
    }

    public void onButtonPlayClicked(View view) {
        if(!isPlaying){
            try{
                mediaPlayer.setDataSource(mediaPath);
                mediaPlayer.prepare();
            }catch(Exception e){
                e.printStackTrace();
            }
            mediaPlayer.start();

            isPlaying = true;
            buttonPlay.setText("Stop Playing");
        }
        else{
            mediaPlayer.stop();

            isPlaying = false;
            buttonPlay.setText("Start Playing");
        }
    }

    public void permissionCheck(){

        int recordPermissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if(recordPermissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

        }
        else if(storagePermissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 0){

            if(grantResults[0] == 0){

                Toast.makeText(this, "레코드 권한 승인됨", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(this, "레코드 권한 거절", Toast.LENGTH_SHORT).show();

            }

        }

    }

}










