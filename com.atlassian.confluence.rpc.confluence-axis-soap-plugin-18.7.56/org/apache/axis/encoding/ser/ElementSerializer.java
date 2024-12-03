/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class ElementSerializer
implements Serializer {
    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        boolean writeWrapper;
        if (!(value instanceof Element)) {
            throw new IOException(Messages.getMessage("cantSerialize01"));
        }
        MessageContext mc = context.getMessageContext();
        context.setWriteXMLType(null);
        boolean bl = writeWrapper = mc == null || mc.isPropertyTrue("writeWrapperForElements", true);
        if (writeWrapper) {
            context.startElement(name, attributes);
        }
        context.writeDOMElement((Element)value);
        if (writeWrapper) {
            context.endElement();
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}

