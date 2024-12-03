/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.ColourScheme;

public class ColorSchemeBean {
    private ColourScheme scheme;

    public ColorSchemeBean(ColourScheme scheme) {
        this.scheme = scheme;
    }

    public String getTopBarColor() {
        return this.scheme.get("property.style.topbarcolour");
    }

    public String getBreadcrumbsTextColor() {
        return this.scheme.get("property.style.breadcrumbstextcolour");
    }

    @Deprecated
    public String getSpaceNameColor() {
        return this.scheme.get("property.style.spacenamecolour");
    }

    public String getHeadingTextColor() {
        return this.scheme.get("property.style.headingtextcolour");
    }

    @Deprecated
    public String getHeadingSecondaryTextColor() {
        return this.scheme.get("property.style.headingtextcolour", 0.24);
    }

    public String getLinkColor() {
        return this.scheme.get("property.style.linkcolour");
    }

    public String getVisitedLinkColor() {
        return "#333";
    }

    public String getBorderColor() {
        return this.scheme.get("property.style.bordercolour");
    }

    @Deprecated
    public String getNavBackgroundColor() {
        return this.scheme.get("property.style.navbgcolour");
    }

    @Deprecated
    public String getNavTextColor() {
        return this.scheme.get("property.style.navtextcolour");
    }

    @Deprecated
    public String getNavSelectedBackgroundColor() {
        return this.scheme.get("property.style.navselectedbgcolour");
    }

    @Deprecated
    public String getNavSelectedTextColor() {
        return this.scheme.get("property.style.navselectedtextcolour");
    }

    public String getSearchFieldBackgroundColor() {
        return this.scheme.get("property.style.searchfieldbgcolour");
    }

    public String getSearchFieldTextColor() {
        return this.scheme.get("property.style.searchfieldtextcolour");
    }

    public String getTopBarMenuItemTextColor() {
        return this.scheme.get("property.style.topbarmenuitemtextcolour");
    }

    public String getMenuSelectedBackgroundColor() {
        return this.scheme.get("property.style.menuselectedbgcolour");
    }

    public String getTopBarMenuSelectedBackgroundColor() {
        return this.scheme.get("property.style.topbarmenuselectedbgcolour");
    }

    public String getTopBarMenuSelectedTextColor() {
        return this.scheme.get("property.style.topbarmenuselectedtextcolour");
    }

    public String getMenuItemTextColor() {
        return this.scheme.get("property.style.menuitemtextcolour");
    }

    public String getMenuItemSelectedBackgroundColor() {
        return this.scheme.get("property.style.menuitemselectedbgcolour");
    }

    public String getMenuItemSelectedTextColor() {
        return this.scheme.get("property.style.menuitemselectedtextcolour");
    }

    public String getHeaderButtonTextColor() {
        return this.scheme.get("property.style.headerbuttontextcolour");
    }

    public String getHeaderButtonBaseBackgroundColor() {
        return this.scheme.get("property.style.headerbuttonbasebgcolour");
    }
}

