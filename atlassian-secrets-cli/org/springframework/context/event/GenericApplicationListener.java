/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface GenericApplicationListener
extends ApplicationListener<ApplicationEvent>,
Ordered {
    public boolean supportsEventType(ResolvableType var1);

    public boolean supportsSourceType(@Nullable Class<?> var1);
}

