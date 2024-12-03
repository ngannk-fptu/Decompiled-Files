/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja.dict;

public interface Dictionary {
    public static final String INTERNAL_SEPARATOR = "\u0000";

    public int getLeftId(int var1);

    public int getRightId(int var1);

    public int getWordCost(int var1);

    public String getPartOfSpeech(int var1);

    public String getReading(int var1, char[] var2, int var3, int var4);

    public String getBaseForm(int var1, char[] var2, int var3, int var4);

    public String getPronunciation(int var1, char[] var2, int var3, int var4);

    public String getInflectionType(int var1);

    public String getInflectionForm(int var1);
}

