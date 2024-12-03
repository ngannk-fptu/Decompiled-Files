/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.log;

import com.opensymphony.provider.LogProvider;
import com.opensymphony.provider.ProviderConfigurationException;

public class NullLogProvider
implements LogProvider {
    private static final Object dummyContext = new Object();

    @Override
    public Object getContext(String name) {
        return dummyContext;
    }

    @Override
    public boolean isEnabled(Object context, int level) {
        return false;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    @Override
    public void log(Object context, int level, Object msg, Throwable throwable) {
    }
}

