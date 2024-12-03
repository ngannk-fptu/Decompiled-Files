/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class QNameDeserializer
extends SimpleDeserializer {
    private DeserializationContext context = null;

    public QNameDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) {
        int colon = (source = source.trim()).lastIndexOf(":");
        String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
        String localPart = colon < 0 ? source : source.substring(colon + 1);
        return new QName(namespace, localPart);
    }

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        this.context = context;
    }
}

