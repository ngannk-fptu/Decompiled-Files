/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 */
package org.springframework.web.context.request;

import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class DestructionCallbackBindingListener
implements HttpSessionBindingListener,
Serializable {
    private final Runnable destructionCallback;

    public DestructionCallbackBindingListener(Runnable destructionCallback) {
        this.destructionCallback = destructionCallback;
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        this.destructionCallback.run();
    }
}

