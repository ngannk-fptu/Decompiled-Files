/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;

public abstract class SpaceContentEntityObject
extends ContentEntityObject
implements Spaced {
    private Space space;

    @Override
    public Space getSpace() {
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.setSpace(null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.space != null ? this.space.getKey().hashCode() : 0);
        return result;
    }

    @Override
    public String getNameForComparison() {
        return this.getSpace().getName();
    }

    public String getSpaceKey() {
        return this.getSpace() == null ? null : this.getSpace().getKey();
    }

    @Override
    public boolean isIndexable() {
        return this.getSpace() != null && super.isIndexable();
    }

    public boolean isInSpace(Space space) {
        if (this.space == null || space == null) {
            return false;
        }
        return this.space.equals(space);
    }
}

