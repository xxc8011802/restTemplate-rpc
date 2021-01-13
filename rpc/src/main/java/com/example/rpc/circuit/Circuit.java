package com.example.rpc.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Circuit
{
    default <T> T execute(Supplier<T> rpcSupplier, Function<? super Throwable, ? extends T> errorHandler,
        CircuitConfig circuitConfig)
    {
        //rpcSupplier = decorateLimit(rpcSupplier, circuitConfig);
        rpcSupplier = decorateBreak(rpcSupplier, circuitConfig);
        rpcSupplier = decorateRetry(rpcSupplier, circuitConfig);
        Try<T> retry = decorateRecover(rpcSupplier, errorHandler);
        return retry.get();
    }

    //熔断装饰器
    <T> Supplier<T> decorateBreak(Supplier<T> supplier, CircuitConfig circuitConfig);

    //重试装饰器
    <T> Supplier<T> decorateRetry(Supplier<T> supplier, CircuitConfig circuitConfig);

    //恢复装饰器
    <T> Try<T> decorateRecover(Supplier<T> supplier, Function<? super Throwable, ? extends T> f);

    //限流装饰器
    <T> Supplier<T> decorateLimit(Supplier<T> supplier, CircuitConfig circuitConfig);


}
