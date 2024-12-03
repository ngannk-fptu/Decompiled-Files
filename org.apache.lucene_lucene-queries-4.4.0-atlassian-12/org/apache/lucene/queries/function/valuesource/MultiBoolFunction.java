/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apache.lucene.queries.function.valuesource.BoolFunction;
import org.apache.lucene.search.IndexSearcher;

public abstract class MultiBoolFunction
extends BoolFunction {
    protected final List<ValueSource> sources;

    public MultiBoolFunction(List<ValueSource> sources) {
        this.sources = sources;
    }

    protected abstract String name();

    protected abstract boolean func(int var1, FunctionValues[] var2);

    @Override
    public BoolDocValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues[] vals = new FunctionValues[this.sources.size()];
        int i = 0;
        for (ValueSource source : this.sources) {
            vals[i++] = source.getValues(context, readerContext);
        }
        return new BoolDocValues(this){

            @Override
            public boolean boolVal(int doc) {
                return MultiBoolFunction.this.func(doc, vals);
            }

            @Override
            public String toString(int doc) {
                StringBuilder sb = new StringBuilder(MultiBoolFunction.this.name());
                sb.append('(');
                boolean first = true;
                for (FunctionValues dv : vals) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(',');
                    }
                    sb.append(dv.toString(doc));
                }
                return sb.toString();
            }
        };
    }

    @Override
    public String description() {
        StringBuilder sb = new StringBuilder(this.name());
        sb.append('(');
        boolean first = true;
        for (ValueSource source : this.sources) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(source.description());
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return this.sources.hashCode() + this.name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        MultiBoolFunction other = (MultiBoolFunction)o;
        return this.sources.equals(other.sources);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        for (ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }
}

