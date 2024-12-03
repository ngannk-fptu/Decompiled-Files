/*
 * Decompiled with CFR 0.152.
 */
package com.adaptavist.confluence.naturalchildren;

public class HasMoreInfo {
    private final boolean hasMoreAfter;
    private final Object id;
    private final Object lastLoadedId;

    public HasMoreInfo(boolean hasMoreAfter, Object id, Object lastLoadedId) {
        this.hasMoreAfter = hasMoreAfter;
        this.id = id;
        this.lastLoadedId = lastLoadedId;
    }

    public boolean isHasMoreAfter() {
        return this.hasMoreAfter;
    }

    public Object getId() {
        return this.id;
    }

    public Object getLastLoadedId() {
        return this.lastLoadedId;
    }
}

