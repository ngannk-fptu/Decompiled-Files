/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="pdf", namespace="http://ns.adobe.com/pdf/1.3/")
public class AdobePDFSchema
extends XMPSchema {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String KEYWORDS = "Keywords";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PDF_VERSION = "PDFVersion";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PRODUCER = "Producer";

    public AdobePDFSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public AdobePDFSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void setKeywords(String value) {
        TextType keywords = this.createTextType(KEYWORDS, value);
        this.addProperty(keywords);
    }

    public void setKeywordsProperty(TextType keywords) {
        this.addProperty(keywords);
    }

    public void setPDFVersion(String value) {
        TextType version = this.createTextType(PDF_VERSION, value);
        this.addProperty(version);
    }

    public void setPDFVersionProperty(TextType version) {
        this.addProperty(version);
    }

    public void setProducer(String value) {
        TextType producer = this.createTextType(PRODUCER, value);
        this.addProperty(producer);
    }

    public void setProducerProperty(TextType producer) {
        this.addProperty(producer);
    }

    public TextType getKeywordsProperty() {
        AbstractField tmp = this.getProperty(KEYWORDS);
        if (tmp instanceof TextType) {
            return (TextType)tmp;
        }
        return null;
    }

    public String getKeywords() {
        AbstractField tmp = this.getProperty(KEYWORDS);
        if (tmp instanceof TextType) {
            return ((TextType)tmp).getStringValue();
        }
        return null;
    }

    public TextType getPDFVersionProperty() {
        AbstractField tmp = this.getProperty(PDF_VERSION);
        if (tmp instanceof TextType) {
            return (TextType)tmp;
        }
        return null;
    }

    public String getPDFVersion() {
        AbstractField tmp = this.getProperty(PDF_VERSION);
        if (tmp instanceof TextType) {
            return ((TextType)tmp).getStringValue();
        }
        return null;
    }

    public TextType getProducerProperty() {
        AbstractField tmp = this.getProperty(PRODUCER);
        if (tmp instanceof TextType) {
            return (TextType)tmp;
        }
        return null;
    }

    public String getProducer() {
        AbstractField tmp = this.getProperty(PRODUCER);
        if (tmp instanceof TextType) {
            return ((TextType)tmp).getStringValue();
        }
        return null;
    }
}

