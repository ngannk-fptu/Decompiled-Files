/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Attribute
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.util.Attribute;

public interface ReadingAttribute
extends Attribute {
    public String getReading();

    public String getPronunciation();

    public void setToken(Token var1);
}

