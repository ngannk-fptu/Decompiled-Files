/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.diffs;

import com.atlassian.confluence.util.diffs.MergeResult;

public class SimpleMergeResult
implements MergeResult {
    private final String mergedContent;
    private final boolean conflicts;
    public static final MergeResult FAIL_MERGE_RESULT = new SimpleMergeResult(true, null);

    public SimpleMergeResult(boolean conflicts, String mergedContent) {
        this.conflicts = conflicts;
        this.mergedContent = mergedContent;
    }

    @Override
    public String getMergedContent() {
        return this.mergedContent;
    }

    @Override
    public boolean hasConflicts() {
        return this.conflicts;
    }
}

