/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;

public interface EventListenerFactory {
    public boolean supportsMethod(Method var1);

    public ApplicationListener<?> createApplicationListener(String var1, Class<?> var2, Method var3);
}

