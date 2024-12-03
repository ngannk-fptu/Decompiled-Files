/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

public interface TermFreqVector {
    public String getField();

    public int size();

    public String[] getTerms();

    public int[] getTermFrequencies();

    public int indexOf(String var1);

    public int[] indexesOf(String[] var1, int var2, int var3);
}

