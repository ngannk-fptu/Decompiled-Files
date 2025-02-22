/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationValue;

public class ArrayAnnotationValue
extends AnnotationValue {
    private AnnotationValue[] values;

    public ArrayAnnotationValue() {
        super(91);
    }

    public void setValues(AnnotationValue[] values) {
        this.values = values;
    }

    public ArrayAnnotationValue(AnnotationValue[] values) {
        super(91);
        this.values = values;
    }

    public AnnotationValue[] getValues() {
        return this.values;
    }

    @Override
    public String stringify() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < this.values.length; ++i) {
            sb.append(this.values[i].stringify());
            if (i + 1 >= this.values.length) continue;
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < this.values.length; ++i) {
            sb.append(this.values[i].toString());
            if (i + 1 >= this.values.length) continue;
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}

