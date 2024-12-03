/*
 * Decompiled with CFR 0.152.
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

    @Override
    default public int getOrder() {
        return Integer.MAX_VALUE;
    }

    default public String getListenerId() {
        return "";
    }
}

