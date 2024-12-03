/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.producer.navigation;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBuilderBase;
import javax.annotation.Nonnull;

public final class NavigationLinkBuilder
extends NavigationLinkBuilderBase<NavigationLinkBuilder, NavigationLink> {
    String label;
    String tooltip;

    public NavigationLinkBuilder() {
        super(NavigationLinkBuilder.class);
    }

    public static NavigationLinkBuilder copyOf(NavigationLink original) {
        return ((NavigationLinkBuilder)new NavigationLinkBuilder().copy(original)).label(original.getLabel()).tooltip(original.getTooltip());
    }

    @Nonnull
    public NavigationLinkBuilder label(String label) {
        this.label = label;
        return this;
    }

    @Nonnull
    public NavigationLinkBuilder tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Nonnull
    public NavigationLink build() {
        return new NavigationLink(this);
    }
}

