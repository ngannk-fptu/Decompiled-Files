/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.module;

import java.util.Collection;

public interface ContainerAccessor {
    public <T> T createBean(Class<T> var1);

    public <T> T injectBean(T var1);

    public <T> T getBean(String var1);

    public <T> Collection<T> getBeansOfType(Class<T> var1);
}

