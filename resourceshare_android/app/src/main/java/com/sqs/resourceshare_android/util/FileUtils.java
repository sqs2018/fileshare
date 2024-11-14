package com.sqs.resourceshare_android.util;

import android.os.Environment;

import java.io.File;

public class FileUtils {
    public static String getExternalStoragePath() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return file.getAbsolutePath();
    }

}
