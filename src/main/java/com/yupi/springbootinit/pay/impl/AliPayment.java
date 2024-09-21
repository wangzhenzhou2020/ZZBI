package com.yupi.springbootinit.pay.impl;

import com.yupi.springbootinit.pay.Payment;

public class AliPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("使用支付宝支付了" + amount + "yuan");
    }
}
