package com.example.rpc.resilience4j;

import com.example.api.bean.UserService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.function.Supplier;

public class CircuitBreakerTest
{
    @Test
    public void testCircuitBreaker(){
        // Create a CircuitBreakerTest (use default configuration)
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .enableAutomaticTransitionFromOpenToHalfOpen()
            .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("backendName",circuitBreakerConfig);

        Supplier<String> supplier =()->{
            System.out.println("supplier" + "a");
            return "a";
        };

        String result = circuitBreaker.executeSupplier(supplier);
        //String result = circuitBreaker.executeSupplier(() -> backendService.doSomethingWithArgs("world"));
        System.out.println(result);
    }

    @Test
    /*public void testRetry(){
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
        // Create a Retry with at most 3 retries and a fixed time interval between retries of 500ms
        Retry retry = Retry.ofDefaults("backendName");

        // Decorate your call to BackendService.doSomething() with a CircuitBreaker
        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, backendService::doSomething);

        // Decorate your call with automatic retry
        decoratedSupplier = Retry
            .decorateSupplier(retry, decoratedSupplier);

        // Execute the decorated supplier and recover from any exception
        String result = Try.ofSupplier(decoratedSupplier)
            .recover(throwable -> "Hello from Recovery").get();
        System.out.println(result);
    }*/


    public static void main(String[] args)
    {

    }
}
