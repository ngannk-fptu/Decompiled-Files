/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeReflector
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ja.util.ToStringUtil;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class PartOfSpeechAttributeImpl
extends AttributeImpl
implements PartOfSpeechAttribute,
Cloneable {
    private Token token;

    @Override
    public String getPartOfSpeech() {
        return this.token == null ? null : this.token.getPartOfSpeech();
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }

    public void clear() {
        this.token = null;
    }

    public void copyTo(AttributeImpl target) {
        PartOfSpeechAttribute t = (PartOfSpeechAttribute)target;
        t.setToken(this.token);
    }

    public void reflectWith(AttributeReflector reflector) {
        String partOfSpeech = this.getPartOfSpeech();
        String partOfSpeechEN = partOfSpeech == null ? null : ToStringUtil.getPOSTranslation(partOfSpeech);
        reflector.reflect(PartOfSpeechAttribute.class, "partOfSpeech", (Object)partOfSpeech);
        reflector.reflect(PartOfSpeechAttribute.class, "partOfSpeech (en)", (Object)partOfSpeechEN);
    }
}

