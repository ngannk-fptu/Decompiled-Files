/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import javax.annotation.Nonnull;

@Unrestricted(value="Internal component, services using this component should take care of enforcing appropriate permission level")
@Internal
public interface OAuthConnectionVerifier {
    public void verifyOAuthConnection(@Nonnull ApplicationLink var1) throws ApplinkStatusException;
}

