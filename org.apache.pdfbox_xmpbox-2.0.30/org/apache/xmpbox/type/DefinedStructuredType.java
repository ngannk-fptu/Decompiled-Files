/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.HashMap;
import java.util.Map;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.PropertyType;

public class DefinedStructuredType
extends AbstractStructuredType {
    private Map<String, PropertyType> definedProperties = new HashMap<String, PropertyType>();

    public DefinedStructuredType(XMPMetadata metadata, String namespaceURI, String fieldPrefix, String propertyName) {
        super(metadata, namespaceURI, fieldPrefix, propertyName);
    }

    public DefinedStructuredType(XMPMetadata metadata) {
        super(metadata);
    }

    public void addProperty(String name, PropertyType type) {
        this.definedProperties.put(name, type);
    }

    public Map<String, PropertyType> getDefinedProperties() {
        return this.definedProperties;
    }
}

