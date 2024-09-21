package com.yupi.springbootinit.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 实验用。
 * 延时队列，该队列没有Consumer，所以定义在这，而没有定义在Listener处。
 * work_exchange 有两个工作队列，一个是code_queue,一个delayed_queue
 */
@Configuration
public class SpringRabbitMqWorkDelayedQueueConfig {
    @Bean
    public DirectExchange workExchange() {
        return new DirectExchange("work_exchange");
    }

    // 设定延时队列，同时为其绑定死信交换机
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder
                .durable("delayed_queue")
                .deadLetterExchange("dlx-direct-exchange")
                .deadLetterRoutingKey("after_ttl")  // todo 也会发给failure 队列。为什么
                .build();
    }

    @Bean
    public Binding bindingDelayed(Queue delayedQueue, DirectExchange workExchange) {
        return BindingBuilder.bind(delayedQueue).to(workExchange).with("delayed");
    }
}
