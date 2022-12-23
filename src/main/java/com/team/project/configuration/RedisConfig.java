package com.team.project.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
    
    @Value("${spring.redis.password}")
    private String password;
    
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /**
     * 단일 Topic 사용을 위한 Bean 설정
     */
//    @Bean
//    public ChannelTopic channelTopic() {
//        return new ChannelTopic("chatroom");
//    }

    //redis 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * redis에 발행(publish)된 메시지 처리를 위한 리스너 설정
     */
//    @Bean
//    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        return container;
//    }

    /**
     * 실제 메시지를 처리하는 subscriber 설정 추가
     */
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
//        return new MessageListenerAdapter(subscriber, "sendMessage");
//    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
//        return redisTemplate;
//    }

//    @Bean(name = "cacheManager")
//    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
//                .disableCachingNullValues() // null value 캐시안함
//                .entryTtl(Duration.ofSeconds(CacheKey.DEFAULT_EXPIRE_SEC)) // 캐시의 기본 유효시간 설정
//                .computePrefixWith(CacheKeyPrefix.simple())
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())); // redis 캐시 데이터 저장방식을 StringSeriallizer로 지정
//
//        // 캐시키별 default 유효시간 설정
//        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//        cacheConfigurations.put(CacheKey.ARTICLE, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(CacheKey.ARTICLE_EXPIRE_SEC)));
//        cacheConfigurations.put(CacheKey.ARTICLES, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(CacheKey.ARTICLE_EXPIRE_SEC)));
//        cacheConfigurations.put(CacheKey.SEARCHPOP, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(CacheKey.ARTICLE_EXPIRE_SEC)));
//        cacheConfigurations.put(CacheKey.SERACH, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(CacheKey.ARTICLE_EXPIRE_SEC)));
//        cacheConfigurations.put(CacheKey.ARTICLEPOP, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(CacheKey.ARTICLE_EXPIRE_SEC)));
//        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory).cacheDefaults(configuration)
//                .withInitialCacheConfigurations(cacheConfigurations).build();
//    }

    //사용할 redisTemplate 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
