/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.ComplexPropertyContainer;

public abstract class AbstractComplexProperty
extends AbstractField {
    private final ComplexPropertyContainer container = new ComplexPropertyContainer();
    private final Map<String, String> namespaceToPrefix = new HashMap<String, String>();

    public AbstractComplexProperty(XMPMetadata metadata, String propertyName) {
        super(metadata, propertyName);
    }

    public void addNamespace(String namespace, String prefix) {
        this.namespaceToPrefix.put(namespace, prefix);
    }

    public String getNamespacePrefix(String namespace) {
        return this.namespaceToPrefix.get(namespace);
    }

    public Map<String, String> getAllNamespacesWithPrefix() {
        return this.namespaceToPrefix;
    }

    public final void addProperty(AbstractField obj) {
        if (!(this instanceof ArrayProperty)) {
            this.container.removePropertiesByName(obj.getPropertyName());
        }
        this.container.addProperty(obj);
    }

    public final void removeProperty(AbstractField property) {
        this.container.removeProperty(property);
    }

    public final ComplexPropertyContainer getContainer() {
        return this.container;
    }

    public final List<AbstractField> getAllProperties() {
        return this.container.getAllProperties();
    }

    public final AbstractField getProperty(String fieldName) {
        List<AbstractField> list = this.container.getPropertiesByLocalName(fieldName);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    public final ArrayProperty getArrayProperty(String fieldName) {
        List<AbstractField> list = this.container.getPropertiesByLocalName(fieldName);
        if (list == null) {
            return null;
        }
        return (ArrayProperty)list.get(0);
    }

    protected final AbstractField getFirstEquivalentProperty(String localName, Class<? extends AbstractField> type) {
        return this.container.getFirstEquivalentProperty(localName, type);
    }
}

