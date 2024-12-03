/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.ReaderUtil
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.search.IndexSearcher;

public class ScaleFloatFunction
extends ValueSource {
    protected final ValueSource source;
    protected final float min;
    protected final float max;

    public ScaleFloatFunction(ValueSource source, float min, float max) {
        this.source = source;
        this.min = min;
        this.max = max;
    }

    @Override
    public String description() {
        return "scale(" + this.source.description() + "," + this.min + "," + this.max + ")";
    }

    private ScaleInfo createScaleInfo(Map context, AtomicReaderContext readerContext) throws IOException {
        List leaves = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).leaves();
        float minVal = Float.POSITIVE_INFINITY;
        float maxVal = Float.NEGATIVE_INFINITY;
        for (AtomicReaderContext leaf : leaves) {
            int maxDoc = leaf.reader().maxDoc();
            FunctionValues vals = this.source.getValues(context, leaf);
            for (int i = 0; i < maxDoc; ++i) {
                float val = vals.floatVal(i);
                if ((Float.floatToRawIntBits(val) & 0x7F800000) == 2139095040) continue;
                if (val < minVal) {
                    minVal = val;
                }
                if (!(val > maxVal)) continue;
                maxVal = val;
            }
        }
        if (minVal == Float.POSITIVE_INFINITY) {
            maxVal = 0.0f;
            minVal = 0.0f;
        }
        ScaleInfo scaleInfo = new ScaleInfo();
        scaleInfo.minVal = minVal;
        scaleInfo.maxVal = maxVal;
        context.put(this.source, scaleInfo);
        return scaleInfo;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        ScaleInfo scaleInfo = (ScaleInfo)context.get(this.source);
        if (scaleInfo == null) {
            scaleInfo = this.createScaleInfo(context, readerContext);
        }
        final float scale = scaleInfo.maxVal - scaleInfo.minVal == 0.0f ? 0.0f : (this.max - this.min) / (scaleInfo.maxVal - scaleInfo.minVal);
        final float minSource = scaleInfo.minVal;
        final float maxSource = scaleInfo.maxVal;
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return (vals.floatVal(doc) - minSource) * scale + ScaleFloatFunction.this.min;
            }

            @Override
            public String toString(int doc) {
                return "scale(" + vals.toString(doc) + ",toMin=" + ScaleFloatFunction.this.min + ",toMax=" + ScaleFloatFunction.this.max + ",fromMin=" + minSource + ",fromMax=" + maxSource + ")";
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }

    @Override
    public int hashCode() {
        int h = Float.floatToIntBits(this.min);
        h *= 29;
        h += Float.floatToIntBits(this.max);
        h *= 29;
        return h += this.source.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (ScaleFloatFunction.class != o.getClass()) {
            return false;
        }
        ScaleFloatFunction other = (ScaleFloatFunction)o;
        return this.min == other.min && this.max == other.max && this.source.equals(other.source);
    }

    private static class ScaleInfo {
        float minVal;
        float maxVal;

        private ScaleInfo() {
        }
    }
}

