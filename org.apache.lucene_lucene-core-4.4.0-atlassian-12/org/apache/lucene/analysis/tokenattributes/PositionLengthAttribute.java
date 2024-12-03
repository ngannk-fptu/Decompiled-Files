/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface PositionLengthAttribute
extends Attribute {
    public void setPositionLength(int var1);

    public int getPositionLength();
}

