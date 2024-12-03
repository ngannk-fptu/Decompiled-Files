/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class QNameSerializer
implements SimpleValueSerializer {
    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        String qnameString = this.getValueAsString(value, context);
        context.startElement(name, attributes);
        context.writeString(qnameString);
        context.endElement();
    }

    public static String qName2String(QName qname, SerializationContext context) {
        String namespace;
        String str = context.qName2String(qname);
        if (str == qname.getLocalPart() && (namespace = qname.getNamespaceURI()) != null && namespace.length() > 0) {
            String prefix = context.getPrefixForURI(qname.getNamespaceURI(), null, true);
            return prefix + ":" + str;
        }
        return str;
    }

    public String getValueAsString(Object value, SerializationContext context) {
        return QNameSerializer.qName2String((QName)value, context);
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}

