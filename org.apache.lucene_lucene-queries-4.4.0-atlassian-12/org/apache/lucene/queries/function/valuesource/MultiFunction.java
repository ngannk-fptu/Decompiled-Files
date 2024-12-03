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
import org.apache.lucene.search.IndexSearcher;

public abstract class MultiFunction
extends ValueSource {
    protected final List<ValueSource> sources;

    public MultiFunction(List<ValueSource> sources) {
        this.sources = sources;
    }

    protected abstract String name();

    @Override
    public String description() {
        return MultiFunction.description(this.name(), this.sources);
    }

    public static String description(String name, List<ValueSource> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
        boolean firstTime = true;
        for (ValueSource source : sources) {
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

    public static FunctionValues[] valsArr(List<ValueSource> sources, Map fcontext, AtomicReaderContext readerContext) throws IOException {
        FunctionValues[] valsArr = new FunctionValues[sources.size()];
        int i = 0;
        for (ValueSource source : sources) {
            valsArr[i++] = source.getValues(fcontext, readerContext);
        }
        return valsArr;
    }

    public static String toString(String name, FunctionValues[] valsArr, int doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
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

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        for (ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
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
        MultiFunction other = (MultiFunction)o;
        return this.sources.equals(other.sources);
    }

    public class Values
    extends FunctionValues {
        final FunctionValues[] valsArr;

        public Values(FunctionValues[] valsArr) {
            this.valsArr = valsArr;
        }

        @Override
        public String toString(int doc) {
            return MultiFunction.toString(MultiFunction.this.name(), this.valsArr, doc);
        }

        @Override
        public FunctionValues.ValueFiller getValueFiller() {
            return super.getValueFiller();
        }
    }
}

