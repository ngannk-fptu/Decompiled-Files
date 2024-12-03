/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.ReaderUtil
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstIntDocValues;

public class NumDocsValueSource
extends ValueSource {
    public String name() {
        return "numdocs";
    }

    @Override
    public String description() {
        return this.name() + "()";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return new ConstIntDocValues(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader().numDocs(), this);
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}

