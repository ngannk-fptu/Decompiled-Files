/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.applinks.spi.auth;

import com.atlassian.applinks.api.ApplicationLink;

public interface IncomingTrustAuthenticationProviderPluginModule {
    public boolean incomingEnabled(ApplicationLink var1);
}

