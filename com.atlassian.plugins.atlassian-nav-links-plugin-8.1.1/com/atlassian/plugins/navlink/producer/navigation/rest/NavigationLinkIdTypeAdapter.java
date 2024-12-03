/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.atlassian.plugins.navlink.producer.navigation.rest.NavigationLinkId;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NavigationLinkIdTypeAdapter
extends XmlAdapter<String, NavigationLinkId> {
    @Nullable
    public NavigationLinkId unmarshal(@Nullable String serializedNavigationLinkId) throws Exception {
        return serializedNavigationLinkId != null ? new NavigationLinkId(serializedNavigationLinkId) : null;
    }

    @Nullable
    public String marshal(@Nullable NavigationLinkId navigationLinkId) throws Exception {
        return navigationLinkId != null ? navigationLinkId.get() : null;
    }
}

