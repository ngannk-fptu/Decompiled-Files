/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractSimpleProperty;

public class IntegerType
extends AbstractSimpleProperty {
    private int integerValue;

    public IntegerType(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, namespaceURI, prefix, propertyName, value);
    }

    @Override
    public Integer getValue() {
        return this.integerValue;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer) {
            this.integerValue = (Integer)value;
        } else if (value instanceof String) {
            this.integerValue = Integer.parseInt((String)value);
        } else {
            throw new IllegalArgumentException("Value given is not allowed for the Integer type: " + value);
        }
    }

    @Override
    public String getStringValue() {
        return Integer.toString(this.integerValue);
    }
}

