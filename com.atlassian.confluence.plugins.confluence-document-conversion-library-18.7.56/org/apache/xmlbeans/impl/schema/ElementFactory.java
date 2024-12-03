/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlOptions;

public class ElementFactory<T> {
    private final SchemaType type;
    private final SchemaTypeSystem typeSystem;

    public ElementFactory(SchemaTypeSystem typeSystem, String typeHandle) {
        this.typeSystem = typeSystem;
        this.type = (SchemaType)typeSystem.resolveHandle(typeHandle);
    }

    public SchemaType getType() {
        return this.type;
    }

    public SchemaTypeSystem getTypeLoader() {
        return this.typeSystem;
    }

    public T newInstance() {
        return (T)this.getTypeLoader().newInstance(this.type, null);
    }

    public T newInstance(XmlOptions options) {
        return (T)this.getTypeLoader().newInstance(this.type, options);
    }
}

