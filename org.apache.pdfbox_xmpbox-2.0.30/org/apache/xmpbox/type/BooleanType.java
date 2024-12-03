/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractSimpleProperty;

public class BooleanType
extends AbstractSimpleProperty {
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    private boolean booleanValue;

    public BooleanType(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, namespaceURI, prefix, propertyName, value);
    }

    @Override
    public Boolean getValue() {
        return this.booleanValue;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.booleanValue = (Boolean)value;
            return;
        } else {
            if (!(value instanceof String)) throw new IllegalArgumentException("Value given is not allowed for the Boolean type.");
            String s = value.toString().trim().toUpperCase();
            if ("TRUE".equals(s)) {
                this.booleanValue = true;
                return;
            } else {
                if (!"FALSE".equals(s)) throw new IllegalArgumentException("Not a valid boolean value : '" + value + "'");
                this.booleanValue = false;
            }
        }
    }

    @Override
    public String getStringValue() {
        return this.booleanValue ? TRUE : FALSE;
    }
}

