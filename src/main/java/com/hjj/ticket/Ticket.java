package com.hjj.ticket;

import java.util.concurrent.locks.ReentrantLock;

public class Ticket {
    private int number = 50;

    ReentrantLock lock = new ReentrantLock(true);

    public void sale () {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第：\t" + (number--) + "\t 还剩下：" + number);
            }
        } finally {
            lock.unlock();
        }
    }
}