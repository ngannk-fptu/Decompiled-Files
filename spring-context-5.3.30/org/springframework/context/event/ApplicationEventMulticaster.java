/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.event;

import java.util.function.Predicate;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface ApplicationEventMulticaster {
    public void addApplicationListener(ApplicationListener<?> var1);

    public void addApplicationListenerBean(String var1);

    public void removeApplicationListener(ApplicationListener<?> var1);

    public void removeApplicationListenerBean(String var1);

    public void removeApplicationListeners(Predicate<ApplicationListener<?>> var1);

    public void removeApplicationListenerBeans(Predicate<String> var1);

    public void removeAllListeners();

    public void multicastEvent(ApplicationEvent var1);

    public void multicastEvent(ApplicationEvent var1, @Nullable ResolvableType var2);
}

