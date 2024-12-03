/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.spi.permission;

import javax.annotation.Nonnull;

public interface ResourceContextPermissionChecker {
    public boolean hasResourceAuditViewPermission(@Nonnull String var1, @Nonnull String var2);
}

