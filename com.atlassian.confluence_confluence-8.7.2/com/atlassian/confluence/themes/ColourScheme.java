/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import java.util.List;

public interface ColourScheme {
    public static final String TOP_BAR = "property.style.topbarcolour";
    public static final String BREADCRUMBS_TEXT = "property.style.breadcrumbstextcolour";
    @Deprecated
    public static final String SPACE_NAME = "property.style.spacenamecolour";
    public static final String HEADING_TEXT = "property.style.headingtextcolour";
    public static final String LINK = "property.style.linkcolour";
    public static final String BORDER = "property.style.bordercolour";
    @Deprecated
    public static final String NAV_BACKGROUND = "property.style.navbgcolour";
    @Deprecated
    public static final String NAV_TEXT = "property.style.navtextcolour";
    @Deprecated
    public static final String NAV_SELECTED_BACKGROUND = "property.style.navselectedbgcolour";
    @Deprecated
    public static final String NAV_SELECTED_TEXT = "property.style.navselectedtextcolour";
    public static final String SEARCH_FIELD_BACKGROUND = "property.style.searchfieldbgcolour";
    public static final String SEARCH_FIELD_TEXT = "property.style.searchfieldtextcolour";
    public static final String TOP_BAR_MENU_SELECTED_BACKGROUND = "property.style.topbarmenuselectedbgcolour";
    public static final String TOP_BAR_MENU_SELECTED_TEXT = "property.style.topbarmenuselectedtextcolour";
    public static final String TOP_BAR_MENU_ITEM_TEXT = "property.style.topbarmenuitemtextcolour";
    public static final String MENU_SELECTED_BACKGROUND = "property.style.menuselectedbgcolour";
    public static final String MENU_ITEM_TEXT = "property.style.menuitemtextcolour";
    public static final String MENU_ITEM_SELECTED_BACKGROUND = "property.style.menuitemselectedbgcolour";
    public static final String MENU_ITEM_SELECTED_TEXT = "property.style.menuitemselectedtextcolour";
    public static final String HEADER_BUTTON_BASE_BACKGROUND = "property.style.headerbuttonbasebgcolour";
    public static final String HEADER_BUTTON_TEXT = "property.style.headerbuttontextcolour";
    public static final List<String> ORDERED_KEYS = List.of("property.style.topbarcolour", "property.style.breadcrumbstextcolour", "property.style.headerbuttonbasebgcolour", "property.style.headerbuttontextcolour", "property.style.topbarmenuselectedbgcolour", "property.style.topbarmenuselectedtextcolour", "property.style.topbarmenuitemtextcolour", "property.style.menuitemselectedbgcolour", "property.style.menuitemselectedtextcolour", "property.style.searchfieldbgcolour", "property.style.searchfieldtextcolour", "property.style.menuselectedbgcolour", "property.style.menuitemtextcolour", "property.style.headingtextcolour", "property.style.linkcolour", "property.style.bordercolour");
    public static final List<String> DEPRECATED_KEYS = List.of("property.style.spacenamecolour", "property.style.navbgcolour", "property.style.navtextcolour", "property.style.navselectedbgcolour", "property.style.navselectedtextcolour");

    public String get(String var1);

    public String get(String var1, double var2);

    public boolean equals(Object var1);

    public int hashCode();

    public boolean isDefaultColourScheme();
}

