/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.annotations.Internal;
import javax.annotation.Nonnull;

@Internal
public interface RedirectUrlResolver {
    @Nonnull
    public String getInitFlowUrl(@Nonnull String var1);
}

