/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface OffsetAttribute
extends Attribute {
    public int startOffset();

    public void setOffset(int var1, int var2);

    public int endOffset();
}

