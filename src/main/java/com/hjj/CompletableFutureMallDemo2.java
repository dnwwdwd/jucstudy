package com.hjj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 1.同一款产品，同时搜索出同款产品在各大电商平台的售价
 * 2.同一款产品，同时瘦索出本产品在同一个电商平台下，各个入驻卖家售价是多少
 */
public class CompletableFutureMallDemo2 {
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("dangdang"),
            new NetMall("taobao")
    );

    public static List<String> getPrice(List<NetMall> list, String productName) {
        return list.stream().map(netMall ->
                String.format(productName + "in %s price is %.2f",
                        netMall.getNetMallName(),
                        netMall.calcPrice(productName)))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
//        List<String> mySQLList = getPrice(list, "MySQL"); // 耗时 3s
        List<String> mySQLList = getPriceByCompletableFuture(list, "MySQL"); // 耗时 1s
        for (String x : mySQLList) {
            System.out.println(x);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("-----耗费时间" + (endTime - startTime) + "ms");
    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list, String productName) {
        return list.stream().map(netMall -> CompletableFuture.supplyAsync(() ->
                String.format(productName + "in %s price is %.2f",
                        netMall.getNetMallName(),
                        netMall.calcPrice(productName)))
        ).collect(Collectors.toList()).stream().map(s -> s.join()).collect(Collectors.toList());
    }

}

@Data
@AllArgsConstructor
class NetMall {

    private String netMallName;

    public double calcPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}