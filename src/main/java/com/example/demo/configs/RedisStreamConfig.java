package com.example.demo.configs;

import com.example.demo.constants.RedisKey;
import com.example.demo.modules.email.dtos.SendEmailStreamRequest;
import com.example.demo.modules.notification.dtos.SendNotificationStreamRequest;
import com.example.demo.stream.SendEmailStreamListener;
import com.example.demo.stream.SendNotificationStreamListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class RedisStreamConfig {

    @Bean
    public Subscription sendEmail(RedisConnectionFactory redisConnectionFactory, SendEmailStreamListener listener) {
        return createSubscription(new CreateSubscriptionRequest<>(redisConnectionFactory, listener, SendEmailStreamRequest.class, RedisKey.SEND_EMAIL_STREAM));
    }

    @Bean
    public Subscription sendNotification(RedisConnectionFactory redisConnectionFactory, SendNotificationStreamListener listener) {
        return createSubscription(new CreateSubscriptionRequest<>(redisConnectionFactory, listener, SendNotificationStreamRequest.class, RedisKey.SEND_NOTIFICATION_STREAM));
    }

    private <T> Subscription createSubscription(CreateSubscriptionRequest<T> request) {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(10);
        executor.setThreadNamePrefix(request.redisKey().concat("-"));

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(1))
                .targetType(request.request())
                .batchSize(10)
                .executor(executor)
                .build();

        var container = StreamMessageListenerContainer.create(request.redisConnectionFactory(), options);

        var subscription = container.receive(StreamOffset.create(request.redisKey(), ReadOffset.lastConsumed()), request.listener());

        container.start();
        return subscription;
    }

    private record CreateSubscriptionRequest<T>(
            RedisConnectionFactory redisConnectionFactory,
            StreamListener<String, ObjectRecord<String, T>> listener,
            Class<T> request,
            String redisKey
    ) {
    }

}
