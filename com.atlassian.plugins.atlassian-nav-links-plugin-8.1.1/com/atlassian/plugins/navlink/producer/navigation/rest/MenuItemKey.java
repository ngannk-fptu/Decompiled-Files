/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.atlassian.plugins.navlink.producer.navigation.rest.MenuItemKeyTypeAdapter;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlJavaTypeAdapter(value=MenuItemKeyTypeAdapter.class)
@Immutable
public class MenuItemKey {
    private final String key;

    public MenuItemKey(String key) {
        this.key = key;
    }

    public String get() {
        return this.key;
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.key).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        MenuItemKey rhs = (MenuItemKey)obj;
        return new EqualsBuilder().append((Object)this.key, (Object)rhs.key).isEquals();
    }

    public String toString() {
        return this.key;
    }
}

