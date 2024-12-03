/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface FlagsAttribute
extends Attribute {
    public int getFlags();

    public void setFlags(int var1);
}

