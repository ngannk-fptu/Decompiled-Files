/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.BoolFunction;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

public class IfFunction
extends BoolFunction {
    private final ValueSource ifSource;
    private final ValueSource trueSource;
    private final ValueSource falseSource;

    public IfFunction(ValueSource ifSource, ValueSource trueSource, ValueSource falseSource) {
        this.ifSource = ifSource;
        this.trueSource = trueSource;
        this.falseSource = falseSource;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues ifVals = this.ifSource.getValues(context, readerContext);
        final FunctionValues trueVals = this.trueSource.getValues(context, readerContext);
        final FunctionValues falseVals = this.falseSource.getValues(context, readerContext);
        return new FunctionValues(){

            @Override
            public byte byteVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.byteVal(doc) : falseVals.byteVal(doc);
            }

            @Override
            public short shortVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.shortVal(doc) : falseVals.shortVal(doc);
            }

            @Override
            public float floatVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.floatVal(doc) : falseVals.floatVal(doc);
            }

            @Override
            public int intVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.intVal(doc) : falseVals.intVal(doc);
            }

            @Override
            public long longVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.longVal(doc) : falseVals.longVal(doc);
            }

            @Override
            public double doubleVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.doubleVal(doc) : falseVals.doubleVal(doc);
            }

            @Override
            public String strVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.strVal(doc) : falseVals.strVal(doc);
            }

            @Override
            public boolean boolVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.boolVal(doc) : falseVals.boolVal(doc);
            }

            @Override
            public boolean bytesVal(int doc, BytesRef target) {
                return ifVals.boolVal(doc) ? trueVals.bytesVal(doc, target) : falseVals.bytesVal(doc, target);
            }

            @Override
            public Object objectVal(int doc) {
                return ifVals.boolVal(doc) ? trueVals.objectVal(doc) : falseVals.objectVal(doc);
            }

            @Override
            public boolean exists(int doc) {
                return true;
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return super.getValueFiller();
            }

            @Override
            public String toString(int doc) {
                return "if(" + ifVals.toString(doc) + ',' + trueVals.toString(doc) + ',' + falseVals.toString(doc) + ')';
            }
        };
    }

    @Override
    public String description() {
        return "if(" + this.ifSource.description() + ',' + this.trueSource.description() + ',' + this.falseSource + ')';
    }

    @Override
    public int hashCode() {
        int h = this.ifSource.hashCode();
        h = h * 31 + this.trueSource.hashCode();
        h = h * 31 + this.falseSource.hashCode();
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IfFunction)) {
            return false;
        }
        IfFunction other = (IfFunction)o;
        return this.ifSource.equals(other.ifSource) && this.trueSource.equals(other.trueSource) && this.falseSource.equals(other.falseSource);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.ifSource.createWeight(context, searcher);
        this.trueSource.createWeight(context, searcher);
        this.falseSource.createWeight(context, searcher);
    }
}

