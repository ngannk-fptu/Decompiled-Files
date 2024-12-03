/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.activeobjects.internal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;

@ParametersAreNonnullByDefault
public interface DriverNameExtractor {
    @Nonnull
    public String getDriverName(DataSource var1);
}

