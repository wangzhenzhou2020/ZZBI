//package com.yupi.springbootinit.config;
//
//import com.yupi.springbootinit.factory.PaymentFactory;
//import com.yupi.springbootinit.pay.impl.AliPayment;
//import com.yupi.springbootinit.pay.impl.WeChatPayment;
//import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
//import org.springframework.beans.factory.serviceloader.ServiceLoaderFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Scope;
//
///**
// * 工厂模式 demo
// */
//@Configuration
//public class PaymentConfig {
//    @Bean
//    public ServiceLocatorFactoryBean serviceLoaderFactoryBean(){
//        ServiceLocatorFactoryBean serviceLocatorFactoryBean = new ServiceLocatorFactoryBean();
//        serviceLocatorFactoryBean.setServiceLocatorInterface(PaymentFactory.class);
//        return serviceLocatorFactoryBean;
//    }
//
//    @Bean(name="wechat")
//    @Scope("prototype")
//    public WeChatPayment weChatPayment(){
//        return new WeChatPayment();
//    }
//
//    @Bean(name="ali")
//    @Scope("prototype")
//    public AliPayment aliPayment(){
//        return new AliPayment();
//    }
//
//}
