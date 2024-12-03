/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.SmartApplicationListener
 *  org.springframework.util.Assert
 */
package org.springframework.security.context;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.util.Assert;

public final class DelegatingApplicationListener
implements ApplicationListener<ApplicationEvent> {
    private List<SmartApplicationListener> listeners = new CopyOnWriteArrayList<SmartApplicationListener>();

    public void onApplicationEvent(ApplicationEvent event) {
        if (event == null) {
            return;
        }
        for (SmartApplicationListener listener : this.listeners) {
            Object source = event.getSource();
            if (source == null || !listener.supportsEventType(event.getClass()) || !listener.supportsSourceType(source.getClass())) continue;
            listener.onApplicationEvent(event);
        }
    }

    public void addListener(SmartApplicationListener smartApplicationListener) {
        Assert.notNull((Object)smartApplicationListener, (String)"smartApplicationListener cannot be null");
        this.listeners.add(smartApplicationListener);
    }
}

