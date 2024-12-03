/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractSimpleProperty;

public class TextType
extends AbstractSimpleProperty {
    private String textValue;

    public TextType(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, namespaceURI, prefix, propertyName, value);
    }

    @Override
    public void setValue(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Value given is not allowed for the Text type : '" + value + "'");
        }
        this.textValue = (String)value;
    }

    @Override
    public String getStringValue() {
        return this.textValue;
    }

    @Override
    public Object getValue() {
        return this.textValue;
    }
}

