/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class ParserUtil {
    public static String getAttribute(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    public static String getAttribute(XMLStreamReader reader, String nsUri, String name) {
        return reader.getAttributeValue(nsUri, name);
    }

    public static String getAttribute(XMLStreamReader reader, QName name) {
        return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
    }

    public static QName getQName(XMLStreamReader reader, String tag) {
        String localName = XmlUtil.getLocalPart(tag);
        String pfix = XmlUtil.getPrefix(tag);
        String uri = reader.getNamespaceURI(ParserUtil.fixNull(pfix));
        return new QName(uri, localName);
    }

    public static String getMandatoryNonEmptyAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            ParserUtil.failWithLocalName("client.missing.attribute", reader, name);
        } else if (value.equals("")) {
            ParserUtil.failWithLocalName("client.invalidAttributeValue", reader, name);
        }
        return value;
    }

    public static void failWithFullName(String key, XMLStreamReader reader) {
    }

    public static void failWithLocalName(String key, XMLStreamReader reader) {
    }

    public static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
    }

    @NotNull
    private static String fixNull(@Nullable String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}

