package com.yupi.springbootinit.factory;

import com.yupi.springbootinit.pay.Payment;

/**
 * 工厂模板模式 工厂接口Demo。
 */
public interface PaymentFactory {
    Payment getPayment(String type);
}
