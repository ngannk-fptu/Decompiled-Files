/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.search.IndexSearcher;

public abstract class MultiFloatFunction
extends ValueSource {
    protected final ValueSource[] sources;

    public MultiFloatFunction(ValueSource[] sources) {
        this.sources = sources;
    }

    protected abstract String name();

    protected abstract float func(int var1, FunctionValues[] var2);

    @Override
    public String description() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name()).append('(');
        boolean firstTime = true;
        for (ValueSource source : this.sources) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(',');
            }
            sb.append(source);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues[] valsArr = new FunctionValues[this.sources.length];
        for (int i = 0; i < this.sources.length; ++i) {
            valsArr[i] = this.sources[i].getValues(context, readerContext);
        }
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return MultiFloatFunction.this.func(doc, valsArr);
            }

            @Override
            public String toString(int doc) {
                StringBuilder sb = new StringBuilder();
                sb.append(MultiFloatFunction.this.name()).append('(');
                boolean firstTime = true;
                for (FunctionValues vals : valsArr) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        sb.append(',');
                    }
                    sb.append(vals.toString(doc));
                }
                sb.append(')');
                return sb.toString();
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        for (ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sources) + this.name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        MultiFloatFunction other = (MultiFloatFunction)o;
        return this.name().equals(other.name()) && Arrays.equals(this.sources, other.sources);
    }
}

