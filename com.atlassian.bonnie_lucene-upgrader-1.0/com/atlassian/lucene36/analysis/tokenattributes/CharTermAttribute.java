/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface CharTermAttribute
extends Attribute,
CharSequence,
Appendable {
    public void copyBuffer(char[] var1, int var2, int var3);

    public char[] buffer();

    public char[] resizeBuffer(int var1);

    public CharTermAttribute setLength(int var1);

    public CharTermAttribute setEmpty();

    public CharTermAttribute append(CharSequence var1);

    public CharTermAttribute append(CharSequence var1, int var2, int var3);

    public CharTermAttribute append(char var1);

    public CharTermAttribute append(String var1);

    public CharTermAttribute append(StringBuilder var1);

    public CharTermAttribute append(CharTermAttribute var1);
}

