package com.hjj;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureThreadPoolDemo {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        long startTime = System.currentTimeMillis();
        FutureTask<String> futureTask1 = new FutureTask<>(() -> {
            TimeUnit.MICROSECONDS.sleep(500);
            return "task1 over";
        });

        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            TimeUnit.MICROSECONDS.sleep(500);
            return "task2 over";
        });

        threadPool.submit(futureTask1);
        threadPool.submit(futureTask2);

        System.out.println(futureTask1.get());
        System.out.println(futureTask2.get());

        TimeUnit.MILLISECONDS.sleep(300);
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime：" + (endTime - startTime) + "ms");
        threadPool.shutdown();
    }

    @SneakyThrows
    private static void m1() {
        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        // 暂停毫米
        TimeUnit.MICROSECONDS.sleep(500);
        TimeUnit.MICROSECONDS.sleep(500);
        TimeUnit.MICROSECONDS.sleep(500);
        System.out.println("----costTime：" + (endTime - startTime) + "ms");
        System.out.println(Thread.currentThread().getName() + "\t----end");
    }
}
