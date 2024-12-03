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

public abstract class DualFloatFunction
extends ValueSource {
    protected final ValueSource a;
    protected final ValueSource b;

    public DualFloatFunction(ValueSource a, ValueSource b) {
        this.a = a;
        this.b = b;
    }

    protected abstract String name();

    protected abstract float func(int var1, FunctionValues var2, FunctionValues var3);

    @Override
    public String description() {
        return this.name() + "(" + this.a.description() + "," + this.b.description() + ")";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues aVals = this.a.getValues(context, readerContext);
        final FunctionValues bVals = this.b.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return DualFloatFunction.this.func(doc, aVals, bVals);
            }

            @Override
            public String toString(int doc) {
                return DualFloatFunction.this.name() + '(' + aVals.toString(doc) + ',' + bVals.toString(doc) + ')';
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.a.createWeight(context, searcher);
        this.b.createWeight(context, searcher);
    }

    @Override
    public int hashCode() {
        int h = this.a.hashCode();
        h ^= h << 13 | h >>> 20;
        h += this.b.hashCode();
        h ^= h << 23 | h >>> 10;
        return h += this.name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        DualFloatFunction other = (DualFloatFunction)o;
        return this.a.equals(other.a) && this.b.equals(other.b);
    }
}

