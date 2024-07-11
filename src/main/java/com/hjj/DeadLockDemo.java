package com.hjj;

import java.util.concurrent.TimeUnit;

public class DeadLockDemo {
    public static void main(String[] args) {
        final Object objectA = new Object();
        final Object objectB = new Object();

        new Thread(() -> {
            synchronized (objectA) {
                System.out.println(Thread.currentThread().getName() + "\t 自己持有 A 锁，希望获得 B 锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (objectB) {
                    System.out.println(Thread.currentThread().getName() + "\t 成功获得 B 锁");
                }
            }
        }, "A").start();

        new Thread(() -> {
           synchronized (objectB) {
               System.out.println(Thread.currentThread().getName() + "\t 自己持有 B 锁，希望获得 A 锁");
               try {
                   TimeUnit.SECONDS.sleep(1);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
               synchronized (objectA) {
                   System.out.println(Thread.currentThread().getName() + "\t 成功获得 A 锁");
               }
           }
        }, "B").start();
    }
}
