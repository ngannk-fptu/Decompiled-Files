/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.waiters.PollingStrategy;

public final class WaiterParameters<Input extends AmazonWebServiceRequest> {
    private final Input request;
    private final PollingStrategy pollingStrategy;

    public WaiterParameters() {
        this.request = null;
        this.pollingStrategy = null;
    }

    public WaiterParameters(Input request) {
        this.request = request;
        this.pollingStrategy = null;
    }

    private WaiterParameters(Input request, PollingStrategy pollingStrategy) {
        this.request = request;
        this.pollingStrategy = pollingStrategy;
    }

    public WaiterParameters<Input> withRequest(Input request) {
        return new WaiterParameters<Input>(request, this.pollingStrategy);
    }

    public WaiterParameters<Input> withPollingStrategy(PollingStrategy pollingStrategy) {
        return new WaiterParameters<Input>(this.request, pollingStrategy);
    }

    public Input getRequest() {
        return this.request;
    }

    public PollingStrategy getPollingStrategy() {
        return this.pollingStrategy;
    }
}

