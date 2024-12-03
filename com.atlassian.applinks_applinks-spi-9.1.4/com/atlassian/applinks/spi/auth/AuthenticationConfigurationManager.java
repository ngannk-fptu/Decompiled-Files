/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 */
package com.atlassian.applinks.spi.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import java.util.Map;

public interface AuthenticationConfigurationManager {
    public boolean isConfigured(ApplicationId var1, Class<? extends AuthenticationProvider> var2);

    public void registerProvider(ApplicationId var1, Class<? extends AuthenticationProvider> var2, Map<String, String> var3);

    public void unregisterProvider(ApplicationId var1, Class<? extends AuthenticationProvider> var2);

    public Map<String, String> getConfiguration(ApplicationId var1, Class<? extends AuthenticationProvider> var2);
}

