/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.RequestFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.RequestFactory;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.ADMIN})
@Internal
public interface OAuthAutoConfigurator {
    public void enable(@Nonnull OAuthConfig var1, @Nonnull ApplicationLink var2, @Nonnull RequestFactory var3) throws AuthenticationConfigurationException;

    public void enable(@Nonnull OAuthConfig var1, @Nonnull OAuthConfig var2, @Nonnull ApplicationLink var3, @Nonnull ApplicationLinkRequestFactory var4) throws AuthenticationConfigurationException;

    public void disable(@Nonnull ApplicationLink var1, @Nonnull RequestFactory var2) throws AuthenticationConfigurationException;
}

