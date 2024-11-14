package com.sqs.resourceshare_android.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sqs.resourceshare_android.entity.ResourceChunk;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientUtil {
    /***
     *  编码集
     */
    private final static String CHAR_SET = "UTF-8";
    /***
     *  Post表单请求形式请求头
     */
    private final static String CONTENT_TYPE_POST_FORM = "application/x-www-form-urlencoded";
    /***
     *  Post Json请求头
     */
    private final static String CONTENT_TYPE_JSON = "application/json";


    public static void doGet(String url, Map<String, String> params, HttpCallBack callBack) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        // uri拼接参数
        if (null != params) {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                urlBuilder.addQueryParameter(next.getKey(), next.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(5, TimeUnit.SECONDS) // 设置读取超时时间为10秒
                .writeTimeout(5, TimeUnit.SECONDS); // 设置写入超时时间为10秒
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.error(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                callBack.response(str);
                //注意，这里返回的线程不是主线程,注意UI操作。而Retrofit返回的是在主线程。
                Log.d("test", "当前线程为: " + Thread.currentThread().getName());
            }
        });
    }

    /**
     * Post请求,表单形式
     */
    public static void doPost(String url, Map<String, String> params, HttpCallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();// 创建 FormBody.Builder 对象

        // uri拼接参数
        if (null != params) {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                builder.add(next.getKey(), next.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(5, TimeUnit.SECONDS) // 设置读取超时时间为10秒
                .writeTimeout(5, TimeUnit.SECONDS); // 设置写入超时时间为10秒
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.error(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                callBack.response(str);
                //注意，这里返回的线程不是主线程,注意UI操作。而Retrofit返回的是在主线程。
                Log.d("test", "当前线程为: " + Thread.currentThread().getName());
            }
        });

    }


    public static void downloadFile(String fileURL, String savePath, HttpCallBack callback) {
        try {
            long downloadSize = 0;//下载文件大小
            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(savePath,false);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                downloadSize += bytesRead;
                callback.response("" + downloadSize);
            }
            callback.response("success");
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            callback.error(e.getMessage());
        }
    }


    /***
     *  Post请求,Json形式
     */
    public static void doPostJson(String url, String jsonStr, HttpCallBack callBack) {


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(5, TimeUnit.SECONDS) // 设置读取超时时间为10秒
                .writeTimeout(5, TimeUnit.SECONDS); // 设置写入超时时间为10秒
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.error(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                callBack.response(str);
                //注意，这里返回的线程不是主线程,注意UI操作。而Retrofit返回的是在主线程。
                Log.d("test", "当前线程为: " + Thread.currentThread().getName());
            }
        });


    }

   /* public static String sendPost(String url, HashMap<String, String> map, File file) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);

            String fileName = "file.txt";

            // 构建multipart form data
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, fileName);

            // 设置请求头和实体
            httpPost.setHeader("Authorization", "Bearer your_access_token"); // 如果需要的话添加认证头
            httpPost.setEntity(builder.build());

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);

            // 打印响应
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static void uploadFile(String url, ResourceChunk resourceChunk, File file, HttpCallBack callBack) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(5, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(5, TimeUnit.SECONDS) // 设置读取超时时间为10秒
                .writeTimeout(5, TimeUnit.SECONDS); // 设置写入超时时间为10秒
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), requestBody)
                .addFormDataPart("chunkNumber", resourceChunk.getChunkNumber().toString())
                .addFormDataPart("chunkSize", resourceChunk.getChunkSize().toString())
                .addFormDataPart("currentChunkSize", resourceChunk.getCurrentChunkSize().toString())
                .addFormDataPart("totalSize", resourceChunk.getTotalSize().toString())
                .addFormDataPart("identifier", resourceChunk.getIdentifier())
                .addFormDataPart("filename", resourceChunk.getFilename())
                .addFormDataPart("totalChunks", resourceChunk.getTotalChunks().toString())
                .addFormDataPart("relativePath", resourceChunk.getRelativePath())
                // 可以添加其他表单字段
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.error(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                callBack.response(str);
                //注意，这里返回的线程不是主线程,注意UI操作。而Retrofit返回的是在主线程。
                Log.d("test", "当前线程为: " + Thread.currentThread().getName());
            }
        });
    }

    public static void uploadFile(String url, ResourceChunk resourceChunk, byte[] fileInput, HttpCallBack callBack) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileInput);

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", resourceChunk.getFilename(), requestBody)
                .addFormDataPart("chunkNumber", resourceChunk.getChunkNumber().toString())
                .addFormDataPart("chunkSize", resourceChunk.getChunkSize().toString())
                .addFormDataPart("currentChunkSize", resourceChunk.getCurrentChunkSize().toString())
                .addFormDataPart("totalSize", resourceChunk.getTotalSize().toString())
                .addFormDataPart("identifier", resourceChunk.getIdentifier())
                .addFormDataPart("filename", resourceChunk.getFilename())
                .addFormDataPart("totalChunks", resourceChunk.getTotalChunks().toString())
                .addFormDataPart("relativePath", resourceChunk.getRelativePath())
                // 可以添加其他表单字段
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.error(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                callBack.response(str);
                //注意，这里返回的线程不是主线程,注意UI操作。而Retrofit返回的是在主线程。
                Log.d("test", "当前线程为: " + Thread.currentThread().getName());
            }
        });

    }

    public static void main(String[] args) {
        // get 请求
        //String getUrl = "https://www.baidu.com/s";
        //String getUrl = "http://localhost:9888/boxes/getprojectdaystats";
        //String getUrl = "https://gw.alipayobjects.com/os/antvdemo/assets/data/algorithm-category.json";
        //Map m = new HashMap();
        //  m.put("year","2023");
        // m.put("age","123");
        //String result = doGet(getUrl,m );
        //  System.out.println("result = " + result);

        //Post 请求  baocuo
        // String postUrl = "http://localhost:8088/device/factmag/getInfo";
        // Map m = new HashMap();
        //m.put("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6IjNkNmI3ZTFjLTU0ZjgtNGE2NS1iMTY1LTE2NjczYzM1MWIxZiIsInVzZXJuYW1lIjoiYWRtaW4ifQ.AyLfDdY9PjpWInEnaiDUBdJVpStEsP1oheWrxCdRcflzJSWPL2VMFFL2SngTO5S0jrI3uwQBWAsccCOG4hRygg");
        //m.put("facregCode","999999");
        // String s = doPost(postUrl, m);
        //System.out.println("s = " + s);


        //String postJsonUrl = "http://127.0.0.1:8080/testHttpClientUtilPostJson";
     /*   User user = new User();
        user.setUid("123");
        user.setUserName("小明");
        user.setAge("18");
        user.setSex("男");
         String jsonStr = JSON.toJSONString(user);
        doPostJson(postJsonUrl,jsonStr); */

        // System.out.println(s);
        // System.out.println(result);
    }
}
