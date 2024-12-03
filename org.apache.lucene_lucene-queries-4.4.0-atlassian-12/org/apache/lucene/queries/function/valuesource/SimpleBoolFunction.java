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
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apache.lucene.queries.function.valuesource.BoolFunction;
import org.apache.lucene.search.IndexSearcher;

public abstract class SimpleBoolFunction
extends BoolFunction {
    protected final ValueSource source;

    public SimpleBoolFunction(ValueSource source) {
        this.source = source;
    }

    protected abstract String name();

    protected abstract boolean func(int var1, FunctionValues var2);

    @Override
    public BoolDocValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new BoolDocValues(this){

            @Override
            public boolean boolVal(int doc) {
                return SimpleBoolFunction.this.func(doc, vals);
            }

            @Override
            public String toString(int doc) {
                return SimpleBoolFunction.this.name() + '(' + vals.toString(doc) + ')';
            }
        };
    }

    @Override
    public String description() {
        return this.name() + '(' + this.source.description() + ')';
    }

    @Override
    public int hashCode() {
        return this.source.hashCode() + this.name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        SimpleBoolFunction other = (SimpleBoolFunction)o;
        return this.source.equals(other.source);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
}

