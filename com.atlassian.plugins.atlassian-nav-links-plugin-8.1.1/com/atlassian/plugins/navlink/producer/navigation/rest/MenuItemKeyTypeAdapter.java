/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.atlassian.plugins.navlink.producer.navigation.rest.MenuItemKey;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MenuItemKeyTypeAdapter
extends XmlAdapter<String, MenuItemKey> {
    @Nullable
    public MenuItemKey unmarshal(@Nullable String serializedMenuItemKey) throws Exception {
        return serializedMenuItemKey != null ? new MenuItemKey(serializedMenuItemKey) : null;
    }

    @Nullable
    public String marshal(@Nullable MenuItemKey menuItemKey) throws Exception {
        return menuItemKey != null ? menuItemKey.get() : null;
    }
}

