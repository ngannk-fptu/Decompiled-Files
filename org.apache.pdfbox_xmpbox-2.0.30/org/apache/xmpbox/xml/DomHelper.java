/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmpbox.xml.XmpParsingException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class DomHelper {
    private DomHelper() {
    }

    public static Element getUniqueElementChild(Element description) throws XmpParsingException {
        NodeList nl = description.getChildNodes();
        int pos = -1;
        for (int i = 0; i < nl.getLength(); ++i) {
            if (!(nl.item(i) instanceof Element)) continue;
            if (pos >= 0) {
                throw new XmpParsingException(XmpParsingException.ErrorType.Undefined, "Found two child elements in " + description);
            }
            pos = i;
        }
        return (Element)nl.item(pos);
    }

    public static Element getFirstChildElement(Element description) throws XmpParsingException {
        NodeList nl = description.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            if (!(nl.item(i) instanceof Element)) continue;
            return (Element)nl.item(i);
        }
        return null;
    }

    public static List<Element> getElementChildren(Element description) throws XmpParsingException {
        NodeList nl = description.getChildNodes();
        ArrayList<Element> ret = new ArrayList<Element>(nl.getLength());
        for (int i = 0; i < nl.getLength(); ++i) {
            if (!(nl.item(i) instanceof Element)) continue;
            ret.add((Element)nl.item(i));
        }
        return ret;
    }

    public static QName getQName(Element element) {
        return new QName(element.getNamespaceURI(), element.getLocalName(), element.getPrefix());
    }

    public static boolean isRdfDescription(Element element) {
        return "rdf".equals(element.getPrefix()) && "Description".equals(element.getLocalName());
    }

    public static boolean isParseTypeResource(Element element) {
        Attr parseType = element.getAttributeNodeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType");
        return parseType != null && "Resource".equals(parseType.getValue());
    }
}

