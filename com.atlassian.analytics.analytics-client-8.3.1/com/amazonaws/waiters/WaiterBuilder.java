/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.SdkFunction;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.waiters.WaiterAcceptor;
import com.amazonaws.waiters.WaiterImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@SdkProtectedApi
public class WaiterBuilder<Input extends AmazonWebServiceRequest, Output> {
    private SdkFunction<Input, Output> sdkFunction;
    private List<WaiterAcceptor<Output>> acceptors = new ArrayList<WaiterAcceptor<Output>>();
    private PollingStrategy defaultPollingStrategy;
    private ExecutorService executorService;

    public WaiterBuilder<Input, Output> withSdkFunction(SdkFunction<Input, Output> sdkFunction) {
        this.sdkFunction = sdkFunction;
        return this;
    }

    public WaiterBuilder<Input, Output> withAcceptors(WaiterAcceptor<Output> ... acceptors) {
        Collections.addAll(this.acceptors, acceptors);
        return this;
    }

    public WaiterBuilder<Input, Output> withDefaultPollingStrategy(PollingStrategy pollingStrategy) {
        this.defaultPollingStrategy = pollingStrategy;
        return this;
    }

    public WaiterBuilder<Input, Output> withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public List<WaiterAcceptor<Output>> getAcceptor() {
        return this.acceptors;
    }

    public SdkFunction<Input, Output> getSdkFunction() {
        return this.sdkFunction;
    }

    PollingStrategy getDefaultPollingStrategy() {
        return this.defaultPollingStrategy;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public Waiter<Input> build() {
        return new WaiterImpl(this);
    }
}

