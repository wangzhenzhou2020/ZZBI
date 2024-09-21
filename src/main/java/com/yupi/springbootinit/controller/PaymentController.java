//package com.yupi.springbootinit.controller;
//
//import com.yupi.springbootinit.common.BaseResponse;
//import com.yupi.springbootinit.common.ResultUtils;
//import com.yupi.springbootinit.factory.PaymentFactory;
//import com.yupi.springbootinit.pay.Payment;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 工厂模式 demo
// */
//@RestController
//public class PaymentController {
//    @Autowired
//    private PaymentFactory paymentFactory;
//
//    @GetMapping("/pay")
//    public BaseResponse<String> pay(@RequestParam String type) {
//        Payment payment = paymentFactory.getPayment(type);
//        double amount = 100;
//        payment.pay(amount);
//        return ResultUtils.success(String.format("使用%s支付了%s", type, amount));
//    }
//}
