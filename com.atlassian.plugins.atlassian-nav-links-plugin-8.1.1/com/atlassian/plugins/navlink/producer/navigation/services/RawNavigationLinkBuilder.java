/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBuilderBase;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;

public class RawNavigationLinkBuilder
extends NavigationLinkBuilderBase<RawNavigationLinkBuilder, RawNavigationLink> {
    String labelKey;
    String tooltipKey;

    public RawNavigationLinkBuilder() {
        super(RawNavigationLinkBuilder.class);
    }

    public static RawNavigationLinkBuilder copyOf(RawNavigationLink that) {
        return new RawNavigationLinkBuilder().copy(that);
    }

    public RawNavigationLinkBuilder copy(RawNavigationLink that) {
        return ((RawNavigationLinkBuilder)super.copy(that)).labelKey(that.getLabelKey()).tooltipKey(that.getTooltipKey());
    }

    public RawNavigationLinkBuilder setLabelKey(String labelKey) {
        this.labelKey = labelKey;
        return this;
    }

    public RawNavigationLinkBuilder labelKey(String labelKey) {
        return this.setLabelKey(labelKey);
    }

    public RawNavigationLinkBuilder setHref(String href) {
        return (RawNavigationLinkBuilder)this.href(href);
    }

    public RawNavigationLinkBuilder setTooltipKey(String tooltipKey) {
        this.tooltipKey = tooltipKey;
        return this;
    }

    public RawNavigationLinkBuilder tooltipKey(String tooltipKey) {
        return this.setTooltipKey(tooltipKey);
    }

    public RawNavigationLinkBuilder setIcon(String icon) {
        return (RawNavigationLinkBuilder)this.iconUrl(icon);
    }

    public RawNavigationLink createLocalNavigationLink() {
        return new RawNavigationLink(this);
    }

    public RawNavigationLink build() {
        return this.createLocalNavigationLink();
    }
}

