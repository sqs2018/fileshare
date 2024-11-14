package com.sqs.resourceshare_android.util;

import java.io.File;

/***
 * 生成文件唯一码
 */
public class GenerateUniqueIdentifier {
    public static String generateUniqueIdentifier(File file) {
        String idetifier = file.length() + "-" + file.getName().replaceAll("[^0-9a-zA-Z_-]", "");
        return idetifier;
    }
    public static String generateUniqueIdentifier(long fileSize,String fileName) {
        String idetifier = fileSize+ "-" + fileName.replaceAll("[^0-9a-zA-Z_-]", "");
        return idetifier;
    }

    public static void main(String[] args) {
       // System.out.println(generateUniqueIdentifier("D:\\resourceshare\\wechat_desktop-main (2) - 副本.zip"));

    }
}
