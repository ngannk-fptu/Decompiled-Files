/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractSimpleProperty;

public class RealType
extends AbstractSimpleProperty {
    private float realValue;

    public RealType(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, namespaceURI, prefix, propertyName, value);
    }

    @Override
    public Float getValue() {
        return Float.valueOf(this.realValue);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Float) {
            this.realValue = ((Float)value).floatValue();
        } else if (value instanceof String) {
            this.realValue = Float.valueOf((String)value).floatValue();
        } else {
            throw new IllegalArgumentException("Value given is not allowed for the Real type: " + value);
        }
    }

    @Override
    public String getStringValue() {
        return Float.toString(this.realValue);
    }
}

