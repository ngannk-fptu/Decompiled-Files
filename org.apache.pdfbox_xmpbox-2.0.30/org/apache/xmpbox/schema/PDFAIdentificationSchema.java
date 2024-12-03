/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="pdfaid", namespace="http://www.aiim.org/pdfa/ns/id/")
public class PDFAIdentificationSchema
extends XMPSchema {
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String PART = "part";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String AMD = "amd";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String CONFORMANCE = "conformance";

    public PDFAIdentificationSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public PDFAIdentificationSchema(XMPMetadata metadata, String prefix) {
        super(metadata, prefix);
    }

    public void setPartValueWithString(String value) {
        IntegerType part = (IntegerType)this.instanciateSimple(PART, value);
        this.addProperty(part);
    }

    public void setPartValueWithInt(int value) {
        IntegerType part = (IntegerType)this.instanciateSimple(PART, value);
        this.addProperty(part);
    }

    public void setPart(Integer value) {
        this.setPartValueWithInt(value);
    }

    public void setPartProperty(IntegerType part) {
        this.addProperty(part);
    }

    public void setAmd(String value) {
        TextType amd = this.createTextType(AMD, value);
        this.addProperty(amd);
    }

    public void setAmdProperty(TextType amd) {
        this.addProperty(amd);
    }

    public void setConformance(String value) throws BadFieldValueException {
        if (!(value.equals("A") || value.equals("B") || value.equals("U"))) {
            throw new BadFieldValueException("The property given not seems to be a PDF/A conformance level (must be A, B or U)");
        }
        TextType conf = this.createTextType(CONFORMANCE, value);
        this.addProperty(conf);
    }

    public void setConformanceProperty(TextType conf) throws BadFieldValueException {
        String value = conf.getStringValue();
        if (!(value.equals("A") || value.equals("B") || value.equals("U"))) {
            throw new BadFieldValueException("The property given not seems to be a PDF/A conformance level (must be A, B or U)");
        }
        this.addProperty(conf);
    }

    public Integer getPart() {
        IntegerType tmp = this.getPartProperty();
        if (tmp == null) {
            return null;
        }
        return tmp.getValue();
    }

    public IntegerType getPartProperty() {
        AbstractField tmp = this.getProperty(PART);
        if (tmp instanceof IntegerType) {
            return (IntegerType)tmp;
        }
        return null;
    }

    public String getAmendment() {
        AbstractField tmp = this.getProperty(AMD);
        if (tmp instanceof TextType) {
            return ((TextType)tmp).getStringValue();
        }
        return null;
    }

    public TextType getAmdProperty() {
        AbstractField tmp = this.getProperty(AMD);
        if (tmp instanceof TextType) {
            return (TextType)tmp;
        }
        return null;
    }

    public String getAmd() {
        TextType tmp = this.getAmdProperty();
        if (tmp == null) {
            for (Attribute attribute : this.getAllAttributes()) {
                if (!attribute.getName().equals(AMD)) continue;
                return attribute.getValue();
            }
            return null;
        }
        return tmp.getStringValue();
    }

    public TextType getConformanceProperty() {
        AbstractField tmp = this.getProperty(CONFORMANCE);
        if (tmp instanceof TextType) {
            return (TextType)tmp;
        }
        return null;
    }

    public String getConformance() {
        TextType tt = this.getConformanceProperty();
        if (tt == null) {
            for (Attribute attribute : this.getAllAttributes()) {
                if (!attribute.getName().equals(CONFORMANCE)) continue;
                return attribute.getValue();
            }
            return null;
        }
        return tt.getStringValue();
    }
}

