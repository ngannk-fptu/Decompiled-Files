/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.Comparator;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class PluginId
implements Comparable<PluginId> {
    private static final Comparator<PluginId> COMPARATOR = Comparator.comparing(PluginId::getKey, Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(PluginId::getVersion, Comparator.nullsFirst(Comparator.naturalOrder()));
    private String key;
    private String version;

    public PluginId(String key, String version) {
        if (StringUtils.isEmpty((CharSequence)key) && StringUtils.isEmpty((CharSequence)version)) {
            throw new IllegalArgumentException("If key and version are empty then the plugin should be represented as null");
        }
        if (StringUtils.isEmpty((CharSequence)key) && StringUtils.isNotEmpty((CharSequence)version)) {
            throw new IllegalArgumentException("Plugin key should be non-empty if a version is supplied");
        }
        this.key = key;
        this.version = version;
    }

    @Override
    public int compareTo(@Nonnull PluginId other) {
        return COMPARATOR.compare(this, other);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginId plugin = (PluginId)o;
        return Objects.equal((Object)this.key, (Object)plugin.key) && Objects.equal((Object)this.version, (Object)plugin.version);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.key, this.version});
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return this.version;
    }

    public String toString() {
        return MoreObjects.toStringHelper((String)"").addValue((Object)this.key).addValue((Object)this.version).toString();
    }

    public static PluginId valueOf(String key, String version) {
        return StringUtils.isEmpty((CharSequence)key) && StringUtils.isEmpty((CharSequence)version) ? null : new PluginId(key, version);
    }
}

