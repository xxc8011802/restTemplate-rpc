package com.example.rpc.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

import java.time.Duration;
import java.util.HashMap;

public class CircuitConfig
{
    private CircuitBreaker circuitBreaker;

    private Retry retry;

    private RateLimiter rateLimiter;

    private CircuitBreakerConfig circuitBreakerConfig;

    public CircuitConfig(){

    }

    public CircuitConfig(String methodName){
        circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .minimumNumberOfCalls(5)
            .failureRateThreshold(10)//触发熔断的失败率阈值
            .waitDurationInOpenState(Duration.ofSeconds(30))//熔断器从打开状态到半开状态的等待时间
            .enableAutomaticTransitionFromOpenToHalfOpen()//如果置为true，当等待时间结束会自动由打开变为半开，若置为false，则需要一个请求进入来触发熔断器状态转换
            .build();
            circuitBreaker = CircuitBreaker.of(methodName,circuitBreakerConfig);

            RetryConfig
                .custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(100));
            retry = Retry.ofDefaults(methodName);

            RateLimiterConfig config =  RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMillis(10000))//限流周期时长
            .limitForPeriod(5)//周期内允许通过的请求数量
            .timeoutDuration(Duration.ofMillis(1000))//获取授权操作的超时时间
            .build();
            rateLimiter = RateLimiter.of(methodName, config);
    }

    public CircuitBreaker getCircuitBreaker()
    {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreaker circuitBreaker)
    {
        this.circuitBreaker = circuitBreaker;
    }

    public Retry getRetry()
    {
        return retry;
    }

    public void setRetry(Retry retry)
    {
        this.retry = retry;
    }

    public RateLimiter getRateLimiter()
    {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter)
    {
        this.rateLimiter = rateLimiter;
    }

    public CircuitBreakerConfig getCircuitBreakerConfig()
    {
        return circuitBreakerConfig;
    }

    public void setCircuitBreakerConfig(CircuitBreakerConfig circuitBreakerConfig)
    {
        this.circuitBreakerConfig = circuitBreakerConfig;
    }


}
