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


    String srcDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    String dstDir = srcDir + "/copy";

    public FileCombination(Activity act){
        activity = act;

    }


    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if(sourceLocation.isDirectory()){
            if(!targetLocation.exists()){
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for(int i = 0; i < children.length; i++){
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        }
        else{
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf))>0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }


}
