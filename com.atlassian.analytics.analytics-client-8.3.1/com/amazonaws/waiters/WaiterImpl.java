/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.util.ValidationUtils;
import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.SdkFunction;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.waiters.WaiterAcceptor;
import com.amazonaws.waiters.WaiterBuilder;
import com.amazonaws.waiters.WaiterExecution;
import com.amazonaws.waiters.WaiterExecutionBuilder;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.waiters.WaiterTimedOutException;
import com.amazonaws.waiters.WaiterUnrecoverableException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@SdkProtectedApi
public class WaiterImpl<Input extends AmazonWebServiceRequest, Output>
implements Waiter<Input> {
    private final SdkFunction<Input, Output> sdkFunction;
    private final List<WaiterAcceptor<Output>> acceptors;
    private final PollingStrategy defaultPollingStrategy;
    private final ExecutorService executorService;

    @SdkProtectedApi
    public WaiterImpl(WaiterBuilder<Input, Output> waiterBuilder) {
        this.sdkFunction = ValidationUtils.assertNotNull(waiterBuilder.getSdkFunction(), "sdkFunction");
        this.acceptors = ValidationUtils.assertNotNull(waiterBuilder.getAcceptor(), "acceptors");
        this.defaultPollingStrategy = ValidationUtils.assertNotNull(waiterBuilder.getDefaultPollingStrategy(), "defaultPollingStrategy");
        this.executorService = ValidationUtils.assertNotNull(waiterBuilder.getExecutorService(), "executorService");
    }

    @Override
    public void run(WaiterParameters<Input> waiterParameters) throws AmazonServiceException, WaiterTimedOutException, WaiterUnrecoverableException {
        ValidationUtils.assertNotNull(waiterParameters, "waiterParameters");
        AmazonWebServiceRequest request = ((AmazonWebServiceRequest)ValidationUtils.assertNotNull(waiterParameters.getRequest(), "request")).clone();
        request.getRequestClientOptions().appendUserAgent("waiter-request");
        WaiterExecution waiterExecution = new WaiterExecutionBuilder().withRequest(request).withPollingStrategy(waiterParameters.getPollingStrategy() != null ? waiterParameters.getPollingStrategy() : this.defaultPollingStrategy).withAcceptors(this.acceptors).withSdkFunction(this.sdkFunction).build();
        waiterExecution.pollResource();
    }

    @Override
    public Future<Void> runAsync(final WaiterParameters<Input> waiterParameters, final WaiterHandler callback) throws AmazonServiceException, WaiterTimedOutException, WaiterUnrecoverableException {
        return this.executorService.submit(new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                try {
                    WaiterImpl.this.run(waiterParameters);
                    callback.onWaitSuccess(waiterParameters.getRequest());
                }
                catch (Exception ex) {
                    callback.onWaitFailure(ex);
                    throw ex;
                }
                return null;
            }
        });
    }
}

