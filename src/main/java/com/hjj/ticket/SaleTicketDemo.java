package com.hjj.ticket;

public class SaleTicketDemo {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i=0;i<55;i++) {
                ticket.sale();
            }
        }, "a").start();

        new Thread(() -> {
            for (int i=0;i<55;i++) {
                ticket.sale();
            }
        }, "b").start();

        new Thread(() -> {
            for (int i=0;i<55;i++) {
                ticket.sale();
            }
        }, "c").start();
    }
}
