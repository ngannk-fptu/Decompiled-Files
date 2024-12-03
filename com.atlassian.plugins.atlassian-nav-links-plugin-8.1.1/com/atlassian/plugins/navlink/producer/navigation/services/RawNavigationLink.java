/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBase;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLinkBuilder;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RawNavigationLink
extends NavigationLinkBase {
    private final String labelKey;
    private final String tooltipKey;

    RawNavigationLink(RawNavigationLinkBuilder builder) {
        super(builder);
        this.labelKey = (String)Preconditions.checkNotNull((Object)builder.labelKey);
        this.tooltipKey = builder.tooltipKey;
    }

    @Nonnull
    public String getLabelKey() {
        return this.labelKey;
    }

    @Nullable
    public String getTooltipKey() {
        return this.tooltipKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RawNavigationLink that = (RawNavigationLink)o;
        return this.isEqualTo(that) && Objects.equal((Object)this.labelKey, (Object)that.labelKey) && Objects.equal((Object)this.tooltipKey, (Object)that.tooltipKey);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.hashCodeBase(), this.labelKey, this.tooltipKey});
    }

    public String toString() {
        return "RawNavigationLink{" + this.toStringBase() + ", labelKey='" + this.labelKey + '\'' + ", tooltipKey='" + this.tooltipKey + '\'' + '}';
    }
}

