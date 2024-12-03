/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.verifiers;

import com.opensymphony.module.propertyset.verifiers.PropertyVerifier;
import com.opensymphony.module.propertyset.verifiers.VerifyException;

public class NumberVerifier
implements PropertyVerifier {
    private Class type;
    private Number max;
    private Number min;

    public void setMax(Number num) {
        this.max = num;
    }

    public Number getMax() {
        return this.max;
    }

    public void setMin(Number num) {
        this.min = num;
    }

    public Number getMin() {
        return this.min;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Class getType() {
        return this.type;
    }

    public void verify(Object value) throws VerifyException {
        Number num = (Number)value;
        if (num.getClass() != this.type) {
            throw new VerifyException("value is of type " + num.getClass() + " expected type is " + this.type);
        }
        if (this.min != null && value != null && this.min.doubleValue() > num.doubleValue()) {
            throw new VerifyException("value " + num.doubleValue() + " < min limit " + this.min.doubleValue());
        }
        if (this.max != null && value != null && this.max.doubleValue() < num.doubleValue()) {
            throw new VerifyException("value " + num.doubleValue() + " > max limit " + this.max.doubleValue());
        }
    }
}

