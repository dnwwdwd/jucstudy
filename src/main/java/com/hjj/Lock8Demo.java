package com.hjj;

import java.util.concurrent.TimeUnit;

/**
 * 题目：谈谈你对多线程锁的理解，8锁案例说明
 * 口决：线程
 * 操作
 * 资源类
 * 8锁案例说明：
 * 1.标准访问有αb两个线程，请问先打印邮件还是短信
 * 2.sendEmail方法中加入暂停3秒钟，请问先打印邮还是短信
 * 3.添加一个普通的hello方法，清间先打印邮件还是hello
 * 4.有两部手机，请间先打印邮件还是短信
 * 5.有两个静态同步方法，有1部手机，请间先打印娜件还是短信
 * 6.有两个静态同步方法，有2部手机，请间先打印那件还是短信
 * 7.有1个静态同步方法，有1个普通同步方法，有1部手机，请问先打印娜件还是短后
 * 8.有1个静态同步方法，有1个普通同步方法，有2部手机，请问先打印邮件还是短局
 */
class Phone {
    public static synchronized void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("------sendEmail");
    }

    public synchronized void sendMsg() {
        System.out.println("------sendMsg");
    }

    public void hello() {
        System.out.println("-------hello");
    }
}

public class Lock8Demo {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> {
            phone.sendEmail();
        }, "a").start();

        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            // phone.sendMsg();
            // phone.hello();
            phone.sendMsg();
        }, "b").start();

    }
}
