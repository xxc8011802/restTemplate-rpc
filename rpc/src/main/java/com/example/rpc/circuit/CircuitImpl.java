package com.example.rpc.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;

import java.util.function.Function;
import java.util.function.Supplier;

public class CircuitImpl implements Circuit
{
    /**
     *
     * @param supplier  装饰的代码块
     * @param circuitConfig 熔断配置项
     * @param <T>
     * @return
     */
    @Override
    public <T> Supplier<T> decorateBreak(Supplier<T> supplier,CircuitConfig circuitConfig){
        return CircuitBreaker.decorateSupplier(circuitConfig.getCircuitBreaker(), supplier);
    }

    /**
     *
     * @param supplier  装饰的代码块
     * @param circuitConfig  Retry重试配置项
     * @param <T>
     * @return
     */
    @Override
    public <T> Supplier<T> decorateRetry(Supplier<T> supplier,CircuitConfig circuitConfig){
        return Retry.decorateSupplier(circuitConfig.getRetry(), supplier);
    }

    /**
     *
     * @param supplier 装饰的代码块
     * @param errorHandle 异常捕获处理
     * @param <T>
     * @return
     */
    @Override
    public <T> Try<T> decorateRecover(Supplier<T> supplier,Function<? super Throwable, ? extends T> errorHandle){
        return Try.ofSupplier(supplier).recover(errorHandle);
    }

    /**
     *
     * @param supplier 装饰的代码块
     * @param circuitConfig rateLimiter 限流配置项
     * @param <T>
     * @return
     */
    @Override
    public <T> Supplier<T> decorateLimit(Supplier<T> supplier,CircuitConfig circuitConfig){
        RateLimiter rateLimiter = circuitConfig.getRateLimiter();
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        rateLimiter.getEventPublisher().onSuccess(event -> {
            System.out.println(event.getEventType() + ":::请求成功可用令牌数: " + metrics.getAvailablePermissions() + ", 等待线程数: "
                + metrics.getNumberOfWaitingThreads());
        }).onFailure(event -> {
            System.out.println(event.getEventType() + ":::请求失败可用令牌数: " + metrics.getAvailablePermissions() + ", 等待线程数: "
                + metrics.getNumberOfWaitingThreads());
        });
        return RateLimiter.decorateSupplier(rateLimiter,supplier);
    }
}
