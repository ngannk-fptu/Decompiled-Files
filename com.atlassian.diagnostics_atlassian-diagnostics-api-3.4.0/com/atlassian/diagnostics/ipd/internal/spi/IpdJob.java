/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface IpdJob {
    public void runJob();

    default public boolean isWorkInProgressJob() {
        return false;
    }
}

