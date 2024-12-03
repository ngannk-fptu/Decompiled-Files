/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.detail;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface ThreadDump {
    public long getId();

    @Nonnull
    public String getName();

    @Nonnull
    public Thread.State getState();

    @Nonnull
    public Optional<String> getStackTrace();

    public boolean isDaemon();
}

