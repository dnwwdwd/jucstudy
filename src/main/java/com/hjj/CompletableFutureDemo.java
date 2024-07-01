package com.hjj;

import java.util.concurrent.Callable;

public class CompletableFutureDemo {
    public static void main(String[] args) {

    }
}

class MyThread implements Runnable {
    @Override
    public void run() {

    }
}

class MyThread2 implements Callable<String> {
    @Override
    public String call() throws Exception {
        return null;
    }
}
