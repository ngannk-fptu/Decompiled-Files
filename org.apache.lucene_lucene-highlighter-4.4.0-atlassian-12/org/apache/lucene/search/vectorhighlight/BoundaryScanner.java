/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

public interface BoundaryScanner {
    public int findStartOffset(StringBuilder var1, int var2);

    public int findEndOffset(StringBuilder var1, int var2);
}

