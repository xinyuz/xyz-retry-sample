package com.xyz.retry;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * 用Spring-Retryer实现的重试
 * 
 * @author xinyuz
 *
 */
public class SampleSpringRetry {

    private static final int ATTEMPT_TIMES = 10;

    /**
     * 用Spring Retry实现的重试
     * 
     * @throws Exception
     */
    @Test
    @Order(2)
    public void springRetryer() throws Exception {
        RetryTemplate template = new RetryTemplate();

        // 策略
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(ATTEMPT_TIMES);
        template.setRetryPolicy(policy);

        String result = template.execute(
                new RetryCallback<String, Exception>() {
                    @Override
                    public String doWithRetry(RetryContext arg0) {
                        //                        throw new NullPointerException();
                        return String.valueOf(UnstableFunction.randomFailureFunction());
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) {
                        return "recovery callback";
                    }
                });
        System.out.printf("Result is %s.", result);
    }
}