package com.hjj;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CompletableFutureUseDemo {
    @SneakyThrows
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "-----come in");
            int result = ThreadLocalRandom.current().nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("----- 1 秒后出结果" + result);
            return result;
        }).whenComplete((v,e) -> {
            if (e == null) {
                System.out.println("----- 计算完成，更新系统 Update" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println("异常情况：" + e.getCause() + "\t" + e.getMessage());
            return null;
        });
        System.out.println(Thread.currentThread().getName() + "线程先去忙其他任务了");
    }

    @SneakyThrows
    public static void future1 () {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t -----come in");
            int result = ThreadLocalRandom.current().nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("----- 1 秒后出结果" + result);
            return result;
        });
        System.out.println(Thread.currentThread().getName() + "线程先去忙其他任务了");
    }
}
