/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.diffs;

public interface MergeResult {
    public boolean hasConflicts();

    public String getMergedContent();
}

