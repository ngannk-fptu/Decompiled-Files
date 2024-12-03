/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster.shareddata;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated(since="8.2", forRemoval=true)
public class PluginSharedDataKey
implements Serializable {
    private static final long serialVersionUID = 962835242393570432L;
    private final String pluginKey;
    private final String sharedDataKey;

    public PluginSharedDataKey(String pluginKey, String sharedDataKey) {
        this.pluginKey = (String)Preconditions.checkNotNull((Object)pluginKey);
        this.sharedDataKey = (String)Preconditions.checkNotNull((Object)sharedDataKey);
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getSharedDataKey() {
        return this.sharedDataKey;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return String.format("%s:%s", this.pluginKey, this.sharedDataKey);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginSharedDataKey that = (PluginSharedDataKey)o;
        return this.pluginKey.equals(that.pluginKey) && this.sharedDataKey.equals(that.sharedDataKey);
    }

    public int hashCode() {
        return Objects.hash(this.pluginKey, this.sharedDataKey);
    }
}

