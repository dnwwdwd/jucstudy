# JUC 笔记

![image-20240701204232476](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240701204232.png)

## 一.基础

### **并发和并行**

并发：是在同一个实体上的多个事件，是在一台处理器上“同时”处理多个任务，同一时刻只有一个事件发生

并行：是在不同实体上的多个事件，是在多台处理器上同时处理多个任务，同一时刻，大家真的都在做事情

**管程**

管程（Monitor）Monitor 就是一种同步机制，他的义务是保证同一时间只有一个线程可以访问被保护的数据和代码

JVM 中同步是基于进入和退出监视器对象（管程对象）来实现的，每个对象实例都会有一个 Monitor 对象，底层是 C++ 实现的



### **用户线程和守护线程**

用户线程：是系统的工作线程，它会完成这个程序需要完成的业务操作

守护线程：是一种特殊的线程，为其他线程服务的，在后台默默地完成一些系统性的服务（垃圾回收线程就是最好的例子）。没有服务对象就没有必要继续运行了。（当系统只剩下守护线城时，JVM 就会自动退出）

线程的 daemon 属性：true 表示是守护线程，false 表示不是的

小总结：

1. 如果用户线程全部结束意味着程序要完成的业务操作已经结束，守护进程随着 JVM 一起结束工作
2. setDaemon(true) 方法必须在 start 前设置，否则抛出 llegalThreadStateException



### **Future 接口**

Future 接口（FutureTask 实现类）定义了操作执行异步任务的一些方法，如获取异步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等

作用：为主线程开一个分支任务，专门为主线程处理耗时和费力的复杂业务。提供了一种异步并行计算的功能。

> 如果主线程需要执行一个耗时很长的任务，可以通过 Future 接口把这个任务放到异步线程中执行，主线程继续处理其他任务或先结束，再通过 Future 获取计算结果。

目的：异步多线程任务执行且返回有结果  三个特点：多线程/有返回/异步任务



FutureTask 实现 RunnableFuture -> Future、Runnable



优点：FutureTask + 线程池能显著提高程序的执行效率

多线程实战

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

缺点：

1. get 方法容易阻塞（一旦调用必须等到结果才会离开，不管是否计算完成，容易程序堵塞）

   ```java
       @SneakyThrows
       public static void main(String[] args) {
           FutureTask<String> futureTask = new FutureTask<>(() -> {
               System.out.println(Thread.currentThread().getName() + "\t ----come in");
               TimeUnit.SECONDS.sleep(5);
               return "task over";
           });
           Thread t1 = new Thread(futureTask, "t1");
           t1.start();
           System.out.println(futureTask.get());
           System.out.println(Thread.currentThread().getName() + "\t ----忙其他任务了");
       }
   ```

   输出结果（先执行子线程，执行完才执行 main 线程  -> 阻塞了主线程的执行）：

   ```
   t1	 ----come in
   task over
   main	 ----忙其他任务了
   ```

   > 解决方法：通过指定等待时间来避免，过时不候
   >
   > System.*out*.println(futureTask.get(3, TimeUnit.*SECONDS*));  这样子就会抛出超时异常，因为子线程执行时间为 5 秒

2. isDone 轮询：轮询会耗费无谓的 CPU 的资源，也不能及时获取计算结果

   ```java
       @SneakyThrows
       public static void main(String[] args) {
           FutureTask<String> futureTask = new FutureTask<>(() -> {
               System.out.println(Thread.currentThread().getName() + "\t ----come in");
               TimeUnit.SECONDS.sleep(5);
               return "task over";
           });
           Thread t1 = new Thread(futureTask, "t1");
           t1.start();
           System.out.println(Thread.currentThread().getName() + "\t ----忙其他任务了");
           while(true) {
               if (futureTask.isDone()) {
                   System.out.println(futureTask.get());
                   break;
               } else {
                   TimeUnit.MILLISECONDS.sleep(500);
                   System.out.println("正在处理中，不要在催了，越催越慢，再催熄火");
               }
           }
       }
   ```

结论：Future 对于结果的获取不是很友好，只能通过阻塞方式或轮询的方式获取任务的结果



### CompletableFuture

作用：

1. 可将多个异步任务的计算结果组合起来，后一个异步任务的计算结果依赖前一个结果
2. 将两个或多个异步计算合成一个计算，这个几个异步计算相互独立

CompletableFuture 提供了一种类似观察者模式的机制，当任务执行完成会通知监听方

