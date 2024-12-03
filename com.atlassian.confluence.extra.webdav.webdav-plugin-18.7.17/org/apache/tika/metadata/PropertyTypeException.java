/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public final class PropertyTypeException
extends IllegalArgumentException {
    public PropertyTypeException(String msg) {
        super(msg);
    }

    public PropertyTypeException(Property.PropertyType expected, Property.PropertyType found) {
        super("Expected a property of type " + (Object)((Object)expected) + ", but received " + (Object)((Object)found));
    }

    public PropertyTypeException(Property.ValueType expected, Property.ValueType found) {
        super("Expected a property with a " + (Object)((Object)expected) + " value, but received a " + (Object)((Object)found));
    }

    public PropertyTypeException(Property.PropertyType unsupportedPropertyType) {
        super(unsupportedPropertyType != Property.PropertyType.COMPOSITE ? (Object)((Object)unsupportedPropertyType) + " is not supported" : "Composite Properties must not include other Composite Properties as either Primary or Secondary");
    }
}

