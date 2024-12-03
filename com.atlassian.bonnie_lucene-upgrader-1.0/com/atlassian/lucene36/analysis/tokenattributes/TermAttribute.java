/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

@Deprecated
public interface TermAttribute
extends Attribute {
    public String term();

    public void setTermBuffer(char[] var1, int var2, int var3);

    public void setTermBuffer(String var1);

    public void setTermBuffer(String var1, int var2, int var3);

    public char[] termBuffer();

    public char[] resizeTermBuffer(int var1);

    public int termLength();

    public void setTermLength(int var1);
}

