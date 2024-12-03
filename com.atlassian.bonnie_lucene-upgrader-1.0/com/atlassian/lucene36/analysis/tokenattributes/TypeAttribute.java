/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface TypeAttribute
extends Attribute {
    public static final String DEFAULT_TYPE = "word";

    public String type();

    public void setType(String var1);
}

