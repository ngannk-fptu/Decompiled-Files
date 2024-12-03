/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.parser.XMLParser;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.parser.XMLParserImpl;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XMLUtils {
    private static boolean ignoreLineBreaks = AccessController.doPrivileged(() -> Boolean.getBoolean("org.apache.xml.security.ignoreLineBreaks"));
    private static final Logger LOG = LoggerFactory.getLogger(XMLUtils.class);
    private static XMLParser xmlParserImpl = AccessController.doPrivileged(() -> {
        String xmlParserClass = System.getProperty("org.apache.xml.security.XMLParser");
        if (xmlParserClass != null) {
            try {
                return (XMLParser)JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(xmlParserClass, XMLUtils.class));
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LOG.error("Error instantiating XMLParser. Falling back to XMLParserImpl");
            }
        }
        return new XMLParserImpl();
    });
    private static volatile String dsPrefix = "ds";
    private static volatile String ds11Prefix = "dsig11";
    private static volatile String xencPrefix = "xenc";
    private static volatile String xenc11Prefix = "xenc11";

    private XMLUtils() {
    }

    public static void setDsPrefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        dsPrefix = prefix;
    }

    public static void setDs11Prefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        ds11Prefix = prefix;
    }

    public static void setXencPrefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        xencPrefix = prefix;
    }

    public static void setXenc11Prefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        xenc11Prefix = prefix;
    }

    public static Element getNextElement(Node el) {
        Node node;
        for (node = el; node != null && node.getNodeType() != 1; node = node.getNextSibling()) {
        }
        return (Element)node;
    }

    public static void getSet(Node rootNode, Set<Node> result, Node exclude, boolean com) {
        if (exclude != null && XMLUtils.isDescendantOrSelf(exclude, rootNode)) {
            return;
        }
        XMLUtils.getSetRec(rootNode, result, exclude, com);
    }

    private static void getSetRec(Node rootNode, Set<Node> result, Node exclude, boolean com) {
        if (rootNode == exclude) {
            return;
        }
        switch (rootNode.getNodeType()) {
            case 1: {
                result.add(rootNode);
                Element el = (Element)rootNode;
                if (el.hasAttributes()) {
                    NamedNodeMap nl = el.getAttributes();
                    int length = nl.getLength();
                    for (int i = 0; i < length; ++i) {
                        result.add(nl.item(i));
                    }
                }
            }
            case 9: {
                for (Node r = rootNode.getFirstChild(); r != null; r = r.getNextSibling()) {
                    if (r.getNodeType() == 3) {
                        result.add(r);
                        while (r != null && r.getNodeType() == 3) {
                            r = r.getNextSibling();
                        }
                        if (r == null) {
                            return;
                        }
                    }
                    XMLUtils.getSetRec(r, result, exclude, com);
                }
                break;
            }
            case 8: {
                if (!com) break;
                result.add(rootNode);
                break;
            }
            case 10: {
                break;
            }
            default: {
                result.add(rootNode);
            }
        }
    }

    public static void outputDOM(Node contextNode, OutputStream os) {
        XMLUtils.outputDOM(contextNode, os, false);
    }

    public static void outputDOM(Node contextNode, OutputStream os, boolean addPreamble) {
        try {
            if (addPreamble) {
                os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
            }
            Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical").canonicalizeSubtree(contextNode, os);
        }
        catch (IOException | CanonicalizationException | InvalidCanonicalizerException ex) {
            LOG.debug(ex.getMessage(), (Throwable)ex);
        }
    }

    public static void outputDOMc14nWithComments(Node contextNode, OutputStream os) {
        try {
            Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(contextNode, os);
        }
        catch (CanonicalizationException | InvalidCanonicalizerException ex) {
            LOG.debug(ex.getMessage(), (Throwable)ex);
        }
    }

    public static String getFullTextChildrenFromNode(Node node) {
        StringBuilder sb = new StringBuilder();
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() != 3) continue;
            sb.append(((Text)child).getData());
        }
        return sb.toString();
    }

    public static Element createElementInSignatureSpace(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (dsPrefix == null || dsPrefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", dsPrefix + ":" + elementName);
    }

    public static Element createElementInSignature11Space(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (ds11Prefix == null || ds11Prefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2009/xmldsig11#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2009/xmldsig11#", ds11Prefix + ":" + elementName);
    }

    public static Element createElementInEncryptionSpace(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (xencPrefix == null || xencPrefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2001/04/xmlenc#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2001/04/xmlenc#", xencPrefix + ":" + elementName);
    }

    public static Element createElementInEncryption11Space(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (xenc11Prefix == null || xenc11Prefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2009/xmlenc11#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2009/xmlenc11#", xenc11Prefix + ":" + elementName);
    }

    public static boolean elementIsInSignatureSpace(Element element, String localName) {
        if (element == null) {
            return false;
        }
        return "http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInSignature11Space(Element element, String localName) {
        if (element == null) {
            return false;
        }
        return "http://www.w3.org/2009/xmldsig11#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInEncryptionSpace(Element element, String localName) {
        if (element == null) {
            return false;
        }
        return "http://www.w3.org/2001/04/xmlenc#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInEncryption11Space(Element element, String localName) {
        if (element == null) {
            return false;
        }
        return "http://www.w3.org/2009/xmlenc11#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static Document getOwnerDocument(Node node) {
        if (node.getNodeType() == 9) {
            return (Document)node;
        }
        try {
            return node.getOwnerDocument();
        }
        catch (NullPointerException npe) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + npe.getMessage() + "\"");
        }
    }

    public static Document getOwnerDocument(Set<Node> xpathNodeSet) {
        NullPointerException npe = null;
        for (Node node : xpathNodeSet) {
            short nodeType = node.getNodeType();
            if (nodeType == 9) {
                return (Document)node;
            }
            try {
                if (nodeType == 2) {
                    return ((Attr)node).getOwnerElement().getOwnerDocument();
                }
                return node.getOwnerDocument();
            }
            catch (NullPointerException e) {
                npe = e;
            }
        }
        throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + (npe == null ? "" : npe.getMessage()) + "\"");
    }

    public static void addReturnToElement(Element e) {
        if (!ignoreLineBreaks) {
            Document doc = e.getOwnerDocument();
            e.appendChild(doc.createTextNode("\n"));
        }
    }

    public static void addReturnToElement(Document doc, HelperNodeList nl) {
        if (!ignoreLineBreaks) {
            nl.appendChild(doc.createTextNode("\n"));
        }
    }

    public static void addReturnBeforeChild(Element e, Node child) {
        if (!ignoreLineBreaks) {
            Document doc = e.getOwnerDocument();
            e.insertBefore(doc.createTextNode("\n"), child);
        }
    }

    public static String encodeToString(byte[] bytes) {
        if (ignoreLineBreaks) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return Base64.getMimeEncoder().encodeToString(bytes);
    }

    public static byte[] decode(String encodedString) {
        return Base64.getMimeDecoder().decode(encodedString);
    }

    public static byte[] decode(byte[] encodedBytes) {
        return Base64.getMimeDecoder().decode(encodedBytes);
    }

    public static boolean isIgnoreLineBreaks() {
        return ignoreLineBreaks;
    }

    public static Set<Node> convertNodelistToSet(NodeList xpathNodeSet) {
        if (xpathNodeSet == null) {
            return new HashSet<Node>();
        }
        int length = xpathNodeSet.getLength();
        HashSet<Node> set = new HashSet<Node>(length);
        for (int i = 0; i < length; ++i) {
            set.add(xpathNodeSet.item(i));
        }
        return set;
    }

    public static void circumventBug2650(Document doc) {
        Element documentElement = doc.getDocumentElement();
        Attr xmlnsAttr = documentElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        if (xmlnsAttr == null) {
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
        }
        XMLUtils.circumventBug2650internal(doc);
    }

    private static void circumventBug2650internal(Node node) {
        Node parent = null;
        Node sibling = null;
        String namespaceNs = "http://www.w3.org/2000/xmlns/";
        while (true) {
            switch (node.getNodeType()) {
                case 1: {
                    Element element = (Element)node;
                    if (!element.hasChildNodes()) break;
                    if (element.hasAttributes()) {
                        NamedNodeMap attributes = element.getAttributes();
                        int attributesLength = attributes.getLength();
                        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                            if (child.getNodeType() != 1) continue;
                            Element childElement = (Element)child;
                            for (int i = 0; i < attributesLength; ++i) {
                                Attr currentAttr = (Attr)attributes.item(i);
                                if (!"http://www.w3.org/2000/xmlns/".equals(currentAttr.getNamespaceURI()) || childElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", currentAttr.getLocalName())) continue;
                                childElement.setAttributeNS("http://www.w3.org/2000/xmlns/", currentAttr.getName(), currentAttr.getNodeValue());
                            }
                        }
                    }
                }
                case 5: 
                case 9: {
                    parent = node;
                    sibling = node.getFirstChild();
                }
            }
            while (sibling == null && parent != null) {
                sibling = parent.getNextSibling();
                parent = parent.getParentNode();
            }
            if (sibling == null) {
                return;
            }
            node = sibling;
            sibling = node.getNextSibling();
        }
    }

    public static Element selectDsNode(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2000/09/xmldsig#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element)sibling;
                }
                --number;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectDs11Node(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2009/xmldsig11#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element)sibling;
                }
                --number;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectXencNode(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2001/04/xmlenc#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element)sibling;
                }
                --number;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectNode(Node sibling, String uri, String nodeName, int number) {
        while (sibling != null) {
            if (sibling.getNamespaceURI() != null && sibling.getNamespaceURI().equals(uri) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element)sibling;
                }
                --number;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element[] selectDsNodes(Node sibling, String nodeName) {
        return XMLUtils.selectNodes(sibling, "http://www.w3.org/2000/09/xmldsig#", nodeName);
    }

    public static Element[] selectDs11Nodes(Node sibling, String nodeName) {
        return XMLUtils.selectNodes(sibling, "http://www.w3.org/2009/xmldsig11#", nodeName);
    }

    public static Element[] selectNodes(Node sibling, String uri, String nodeName) {
        ArrayList<Element> list = new ArrayList<Element>();
        while (sibling != null) {
            if (sibling.getNamespaceURI() != null && sibling.getNamespaceURI().equals(uri) && sibling.getLocalName().equals(nodeName)) {
                list.add((Element)sibling);
            }
            sibling = sibling.getNextSibling();
        }
        return list.toArray(new Element[list.size()]);
    }

    public static Set<Node> excludeNodeFromSet(Node signatureElement, Set<Node> inputSet) {
        return inputSet.stream().filter(inputNode -> !XMLUtils.isDescendantOrSelf(signatureElement, inputNode)).collect(Collectors.toSet());
    }

    public static String getStrFromNode(Node xpathnode) {
        if (xpathnode.getNodeType() == 3) {
            StringBuilder sb = new StringBuilder();
            for (Node currentSibling = xpathnode.getParentNode().getFirstChild(); currentSibling != null; currentSibling = currentSibling.getNextSibling()) {
                if (currentSibling.getNodeType() != 3) continue;
                sb.append(((Text)currentSibling).getData());
            }
            return sb.toString();
        }
        if (xpathnode.getNodeType() == 2) {
            return xpathnode.getNodeValue();
        }
        if (xpathnode.getNodeType() == 7) {
            return xpathnode.getNodeValue();
        }
        return null;
    }

    public static boolean isDescendantOrSelf(Node ctx, Node descendantOrSelf) {
        if (ctx == descendantOrSelf) {
            return true;
        }
        Node parent = descendantOrSelf;
        while (parent != null) {
            if (parent == ctx) {
                return true;
            }
            if (parent.getNodeType() == 2) {
                parent = ((Attr)parent).getOwnerElement();
                continue;
            }
            parent = parent.getParentNode();
        }
        return false;
    }

    public static boolean ignoreLineBreaks() {
        return ignoreLineBreaks;
    }

    public static boolean protectAgainstWrappingAttack(Node startNode, String value) {
        String id = value.trim();
        if (!id.isEmpty() && id.charAt(0) == '#') {
            id = id.substring(1);
        }
        Node startParent = null;
        Node processedNode = null;
        Element foundElement = null;
        if (startNode != null) {
            startParent = startNode.getParentNode();
        }
        while (startNode != null) {
            Element se;
            NamedNodeMap attributes;
            if (startNode.getNodeType() == 1 && (attributes = (se = (Element)startNode).getAttributes()) != null) {
                int length = attributes.getLength();
                for (int i = 0; i < length; ++i) {
                    Attr attr = (Attr)attributes.item(i);
                    if (!attr.isId() || !id.equals(attr.getValue())) continue;
                    if (foundElement == null) {
                        foundElement = attr.getOwnerElement();
                        continue;
                    }
                    LOG.debug("Multiple elements with the same 'Id' attribute value!");
                    return false;
                }
            }
            processedNode = startNode;
            if ((startNode = startNode.getFirstChild()) == null) {
                startNode = processedNode.getNextSibling();
            }
            while (startNode == null) {
                if ((processedNode = processedNode.getParentNode()) == startParent) {
                    return true;
                }
                startNode = processedNode.getNextSibling();
            }
        }
        return true;
    }

    public static boolean protectAgainstWrappingAttack(Node startNode, Element knownElement, String value) {
        String id = value.trim();
        if (!id.isEmpty() && id.charAt(0) == '#') {
            id = id.substring(1);
        }
        Node startParent = null;
        Node processedNode = null;
        if (startNode != null) {
            startParent = startNode.getParentNode();
        }
        while (startNode != null) {
            Element se;
            NamedNodeMap attributes;
            if (startNode.getNodeType() == 1 && (attributes = (se = (Element)startNode).getAttributes()) != null) {
                int length = attributes.getLength();
                for (int i = 0; i < length; ++i) {
                    Attr attr = (Attr)attributes.item(i);
                    if (!attr.isId() || !id.equals(attr.getValue()) || se == knownElement) continue;
                    LOG.debug("Multiple elements with the same 'Id' attribute value!");
                    return false;
                }
            }
            processedNode = startNode;
            if ((startNode = startNode.getFirstChild()) == null) {
                startNode = processedNode.getNextSibling();
            }
            while (startNode == null) {
                if ((processedNode = processedNode.getParentNode()) == startParent) {
                    return true;
                }
                startNode = processedNode.getNextSibling();
            }
        }
        return true;
    }

    public static Document read(InputStream inputStream, boolean disallowDocTypeDeclarations) throws XMLParserException {
        return xmlParserImpl.parse(inputStream, disallowDocTypeDeclarations);
    }

    public static byte[] getBytes(BigInteger big, int bitlen) {
        if ((bitlen = bitlen + 7 >> 3 << 3) < big.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        byte[] bigBytes = big.toByteArray();
        if (big.bitLength() % 8 != 0 && big.bitLength() / 8 + 1 == bitlen / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int bigLen = bigBytes.length;
        if (big.bitLength() % 8 == 0) {
            startSrc = 1;
            --bigLen;
        }
        int startDst = bitlen / 8 - bigLen;
        byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);
        return resizedBytes;
    }
}

