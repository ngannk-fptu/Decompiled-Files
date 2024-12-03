/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.ElementFactory;

public class SimpleTypeFactory<T>
extends ElementFactory<T> {
    public SimpleTypeFactory(SchemaTypeSystem typeSystem, String typeHandle) {
        super(typeSystem, typeHandle);
    }

    public T newValue(Object obj) {
        return (T)this.getType().newValue(obj);
    }
}

