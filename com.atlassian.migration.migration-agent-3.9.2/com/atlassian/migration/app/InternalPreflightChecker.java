/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.InternalPreflightCheckResult;

public interface InternalPreflightChecker {
    public InternalPreflightCheckResult runPreflightCheck();

    public String getPluginKey();
}

