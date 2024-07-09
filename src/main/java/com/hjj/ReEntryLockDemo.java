package com.hjj;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReEntryLockDemo {
    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + "\t -----come in");
        m2();
        System.out.println(Thread.currentThread().getName() + "\t -----end m1");
    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + "\t -----come in");
        m3();

    }

    public synchronized void m3() {
        System.out.println(Thread.currentThread().getName() + "\t -----come in");
    }

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        ReEntryLockDemo reEntryLockDemo = new ReEntryLockDemo();
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t -----come in 外层调用");
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + "\t -----come in 内层调用");
                } finally {
                    // lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t -----come in 外层调用");
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

    private static void reEntryM1 () {

    }
}