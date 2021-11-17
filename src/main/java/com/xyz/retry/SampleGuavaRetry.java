package com.xyz.retry;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

/**
 * 用Guava-Retryer实现的重试
 * 
 * @author zhouandr
 *
 */
public class SampleGuavaRetry {

    private static final int ATTEMPT_TIMES = 10;

    /**
     * 用GuavaRetryer实现的重试
     * 
     * @throws ExecutionException
     * @throws RetryException
     * @throws InterruptedException
     */
    @Test
    @Order(1)
    public void guavaRetryer() throws ExecutionException, RetryException, InterruptedException {
        //定义重试器，
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                // 出现异常时，会重试
                .retryIfException()
                // 失败后，隔3秒后重试，固定等待时间
                //.withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                // 失败后，隔3秒重试，然后隔6秒重试，依次增加3秒时间
                .withWaitStrategy(WaitStrategies.incrementingWait(3, TimeUnit.SECONDS, 3, TimeUnit.SECONDS))
                // 重试10次后，仍未成功，就不再重试
                .withStopStrategy(StopStrategies.stopAfterAttempt(ATTEMPT_TIMES))
                .build();

        // 使用重试器，执行具体逻辑
        Boolean res = retryer.call(() -> {
            return UnstableFunction.randomFailureFunction();
        });

        System.out.printf("Result is %s.\n", res);
    }

}

/**
 * 不稳定的方法，30%的概率成功，70%的概率失败
 * 
 *
 */
class UnstableFunction {
    // 可能需要重试执行的操作，随机(0, 1.0)<=0.3则通过，其他
    public static Boolean randomFailureFunction() {
        double value = Math.random();
        if (value <= 0.3) {
            System.out.printf("%s, %s, call success.\n", LocalTime.now(), value);
            return true;
        }

        // 否则抛出异常，触发重试
        System.out.printf("%s, %s, call failed.\n", LocalTime.now(), value);
        throw new RuntimeException("手动测试抛出异常");
    }

}
