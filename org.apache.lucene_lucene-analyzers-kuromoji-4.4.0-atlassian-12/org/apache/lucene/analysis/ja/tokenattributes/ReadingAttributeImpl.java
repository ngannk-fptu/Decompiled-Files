/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeReflector
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.ja.util.ToStringUtil;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class ReadingAttributeImpl
extends AttributeImpl
implements ReadingAttribute,
Cloneable {
    private Token token;

    @Override
    public String getReading() {
        return this.token == null ? null : this.token.getReading();
    }

    @Override
    public String getPronunciation() {
        return this.token == null ? null : this.token.getPronunciation();
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }

    public void clear() {
        this.token = null;
    }

    public void copyTo(AttributeImpl target) {
        ReadingAttribute t = (ReadingAttribute)target;
        t.setToken(this.token);
    }

    public void reflectWith(AttributeReflector reflector) {
        String reading = this.getReading();
        String readingEN = reading == null ? null : ToStringUtil.getRomanization(reading);
        String pronunciation = this.getPronunciation();
        String pronunciationEN = pronunciation == null ? null : ToStringUtil.getRomanization(pronunciation);
        reflector.reflect(ReadingAttribute.class, "reading", (Object)reading);
        reflector.reflect(ReadingAttribute.class, "reading (en)", (Object)readingEN);
        reflector.reflect(ReadingAttribute.class, "pronunciation", (Object)pronunciation);
        reflector.reflect(ReadingAttribute.class, "pronunciation (en)", (Object)pronunciationEN);
    }
}

