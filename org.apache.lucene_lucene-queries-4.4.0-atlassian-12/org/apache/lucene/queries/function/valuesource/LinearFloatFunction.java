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

public class LinearFloatFunction
extends ValueSource {
    protected final ValueSource source;
    protected final float slope;
    protected final float intercept;

    public LinearFloatFunction(ValueSource source, float slope, float intercept) {
        this.source = source;
        this.slope = slope;
        this.intercept = intercept;
    }

    @Override
    public String description() {
        return this.slope + "*float(" + this.source.description() + ")+" + this.intercept;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return vals.floatVal(doc) * LinearFloatFunction.this.slope + LinearFloatFunction.this.intercept;
            }

            @Override
            public String toString(int doc) {
                return LinearFloatFunction.this.slope + "*float(" + vals.toString(doc) + ")+" + LinearFloatFunction.this.intercept;
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }

    @Override
    public int hashCode() {
        int h = Float.floatToIntBits(this.slope);
        h = h >>> 2 | h << 30;
        h += Float.floatToIntBits(this.intercept);
        h ^= h << 14 | h >>> 19;
        return h + this.source.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (LinearFloatFunction.class != o.getClass()) {
            return false;
        }
        LinearFloatFunction other = (LinearFloatFunction)o;
        return this.slope == other.slope && this.intercept == other.intercept && this.source.equals(other.source);
    }
}

