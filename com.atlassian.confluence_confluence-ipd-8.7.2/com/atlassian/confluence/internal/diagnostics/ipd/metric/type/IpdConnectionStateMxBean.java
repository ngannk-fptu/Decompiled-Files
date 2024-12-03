/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.metric.type;

import javax.management.MXBean;

@MXBean
public interface IpdConnectionStateMxBean {
    public boolean isConnected();

    public long getTotalFailures();
}

