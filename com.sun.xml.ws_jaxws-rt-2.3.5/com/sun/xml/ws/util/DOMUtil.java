/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
    private static DocumentBuilder db;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document createDom() {
        Class<DOMUtil> clazz = DOMUtil.class;
        synchronized (DOMUtil.class) {
            if (db == null) {
                try {
                    DocumentBuilderFactory dbf = XmlUtil.newDocumentBuilderFactory(true);
                    dbf.setNamespaceAware(true);
                    db = dbf.newDocumentBuilder();
                }
                catch (ParserConfigurationException e) {
                    throw new FactoryConfigurationError(e);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return db.newDocument();
        }
    }

    public static void serializeNode(Element node, XMLStreamWriter writer) throws XMLStreamException {
        DOMUtil.writeTagWithAttributes(node, writer);
        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            block8: for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                switch (child.getNodeType()) {
                    case 7: {
                        writer.writeProcessingInstruction(child.getNodeValue());
                        continue block8;
                    }
                    case 10: {
                        continue block8;
                    }
                    case 4: {
                        writer.writeCData(child.getNodeValue());
                        continue block8;
                    }
                    case 8: {
                        writer.writeComment(child.getNodeValue());
                        continue block8;
                    }
                    case 3: {
                        writer.writeCharacters(child.getNodeValue());
                        continue block8;
                    }
                    case 1: {
                        DOMUtil.serializeNode((Element)child, writer);
                        continue block8;
                    }
                }
            }
        }
        writer.writeEndElement();
    }

    public static void writeTagWithAttributes(Element node, XMLStreamWriter writer) throws XMLStreamException {
        Node attr;
        int i;
        int numOfAttributes;
        NamedNodeMap attrs;
        String nodePrefix = DOMUtil.fixNull(node.getPrefix());
        String nodeNS = DOMUtil.fixNull(node.getNamespaceURI());
        String nodeLocalName = node.getLocalName() == null ? node.getNodeName() : node.getLocalName();
        boolean prefixDecl = DOMUtil.isPrefixDeclared(writer, nodeNS, nodePrefix);
        writer.writeStartElement(nodePrefix, nodeLocalName, nodeNS);
        if (node.hasAttributes()) {
            attrs = node.getAttributes();
            numOfAttributes = attrs.getLength();
            for (i = 0; i < numOfAttributes; ++i) {
                String local;
                attr = attrs.item(i);
                String nsUri = DOMUtil.fixNull(attr.getNamespaceURI());
                if (!nsUri.equals("http://www.w3.org/2000/xmlns/")) continue;
                String string = local = attr.getLocalName().equals("xmlns") ? "" : attr.getLocalName();
                if (local.equals(nodePrefix) && attr.getNodeValue().equals(nodeNS)) {
                    prefixDecl = true;
                }
                if (local.equals("")) {
                    writer.writeDefaultNamespace(attr.getNodeValue());
                    continue;
                }
                writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                writer.writeNamespace(attr.getLocalName(), attr.getNodeValue());
            }
        }
        if (!prefixDecl) {
            writer.writeNamespace(nodePrefix, nodeNS);
        }
        if (node.hasAttributes()) {
            attrs = node.getAttributes();
            numOfAttributes = attrs.getLength();
            for (i = 0; i < numOfAttributes; ++i) {
                attr = attrs.item(i);
                String attrPrefix = DOMUtil.fixNull(attr.getPrefix());
                String attrNS = DOMUtil.fixNull(attr.getNamespaceURI());
                if (attrNS.equals("http://www.w3.org/2000/xmlns/")) continue;
                String localName = attr.getLocalName();
                if (localName == null) {
                    localName = attr.getNodeName();
                }
                boolean attrPrefixDecl = DOMUtil.isPrefixDeclared(writer, attrNS, attrPrefix);
                if (!attrPrefix.equals("") && !attrPrefixDecl) {
                    writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                    writer.writeNamespace(attrPrefix, attrNS);
                }
                writer.writeAttribute(attrPrefix, attrNS, localName, attr.getNodeValue());
            }
        }
    }

    private static boolean isPrefixDeclared(XMLStreamWriter writer, String nsUri, String prefix) {
        boolean prefixDecl = false;
        NamespaceContext nscontext = writer.getNamespaceContext();
        Iterator<String> prefixItr = nscontext.getPrefixes(nsUri);
        while (prefixItr.hasNext()) {
            if (!prefix.equals(prefixItr.next())) continue;
            prefixDecl = true;
            break;
        }
        return prefixDecl;
    }

    public static Element getFirstChild(Element e, String nsUri, String local) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element c;
            if (n.getNodeType() != 1 || !(c = (Element)n).getLocalName().equals(local) || !c.getNamespaceURI().equals(nsUri)) continue;
            return c;
        }
        return null;
    }

    @NotNull
    private static String fixNull(@Nullable String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    @Nullable
    public static Element getFirstElementChild(Node parent) {
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    @NotNull
    public static List<Element> getChildElements(Node parent) {
        ArrayList<Element> elements = new ArrayList<Element>();
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            elements.add((Element)n);
        }
        return elements;
    }
}

