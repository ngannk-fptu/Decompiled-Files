/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.diffs;

import com.atlassian.confluence.util.diffs.MergeResult;

public interface Merger {
    public MergeResult mergeContent(String var1, String var2, String var3);
}

