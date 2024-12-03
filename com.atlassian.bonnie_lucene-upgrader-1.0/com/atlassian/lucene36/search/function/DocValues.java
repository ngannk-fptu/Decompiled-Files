/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.search.Explanation;

public abstract class DocValues {
    private float minVal = Float.NaN;
    private float maxVal = Float.NaN;
    private float avgVal = Float.NaN;
    private boolean computed = false;

    public abstract float floatVal(int var1);

    public int intVal(int doc) {
        return (int)this.floatVal(doc);
    }

    public long longVal(int doc) {
        return (long)this.floatVal(doc);
    }

    public double doubleVal(int doc) {
        return this.floatVal(doc);
    }

    public String strVal(int doc) {
        return Float.toString(this.floatVal(doc));
    }

    public abstract String toString(int var1);

    public Explanation explain(int doc) {
        return new Explanation(this.floatVal(doc), this.toString(doc));
    }

    Object getInnerArray() {
        throw new UnsupportedOperationException("this optional method is for test purposes only");
    }

    private void compute() {
        if (this.computed) {
            return;
        }
        float sum = 0.0f;
        int n = 0;
        while (true) {
            float val;
            try {
                val = this.floatVal(n);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
            sum += val;
            this.minVal = Float.isNaN(this.minVal) ? val : Math.min(this.minVal, val);
            this.maxVal = Float.isNaN(this.maxVal) ? val : Math.max(this.maxVal, val);
            ++n;
        }
        this.avgVal = n == 0 ? Float.NaN : sum / (float)n;
        this.computed = true;
    }

    public float getMinValue() {
        this.compute();
        return this.minVal;
    }

    public float getMaxValue() {
        this.compute();
        return this.maxVal;
    }

    public float getAverageValue() {
        this.compute();
        return this.avgVal;
    }
}

