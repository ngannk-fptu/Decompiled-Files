/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.producer.navigation;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBase;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class NavigationLink
extends NavigationLinkBase {
    public static final Set<String> MENU_ITEM_KEYS = ImmutableSet.of((Object)"home", (Object)"custom-apps", (Object)"nav-links.product-entity");
    private final String label;
    private final String tooltip;

    NavigationLink(NavigationLinkBuilder builder) {
        super(builder);
        this.label = builder.label != null ? builder.label : "";
        this.tooltip = builder.tooltip;
    }

    public String getLabel() {
        return this.label;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public boolean isProductEntity() {
        return "nav-links.product-entity".equals(this.getKey());
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.hashCodeBase(), this.label, this.tooltip});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NavigationLink)) {
            return false;
        }
        NavigationLink that = (NavigationLink)obj;
        return this.isEqualTo(that) && Objects.equal((Object)this.label, (Object)that.label) && Objects.equal((Object)this.tooltip, (Object)that.tooltip);
    }

    public String toString() {
        return "NavigationLink{" + this.toStringBase() + ", label='" + this.label + '\'' + ", tooltip='" + this.tooltip + '\'' + '}';
    }
}

