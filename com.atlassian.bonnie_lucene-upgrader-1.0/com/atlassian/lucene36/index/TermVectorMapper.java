/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermVectorOffsetInfo;

public abstract class TermVectorMapper {
    private boolean ignoringPositions;
    private boolean ignoringOffsets;

    protected TermVectorMapper() {
    }

    protected TermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets) {
        this.ignoringPositions = ignoringPositions;
        this.ignoringOffsets = ignoringOffsets;
    }

    public abstract void setExpectations(String var1, int var2, boolean var3, boolean var4);

    public abstract void map(String var1, int var2, TermVectorOffsetInfo[] var3, int[] var4);

    public boolean isIgnoringPositions() {
        return this.ignoringPositions;
    }

    public boolean isIgnoringOffsets() {
        return this.ignoringOffsets;
    }

    public void setDocumentNumber(int documentNumber) {
    }
}

