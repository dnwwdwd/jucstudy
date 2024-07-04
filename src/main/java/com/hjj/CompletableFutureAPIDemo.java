package com.hjj;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPIDemo {
    @SneakyThrows
    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "abc";
        });
        // System.out.println(completableFuture.get()); // 会阻塞主线程
        // System.out.println(completableFuture.get(2L, TimeUnit.SECONDS)); // 过时不候，未在规定时间内执行完就抛异常
        // System.out.println(completableFuture.getNow("xxx"));
        System.out.println(completableFuture.complete("completeValue") + "\t" + completableFuture.join());
    }
}
