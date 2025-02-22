/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support;

import java.io.IOException;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.nio.AsyncDataConsumer;
import org.apache.hc.core5.http.nio.AsyncFilterChain;
import org.apache.hc.core5.http.nio.AsyncFilterHandler;
import org.apache.hc.core5.http.protocol.HttpContext;

public final class AsyncServerFilterChainElement {
    private final AsyncFilterHandler handler;
    private final AsyncServerFilterChainElement next;
    private final AsyncFilterChain filterChain;

    public AsyncServerFilterChainElement(AsyncFilterHandler handler, AsyncServerFilterChainElement next) {
        this.handler = handler;
        this.next = next;
        this.filterChain = next != null ? next::handle : null;
    }

    public AsyncDataConsumer handle(HttpRequest request, EntityDetails entityDetails, HttpContext context, AsyncFilterChain.ResponseTrigger responseTrigger) throws HttpException, IOException {
        return this.handler.handle(request, entityDetails, context, responseTrigger, this.filterChain);
    }

    public String toString() {
        return "{handler=" + this.handler.getClass() + ", next=" + (this.next != null ? this.next.handler.getClass() : "null") + '}';
    }
}

