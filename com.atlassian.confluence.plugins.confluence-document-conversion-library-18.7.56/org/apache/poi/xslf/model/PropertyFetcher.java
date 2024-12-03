/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.model;

import org.apache.poi.util.Internal;
import org.apache.poi.xslf.usermodel.XSLFShape;

@Internal
public abstract class PropertyFetcher<T> {
    private T _value;
    private boolean isSet = false;

    public abstract boolean fetch(XSLFShape var1);

    public T getValue() {
        return this._value;
    }

    public void setValue(T val) {
        this._value = val;
        this.isSet = true;
    }

    public boolean isSet() {
        return this.isSet;
    }
}

