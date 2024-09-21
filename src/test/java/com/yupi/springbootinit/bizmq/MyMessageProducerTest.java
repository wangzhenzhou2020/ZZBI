package com.yupi.springbootinit.bizmq;

import com.yupi.springbootinit.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MyMessageProducerTest {

//    @Autowired
//    private MyMessageProducer myMessageProducer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 验证code_queue在重试3次后，发往failure死信队列
     * 验证delayed_queue在延时后，发往after_ttl死信队列。
     * @throws Exception
     */
    @Test
    void sendMessageObject() throws Exception {
        User user = new User();
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("1", "wzz");
        // 发给code队列
//        rabbitTemplate.convertAndSend("code_exchange","my_routingKey", stringObjectHashMap);
        rabbitTemplate.convertAndSend("work_exchange","code", "hh");
//        // 发给delayed队列
//        rabbitTemplate.convertAndSend("code_exchange", "delayed", "hha",
//                message -> {
//                    message.getMessageProperties().setExpiration("10000");
//                    return message;
//                });
        Thread.sleep(2000);
    }

    @Test
    void sendMessageReturnsCallback() throws Exception {
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("1", "wzz");                         // routingKey not exist
        rabbitTemplate.convertAndSend("code_exchange", "my_routingKey1", stringObjectHashMap);
        Thread.sleep(2000);
    }

    @Test
    void lazyAndPersistent() {
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("1", "wzz");
        Message message = MessageBuilder.withBody("hello,SpringAMQP".getBytes(StandardCharsets.UTF_8))
//                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                .build();
        for (int i = 0; i < 100000; i++) {
            rabbitTemplate.convertAndSend("code_exchange", "my_routingKey", message);
        }

    }
}