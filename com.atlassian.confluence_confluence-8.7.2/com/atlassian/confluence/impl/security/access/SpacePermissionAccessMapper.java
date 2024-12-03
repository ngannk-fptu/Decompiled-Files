/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Either
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.security.access;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.fugue.Either;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface SpacePermissionAccessMapper {
    public Either<AccessDenied, Set<SpacePermissionSubjectType>> getPermissionCheckSubjectTypes(@NonNull AccessStatus var1, @NonNull String var2);
}

