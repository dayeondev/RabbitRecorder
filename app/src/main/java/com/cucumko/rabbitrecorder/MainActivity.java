package com.cucumko.rabbitrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = new Record(this);


//        initRecorder();
//        permissionCheck();
//
//        callStartRecorder();

    }

    void callStartRecorder(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 5000); // delay: 처음에 몇 초 기다릴지  period: 얼마마다 실행할지
    }

    void initRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        buttonRecord = (Button) findViewById(R.id.button_record);
        buttonPlay = (Button) findViewById(R.id.button_play);
    }

    // Recorder Set
    void initAudioRecorder(){
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mediaPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/record.aac";
        Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "file path is " + mediaPath);
        mediaRecorder.setOutputFile(mediaPath);
        try{
            mediaRecorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public void onButtonRecordClicked(View view) {
        record.startRecord();
//        if(!isRecording){
////            initAudioRecorder();
////            mediaRecorder.start();
//            record.startRecord();
//
//            isRecording = true;
////            buttonRecord.setText("Stop Recording");
//        } else{
////            mediaRecorder.stop();
//            record.stopRecord();
//
//            isRecording = false;
////            buttonRecord.setText("Start Recording");
//        }

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
        else{

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










