/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.KeywordAttribute;
import com.atlassian.lucene36.util.AttributeImpl;

public final class KeywordAttributeImpl
extends AttributeImpl
implements KeywordAttribute {
    private boolean keyword;

    public void clear() {
        this.keyword = false;
    }

    public void copyTo(AttributeImpl target) {
        KeywordAttribute attr = (KeywordAttribute)((Object)target);
        attr.setKeyword(this.keyword);
    }

    public int hashCode() {
        return this.keyword ? 31 : 37;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        KeywordAttributeImpl other = (KeywordAttributeImpl)obj;
        return this.keyword == other.keyword;
    }

    public boolean isKeyword() {
        return this.keyword;
    }

    public void setKeyword(boolean isKeyword) {
        this.keyword = isKeyword;
    }
}

