/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;

public interface IpdLoggingService {
    public void logMetric(IpdMetric var1, boolean var2);

    public void logMetric(IpdMetric var1, String var2, boolean var3);
}

