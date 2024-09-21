package com.yupi.springbootinit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实验用。afterTTL消息队列（死信队列之一），使用在别处定义的交换机。
 */
@Configuration
public class SpringRabbitMqDeathAfterTTLQueueConfig {
    @Autowired
    @Qualifier("deadDirectExchange")
    private DirectExchange deadDirectExchange;

    @Bean
    public Queue deadAfterTTLQueue(){
        return new Queue("after_ttl_dlx_queue");
    }

    @Bean
    public Binding bindingAfterTTL(Queue deadAfterTTLQueue){
        return BindingBuilder.bind(deadAfterTTLQueue).to(deadDirectExchange).with("after_ttl");
    }
}
