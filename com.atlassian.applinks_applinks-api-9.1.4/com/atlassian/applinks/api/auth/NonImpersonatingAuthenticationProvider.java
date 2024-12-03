/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.auth;

import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;

public interface NonImpersonatingAuthenticationProvider
extends AuthenticationProvider {
    public ApplicationLinkRequestFactory getRequestFactory();
}

