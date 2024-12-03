/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;

public interface ApplicationLinkRequestFactoryFactory {
    public ApplicationLinkRequestFactory getApplicationLinkRequestFactory(ApplicationLink var1);

    public ApplicationLinkRequestFactory getApplicationLinkRequestFactory(ApplicationLink var1, Class<? extends AuthenticationProvider> var2);
}

