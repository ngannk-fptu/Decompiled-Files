/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Alert;
import javax.annotation.Nonnull;

public interface AlertListener {
    public void onAlert(@Nonnull Alert var1);
}

