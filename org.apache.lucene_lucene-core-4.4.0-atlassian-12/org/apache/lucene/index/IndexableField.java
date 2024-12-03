/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

public interface IndexableField {
    public String name();

    public IndexableFieldType fieldType();

    public float boost();

    public BytesRef binaryValue();

    public String stringValue();

    public Reader readerValue();

    public Number numericValue();

    public TokenStream tokenStream(Analyzer var1) throws IOException;
}

