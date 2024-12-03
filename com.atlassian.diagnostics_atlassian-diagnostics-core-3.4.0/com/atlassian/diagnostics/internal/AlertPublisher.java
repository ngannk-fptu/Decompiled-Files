/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import javax.annotation.Nonnull;

public interface AlertPublisher {
    public void publish(@Nonnull Alert var1);

    @Nonnull
    public String subscribe(@Nonnull AlertListener var1);

    public boolean unsubscribe(@Nonnull String var1);
}

