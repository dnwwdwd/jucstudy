package com.hjj;

import lombok.SneakyThrows;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureApiDemo {
    @SneakyThrows
    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread().getName() + "\t ----come in");
            TimeUnit.SECONDS.sleep(5);
            return "task over";
        });
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        futureTask.get(2L, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName() + "\t ----忙其他任务了");
    }
}
