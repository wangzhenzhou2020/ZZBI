//package com.yupi.springbootinit.bizmq;
//
//import org.apache.commons.lang3.ObjectUtils;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Scanner;
//
///**
// * spring rabbitmq测试
// */
//@Component
//public class MyMessageProducer {
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
////    /**
////     * direct 交换机
////     * @param exchange
////     * @param routingKey
////     * @param message
////     */
////    public void sendMessage(String exchange, String routingKey, String message) {
////        rabbitTemplate.convertAndSend(exchange, routingKey, message);
////    }
//
//    /**
//          * direct 交换机
//          * @param exchange
//          * @param routingKey
//          * @param message
//          */
//    public void sendMessage(String exchange, String routingKey, Message message, Long ttl) {
//        if (ObjectUtils.isEmpty(ttl)){
//            rabbitTemplate.convertAndSend(exchange, routingKey, message);
//        }
//        else{
//            rabbitTemplate.convertAndSend(exchange,routingKey,message,()->{
//                message.get
//            });
//        }
//
//    }
//
//    public void sendMessa
//
//}