**类架构说明**：

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240703210335.png" alt="image-20240703210335363" style="zoom:50%;" />

**CompletionStage 代表异步计算过程中的某一阶段，一个阶段完成后会触发另一个阶段**



#### 获取结果和触发计算

**创建异步任务的 2 种方式：**

![image-20240703211447152](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240703211447.png)

1. runAsync 无返回值

   ```java
       @SneakyThrows
       public static void main(String[] args) {
           CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
               System.out.println(Thread.currentThread().getName() + "\t -----come in");
               try {
                   TimeUnit.SECONDS.sleep(1);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               System.out.println("task is over");
           });
           System.out.println(completableFuture.get());
       }
   ```

   输出内容：

   ```
   ForkJoinPool.commonPool-worker-9	 -----come in
   task is over
   null
   ```

2. **supplyAsync 有返回值**

   - get 方法阻塞主线程
   - get(timeout, TimeUnit) 过时不候，未在规定时间内执行完就抛异常
   - getNow 立即获取结果，计算完就获取计算完的结果，否则就获取设定的值。立即获取结果不阻塞
   - complet 方法返回的是布尔值，true 代表打断了 get 方法，反之代表未打断

**CompletableFuture 的优点：**

1. 异步任务结束后会自动回调某个对象的方法
2. 主线程设置好回调后，不再关心异步任务的执行，异步任务之间可顺序执行
3. 异步任务出错时，会自动回调某个对象的方法

![image-20240704203719101](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240704203719.png)

CompletableFuture get 方法和 join 方法的区别：

1. **异常处理**：

   - `get()` 方法声明了 `throws InterruptedException, ExecutionException`，因此必须处理这两个异常，否则会编译错误。
   - `join()` 方法没有声明抛出任何受检异常，因此在处理结果时更加方便，不需要显式捕获异常。

2. **返回值**：

   - `get()` 方法返回 `T` 类型的结果或抛出异常（`ExecutionException` 包装实际异常）。
   - `join()` 方法直接返回 `T` 类型的结果，或者如果有异常，会抛出 `CompletionException`，该异常会包装实际异常。

3. **使用场景**：

   - 如果你希望在获取结果时能够处理异常，可以使用 `join()` 方法，因为它简化了异常处理的过程。
   - 如果你需要对 `InterruptedException` 和 `ExecutionException` 进行精细的处理或转换，你可能更倾向于使用 `get()` 方法。

   

#### 对计算结果进行处理

**thenApply**

1. 计算结果存在依赖关系，这两个线程串行化
2. 异常相关，当前出现异常，不走下一步，并且后面都不走

**handle**

1. 计算结果存在依赖关系，这两个线程串行化
2. 异常相关，当前出现异常，不走下一步，但是后面的继续走，根据带的异常参数进一步处理

![image-20240704220124675](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240704220124.png)

总结：简单一点理解就是一个是并行的一个是串行的,串行的A步骤G了,就直接去处理异常的步骤了,并行的调用步骤A G了,就当没发生过直接去调用步骤B



#### 任务之间顺序执行

![image-20240704221113218](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240704221113.png)

#### 对计算结果进行消费

![image-20240705210126340](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240705210126.png)

#### 对计算结果进行选用

> 那个线程执行快，applyToEither 方法就返回谁

```java
public class CompletableFutureFastDemo {
    public static void main(String[] args) {
        CompletableFuture<String> playA = CompletableFuture.supplyAsync(() -> {
            System.out.println("A come in");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "playA";
        });

        CompletableFuture<String> playB = CompletableFuture.supplyAsync(() -> {
            System.out.println("B come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "playB";
        });

        CompletableFuture<String> result = playA.applyToEither(playB, f -> {
            return f + "is winner";
        });
        System.out.println(Thread.currentThread().getName() + "\t" + result.join());
    }
}
```

#### 对计算结果合并

```java
public class CompletableFutureCombineDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t ----启动");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10;
        });

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t ----启动");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 20;
        });

        completableFuture1.thenCombine(completableFuture2, (x, y) -> {
            System.out.println("开始两个结果合并");
            return x + y;
        });
    }
```



### 说说那些”锁“事

#### 乐观锁和悲观锁

乐观锁：认为使用数据时不会有别的线程修改数据或资源，因此不会加锁，而是通过版本号或者 CAS 算法来判断有无被修改（适合读操作多的场景）

悲观锁：认为使用数据时有别的线程会修改数据，因此会加锁来保证数据不被其他线程修改（适合写操作多的场景）



