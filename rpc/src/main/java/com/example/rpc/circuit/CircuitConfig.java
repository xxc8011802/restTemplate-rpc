package com.example.rpc.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;

import java.time.Duration;
import java.util.HashMap;

public class CircuitConfig
{
    private CircuitBreaker circuitBreaker;

    private Retry retry;

    private CircuitBreakerConfig circuitBreakerConfig;

    private static HashMap<String,CircuitConfig> circuitBreakerMap = new HashMap<>();


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
            retry = Retry.ofDefaults(methodName);
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

    public CircuitBreakerConfig getCircuitBreakerConfig()
    {
        return circuitBreakerConfig;
    }

    public void setCircuitBreakerConfig(CircuitBreakerConfig circuitBreakerConfig)
    {
        this.circuitBreakerConfig = circuitBreakerConfig;
    }


}
