/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.pagebanner;

import com.atlassian.confluence.plugins.pagebanner.IconItem;

public class BannerItem {
    private final String label;
    private final String href;
    private final String tooltip;
    private final String linkId;
    private final String styleClasses;
    private final IconItem icon;
    private final Boolean suppressDefaultStyle;

    public BannerItem(String label, String href, String tooltip, String linkId, String styleClasses, IconItem icon, Boolean suppressDefaultStyle) {
        this.label = label;
        this.href = href;
        this.tooltip = tooltip;
        this.linkId = linkId;
        this.styleClasses = styleClasses;
        this.icon = icon;
        this.suppressDefaultStyle = suppressDefaultStyle;
    }

    public String getLabel() {
        return this.label;
    }

    public String getHref() {
        return this.href;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getLinkId() {
        return this.linkId;
    }

    public String getStyleClasses() {
        return this.styleClasses;
    }

    public IconItem getIcon() {
        return this.icon;
    }

    public Boolean getIsAuiButton() {
        return this.styleClasses.contains("aui-button");
    }

    public Boolean getSuppressDefaultStyle() {
        return this.suppressDefaultStyle;
    }
}

