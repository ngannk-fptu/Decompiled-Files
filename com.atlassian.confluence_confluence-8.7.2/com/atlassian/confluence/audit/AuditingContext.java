/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.audit;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface AuditingContext {
    public void executeWithoutAuditing(Runnable var1);

    public void onlyAuditFor(String var1, Runnable var2);

    public AutoCloseable noAuditing();

    public AutoCloseable noAuditing(String var1);

    public boolean skipAuditing();

    public boolean skipAuditing(@Nullable String var1);
}

