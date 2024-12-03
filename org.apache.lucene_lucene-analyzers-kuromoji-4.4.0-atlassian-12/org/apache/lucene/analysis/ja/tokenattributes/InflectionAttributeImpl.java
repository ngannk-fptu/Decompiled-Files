/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeReflector
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.apache.lucene.analysis.ja.util.ToStringUtil;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class InflectionAttributeImpl
extends AttributeImpl
implements InflectionAttribute,
Cloneable {
    private Token token;

    @Override
    public String getInflectionType() {
        return this.token == null ? null : this.token.getInflectionType();
    }

    @Override
    public String getInflectionForm() {
        return this.token == null ? null : this.token.getInflectionForm();
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }

    public void clear() {
        this.token = null;
    }

    public void copyTo(AttributeImpl target) {
        InflectionAttribute t = (InflectionAttribute)target;
        t.setToken(this.token);
    }

    public void reflectWith(AttributeReflector reflector) {
        String type = this.getInflectionType();
        String typeEN = type == null ? null : ToStringUtil.getInflectionTypeTranslation(type);
        reflector.reflect(InflectionAttribute.class, "inflectionType", (Object)type);
        reflector.reflect(InflectionAttribute.class, "inflectionType (en)", (Object)typeEN);
        String form = this.getInflectionForm();
        String formEN = form == null ? null : ToStringUtil.getInflectedFormTranslation(form);
        reflector.reflect(InflectionAttribute.class, "inflectionForm", (Object)form);
        reflector.reflect(InflectionAttribute.class, "inflectionForm (en)", (Object)formEN);
    }
}

