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

public class RangeMapFloatFunction
extends ValueSource {
    protected final ValueSource source;
    protected final float min;
    protected final float max;
    protected final float target;
    protected final Float defaultVal;

    public RangeMapFloatFunction(ValueSource source, float min, float max, float target, Float def) {
        this.source = source;
        this.min = min;
        this.max = max;
        this.target = target;
        this.defaultVal = def;
    }

    @Override
    public String description() {
        return "map(" + this.source.description() + "," + this.min + "," + this.max + "," + this.target + ")";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                float val = vals.floatVal(doc);
                return val >= RangeMapFloatFunction.this.min && val <= RangeMapFloatFunction.this.max ? RangeMapFloatFunction.this.target : (RangeMapFloatFunction.this.defaultVal == null ? val : RangeMapFloatFunction.this.defaultVal.floatValue());
            }

            @Override
            public String toString(int doc) {
                return "map(" + vals.toString(doc) + ",min=" + RangeMapFloatFunction.this.min + ",max=" + RangeMapFloatFunction.this.max + ",target=" + RangeMapFloatFunction.this.target + ")";
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }

    @Override
    public int hashCode() {
        int h = this.source.hashCode();
        h ^= h << 10 | h >>> 23;
        h += Float.floatToIntBits(this.min);
        h ^= h << 14 | h >>> 19;
        h += Float.floatToIntBits(this.max);
        h ^= h << 13 | h >>> 20;
        h += Float.floatToIntBits(this.target);
        if (this.defaultVal != null) {
            h += this.defaultVal.hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (RangeMapFloatFunction.class != o.getClass()) {
            return false;
        }
        RangeMapFloatFunction other = (RangeMapFloatFunction)o;
        return this.min == other.min && this.max == other.max && this.target == other.target && this.source.equals(other.source) && (this.defaultVal == other.defaultVal || this.defaultVal != null && this.defaultVal.equals(other.defaultVal));
    }
}

