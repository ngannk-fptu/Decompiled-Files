/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface FlagsAttribute
extends Attribute {
    public int getFlags();

    public void setFlags(int var1);
}

