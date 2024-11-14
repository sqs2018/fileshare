package com.sqs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/***
 * 测试用
 */
public class GetFileUtilTest {
    public static void main(String[] args) throws IOException {
        for(int i=1;i<52;i++){
            String filePath="D:\\resourceshare\\resourceshare\\data\\2024\\11\\1035510136-android-studio-20222120-windowsexe\\android-studio-2022.2.1.20-windows.exe-"+i;
            File file=new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte buf[]=new byte[(int)file.length()];
            //fileInputStream.skip(i * Md5Util.CHUNKSIZE);
            int readSize=fileInputStream.read(buf);
            if(readSize!=-1){
                String md5= Md5Util.getMD5Bytes(buf);
                System.out.println("md5=\t"+md5);
            }
            fileInputStream.close();
        }

    }
}
