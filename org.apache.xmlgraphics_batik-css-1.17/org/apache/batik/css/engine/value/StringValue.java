/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.AbstractValue;
import org.w3c.dom.DOMException;

public class StringValue
extends AbstractValue {
    protected String value;
    protected short unitType;

    public static String getCssText(short type, String value) {
        switch (type) {
            case 20: {
                return "url(" + value + ')';
            }
            case 19: {
                char q = value.indexOf(34) != -1 ? (char)'\'' : '\"';
                return q + value + q;
            }
        }
        return value;
    }

    public StringValue(short type, String s) {
        this.unitType = type;
        this.value = s;
    }

    @Override
    public short getPrimitiveType() {
        return this.unitType;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StringValue)) {
            return false;
        }
        StringValue v = (StringValue)obj;
        if (this.unitType != v.unitType) {
            return false;
        }
        return this.value.equals(v.value);
    }

    @Override
    public String getCssText() {
        return StringValue.getCssText(this.unitType, this.value);
    }

    @Override
    public String getStringValue() throws DOMException {
        return this.value;
    }

    public String toString() {
        return this.getCssText();
    }
}

