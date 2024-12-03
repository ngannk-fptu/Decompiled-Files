/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DavDocumentBuilderFactory;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.ResultHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DomUtil {
    private static Logger log = LoggerFactory.getLogger(DomUtil.class);
    private static final DavDocumentBuilderFactory BUILDER_FACTORY = new DavDocumentBuilderFactory();
    private static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    public static void setBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
        BUILDER_FACTORY.setFactory(documentBuilderFactory);
    }

    public static Document createDocument() throws ParserConfigurationException {
        return BUILDER_FACTORY.newDocumentBuilder().newDocument();
    }

    public static Document parseDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder docBuilder = BUILDER_FACTORY.newDocumentBuilder();
        return docBuilder.parse(stream);
    }

    public static String getAttribute(Element parent, String localName, Namespace namespace) {
        if (parent == null) {
            return null;
        }
        Attr attribute = namespace == null ? parent.getAttributeNode(localName) : parent.getAttributeNodeNS(namespace.getURI(), localName);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    public static Attr[] getNamespaceAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        ArrayList<Attr> nsAttr = new ArrayList<Attr>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Attr attr = (Attr)attributes.item(i);
            if (!Namespace.XMLNS_NAMESPACE.getURI().equals(attr.getNamespaceURI())) continue;
            nsAttr.add(attr);
        }
        return nsAttr.toArray(new Attr[nsAttr.size()]);
    }

    public static String getText(Element element) {
        StringBuffer content = new StringBuffer();
        if (element != null) {
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node child = nodes.item(i);
                if (!DomUtil.isText(child)) continue;
                content.append(((CharacterData)child).getData());
            }
        }
        return content.length() == 0 ? null : content.toString();
    }

    public static String getText(Element element, String defaultValue) {
        String txt = DomUtil.getText(element);
        return txt == null ? defaultValue : txt;
    }

    public static String getTextTrim(Element element) {
        String txt = DomUtil.getText(element);
        return txt == null ? txt : txt.trim();
    }

    public static String getChildText(Element parent, String childLocalName, Namespace childNamespace) {
        Element child = DomUtil.getChildElement(parent, childLocalName, childNamespace);
        return child == null ? null : DomUtil.getText(child);
    }

    public static String getChildTextTrim(Element parent, String childLocalName, Namespace childNamespace) {
        Element child = DomUtil.getChildElement(parent, childLocalName, childNamespace);
        return child == null ? null : DomUtil.getTextTrim(child);
    }

    public static String getChildTextTrim(Element parent, QName childName) {
        Element child = DomUtil.getChildElement(parent, childName);
        return child == null ? null : DomUtil.getTextTrim(child);
    }

    public static boolean hasChildElement(Node parent, String childLocalName, Namespace childNamespace) {
        return DomUtil.getChildElement(parent, childLocalName, childNamespace) != null;
    }

    public static Element getChildElement(Node parent, String childLocalName, Namespace childNamespace) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!DomUtil.isElement(child) || !DomUtil.matches(child, childLocalName, childNamespace)) continue;
                return (Element)child;
            }
        }
        return null;
    }

    public static Element getChildElement(Node parent, QName childName) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!DomUtil.isElement(child) || !DomUtil.matches(child, childName)) continue;
                return (Element)child;
            }
        }
        return null;
    }

    public static ElementIterator getChildren(Element parent, String childLocalName, Namespace childNamespace) {
        return new ElementIterator(parent, childLocalName, childNamespace);
    }

    public static ElementIterator getChildren(Element parent, QName childName) {
        return new ElementIterator(parent, childName);
    }

    public static ElementIterator getChildren(Element parent) {
        return new ElementIterator(parent);
    }

    public static Element getFirstChildElement(Node parent) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!DomUtil.isElement(child)) continue;
                return (Element)child;
            }
        }
        return null;
    }

    public static boolean hasContent(Node parent) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!DomUtil.isAcceptedNode(child)) continue;
                return true;
            }
        }
        return false;
    }

    public static List<Node> getContent(Node parent) {
        ArrayList<Node> content = new ArrayList<Node>();
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!DomUtil.isAcceptedNode(child)) continue;
                content.add(child);
            }
        }
        return content;
    }

    public static Namespace getNamespace(Element element) {
        String uri = element.getNamespaceURI();
        String prefix = element.getPrefix();
        if (uri == null) {
            return Namespace.EMPTY_NAMESPACE;
        }
        return Namespace.getNamespace(prefix, uri);
    }

    public static boolean matches(Node node, String requiredLocalName, Namespace requiredNamespace) {
        if (node == null) {
            return false;
        }
        boolean matchingNamespace = DomUtil.matchingNamespace(node, requiredNamespace);
        return matchingNamespace && DomUtil.matchingLocalName(node, requiredLocalName);
    }

    public static boolean matches(Node node, QName requiredName) {
        if (node == null) {
            return false;
        }
        String nodens = node.getNamespaceURI() != null ? node.getNamespaceURI() : "";
        return nodens.equals(requiredName.getNamespaceURI()) && node.getLocalName().equals(requiredName.getLocalPart());
    }

    private static boolean matchingNamespace(Node node, Namespace requiredNamespace) {
        if (requiredNamespace == null) {
            return true;
        }
        return requiredNamespace.isSame(node.getNamespaceURI());
    }

    private static boolean matchingLocalName(Node node, String requiredLocalName) {
        if (requiredLocalName == null) {
            return true;
        }
        String localName = node.getLocalName();
        return requiredLocalName.equals(localName);
    }

    private static boolean isAcceptedNode(Node node) {
        return DomUtil.isElement(node) || DomUtil.isText(node);
    }

    static boolean isElement(Node node) {
        return node.getNodeType() == 1;
    }

    static boolean isText(Node node) {
        short ntype = node.getNodeType();
        return ntype == 3 || ntype == 4;
    }

    public static Element createElement(Document factory, String localName, Namespace namespace) {
        if (namespace != null) {
            return factory.createElementNS(namespace.getURI(), DomUtil.getPrefixedName(localName, namespace));
        }
        return factory.createElement(localName);
    }

    public static Element createElement(Document factory, QName elementName) {
        return factory.createElementNS(elementName.getNamespaceURI(), DomUtil.getPrefixedName(elementName));
    }

    public static Element createElement(Document factory, String localName, Namespace namespace, String text) {
        Element elem = DomUtil.createElement(factory, localName, namespace);
        DomUtil.setText(elem, text);
        return elem;
    }

    public static Element addChildElement(Element parent, String localName, Namespace namespace) {
        Element elem = DomUtil.createElement(parent.getOwnerDocument(), localName, namespace);
        parent.appendChild(elem);
        return elem;
    }

    public static Element addChildElement(Node parent, String localName, Namespace namespace) {
        Document doc = parent.getOwnerDocument();
        if (parent instanceof Document) {
            doc = (Document)parent;
        }
        Element elem = DomUtil.createElement(doc, localName, namespace);
        parent.appendChild(elem);
        return elem;
    }

    public static Element addChildElement(Element parent, String localName, Namespace namespace, String text) {
        Element elem = DomUtil.createElement(parent.getOwnerDocument(), localName, namespace, text);
        parent.appendChild(elem);
        return elem;
    }

    public static void setText(Element element, String text) {
        if (text == null || "".equals(text)) {
            return;
        }
        Text txt = element.getOwnerDocument().createTextNode(text);
        element.appendChild(txt);
    }

    public static void setAttribute(Element element, String attrLocalName, Namespace attrNamespace, String attrValue) {
        if (attrNamespace == null) {
            Attr attr = element.getOwnerDocument().createAttribute(attrLocalName);
            attr.setValue(attrValue);
            element.setAttributeNode(attr);
        } else {
            Attr attr = element.getOwnerDocument().createAttributeNS(attrNamespace.getURI(), DomUtil.getPrefixedName(attrLocalName, attrNamespace));
            attr.setValue(attrValue);
            element.setAttributeNodeNS(attr);
        }
    }

    public static void setNamespaceAttribute(Element element, String prefix, String uri) {
        if (Namespace.EMPTY_NAMESPACE.equals(Namespace.getNamespace(prefix, uri))) {
            log.debug("Empty namespace -> omit attribute setting.");
            return;
        }
        DomUtil.setAttribute(element, prefix, Namespace.XMLNS_NAMESPACE, uri);
    }

    public static Element timeoutToXml(long timeout, Document factory) {
        boolean infinite = timeout / 1000L > Integer.MAX_VALUE || timeout == Integer.MAX_VALUE;
        String expString = infinite ? "Infinite" : "Second-" + timeout / 1000L;
        return DomUtil.createElement(factory, "timeout", DavConstants.NAMESPACE, expString);
    }

    public static Element depthToXml(boolean isDeep, Document factory) {
        return DomUtil.depthToXml(isDeep ? "infinity" : "0", factory);
    }

    public static Element depthToXml(String depth, Document factory) {
        return DomUtil.createElement(factory, "depth", DavConstants.NAMESPACE, depth);
    }

    public static Element hrefToXml(String href, Document factory) {
        return DomUtil.createElement(factory, "href", DavConstants.NAMESPACE, href);
    }

    public static String getQualifiedName(String localName, Namespace namespace) {
        return DomUtil.getExpandedName(localName, namespace);
    }

    public static String getExpandedName(String localName, Namespace namespace) {
        if (namespace == null || namespace.equals(Namespace.EMPTY_NAMESPACE)) {
            return localName;
        }
        StringBuffer b = new StringBuffer("{");
        b.append(namespace.getURI()).append("}");
        b.append(localName);
        return b.toString();
    }

    public static String getPrefixedName(String localName, Namespace namespace) {
        return DomUtil.getPrefixName(namespace.getURI(), namespace.getPrefix(), localName);
    }

    public static String getPrefixedName(QName name) {
        return DomUtil.getPrefixName(name.getNamespaceURI(), name.getPrefix(), name.getLocalPart());
    }

    private static String getPrefixName(String namespaceURI, String prefix, String localName) {
        if (namespaceURI == null || prefix == null || "".equals(namespaceURI) || "".equals(prefix)) {
            return localName;
        }
        StringBuffer buf = new StringBuffer(prefix);
        buf.append(":");
        buf.append(localName);
        return buf.toString();
    }

    public static void transformDocument(Document xmlDoc, Writer writer) throws TransformerException, SAXException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.transform(new DOMSource(xmlDoc), ResultHelper.getResult(new StreamResult(writer)));
    }

    public static void transformDocument(Document xmlDoc, OutputStream out) throws TransformerException, SAXException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.transform(new DOMSource(xmlDoc), ResultHelper.getResult(new StreamResult(out)));
    }
}

