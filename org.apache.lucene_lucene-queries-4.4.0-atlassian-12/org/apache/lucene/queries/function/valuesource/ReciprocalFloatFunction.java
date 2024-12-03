/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.search.IndexSearcher;

public class ReciprocalFloatFunction
extends ValueSource {
    protected final ValueSource source;
    protected final float m;
    protected final float a;
    protected final float b;

    public ReciprocalFloatFunction(ValueSource source, float m, float a, float b) {
        this.source = source;
        this.m = m;
        this.a = a;
        this.b = b;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return ReciprocalFloatFunction.this.a / (ReciprocalFloatFunction.this.m * vals.floatVal(doc) + ReciprocalFloatFunction.this.b);
            }

            @Override
            public String toString(int doc) {
                return Float.toString(ReciprocalFloatFunction.this.a) + "/(" + ReciprocalFloatFunction.this.m + "*float(" + vals.toString(doc) + ')' + '+' + ReciprocalFloatFunction.this.b + ')';
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }

    @Override
    public String description() {
        return Float.toString(this.a) + "/(" + this.m + "*float(" + this.source.description() + ")+" + this.b + ')';
    }

    @Override
    public int hashCode() {
        int h = Float.floatToIntBits(this.a) + Float.floatToIntBits(this.m);
        h ^= h << 13 | h >>> 20;
        return h + Float.floatToIntBits(this.b) + this.source.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (ReciprocalFloatFunction.class != o.getClass()) {
            return false;
        }
        ReciprocalFloatFunction other = (ReciprocalFloatFunction)o;
        return this.m == other.m && this.a == other.a && this.b == other.b && this.source.equals(other.source);
    }
}

