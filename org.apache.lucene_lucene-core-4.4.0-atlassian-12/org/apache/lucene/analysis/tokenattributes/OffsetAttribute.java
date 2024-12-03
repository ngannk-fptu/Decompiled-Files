/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface OffsetAttribute
extends Attribute {
    public int startOffset();

    public void setOffset(int var1, int var2);

    public int endOffset();
}

