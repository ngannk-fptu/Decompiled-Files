/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

public interface SmartApplicationListener
extends ApplicationListener<ApplicationEvent>,
Ordered {
    public boolean supportsEventType(Class<? extends ApplicationEvent> var1);

    default public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return true;
    }

    default public int getOrder() {
        return Integer.MAX_VALUE;
    }

    default public String getListenerId() {
        return "";
    }
}

