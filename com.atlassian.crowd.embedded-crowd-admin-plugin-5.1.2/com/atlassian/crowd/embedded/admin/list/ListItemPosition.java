/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.list;

public final class ListItemPosition {
    private final int index;
    private final int totalItems;

    public ListItemPosition(int index, int totalItems) {
        this.index = index;
        this.totalItems = totalItems;
    }

    public boolean canMoveUp() {
        return this.index > 0;
    }

    public boolean canMoveDown() {
        return this.index < this.totalItems - 1;
    }
}

