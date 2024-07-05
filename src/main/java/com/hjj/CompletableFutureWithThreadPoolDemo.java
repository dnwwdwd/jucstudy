package com.hjj;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureWithThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("1号任务" + Thread.currentThread().getName());
                return "abcd";
            }, threadPool).thenRunAsync(() -> {
                System.out.println("2号任务" + Thread.currentThread().getName());
            }).thenRun(() -> {
                System.out.println("3号任务" + Thread.currentThread().getName());
            }).thenRun(() -> {
                System.out.println("4号任务" + Thread.currentThread().getName());
            });
        } catch (Exception e) {

        }
    }
}
