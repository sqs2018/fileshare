package com.sqs.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPoolUtils
{
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable)
    {
        executorService.execute(runnable);
    }
}
