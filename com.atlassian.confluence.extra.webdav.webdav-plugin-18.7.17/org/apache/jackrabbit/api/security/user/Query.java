/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.user;

import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.jetbrains.annotations.NotNull;

public interface Query {
    public <T> void build(@NotNull QueryBuilder<T> var1);
}

