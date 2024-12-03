/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Unrestricted(value="The goal of this component is to allow anonymous code to execute with elevated permissions")
public class ElevatedPermissionsServiceImpl
implements ElevatedPermissionsService {
    private final ThreadLocal<PermissionLevel> permissionLevelsContext = new ThreadLocal();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public <T> T executeAs(@Nonnull PermissionLevel level, @Nonnull Callable<T> closure) throws Exception {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(closure, "closure");
        this.permissionLevelsContext.set(level);
        try {
            T t = closure.call();
            return t;
        }
        finally {
            this.permissionLevelsContext.remove();
        }
    }

    @Override
    public boolean isElevatedTo(@Nonnull PermissionLevel level) {
        Objects.requireNonNull(level, "level");
        PermissionLevel current = this.permissionLevelsContext.get();
        return current != null && current.ordinal() >= level.ordinal();
    }
}

