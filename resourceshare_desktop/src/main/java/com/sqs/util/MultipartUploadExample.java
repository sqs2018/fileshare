package com.sqs.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class MultipartUploadExample {

    public static void main(String args[]) throws Exception{

        //Creating CloseableHttpClient object
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //Creating a file object
        File file = new File("sample.txt");

        //Creating the FileBody object
        FileBody filebody = new FileBody(file, ContentType.DEFAULT_BINARY);

        //Creating the MultipartEntityBuilder
        MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();

        //Setting the mode
        entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        //Adding text
        entitybuilder.addTextBody("sample_text", "This is the text part of our file");

        //Adding a file
        entitybuilder.addBinaryBody("image", new File("logo.png"));

        //Building a single entity using the parts
        HttpEntity mutiPartHttpEntity = entitybuilder.build();

        //Building the RequestBuilder request object
        RequestBuilder reqbuilder = RequestBuilder.post("http://httpbin.org/post");

        //Set the entity object to the RequestBuilder
        reqbuilder.setEntity(mutiPartHttpEntity);

        //Building the request
        HttpUriRequest multipartRequest = reqbuilder.build();

        //Executing the request
        HttpResponse httpresponse = httpclient.execute(multipartRequest);

        //Printing the status and the contents of the response
        System.out.println(EntityUtils.toString(httpresponse.getEntity()));
        System.out.println(httpresponse.getStatusLine());
    }
}