/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class DocumentSerializer
implements Serializer {
    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (!(value instanceof Document)) {
            throw new IOException(Messages.getMessage("cantSerialize01"));
        }
        context.startElement(name, attributes);
        Document document = (Document)value;
        context.writeDOMElement(document.getDocumentElement());
        context.endElement();
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}

