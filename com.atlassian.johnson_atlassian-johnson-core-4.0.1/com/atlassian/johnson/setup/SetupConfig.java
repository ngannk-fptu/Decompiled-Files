/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.setup;

import javax.annotation.Nonnull;

public interface SetupConfig {
    public boolean isSetup();

    public boolean isSetupPage(@Nonnull String var1);
}

