package com.yupi.springbootinit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * springamqp 配置
 */
@Configuration
@Slf4j
public class SpringRabbitMqConfig {

    /**
     * confirm 和 return 机制
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // ConfirmCallback 是否到达Exchange。
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
//                    log.info("Message successfully reached the exchange.");
                } else {
                    log.error("Message failed to reach the exchange: " + cause);
                }
            }
        });
        // ReturnsCallback 未到队列
        // 启用 mandatory 参数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.error("rabbitmq returnsCallback");
                log.error("Exchange: {}", returnedMessage.getExchange());
                log.error("Routing Key: {}", returnedMessage.getRoutingKey());
                log.error("Message : {}", returnedMessage.getMessage());
                log.error("replyCode: {}", returnedMessage.getReplyCode());
                log.error("replyText: {}", returnedMessage.getReplyText());
            }
        });

        // 为生产者发送的消息添加uuid
        rabbitTemplate.setBeforePublishPostProcessors(addMessageIdProcessor());

//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());  // 这样会失败。通过yml可以成功配置。
        return rabbitTemplate;
    }

    //为全部的工作队列统一设置 过期和超重试的 死信队列  1、单个延时工作队列发送到after_ttl 不受影响 2、重试耗尽了成功发给failure
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "dlx-direct-exchange", "failure");
    }



    // 为发送者添加消息id，consumer可依据消息id 实现操作的幂等性。
    @Bean
    public MessagePostProcessor addMessageIdProcessor() {
        return (Message message) -> {
            // 如果消息没有 messageId，生成一个 UUID 作为 messageId
            if (message.getMessageProperties().getMessageId() == null) {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            }
            return message;
        };
    }

}
