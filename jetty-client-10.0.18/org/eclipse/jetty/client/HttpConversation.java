/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.AttributesMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.util.AttributesMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConversation
extends AttributesMap {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConversation.class);
    private final Deque<HttpExchange> exchanges = new ConcurrentLinkedDeque<HttpExchange>();
    private volatile List<Response.ResponseListener> listeners;

    public Deque<HttpExchange> getExchanges() {
        return this.exchanges;
    }

    public List<Response.ResponseListener> getResponseListeners() {
        return this.listeners;
    }

    public void updateResponseListeners(Response.ResponseListener overrideListener) {
        HttpExchange firstExchange = this.exchanges.peekFirst();
        HttpExchange lastExchange = this.exchanges.peekLast();
        ArrayList<Response.ResponseListener> listeners = new ArrayList<Response.ResponseListener>(firstExchange.getResponseListeners().size() + lastExchange.getResponseListeners().size());
        if (firstExchange == lastExchange) {
            if (overrideListener != null) {
                listeners.add(overrideListener);
            } else {
                listeners.addAll(firstExchange.getResponseListeners());
            }
        } else {
            listeners.addAll(lastExchange.getResponseListeners());
            if (overrideListener != null) {
                listeners.add(overrideListener);
            } else {
                listeners.addAll(firstExchange.getResponseListeners());
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exchanges in conversation {}, override={}, listeners={}", new Object[]{this.exchanges.size(), overrideListener, listeners});
        }
        this.listeners = listeners;
    }

    public long getTimeout() {
        HttpExchange firstExchange = this.exchanges.peekFirst();
        return firstExchange == null ? 0L : firstExchange.getRequest().getTimeout();
    }

    public boolean abort(Throwable cause) {
        HttpExchange exchange = this.exchanges.peekLast();
        return exchange != null && exchange.abort(cause);
    }

    public String toString() {
        return String.format("%s[%x]", HttpConversation.class.getSimpleName(), ((Object)((Object)this)).hashCode());
    }
}

