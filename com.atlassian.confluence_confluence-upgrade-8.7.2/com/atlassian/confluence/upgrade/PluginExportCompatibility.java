/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.upgrade;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class PluginExportCompatibility {
    private final String currentVersion;
    private final String earliestVersion;

    public PluginExportCompatibility(String earliestVersion, String currentVersion) {
        this.currentVersion = (String)Preconditions.checkNotNull((Object)currentVersion);
        this.earliestVersion = (String)Preconditions.checkNotNull((Object)earliestVersion);
    }

    public String getCurrentVersion() {
        return this.currentVersion;
    }

    public String getEarliestVersion() {
        return this.earliestVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginExportCompatibility that = (PluginExportCompatibility)o;
        return Objects.equals(this.currentVersion, that.currentVersion) && Objects.equals(this.earliestVersion, that.earliestVersion);
    }

    public int hashCode() {
        return Objects.hash(this.currentVersion, this.earliestVersion);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("currentVersion", (Object)this.currentVersion).append("earliestVersion", (Object)this.earliestVersion).toString();
    }
}

