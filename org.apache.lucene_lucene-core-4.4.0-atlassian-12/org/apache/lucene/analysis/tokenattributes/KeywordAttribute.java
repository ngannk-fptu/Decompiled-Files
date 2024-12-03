/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface KeywordAttribute
extends Attribute {
    public boolean isKeyword();

    public void setKeyword(boolean var1);
}

