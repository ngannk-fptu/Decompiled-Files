/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.index.FieldInfo;
import java.io.Reader;
import java.io.Serializable;

public interface Fieldable
extends Serializable {
    public void setBoost(float var1);

    public float getBoost();

    public String name();

    public String stringValue();

    public Reader readerValue();

    public TokenStream tokenStreamValue();

    public boolean isStored();

    public boolean isIndexed();

    public boolean isTokenized();

    public boolean isTermVectorStored();

    public boolean isStoreOffsetWithTermVector();

    public boolean isStorePositionWithTermVector();

    public boolean isBinary();

    public boolean getOmitNorms();

    public void setOmitNorms(boolean var1);

    public boolean isLazy();

    public int getBinaryOffset();

    public int getBinaryLength();

    public byte[] getBinaryValue();

    public byte[] getBinaryValue(byte[] var1);

    public FieldInfo.IndexOptions getIndexOptions();

    public void setIndexOptions(FieldInfo.IndexOptions var1);
}

