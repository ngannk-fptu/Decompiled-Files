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
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.ConstNumberSource;

public class ConstValueSource
extends ConstNumberSource {
    final float constant;
    private final double dv;

    public ConstValueSource(float constant) {
        this.constant = constant;
        this.dv = constant;
    }

    @Override
    public String description() {
        return "const(" + this.constant + ")";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return ConstValueSource.this.constant;
            }

            @Override
            public int intVal(int doc) {
                return (int)ConstValueSource.this.constant;
            }

            @Override
            public long longVal(int doc) {
                return (long)ConstValueSource.this.constant;
            }

            @Override
            public double doubleVal(int doc) {
                return ConstValueSource.this.dv;
            }

            @Override
            public String toString(int doc) {
                return ConstValueSource.this.description();
            }

            @Override
            public Object objectVal(int doc) {
                return Float.valueOf(ConstValueSource.this.constant);
            }

            @Override
            public boolean boolVal(int doc) {
                return ConstValueSource.this.constant != 0.0f;
            }
        };
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.constant) * 31;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstValueSource)) {
            return false;
        }
        ConstValueSource other = (ConstValueSource)o;
        return this.constant == other.constant;
    }

    @Override
    public int getInt() {
        return (int)this.constant;
    }

    @Override
    public long getLong() {
        return (long)this.constant;
    }

    @Override
    public float getFloat() {
        return this.constant;
    }

    @Override
    public double getDouble() {
        return this.dv;
    }

    @Override
    public Number getNumber() {
        return Float.valueOf(this.constant);
    }

    @Override
    public boolean getBool() {
        return this.constant != 0.0f;
    }
}

