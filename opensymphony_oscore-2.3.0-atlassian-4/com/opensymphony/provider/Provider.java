/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.ProviderConfigurationException;

public interface Provider {
    public void destroy();

    public void init() throws ProviderConfigurationException;
}

