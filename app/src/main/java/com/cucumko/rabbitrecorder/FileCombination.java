package com.cucumko.rabbitrecorder;

import android.app.Activity;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCombination {

    Activity activity;


    String srcDir;
    String dstDir;

    public FileCombination(){
    }


    public void copyDirectory(File sourceF, File targetF){

        File[] target_file = sourceF.listFiles();
        for (File file : target_file) {
            File temp = new File(targetF.getAbsolutePath() + File.separator + file.getName());
            if(file.isDirectory()){
                temp.mkdir();
                copyDirectory(file, temp);
            } else {
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    fis = new FileInputStream(file);
                    fos = new FileOutputStream(temp);
                    byte[] b = new byte[4096];
                    int cnt = 0;
                    while((cnt=fis.read(b)) != -1){
                        fos.write(b, 0, cnt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    try {
                        fis.close();
                        fos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
