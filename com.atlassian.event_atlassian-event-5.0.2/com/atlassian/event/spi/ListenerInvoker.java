/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.spi;

import java.util.Optional;
import java.util.Set;

public interface ListenerInvoker {
    public Set<Class<?>> getSupportedEventTypes();

    public void invoke(Object var1);

    public boolean supportAsynchronousEvents();

    default public Optional<String> getScope() {
        return Optional.empty();
    }

    default public int getOrder() {
        return 0;
    }
}

