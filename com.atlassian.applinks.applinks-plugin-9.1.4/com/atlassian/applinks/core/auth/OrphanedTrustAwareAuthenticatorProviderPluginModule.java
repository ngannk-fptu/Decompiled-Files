/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule;

@Deprecated
public interface OrphanedTrustAwareAuthenticatorProviderPluginModule
extends AutoConfiguringAuthenticatorProviderPluginModule {
    public boolean isApplicable(String var1);
}

