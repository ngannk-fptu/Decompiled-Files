/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.atlassian.plugins.navlink.producer.navigation.rest.NavigationLinkIdTypeAdapter;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(value=NavigationLinkIdTypeAdapter.class)
public class NavigationLinkId {
    private final String navigationLinkId;

    public NavigationLinkId(@Nonnull String navigationLinkId) {
        this.navigationLinkId = (String)Preconditions.checkNotNull((Object)navigationLinkId);
    }

    public String get() {
        return this.navigationLinkId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NavigationLinkId that = (NavigationLinkId)o;
        return this.navigationLinkId.equals(that.navigationLinkId);
    }

    public int hashCode() {
        return this.navigationLinkId.hashCode();
    }

    public String toString() {
        return this.navigationLinkId;
    }
}

