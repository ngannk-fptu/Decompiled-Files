/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface PositionIncrementAttribute
extends Attribute {
    public void setPositionIncrement(int var1);

    public int getPositionIncrement();
}

