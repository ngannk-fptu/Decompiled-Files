/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URIType;

@StructuredType(preferedPrefix="pdfaSchema", namespace="http://www.aiim.org/pdfa/ns/schema#")
public class PDFASchemaType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String SCHEMA = "schema";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String NAMESPACE_URI = "namespaceURI";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PREFIX = "prefix";
    @PropertyType(type=Types.PDFAProperty, card=Cardinality.Seq)
    public static final String PROPERTY = "property";
    @PropertyType(type=Types.PDFAType, card=Cardinality.Seq)
    public static final String VALUE_TYPE = "valueType";

    public PDFASchemaType(XMPMetadata metadata) {
        super(metadata);
    }

    public String getNamespaceURI() {
        URIType tt = (URIType)this.getProperty(NAMESPACE_URI);
        return tt == null ? null : tt.getStringValue();
    }

    public String getPrefixValue() {
        TextType tt = (TextType)this.getProperty(PREFIX);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getProperty() {
        return this.getArrayProperty(PROPERTY);
    }

    public ArrayProperty getValueType() {
        return this.getArrayProperty(VALUE_TYPE);
    }
}

