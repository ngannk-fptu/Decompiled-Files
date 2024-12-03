/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.lucene.util.BytesRef;

public class LiteralValueSource
extends ValueSource {
    protected final String string;
    protected final BytesRef bytesRef;
    public static final int hash = LiteralValueSource.class.hashCode();

    public LiteralValueSource(String string) {
        this.string = string;
        this.bytesRef = new BytesRef((CharSequence)string);
    }

    public String getValue() {
        return this.string;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return new StrDocValues(this){

            @Override
            public String strVal(int doc) {
                return LiteralValueSource.this.string;
            }

            @Override
            public boolean bytesVal(int doc, BytesRef target) {
                target.copyBytes(LiteralValueSource.this.bytesRef);
                return true;
            }

            @Override
            public String toString(int doc) {
                return LiteralValueSource.this.string;
            }
        };
    }

    @Override
    public String description() {
        return "literal(" + this.string + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralValueSource)) {
            return false;
        }
        LiteralValueSource that = (LiteralValueSource)o;
        return this.string.equals(that.string);
    }

    @Override
    public int hashCode() {
        return hash + this.string.hashCode();
    }
}

