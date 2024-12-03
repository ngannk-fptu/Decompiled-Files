/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class MapSerializer
implements Serializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$MapSerializer == null ? (class$org$apache$axis$encoding$ser$MapSerializer = MapSerializer.class$("org.apache.axis.encoding.ser.MapSerializer")) : class$org$apache$axis$encoding$ser$MapSerializer).getName());
    private static final QName QNAME_KEY = new QName("", "key");
    private static final QName QNAME_ITEM = new QName("", "item");
    private static final QName QNAME_VALUE = new QName("", "value");
    private static final QName QNAME_ITEMTYPE = new QName("http://xml.apache.org/xml-soap", "item");
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MapSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (!(value instanceof Map)) {
            throw new IOException(Messages.getMessage("noMap00", "MapSerializer", value.getClass().getName()));
        }
        Map map = (Map)value;
        context.startElement(name, attributes);
        AttributesImpl itemsAttributes = new AttributesImpl();
        String encodingURI = context.getMessageContext().getEncodingStyle();
        String encodingPrefix = context.getPrefixForURI(encodingURI);
        String soapPrefix = context.getPrefixForURI(Constants.SOAP_MAP.getNamespaceURI());
        itemsAttributes.addAttribute(encodingURI, "type", encodingPrefix + ":type", "CDATA", encodingPrefix + ":Array");
        itemsAttributes.addAttribute(encodingURI, "arrayType", encodingPrefix + ":arrayType", "CDATA", soapPrefix + ":item[" + map.size() + "]");
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            context.startElement(QNAME_ITEM, null);
            context.serialize(QNAME_KEY, null, key, null, null, Boolean.TRUE);
            context.serialize(QNAME_VALUE, null, val, null, null, Boolean.TRUE);
            context.endElement();
        }
        context.endElement();
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        complexType.setAttribute("name", "Map");
        Element seq = types.createElement("sequence");
        complexType.appendChild(seq);
        Element element = types.createElement("element");
        element.setAttribute("name", "item");
        element.setAttribute("minOccurs", "0");
        element.setAttribute("maxOccurs", "unbounded");
        element.setAttribute("type", types.getQNameString(new QName("http://xml.apache.org/xml-soap", "mapItem")));
        seq.appendChild(element);
        Element itemType = types.createElement("complexType");
        itemType.setAttribute("name", "mapItem");
        Element seq2 = types.createElement("sequence");
        itemType.appendChild(seq2);
        Element element2 = types.createElement("element");
        element2.setAttribute("name", "key");
        element2.setAttribute("nillable", "true");
        element2.setAttribute("type", "xsd:anyType");
        seq2.appendChild(element2);
        Element element3 = types.createElement("element");
        element3.setAttribute("name", "value");
        element3.setAttribute("nillable", "true");
        element3.setAttribute("type", "xsd:anyType");
        seq2.appendChild(element3);
        types.writeSchemaTypeDecl(QNAME_ITEMTYPE, itemType);
        return complexType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

