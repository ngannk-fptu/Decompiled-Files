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

@StructuredType(preferedPrefix="pdfaType", namespace="http://www.aiim.org/pdfa/ns/type#")
public class PDFATypeType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String TYPE = "type";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String NS_URI = "namespaceURI";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PREFIX = "prefix";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String DESCRIPTION = "description";
    @PropertyType(type=Types.PDFAField, card=Cardinality.Seq)
    public static final String FIELD = "field";

    public PDFATypeType(XMPMetadata metadata) {
        super(metadata);
    }

    public String getNamespaceURI() {
        URIType tt = (URIType)this.getProperty(NS_URI);
        return tt == null ? null : tt.getStringValue();
    }

    public String getType() {
        TextType tt = (TextType)this.getProperty(TYPE);
        return tt == null ? null : tt.getStringValue();
    }

    public String getPrefixValue() {
        TextType tt = (TextType)this.getProperty(PREFIX);
        return tt == null ? null : tt.getStringValue();
    }

    public String getDescription() {
        TextType tt = (TextType)this.getProperty(DESCRIPTION);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getFields() {
        return this.getArrayProperty(FIELD);
    }
}

