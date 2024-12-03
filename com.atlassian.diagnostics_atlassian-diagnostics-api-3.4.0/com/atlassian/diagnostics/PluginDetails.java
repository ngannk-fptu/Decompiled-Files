/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class PluginDetails {
    private final String key;
    private final String name;
    private final String version;

    public PluginDetails(String key, String name, String version) {
        this.key = (String)MoreObjects.firstNonNull((Object)StringUtils.trimToNull((String)key), (Object)"not-detected");
        this.name = (String)MoreObjects.firstNonNull((Object)name, (Object)key);
        this.version = version;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginDetails that = (PluginDetails)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.version, that.version);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.version);
    }
}

