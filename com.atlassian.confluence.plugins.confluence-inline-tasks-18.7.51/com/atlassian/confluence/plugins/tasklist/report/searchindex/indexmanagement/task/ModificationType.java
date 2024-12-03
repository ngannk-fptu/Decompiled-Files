/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task;

public enum ModificationType {
    REMOVE(true, false),
    REMOVE_AND_ADD(true, true);

    public final boolean isRemove;
    public final boolean isAdd;

    private ModificationType(boolean isRemove, boolean isAdd) {
        this.isRemove = isRemove;
        this.isAdd = isAdd;
    }
}

