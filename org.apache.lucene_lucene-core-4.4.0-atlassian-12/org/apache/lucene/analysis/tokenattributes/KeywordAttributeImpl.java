/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.util.AttributeImpl;

public final class KeywordAttributeImpl
extends AttributeImpl
implements KeywordAttribute {
    private boolean keyword;

    @Override
    public void clear() {
        this.keyword = false;
    }

    @Override
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

    @Override
    public boolean isKeyword() {
        return this.keyword;
    }

    @Override
    public void setKeyword(boolean isKeyword) {
        this.keyword = isKeyword;
    }
}

