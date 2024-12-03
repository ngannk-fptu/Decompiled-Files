/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.plugin.configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PropertiesProvider {
    @Nullable
    public String getProperty(@Nonnull String var1);

    public boolean getBoolean(@Nonnull String var1);

    @Nullable
    public String getProperty(@Nonnull String var1, String var2);

    public int getInteger(@Nonnull String var1, int var2);

    public long getLong(@Nonnull String var1, long var2);
}

