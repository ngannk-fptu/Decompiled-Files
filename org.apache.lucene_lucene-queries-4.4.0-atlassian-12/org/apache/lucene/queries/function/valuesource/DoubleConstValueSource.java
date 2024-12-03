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
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.queries.function.valuesource.ConstNumberSource;

public class DoubleConstValueSource
extends ConstNumberSource {
    final double constant;
    private final float fv;
    private final long lv;

    public DoubleConstValueSource(double constant) {
        this.constant = constant;
        this.fv = (float)constant;
        this.lv = (long)constant;
    }

    @Override
    public String description() {
        return "const(" + this.constant + ")";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return new DoubleDocValues(this){

            @Override
            public float floatVal(int doc) {
                return DoubleConstValueSource.this.fv;
            }

            @Override
            public int intVal(int doc) {
                return (int)DoubleConstValueSource.this.lv;
            }

            @Override
            public long longVal(int doc) {
                return DoubleConstValueSource.this.lv;
            }

            @Override
            public double doubleVal(int doc) {
                return DoubleConstValueSource.this.constant;
            }

            @Override
            public String strVal(int doc) {
                return Double.toString(DoubleConstValueSource.this.constant);
            }

            @Override
            public Object objectVal(int doc) {
                return DoubleConstValueSource.this.constant;
            }

            @Override
            public String toString(int doc) {
                return DoubleConstValueSource.this.description();
            }
        };
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToRawLongBits(this.constant);
        return (int)(bits ^ bits >>> 32);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoubleConstValueSource)) {
            return false;
        }
        DoubleConstValueSource other = (DoubleConstValueSource)o;
        return this.constant == other.constant;
    }

    @Override
    public int getInt() {
        return (int)this.lv;
    }

    @Override
    public long getLong() {
        return this.lv;
    }

    @Override
    public float getFloat() {
        return this.fv;
    }

    @Override
    public double getDouble() {
        return this.constant;
    }

    @Override
    public Number getNumber() {
        return this.constant;
    }

    @Override
    public boolean getBool() {
        return this.constant != 0.0;
    }
}

