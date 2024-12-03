/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.util.Attribute;

public interface KeywordAttribute
extends Attribute {
    public boolean isKeyword();

    public void setKeyword(boolean var1);
}

