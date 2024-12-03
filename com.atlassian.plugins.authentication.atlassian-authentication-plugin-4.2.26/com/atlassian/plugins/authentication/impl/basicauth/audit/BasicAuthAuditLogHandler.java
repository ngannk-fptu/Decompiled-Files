/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.impl.basicauth.audit;

import java.util.Set;
import javax.annotation.Nonnull;

public interface BasicAuthAuditLogHandler {
    public void logDoNotBlockBasicAuthRequests();

    public void logBlockingBasicAuthRequests();

    public void logAllowedPathsChange(@Nonnull Set<String> var1, @Nonnull Set<String> var2);

    public void logAllowedUsersChange(@Nonnull Set<String> var1, @Nonnull Set<String> var2);
}

