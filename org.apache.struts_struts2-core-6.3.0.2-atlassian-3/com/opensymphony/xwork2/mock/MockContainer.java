/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;
import java.util.Set;

public class MockContainer
implements Container {
    @Override
    public void inject(Object o) {
    }

    @Override
    public <T> T inject(Class<T> implementation) {
        return null;
    }

    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return null;
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return null;
    }

    @Override
    public Set<String> getInstanceNames(Class<?> type) {
        return null;
    }

    @Override
    public void setScopeStrategy(Scope.Strategy scopeStrategy) {
    }

    @Override
    public void removeScopeStrategy() {
    }
}

