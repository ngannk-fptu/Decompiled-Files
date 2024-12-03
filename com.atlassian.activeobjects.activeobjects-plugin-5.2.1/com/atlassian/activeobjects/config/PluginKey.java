/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.osgi.framework.Bundle
 */
package com.atlassian.activeobjects.config;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.osgi.framework.Bundle;

public final class PluginKey {
    private final String bundleSymbolicName;

    PluginKey(String bundleSymbolicName) {
        this.bundleSymbolicName = (String)Preconditions.checkNotNull((Object)bundleSymbolicName);
    }

    public static PluginKey fromBundle(Bundle bundle) {
        Preconditions.checkNotNull((Object)bundle);
        return new PluginKey(bundle.getSymbolicName());
    }

    public String asString() {
        return this.bundleSymbolicName;
    }

    public String toString() {
        return this.asString();
    }

    public int hashCode() {
        return new HashCodeBuilder(3, 11).append((Object)this.bundleSymbolicName).toHashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        PluginKey pluginKey = (PluginKey)o;
        return new EqualsBuilder().append((Object)this.bundleSymbolicName, (Object)pluginKey.bundleSymbolicName).isEquals();
    }
}

