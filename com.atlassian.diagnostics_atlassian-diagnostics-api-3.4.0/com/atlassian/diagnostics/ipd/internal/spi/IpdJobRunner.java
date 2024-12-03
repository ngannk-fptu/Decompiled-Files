/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface IpdJobRunner {
    public void register(@Nonnull IpdJob var1);

    public void runJobs();
}

