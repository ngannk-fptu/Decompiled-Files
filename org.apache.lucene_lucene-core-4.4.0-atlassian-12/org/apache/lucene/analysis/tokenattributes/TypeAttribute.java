/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface TypeAttribute
extends Attribute {
    public static final String DEFAULT_TYPE = "word";

    public String type();

    public void setType(String var1);
}

