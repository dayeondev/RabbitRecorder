package com.cucumko.rabbitrecorder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.File;

class Record {


    // 레코더 생성, 저장경로 지정
    private MediaRecorder mediaRecorder = new MediaRecorder();
    String downloadPath;
    private String mediaPath;
    int recordNth = 0;
    private Activity activity;


    private boolean isRecording = false;
    int fileNum = 0;

    Record(Activity activity){

        this.activity = activity;
        downloadPath = this.activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//        downloadPath = Environment.getDataDirectory().getAbsolutePath() + "/RabbitRecorder";

    }

    void startRecord(){

        this.stopRecord();

        // 레코더 초기화
        isRecording = true;
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        //숫자 + record.acc로 저장
        File newDir = new File(downloadPath + "/" + String.format("%03d", recordNth));
        if(!newDir.exists()){
            newDir.mkdir();
        }

        mediaPath = downloadPath + "/"
                    + String.format("%03d", recordNth
                        ) + "/record"
                    + String.format("%02d",
                                    ++fileNum <20 ? fileNum: (fileNum=0)
                        )
                    + ".aac";
//        FILE TEST TOAST
//        Toast.makeText(activity.getApplicationContext(), mediaPath, Toast.LENGTH_SHORT).show();
        mediaRecorder.setOutputFile(mediaPath);
        try{
            mediaRecorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }

        // 녹음 시작
        mediaRecorder.start();

    }

    private void stopRecord(){
        if(isRecording){
            mediaRecorder.stop();
//            만약 stop에서 계속 에러가 발생한다면 확인해보자
//            https://itpangpang.xyz/318

        }


    }

    public void nextDirectory(){
        recordNth++;
    }

}
