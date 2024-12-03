/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.auth.types;

import com.atlassian.applinks.api.auth.DependsOn;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;

@DependsOn(value={OAuthAuthenticationProvider.class})
public interface TwoLeggedOAuthWithImpersonationAuthenticationProvider
extends ImpersonatingAuthenticationProvider {
}

