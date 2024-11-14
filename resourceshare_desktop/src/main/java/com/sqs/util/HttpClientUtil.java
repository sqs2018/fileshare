package com.sqs.util;

import com.sqs.resourceshare.entity.ResourceChunk;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.security.auth.callback.Callback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

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
    private static final Log log = LogFactory.getLog(HttpClientUtil.class);
    /***
     *  连接管理器
     */
    private static PoolingHttpClientConnectionManager poolManager;
    /***
     *  请求配置
     */
    private static RequestConfig requestConfig;

    /*static {
        // 静态代码块,初始化HtppClinet连接池配置信息,同时支持http和https
        try {
            System.out.println("初始化连接池-------->>>>开始");
            // 使用SSL连接Https
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLContext sslContext = builder.build();
            // 创建SSL连接工厂
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();
            // 初始化连接管理器
            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 设置最大连接数
            poolManager.setMaxTotal(1);
            // 设置最大路由
            poolManager.setDefaultMaxPerRoute(10);
            // 从连接池获取连接超时时间
            int coonectionRequestTimeOut = 5000;
            // 客户端和服务器建立连接超时时间
            int connectTimeout = 5000;
            // 客户端从服务器建立连接超时时间
            int socketTimeout = 5000;
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(coonectionRequestTimeOut)
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            System.out.println("初始化连接池-------->>>>结束");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("初始化连接池-------->>>>失败");
        }
    }*/


    public static String doGet(String url, Map<String, String> params, HttpCallBack callBack) {
        String result = "";
        // 获取http客户端
        // CloseableHttpClient httpClient = getCloseableHttpClient();
        // 获取http客户端从连接池中
        CloseableHttpClient httpClient = new DefaultHttpClient();
        // 响应模型
        CloseableHttpResponse httpResponse = null;
        try {
            // 创建URI 拼接请求参数
            URIBuilder uriBuilder = new URIBuilder(url);
            // uri拼接参数
            if (null != params) {
                Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> next = it.next();
                    uriBuilder.addParameter(next.getKey(), next.getValue());
                }
            }
            URI uri = uriBuilder.build();
            // 创建Get请求
            HttpGet httpGet = new HttpGet(uri);
            httpResponse = httpClient.execute(httpGet, response -> {
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 获取响应实体
                    HttpEntity httpEntity = response.getEntity();
                    if (null != httpEntity) {
                        String resultTemp = EntityUtils.toString(httpEntity, CHAR_SET);
                        callBack.response(resultTemp);
                        System.out.println("响应内容:" + resultTemp);
                        return (CloseableHttpResponse) response;
                    }
                } else {
                    callBack.response("");
                }
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                System.out.println("响应码:" + statusCode);
                return (CloseableHttpResponse) response;
            });


        } catch (Exception e) {
            e.printStackTrace();
            callBack.error(e.getMessage());
        } finally {
            try {
                if (null != httpResponse) {
                    httpResponse.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static CloseableHttpClient getCloseableHttpClient() {
        return HttpClientBuilder.create().build();
    }

    /**
     * 从http连接池中获取连接
     */
   /* private static CloseableHttpClient getCloseableHttpClientFromPool() {
        //
        ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new ServiceUnavailableRetryStrategy() {
            @Override
            public boolean retryRequest(HttpResponse httpResponse, int executionCount, HttpContext httpContext) {
                if (executionCount < 3) {
                    System.out.println("ServiceUnavailableRetryStrategy");
                    return true;
                } else {
                    return false;
                }
            }

            // 重试时间间隔
            @Override
            public long getRetryInterval() {
                return 3000L;
            }
        };
        // 设置连接池管理
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolManager)
                // 设置请求配置策略
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
        return httpClient;

    }*/


    /**
     * Post请求,表单形式
     */
    public static String doPost(String url, Map<String, String> params, HttpCallBack callBack) {

        String result = "";
        // 获取http客户端
        CloseableHttpClient httpClient = new DefaultHttpClient();
        // 响应模型
        CloseableHttpResponse httpResponse = null;
        try {
            // Post提交封装参数列表
            ArrayList<NameValuePair> postParamsList = new ArrayList<>();
            if (null != params) {
                Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> next = it.next();
                    postParamsList.add(new BasicNameValuePair(next.getKey(), next.getValue()));
                }
            }
            // 创建Uri
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(postParamsList, CHAR_SET);
            // 设置表达请求类型
            urlEncodedFormEntity.setContentType(CONTENT_TYPE_POST_FORM);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            // 设置请求体
            httpPost.setEntity(urlEncodedFormEntity);
            // 执行post请求
            httpResponse = httpClient.execute(httpPost, response -> {

                if (response.getStatusLine().getStatusCode() == 200) {
                    String resultTemp = EntityUtils.toString(response.getEntity(), CHAR_SET);
                    System.out.println("Post form reponse {}" + resultTemp);
                    callBack.response(resultTemp);
                }
                return (CloseableHttpResponse) response;
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBack.error(e.getMessage());
        } finally {
            try {
                CloseResource(httpClient, httpResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static void downloadFile(String fileURL, String savePath, HttpCallBack callback) {
        try {
            long downloadSize = 0;//下载文件大小
            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[2048000];
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

    private static void CloseResource(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) throws IOException {
        if (null != httpResponse) {
            httpResponse.close();
        }
        if (null != httpClient) {
            httpClient.close();
        }
    }


    /***
     *  Post请求,Json形式
     */
    public static void doPostJson(String url, String jsonStr, HttpCallBack callBack) {
        String result = "";
        CloseableHttpClient httpClient = getCloseableHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            // 创建Post
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            // 封装请求参数
            StringEntity stringEntity = new StringEntity(jsonStr, CHAR_SET);
            // 设置请求参数封装形式
            stringEntity.setContentType(CONTENT_TYPE_JSON);
            httpPost.setEntity(stringEntity);
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(), CHAR_SET);
                //System.out.println(result);
                callBack.response(result);
                // return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            callBack.error(e.getMessage());
        } finally {
            try {
                CloseResource(httpClient, httpResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // return result;
    }

    public static String sendPost(String url, HashMap<String, String> map, File file) {
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
    }

    public static void uploadFile(String url, ResourceChunk resourceChunk, File file, HttpCallBack callBack) {
        try (CloseableHttpClient httpClient = new DefaultHttpClient();) {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            builder.addPart("file", fileBody);
            //
            // httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            builder.addTextBody("chunkNumber", resourceChunk.getChunkNumber().toString());
            builder.addTextBody("chunkSize", resourceChunk.getChunkSize().toString());
            builder.addTextBody("currentChunkSize", resourceChunk.getCurrentChunkSize().toString());
            builder.addTextBody("totalSize", resourceChunk.getTotalSize().toString());
            builder.addTextBody("identifier", resourceChunk.getIdentifier());
            builder.addTextBody("filename", StringUtils.chineseToUnicode(resourceChunk.getFilename()));
            builder.addTextBody("totalChunks", resourceChunk.getTotalChunks().toString());
            builder.addTextBody("relativePath", StringUtils.chineseToUnicode(resourceChunk.getRelativePath()));


            httpPost.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int code = response.getStatusLine().getStatusCode();
                //上传成功
                callBack.response(String.valueOf(code));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callBack.error(e.getMessage());
        }

    }

    public static void uploadFile(String url, ResourceChunk resourceChunk, byte[] fileInput, HttpCallBack callBack) {
        try (CloseableHttpClient httpClient = new DefaultHttpClient();) {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //InputStreamBody inputStreamBody=new InputStreamBody(fileInput, ContentType.DEFAULT_BINARY,resourceChunk.getFilename());
            ByteArrayBody formBodyPart = new ByteArrayBody(fileInput, StringUtils.chineseToUnicode(resourceChunk.getFilename()));
            builder.addPart("file", formBodyPart);
            //
            // httpPost.setHeader("Content-Type", "multipart/form-data; charset=UTF-8");
            builder.addTextBody("chunkNumber", resourceChunk.getChunkNumber().toString());
            builder.addTextBody("chunkSize", resourceChunk.getChunkSize().toString());
            builder.addTextBody("currentChunkSize", resourceChunk.getCurrentChunkSize().toString());
            builder.addTextBody("totalSize", resourceChunk.getTotalSize().toString());
            builder.addTextBody("identifier", resourceChunk.getIdentifier());
            builder.addTextBody("filename", StringUtils.chineseToUnicode(resourceChunk.getFilename()));
            builder.addTextBody("totalChunks", resourceChunk.getTotalChunks().toString());
            builder.addTextBody("relativePath", StringUtils.chineseToUnicode(resourceChunk.getRelativePath()));

            httpPost.setEntity(builder.build());
            System.out.println();

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int code = response.getStatusLine().getStatusCode();
                //上传成功
                callBack.response(String.valueOf(code));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callBack.error(e.getMessage());
        }

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
