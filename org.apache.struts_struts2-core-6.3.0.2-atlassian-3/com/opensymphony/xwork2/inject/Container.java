/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Scope;
import java.io.Serializable;
import java.util.Set;

public interface Container
extends Serializable {
    public static final String DEFAULT_NAME = "default";

    public void inject(Object var1);

    public <T> T inject(Class<T> var1);

    public <T> T getInstance(Class<T> var1, String var2);

    public <T> T getInstance(Class<T> var1);

    public Set<String> getInstanceNames(Class<?> var1);

    public void setScopeStrategy(Scope.Strategy var1);

    public void removeScopeStrategy();
}

