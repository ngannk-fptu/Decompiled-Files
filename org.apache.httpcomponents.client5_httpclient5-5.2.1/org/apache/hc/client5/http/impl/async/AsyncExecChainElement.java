/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.nio.AsyncEntityProducer
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import org.apache.hc.client5.http.async.AsyncExecCallback;
import org.apache.hc.client5.http.async.AsyncExecChain;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;

class AsyncExecChainElement {
    private final AsyncExecChainHandler handler;
    private final AsyncExecChainElement next;

    AsyncExecChainElement(AsyncExecChainHandler handler, AsyncExecChainElement next) {
        this.handler = handler;
        this.next = next;
    }

    public void execute(HttpRequest request, AsyncEntityProducer entityProducer, AsyncExecChain.Scope scope, AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        this.handler.execute(request, entityProducer, scope, this.next != null ? this.next::execute : null, asyncExecCallback);
    }

    public String toString() {
        return "{handler=" + this.handler.getClass() + ", next=" + (this.next != null ? this.next.handler.getClass() : "null") + '}';
    }
}

