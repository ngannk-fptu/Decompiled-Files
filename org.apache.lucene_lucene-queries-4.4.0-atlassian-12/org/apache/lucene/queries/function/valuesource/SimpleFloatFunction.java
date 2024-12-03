/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.SingleFunction;

public abstract class SimpleFloatFunction
extends SingleFunction {
    public SimpleFloatFunction(ValueSource source) {
        super(source);
    }

    protected abstract float func(int var1, FunctionValues var2);

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return SimpleFloatFunction.this.func(doc, vals);
            }

            @Override
            public String toString(int doc) {
                return SimpleFloatFunction.this.name() + '(' + vals.toString(doc) + ')';
            }
        };
    }
}

