package com.hjj;

import java.util.concurrent.TimeUnit;

    /**
     * 题目：谈谈你对多线程锁的理解，8锁案例说明
     * 口诀：线程
     * 操作
     * 资源类
     * 8锁案例说明：
     * 1.标准访问有 α、b 两个线程，请问先打印邮件还是短信
     * 2.sendEmail方法中加入暂停3秒钟，请问先打印邮还是短信
     * 3.添加一个普通的hello方法，清间先打印邮件还是hello
     * 4.有两部手机，请间先打印邮件还是短信
     * 5.有两个静态同步方法，有1部手机，请间先打印邮件还是短信
     * 6.有两个静态同步方法，有2部手机，请间先打印那件还是短信
     * 7.有1个静态同步方法，有1个普通同步方法，有1部手机，请问先打印邮件还是短后
     * 8.有1个静态同步方法，有1个普通同步方法，有2部手机，请问先打印邮件还是短局
     */

    /**
     * 笔记总结：
     * 1-2：一个对象里有多个 synchronized 方法，锁的是对象，前提方法不被 static 修饰 -> 同一个对象调用两个方法，谁先调用谁先执行
     * 3-4：普通方法和同步锁无关 -> 所以普通方法先执行
     * 5-6：对于普通同步方法锁的是当前实例对象，对于静态同步方法锁的是当前 Class 对象，对于同步方法块，锁的是 synchronized 括号内的对象
     * 7-8：静态同步方法锁的是类，普通同步方法锁的是对象 -> 后者优先级大于前者
     */
    class Phone {
        public synchronized void sendEmail() {
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
                phone2.hello();
            }, "b").start();
        }
    }
