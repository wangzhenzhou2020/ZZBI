package com.yupi.springbootinit.pay.impl;

import com.yupi.springbootinit.pay.Payment;

public class WeChatPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("使用微信支付了" + amount + "yuan");
    }
}
