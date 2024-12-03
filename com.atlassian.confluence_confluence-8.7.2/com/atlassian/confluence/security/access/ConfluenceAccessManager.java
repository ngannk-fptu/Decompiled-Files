/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ConfluenceAccessManager {
    public @NonNull AccessStatus getUserAccessStatus(@Nullable User var1);

    public @NonNull AccessStatus getUserAccessStatusNoExemptions(@Nullable User var1);
}

