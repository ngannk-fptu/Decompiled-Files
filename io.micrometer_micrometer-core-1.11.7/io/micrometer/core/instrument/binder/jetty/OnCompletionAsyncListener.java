/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.binder.jetty.TimedHandler;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

class OnCompletionAsyncListener
implements AsyncListener {
    private final Object handler;

    OnCompletionAsyncListener(Object handler) {
        this.handler = handler;
    }

    public void onTimeout(AsyncEvent event) {
        ((TimedHandler)((Object)this.handler)).onAsyncTimeout(event);
    }

    public void onStartAsync(AsyncEvent event) {
        event.getAsyncContext().addListener((AsyncListener)this);
    }

    public void onError(AsyncEvent event) {
    }

    public void onComplete(AsyncEvent event) {
        ((TimedHandler)((Object)this.handler)).onAsyncComplete(event);
    }
}

