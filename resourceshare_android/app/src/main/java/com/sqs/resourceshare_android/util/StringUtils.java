package com.sqs.resourceshare_android.util;


import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class StringUtils {

    //处理ISO 8601格式时间
    public static String formatDate(String dateTimeString) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  //yyyy-MM-dd'T'HH:mm:ss.SSSZ
            Date date = df.parse(dateTimeString);
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = df1.parse(date.toString());
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df2.format(date1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 格式化文件大小, 输出成带单位的字符串
     *
     * @param {Number} size 文件大小
     * @param {Number} [pointLength=2] 精确到的小数点数。
     * @param {Array}  [units=[ 'B', 'K', 'M', 'G', 'TB' ]] 单位数组。从字节，到千字节，一直往上指定。如果单位数组里面只指定了到了K(千字节)，同时文件大小大于M, 此方法的输出将还是显示成多少K.
     * @method formatSize
     * @grammar formatSize(size) => String
     * @grammar formatSize(size, pointLength) => String
     * @grammar formatSize(size, pointLength, units) => String
     * @example console.log(formatSize ( 100) );    // => 100B
     * console.log( formatSize( 1024 ) );    // => 1.00K
     * console.log( formatSize( 1024, 0 ) );    // => 1K
     * console.log( formatSize( 1024 * 1024 ) );    // => 1.00M
     * console.log( formatSize( 1024 * 1024 * 1024 ) );    // => 1.00G
     * console.log( formatSize( 1024 * 1024 * 1024, 0, ['B', 'KB', 'MB'] ) );    // => 1024MB
     */
    public static String formatSize(long size) {

        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return size / 1024 + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            return size / 1024 / 1024 + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            return size / 1024 / 1024 / 1024 + "GB";
        } else {
            return size / 1024 / 1024 / 1024 / 1024 + "TB";
        }

    }


    //从文本内容提取文件名
    public static String getFileName(String context) {
        char fileName[] = new char[5];
        int i = 0;
        int index = 0;
        do {
            if (i > context.length() - 1) {
                break;
            }
            char c = context.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')) {
                fileName[index] = c;
                index++;
            }
            i++;
        } while (i < 5);
        return String.copyValueOf(fileName).trim() + getUUID() + ".txt";
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName 文件名
     * @return FileType
     */
    public static String getFileType(String fileName) {
        int index = fileName.lastIndexOf(".");
        String type = fileName.substring(index);
        return type;
    }

    public static String chineseToUnicode(String chinese) {
        try {
            return new String(Base64.getEncoder().encode(chinese.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String unicodeToChinese(String unicode) {
        try {
            return new String(Base64.getDecoder().decode(unicode));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String[] args) {
        String temp = getFileName("中");
        System.out.println(temp);
    }
}
