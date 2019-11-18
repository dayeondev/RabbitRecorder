package com.cucumko.rabbitrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";

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
        fileCombination = new FileCombination(this);


    }

    void callStartRecorder(){
//        핸들러 참고
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
//        fileCombination.copyDirectory(new File(record.downloadPath), new File(record.downloadPath + "/../copy"));
//        fileCombination.CombineWaveFile(record.downloadPath + "/001/record02.aac", record.downloadPath + "/001/record03.aac", record.downloadPath + "/test_taeyang.aac");
        fileCombination.CombineAllFiles(record.downloadPath, 0, 0, 20);
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










