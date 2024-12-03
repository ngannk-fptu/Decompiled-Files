/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.util.ValidationUtils;
import com.amazonaws.waiters.CompositeAcceptor;
import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.PollingStrategyContext;
import com.amazonaws.waiters.SdkFunction;
import com.amazonaws.waiters.WaiterExecutionBuilder;
import com.amazonaws.waiters.WaiterState;
import com.amazonaws.waiters.WaiterTimedOutException;
import com.amazonaws.waiters.WaiterUnrecoverableException;

@SdkProtectedApi
public class WaiterExecution<Input extends AmazonWebServiceRequest, Output> {
    private final SdkFunction<Input, Output> sdkFunction;
    private final Input request;
    private final CompositeAcceptor<Output> acceptor;
    private final PollingStrategy pollingStrategy;

    public WaiterExecution(WaiterExecutionBuilder<Input, Output> waiterExecutionBuilder) {
        this.sdkFunction = ValidationUtils.assertNotNull(waiterExecutionBuilder.getSdkFunction(), "sdkFunction");
        this.request = (AmazonWebServiceRequest)ValidationUtils.assertNotNull(waiterExecutionBuilder.getRequest(), "request");
        this.acceptor = new CompositeAcceptor<Output>(ValidationUtils.assertNotNull(waiterExecutionBuilder.getAcceptorsList(), "acceptors"));
        this.pollingStrategy = ValidationUtils.assertNotNull(waiterExecutionBuilder.getPollingStrategy(), "pollingStrategy");
    }

    public boolean pollResource() throws AmazonServiceException, WaiterTimedOutException, WaiterUnrecoverableException {
        int retriesAttempted = 0;
        while (true) {
            switch (this.getCurrentState()) {
                case SUCCESS: {
                    return true;
                }
                case FAILURE: {
                    throw new WaiterUnrecoverableException("Resource never entered the desired state as it failed.");
                }
                case RETRY: {
                    PollingStrategyContext pollingStrategyContext = new PollingStrategyContext((AmazonWebServiceRequest)this.request, retriesAttempted);
                    if (this.pollingStrategy.getRetryStrategy().shouldRetry(pollingStrategyContext)) {
                        this.safeCustomDelay(pollingStrategyContext);
                        ++retriesAttempted;
                        break;
                    }
                    throw new WaiterTimedOutException("Reached maximum attempts without transitioning to the desired state");
                }
            }
        }
    }

    private WaiterState getCurrentState() throws AmazonServiceException {
        try {
            return this.acceptor.accepts(this.sdkFunction.apply(this.request));
        }
        catch (AmazonServiceException amazonServiceException) {
            return this.acceptor.accepts(amazonServiceException);
        }
    }

    private void safeCustomDelay(PollingStrategyContext pollingStrategyContext) {
        try {
            this.pollingStrategy.getDelayStrategy().delayBeforeNextRetry(pollingStrategyContext);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

