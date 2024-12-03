/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;

public class DocumentFactory<T>
extends AbstractDocumentFactory<T> {
    public DocumentFactory(SchemaTypeSystem typeSystem, String typeHandle) {
        super(typeSystem, typeHandle);
    }

    @Override
    public T newInstance() {
        return (T)this.getTypeLoader().newInstance(this.getType(), null);
    }

    @Override
    public T newInstance(XmlOptions options) {
        return (T)this.getTypeLoader().newInstance(this.getType(), options);
    }
}

