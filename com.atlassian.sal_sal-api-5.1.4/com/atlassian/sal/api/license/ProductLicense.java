/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.license;

import com.atlassian.annotations.PublicApi;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface ProductLicense {
    public static final int UNLIMITED_USER_COUNT = -1;

    @Nonnull
    public String getProductKey();

    public boolean isUnlimitedNumberOfUsers();

    public int getNumberOfUsers();

    @Nonnull
    public String getProductDisplayName();

    @Nullable
    public String getProperty(@Nonnull String var1);
}

