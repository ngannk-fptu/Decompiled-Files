/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="pdfaExtension", namespace="http://www.aiim.org/pdfa/ns/extension/")
public class PDFAExtensionSchema
extends XMPSchema {
    @PropertyType(type=Types.PDFASchema, card=Cardinality.Bag)
    public static final String SCHEMAS = "schemas";

    public PDFAExtensionSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public PDFAExtensionSchema(XMPMetadata metadata, String prefix) {
        super(metadata, prefix);
    }

    public ArrayProperty getSchemasProperty() {
        return (ArrayProperty)this.getProperty(SCHEMAS);
    }
}

