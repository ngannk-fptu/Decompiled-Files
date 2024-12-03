/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.Attribute;

public abstract class AbstractField {
    private final XMPMetadata metadata;
    private String propertyName;
    private final Map<String, Attribute> attributes;

    public AbstractField(XMPMetadata metadata, String propertyName) {
        this.metadata = metadata;
        this.propertyName = propertyName;
        this.attributes = new HashMap<String, Attribute>();
    }

    public final String getPropertyName() {
        return this.propertyName;
    }

    public final void setPropertyName(String value) {
        this.propertyName = value;
    }

    public final void setAttribute(Attribute value) {
        this.attributes.put(value.getName(), value);
    }

    public final boolean containsAttribute(String qualifiedName) {
        return this.attributes.containsKey(qualifiedName);
    }

    public final Attribute getAttribute(String qualifiedName) {
        return this.attributes.get(qualifiedName);
    }

    public final List<Attribute> getAllAttributes() {
        return new ArrayList<Attribute>(this.attributes.values());
    }

    public final void removeAttribute(String qualifiedName) {
        this.attributes.remove(qualifiedName);
    }

    public final XMPMetadata getMetadata() {
        return this.metadata;
    }

    public abstract String getNamespace();

    public abstract String getPrefix();
}

