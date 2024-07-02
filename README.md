# JUC 笔记

![image-20240701204232476](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240701204232.png)

## 一.基础

**并发和并行**

并发：是在同一个实体上的多个事件，是在一台处理器上“同时”处理多个任务，同一时刻只有一个事件发生

并行：是在不同实体上的多个事件，是在多台处理器上同时处理多个任务，同一时刻，大家真的都在做事情

**管程**

管程（Monitor）Monitor 就是一种同步机制，他的义务是保证同一时间只有一个线程可以访问被保护的数据和代码

JVM 中同步是基于进入和退出监视器对象（管程对象）来实现的，每个对象实例都会有一个 Monitor 对象，底层是 C++ 实现的



**用户线程和守护线程**

用户线程：是系统的工作线程，它会完成这个程序需要完成的业务操作

守护线程：是一种特殊的线程，为其他线程服务的，在后台默默地完成一些系统性的服务（垃圾回收线程就是最好的例子）。没有服务对象就没有必要继续运行了。（当系统只剩下守护线城时，JVM 就会自动退出）

线程的 daemon 属性：true 表示是守护线程，false 表示不是的

小总结：

1. 如果用户线程全部结束意味着程序要完成的业务操作已经结束，守护进程随着 JVM 一起结束工作
2. setDaemon(true) 方法必须在 start 前设置，否则抛出 llegalThreadStateException



**Future 接口**

Future 接口（FutureTask 实现类）定义了操作执行异步任务的一些方法，如获取异步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等

作用：为主线程开一个分支任务，专门为主线程处理耗时和费力的复杂业务。提供了一种异步并行计算的功能。

> 如果主线程需要执行一个耗时很长的任务，可以通过 Future 接口把这个任务放到异步线程中执行，主线程继续处理其他任务或先结束，再通过 Future 获取计算结果。

目的：异步多线程任务执行且返回有结果  三个特点：多线程/有返回/异步任务



FutureTask 实现 RunnableFuture -> Future、Runnable



FutureTask + 线程池多线程实战

```java
public class FutureThreadPoolDemo {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        long startTime = System.currentTimeMillis();
        FutureTask<String> futureTask1 = new FutureTask<>(() -> {
            TimeUnit.MICROSECONDS.sleep(500);
            return "task1 over";
        });

        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            TimeUnit.MICROSECONDS.sleep(500);
            return "task2 over";
        });

        threadPool.submit(futureTask1);
        threadPool.submit(futureTask2);

        System.out.println(futureTask1.get());
        System.out.println(futureTask2.get());

        TimeUnit.MILLISECONDS.sleep(300);
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime：" + (endTime - startTime) + "ms");
        threadPool.shutdown();
    }

    @SneakyThrows
    private static void m1() {
        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        // 暂停毫米
        TimeUnit.MICROSECONDS.sleep(500);
        TimeUnit.MICROSECONDS.sleep(500);
        TimeUnit.MICROSECONDS.sleep(500);
        System.out.println("----costTime：" + (endTime - startTime) + "ms");
        System.out.println(Thread.currentThread().getName() + "\t----end");
    }
}

```

