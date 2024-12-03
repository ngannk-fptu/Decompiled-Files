/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Attribute
 */
package org.apache.lucene.analysis.ja.tokenattributes;

import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.util.Attribute;

public interface BaseFormAttribute
extends Attribute {
    public String getBaseForm();

    public void setToken(Token var1);
}

