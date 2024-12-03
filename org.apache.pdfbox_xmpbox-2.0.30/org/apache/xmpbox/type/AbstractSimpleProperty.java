/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractField;

public abstract class AbstractSimpleProperty
extends AbstractField {
    private final String namespace;
    private final String prefix;
    private final Object rawValue;

    public AbstractSimpleProperty(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, propertyName);
        this.setValue(value);
        this.namespace = namespaceURI;
        this.prefix = prefix;
        this.rawValue = value;
    }

    public abstract void setValue(Object var1);

    public abstract String getStringValue();

    public abstract Object getValue();

    public Object getRawValue() {
        return this.rawValue;
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + ":" + this.getStringValue() + "]";
    }

    @Override
    public final String getNamespace() {
        return this.namespace;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }
}

