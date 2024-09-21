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
 * 实验用。重试若干次后仍失败消息队列（死信队列之一）
 */
@Configuration
public class SpringRabbitMqDeathFailureQueueConfig {
    @Bean
    public DirectExchange deadDirectExchange() {
        return new DirectExchange("dlx-direct-exchange");
    }

    @Bean
    public Queue deadFailureQueue(){
        return new Queue("failure_dlx_queue");
    }

    @Bean
    public Binding bindingFailure(Queue deadFailureQueue,DirectExchange deadDirectExchange){
        return BindingBuilder.bind(deadFailureQueue).to(deadDirectExchange).with("failure");
    }
}
