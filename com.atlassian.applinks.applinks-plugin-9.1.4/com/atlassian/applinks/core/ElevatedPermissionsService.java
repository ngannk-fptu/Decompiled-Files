/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Unrestricted(value="The goal of this component is to allow the code with non-authenticated context to be executed with elevated permissions")
public interface ElevatedPermissionsService {
    @Nullable
    public <T> T executeAs(@Nonnull PermissionLevel var1, @Nonnull Callable<T> var2) throws Exception;

    public boolean isElevatedTo(@Nonnull PermissionLevel var1);
}

