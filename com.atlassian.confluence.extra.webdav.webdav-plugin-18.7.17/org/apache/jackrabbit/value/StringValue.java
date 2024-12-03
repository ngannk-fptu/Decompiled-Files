/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;

public class StringValue
extends BaseValue {
    public static final int TYPE = 1;
    private final String text;

    public StringValue(String text) {
        super(1);
        this.text = text;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof StringValue) {
            StringValue other = (StringValue)obj;
            if (this.text == other.text) {
                return true;
            }
            if (this.text != null && other.text != null) {
                return this.text.equals(other.text);
            }
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    protected String getInternalString() throws ValueFormatException {
        if (this.text != null) {
            return this.text;
        }
        throw new ValueFormatException("empty value");
    }
}

