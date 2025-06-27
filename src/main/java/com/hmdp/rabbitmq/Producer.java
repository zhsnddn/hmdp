package com.hmdp.rabbitmq;

import com.hmdp.config.RabbitMQTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Producer {


    private final RabbitTemplate rabbitTemplate;

    private static final String ROUTINGKEY = "seckill.message";
    /**
     * 发送消息
     */
    public void sendSeckillMessage(String msg){
        log.info("发送消息"+msg);
        rabbitTemplate.convertAndSend(RabbitMQTopicConfig.EXCHANGE,ROUTINGKEY,msg);
    }
}
