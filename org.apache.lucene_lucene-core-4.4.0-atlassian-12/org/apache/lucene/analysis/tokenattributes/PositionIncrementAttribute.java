/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface PositionIncrementAttribute
extends Attribute {
    public void setPositionIncrement(int var1);

    public int getPositionIncrement();
}

