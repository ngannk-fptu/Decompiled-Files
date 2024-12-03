/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.spaces.Space;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BlueprintLock {
    private final Object id;
    private final String spaceKey;

    public BlueprintLock(@Nonnull Object id, @Nullable Space space) {
        this.id = id;
        this.spaceKey = space != null ? space.getKey().toLowerCase() : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlueprintLock that = (BlueprintLock)o;
        if (this.id != null ? !this.id.equals(that.id) : that.id != null) {
            return false;
        }
        return this.spaceKey != null ? this.spaceKey.equals(that.spaceKey) : that.spaceKey == null;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.spaceKey != null ? this.spaceKey.hashCode() : 0);
        return result;
    }
}

