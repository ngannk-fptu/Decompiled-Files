/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.xml.sax.Attributes;

public class AttributeSerializationContextImpl
extends SerializationContext {
    SerializationContext parent;

    public AttributeSerializationContextImpl(Writer writer, SerializationContext parent) {
        super(writer);
        this.parent = parent;
    }

    public void startElement(QName qName, Attributes attributes) throws IOException {
    }

    public void endElement() throws IOException {
    }

    public String qName2String(QName qname) {
        return this.parent.qName2String(qname);
    }
}

