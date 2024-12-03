/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.MultiFunction;
import org.apache.lucene.util.BytesRef;

public class DefFunction
extends MultiFunction {
    public DefFunction(List<ValueSource> sources) {
        super(sources);
    }

    @Override
    protected String name() {
        return "def";
    }

    @Override
    public FunctionValues getValues(Map fcontext, AtomicReaderContext readerContext) throws IOException {
        return new MultiFunction.Values(DefFunction.valsArr(this.sources, fcontext, readerContext)){
            final int upto;
            {
                this.upto = this.valsArr.length - 1;
            }

            private FunctionValues get(int doc) {
                for (int i = 0; i < this.upto; ++i) {
                    FunctionValues vals = this.valsArr[i];
                    if (!vals.exists(doc)) continue;
                    return vals;
                }
                return this.valsArr[this.upto];
            }

            @Override
            public byte byteVal(int doc) {
                return this.get(doc).byteVal(doc);
            }

            @Override
            public short shortVal(int doc) {
                return this.get(doc).shortVal(doc);
            }

            @Override
            public float floatVal(int doc) {
                return this.get(doc).floatVal(doc);
            }

            @Override
            public int intVal(int doc) {
                return this.get(doc).intVal(doc);
            }

            @Override
            public long longVal(int doc) {
                return this.get(doc).longVal(doc);
            }

            @Override
            public double doubleVal(int doc) {
                return this.get(doc).doubleVal(doc);
            }

            @Override
            public String strVal(int doc) {
                return this.get(doc).strVal(doc);
            }

            @Override
            public boolean boolVal(int doc) {
                return this.get(doc).boolVal(doc);
            }

            @Override
            public boolean bytesVal(int doc, BytesRef target) {
                return this.get(doc).bytesVal(doc, target);
            }

            @Override
            public Object objectVal(int doc) {
                return this.get(doc).objectVal(doc);
            }

            @Override
            public boolean exists(int doc) {
                for (FunctionValues vals : this.valsArr) {
                    if (!vals.exists(doc)) continue;
                    return true;
                }
                return false;
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return super.getValueFiller();
            }
        };
    }
}

