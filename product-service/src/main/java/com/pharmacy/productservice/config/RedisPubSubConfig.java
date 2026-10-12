package com.pharmacy.productservice.config;

import com.pharmacy.productservice.subscriber.PromotionUpdateSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {

    public static final String CHANNEL = "promotion-updates";

    @Bean
    public ChannelTopic promotionUpdateTopic() {
        return new ChannelTopic(CHANNEL);
    }

    @Bean
    public MessageListenerAdapter promotionUpdateListenerAdapter(
            PromotionUpdateSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory factory,
            MessageListenerAdapter promotionUpdateListenerAdapter,
            ChannelTopic promotionUpdateTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(promotionUpdateListenerAdapter, promotionUpdateTopic);
        return container;
    }
}