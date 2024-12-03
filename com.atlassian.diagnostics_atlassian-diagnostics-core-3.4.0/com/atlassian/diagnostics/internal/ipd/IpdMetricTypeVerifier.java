/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;

@FunctionalInterface
public interface IpdMetricTypeVerifier {
    public void verifyIpdMetricType(IpdMetric var1) throws ClassCastException;
}

