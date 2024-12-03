/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface ApplicationEventMulticaster {
    public void addApplicationListener(ApplicationListener<?> var1);

    public void addApplicationListenerBean(String var1);

    public void removeApplicationListener(ApplicationListener<?> var1);

    public void removeApplicationListenerBean(String var1);

    public void removeAllListeners();

    public void multicastEvent(ApplicationEvent var1);

    public void multicastEvent(ApplicationEvent var1, @Nullable ResolvableType var2);
}

