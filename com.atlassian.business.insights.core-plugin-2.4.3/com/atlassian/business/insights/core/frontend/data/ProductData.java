/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.frontend.data;

import java.util.Objects;
import javax.annotation.Nonnull;

public class ProductData {
    private final String productName;
    private final String serverTimeZone;
    private final String userLocale;
    private final String pluginVersion;

    public ProductData(@Nonnull String productName, @Nonnull String serverTimeZone, @Nonnull String userLocale, @Nonnull String pluginVersion) {
        this.productName = Objects.requireNonNull(productName);
        this.serverTimeZone = Objects.requireNonNull(serverTimeZone);
        this.userLocale = Objects.requireNonNull(userLocale);
        this.pluginVersion = Objects.requireNonNull(pluginVersion);
    }

    @Nonnull
    public String getProductName() {
        return this.productName;
    }

    @Nonnull
    public String getServerTimeZone() {
        return this.serverTimeZone;
    }

    @Nonnull
    public String getUserLocale() {
        return this.userLocale;
    }

    @Nonnull
    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductData)) {
            return false;
        }
        ProductData that = (ProductData)o;
        return com.google.common.base.Objects.equal((Object)this.productName, (Object)that.productName) && com.google.common.base.Objects.equal((Object)this.serverTimeZone, (Object)that.serverTimeZone) && com.google.common.base.Objects.equal((Object)this.userLocale, (Object)that.userLocale) && com.google.common.base.Objects.equal((Object)this.pluginVersion, (Object)that.pluginVersion);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.productName, this.serverTimeZone, this.userLocale, this.pluginVersion});
    }
}

