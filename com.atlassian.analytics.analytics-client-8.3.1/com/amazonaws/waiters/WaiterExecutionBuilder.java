/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.SdkFunction;
import com.amazonaws.waiters.WaiterAcceptor;
import com.amazonaws.waiters.WaiterExecution;
import java.util.ArrayList;
import java.util.List;

@SdkProtectedApi
public class WaiterExecutionBuilder<Input extends AmazonWebServiceRequest, Output> {
    private SdkFunction<Input, Output> sdkFunction;
    private Input request;
    private PollingStrategy pollingStrategy;
    private List<WaiterAcceptor<Output>> acceptors = new ArrayList<WaiterAcceptor<Output>>();

    public WaiterExecutionBuilder<Input, Output> withSdkFunction(SdkFunction sdkFunction) {
        this.sdkFunction = sdkFunction;
        return this;
    }

    public WaiterExecutionBuilder<Input, Output> withRequest(Input request) {
        this.request = request;
        return this;
    }

    public WaiterExecutionBuilder<Input, Output> withPollingStrategy(PollingStrategy pollingStrategy) {
        this.pollingStrategy = pollingStrategy;
        return this;
    }

    public WaiterExecutionBuilder<Input, Output> withAcceptors(List<WaiterAcceptor<Output>> acceptors) {
        this.acceptors = acceptors;
        return this;
    }

    public Input getRequest() {
        return this.request;
    }

    public List<WaiterAcceptor<Output>> getAcceptorsList() {
        return this.acceptors;
    }

    public SdkFunction<Input, Output> getSdkFunction() {
        return this.sdkFunction;
    }

    public PollingStrategy getPollingStrategy() {
        return this.pollingStrategy;
    }

    public WaiterExecution<Input, Output> build() {
        return new WaiterExecution(this);
    }
}

