/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeReflector
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class BaseFormAttributeImpl
extends AttributeImpl
implements BaseFormAttribute,
Cloneable {
    private Token token;

    @Override
    public String getBaseForm() {
        return this.token == null ? null : this.token.getBaseForm();
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }

    public void clear() {
        this.token = null;
    }

    public void copyTo(AttributeImpl target) {
        BaseFormAttribute t = (BaseFormAttribute)target;
        t.setToken(this.token);
    }

    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(BaseFormAttribute.class, "baseForm", (Object)this.getBaseForm());
    }
}

