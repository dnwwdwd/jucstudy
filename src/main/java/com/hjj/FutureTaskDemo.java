package com.hjj;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {
    @SneakyThrows
    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(new MyThread3());
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        System.out.println(futureTask.get());
    }
}

class MyThread3 implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("----come in call() ");
        return "hello Callable";
    }
}