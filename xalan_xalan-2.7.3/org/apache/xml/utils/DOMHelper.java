/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.NSInfo;
import org.apache.xml.utils.StringBufferPool;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMHelper {
    Hashtable m_NSInfos = new Hashtable();
    protected static final NSInfo m_NSInfoUnProcWithXMLNS = new NSInfo(false, true);
    protected static final NSInfo m_NSInfoUnProcWithoutXMLNS = new NSInfo(false, false);
    protected static final NSInfo m_NSInfoUnProcNoAncestorXMLNS = new NSInfo(false, false, 2);
    protected static final NSInfo m_NSInfoNullWithXMLNS = new NSInfo(true, true);
    protected static final NSInfo m_NSInfoNullWithoutXMLNS = new NSInfo(true, false);
    protected static final NSInfo m_NSInfoNullNoAncestorXMLNS = new NSInfo(true, false, 2);
    protected Vector m_candidateNoAncestorXMLNS = new Vector();
    protected Document m_DOMFactory = null;

    public static Document createDocument(boolean isSecureProcessing) {
        try {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            dfactory.setValidating(true);
            if (isSecureProcessing) {
                try {
                    dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                }
                catch (ParserConfigurationException parserConfigurationException) {
                    // empty catch block
                }
            }
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Document outNode = docBuilder.newDocument();
            return outNode;
        }
        catch (ParserConfigurationException pce) {
            throw new RuntimeException(XMLMessages.createXMLMessage("ER_CREATEDOCUMENT_NOT_SUPPORTED", null));
        }
    }

    public static Document createDocument() {
        return DOMHelper.createDocument(false);
    }

    public boolean shouldStripSourceNode(Node textNode) throws TransformerException {
        return false;
    }

    public String getUniqueID(Node node) {
        return "N" + Integer.toHexString(node.hashCode()).toUpperCase();
    }

    public static boolean isNodeAfter(Node node1, Node node2) {
        Node parent2;
        if (node1 == node2 || DOMHelper.isNodeTheSame(node1, node2)) {
            return true;
        }
        boolean isNodeAfter = true;
        Node parent1 = DOMHelper.getParentOfNode(node1);
        if (parent1 == (parent2 = DOMHelper.getParentOfNode(node2)) || DOMHelper.isNodeTheSame(parent1, parent2)) {
            if (null != parent1) {
                isNodeAfter = DOMHelper.isNodeAfterSibling(parent1, node1, node2);
            }
        } else {
            int i;
            int adjust;
            int nParents1 = 2;
            int nParents2 = 2;
            while (parent1 != null) {
                ++nParents1;
                parent1 = DOMHelper.getParentOfNode(parent1);
            }
            while (parent2 != null) {
                ++nParents2;
                parent2 = DOMHelper.getParentOfNode(parent2);
            }
            Node startNode1 = node1;
            Node startNode2 = node2;
            if (nParents1 < nParents2) {
                adjust = nParents2 - nParents1;
                for (i = 0; i < adjust; ++i) {
                    startNode2 = DOMHelper.getParentOfNode(startNode2);
                }
            } else if (nParents1 > nParents2) {
                adjust = nParents1 - nParents2;
                for (i = 0; i < adjust; ++i) {
                    startNode1 = DOMHelper.getParentOfNode(startNode1);
                }
            }
            Node prevChild1 = null;
            Node prevChild2 = null;
            while (null != startNode1) {
                if (startNode1 == startNode2 || DOMHelper.isNodeTheSame(startNode1, startNode2)) {
                    if (null == prevChild1) {
                        isNodeAfter = nParents1 < nParents2;
                        break;
                    }
                    isNodeAfter = DOMHelper.isNodeAfterSibling(startNode1, prevChild1, prevChild2);
                    break;
                }
                prevChild1 = startNode1;
                startNode1 = DOMHelper.getParentOfNode(startNode1);
                prevChild2 = startNode2;
                startNode2 = DOMHelper.getParentOfNode(startNode2);
            }
        }
        return isNodeAfter;
    }

    public static boolean isNodeTheSame(Node node1, Node node2) {
        if (node1 instanceof DTMNodeProxy && node2 instanceof DTMNodeProxy) {
            return ((DTMNodeProxy)node1).equals((DTMNodeProxy)node2);
        }
        return node1 == node2;
    }

    private static boolean isNodeAfterSibling(Node parent, Node child1, Node child2) {
        boolean isNodeAfterSibling = false;
        short child1type = child1.getNodeType();
        short child2type = child2.getNodeType();
        if (2 != child1type && 2 == child2type) {
            isNodeAfterSibling = false;
        } else if (2 == child1type && 2 != child2type) {
            isNodeAfterSibling = true;
        } else if (2 == child1type) {
            NamedNodeMap children = parent.getAttributes();
            int nNodes = children.getLength();
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < nNodes; ++i) {
                Node child = children.item(i);
                if (child1 == child || DOMHelper.isNodeTheSame(child1, child)) {
                    if (found2) {
                        isNodeAfterSibling = false;
                        break;
                    }
                    found1 = true;
                    continue;
                }
                if (child2 != child && !DOMHelper.isNodeTheSame(child2, child)) continue;
                if (found1) {
                    isNodeAfterSibling = true;
                    break;
                }
                found2 = true;
            }
        } else {
            boolean found1 = false;
            boolean found2 = false;
            for (Node child = parent.getFirstChild(); null != child; child = child.getNextSibling()) {
                if (child1 == child || DOMHelper.isNodeTheSame(child1, child)) {
                    if (found2) {
                        isNodeAfterSibling = false;
                        break;
                    }
                    found1 = true;
                    continue;
                }
                if (child2 != child && !DOMHelper.isNodeTheSame(child2, child)) continue;
                if (found1) {
                    isNodeAfterSibling = true;
                    break;
                }
                found2 = true;
            }
        }
        return isNodeAfterSibling;
    }

    public short getLevel(Node n) {
        short level = 1;
        while (null != (n = DOMHelper.getParentOfNode(n))) {
            level = (short)(level + 1);
        }
        return level;
    }

    public String getNamespaceForPrefix(String prefix, Element namespaceContext) {
        Node parent = namespaceContext;
        String namespace = null;
        if (prefix.equals("xml")) {
            namespace = "http://www.w3.org/XML/1998/namespace";
        } else if (prefix.equals("xmlns")) {
            namespace = "http://www.w3.org/2000/xmlns/";
        } else {
            short type;
            String declname;
            String string = declname = prefix == "" ? "xmlns" : "xmlns:" + prefix;
            while (null != parent && null == namespace && ((type = parent.getNodeType()) == 1 || type == 5)) {
                Attr attr;
                if (type == 1 && (attr = parent.getAttributeNode(declname)) != null) {
                    namespace = attr.getNodeValue();
                    break;
                }
                parent = DOMHelper.getParentOfNode(parent);
            }
        }
        return namespace;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String getNamespaceOfNode(Node n) {
        String prefix;
        boolean hasProcessedNS;
        NSInfo nsInfo;
        short ntype = n.getNodeType();
        if (2 != ntype) {
            Object nsObj = this.m_NSInfos.get(n);
            nsInfo = nsObj == null ? null : (NSInfo)nsObj;
            hasProcessedNS = nsInfo == null ? false : nsInfo.m_hasProcessedNS;
        } else {
            hasProcessedNS = false;
            nsInfo = null;
        }
        if (hasProcessedNS) {
            return nsInfo.m_namespace;
        }
        String namespaceOfPrefix = null;
        String nodeName = n.getNodeName();
        int indexOfNSSep = nodeName.indexOf(58);
        if (2 == ntype) {
            if (indexOfNSSep <= 0) return namespaceOfPrefix;
            prefix = nodeName.substring(0, indexOfNSSep);
        } else {
            prefix = indexOfNSSep >= 0 ? nodeName.substring(0, indexOfNSSep) : "";
        }
        boolean ancestorsHaveXMLNS = false;
        boolean nHasXMLNS = false;
        if (prefix.equals("xml")) {
            namespaceOfPrefix = "http://www.w3.org/XML/1998/namespace";
        } else {
            Node parent = n;
            while (null != parent && null == namespaceOfPrefix && (null == nsInfo || nsInfo.m_ancestorHasXMLNSAttrs != 2)) {
                short parentType = parent.getNodeType();
                if (null == nsInfo || nsInfo.m_hasXMLNSAttrs) {
                    boolean elementHasXMLNS = false;
                    if (parentType == 1) {
                        NamedNodeMap nnm = parent.getAttributes();
                        for (int i = 0; i < nnm.getLength(); ++i) {
                            String p;
                            Node attr = nnm.item(i);
                            String aname = attr.getNodeName();
                            if (aname.charAt(0) != 'x') continue;
                            boolean isPrefix = aname.startsWith("xmlns:");
                            if (!aname.equals("xmlns") && !isPrefix) continue;
                            if (n == parent) {
                                nHasXMLNS = true;
                            }
                            elementHasXMLNS = true;
                            ancestorsHaveXMLNS = true;
                            String string = p = isPrefix ? aname.substring(6) : "";
                            if (!p.equals(prefix)) continue;
                            namespaceOfPrefix = attr.getNodeValue();
                            break;
                        }
                    }
                    if (2 != parentType && null == nsInfo && n != parent) {
                        nsInfo = elementHasXMLNS ? m_NSInfoUnProcWithXMLNS : m_NSInfoUnProcWithoutXMLNS;
                        this.m_NSInfos.put(parent, nsInfo);
                    }
                }
                if (2 == parentType) {
                    parent = DOMHelper.getParentOfNode(parent);
                } else {
                    this.m_candidateNoAncestorXMLNS.addElement(parent);
                    this.m_candidateNoAncestorXMLNS.addElement(nsInfo);
                    parent = parent.getParentNode();
                }
                if (null == parent) continue;
                Object nsObj = this.m_NSInfos.get(parent);
                nsInfo = nsObj == null ? null : (NSInfo)nsObj;
            }
            int nCandidates = this.m_candidateNoAncestorXMLNS.size();
            if (nCandidates > 0) {
                if (!ancestorsHaveXMLNS && null == parent) {
                    for (int i = 0; i < nCandidates; i += 2) {
                        Object candidateInfo = this.m_candidateNoAncestorXMLNS.elementAt(i + 1);
                        if (candidateInfo == m_NSInfoUnProcWithoutXMLNS) {
                            this.m_NSInfos.put(this.m_candidateNoAncestorXMLNS.elementAt(i), m_NSInfoUnProcNoAncestorXMLNS);
                            continue;
                        }
                        if (candidateInfo != m_NSInfoNullWithoutXMLNS) continue;
                        this.m_NSInfos.put(this.m_candidateNoAncestorXMLNS.elementAt(i), m_NSInfoNullNoAncestorXMLNS);
                    }
                }
                this.m_candidateNoAncestorXMLNS.removeAllElements();
            }
        }
        if (2 == ntype) return namespaceOfPrefix;
        if (null == namespaceOfPrefix) {
            if (ancestorsHaveXMLNS) {
                if (nHasXMLNS) {
                    this.m_NSInfos.put(n, m_NSInfoNullWithXMLNS);
                    return namespaceOfPrefix;
                } else {
                    this.m_NSInfos.put(n, m_NSInfoNullWithoutXMLNS);
                }
                return namespaceOfPrefix;
            } else {
                this.m_NSInfos.put(n, m_NSInfoNullNoAncestorXMLNS);
            }
            return namespaceOfPrefix;
        } else {
            this.m_NSInfos.put(n, new NSInfo(namespaceOfPrefix, nHasXMLNS));
        }
        return namespaceOfPrefix;
    }

    public String getLocalNameOfNode(Node n) {
        String qname = n.getNodeName();
        int index = qname.indexOf(58);
        return index < 0 ? qname : qname.substring(index + 1);
    }

    public String getExpandedElementName(Element elem) {
        String namespace = this.getNamespaceOfNode(elem);
        return null != namespace ? namespace + ":" + this.getLocalNameOfNode(elem) : this.getLocalNameOfNode(elem);
    }

    public String getExpandedAttributeName(Attr attr) {
        String namespace = this.getNamespaceOfNode(attr);
        return null != namespace ? namespace + ":" + this.getLocalNameOfNode(attr) : this.getLocalNameOfNode(attr);
    }

    public boolean isIgnorableWhitespace(Text node) {
        boolean isIgnorable = false;
        return isIgnorable;
    }

    public Node getRoot(Node node) {
        Node root = null;
        while (node != null) {
            root = node;
            node = DOMHelper.getParentOfNode(node);
        }
        return root;
    }

    public Node getRootNode(Node n) {
        short nt = n.getNodeType();
        return 9 == nt || 11 == nt ? n : n.getOwnerDocument();
    }

    public boolean isNamespaceNode(Node n) {
        if (2 == n.getNodeType()) {
            String attrName = n.getNodeName();
            return attrName.startsWith("xmlns:") || attrName.equals("xmlns");
        }
        return false;
    }

    public static Node getParentOfNode(Node node) throws RuntimeException {
        Node parent;
        short nodeType = node.getNodeType();
        if (2 == nodeType) {
            Document doc = node.getOwnerDocument();
            DOMImplementation impl = doc.getImplementation();
            if (impl != null && impl.hasFeature("Core", "2.0")) {
                Element parent2 = ((Attr)node).getOwnerElement();
                return parent2;
            }
            Element rootElem = doc.getDocumentElement();
            if (null == rootElem) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT", null));
            }
            parent = DOMHelper.locateAttrParent(rootElem, node);
        } else {
            parent = node.getParentNode();
        }
        return parent;
    }

    public Element getElementByID(String id, Document doc) {
        return null;
    }

    public String getUnparsedEntityURI(String name, Document doc) {
        String url = "";
        DocumentType doctype = doc.getDoctype();
        if (null != doctype) {
            NamedNodeMap entities = doctype.getEntities();
            if (null == entities) {
                return url;
            }
            Entity entity = (Entity)entities.getNamedItem(name);
            if (null == entity) {
                return url;
            }
            String notationName = entity.getNotationName();
            if (null != notationName && null == (url = entity.getSystemId())) {
                url = entity.getPublicId();
            }
        }
        return url;
    }

    private static Node locateAttrParent(Element elem, Node attr) {
        Node parent = null;
        Attr check = elem.getAttributeNode(attr.getNodeName());
        if (check == attr) {
            parent = elem;
        }
        if (null == parent) {
            for (Node node = elem.getFirstChild(); null != node && (1 != node.getNodeType() || null == (parent = DOMHelper.locateAttrParent((Element)node, attr))); node = node.getNextSibling()) {
            }
        }
        return parent;
    }

    public void setDOMFactory(Document domFactory) {
        this.m_DOMFactory = domFactory;
    }

    public Document getDOMFactory() {
        if (null == this.m_DOMFactory) {
            this.m_DOMFactory = DOMHelper.createDocument();
        }
        return this.m_DOMFactory;
    }

    public static String getNodeData(Node node) {
        String s;
        FastStringBuffer buf = StringBufferPool.get();
        try {
            DOMHelper.getNodeData(node, buf);
            s = buf.length() > 0 ? buf.toString() : "";
        }
        finally {
            StringBufferPool.free(buf);
        }
        return s;
    }

    public static void getNodeData(Node node, FastStringBuffer buf) {
        switch (node.getNodeType()) {
            case 1: 
            case 9: 
            case 11: {
                for (Node child = node.getFirstChild(); null != child; child = child.getNextSibling()) {
                    DOMHelper.getNodeData(child, buf);
                }
                break;
            }
            case 3: 
            case 4: {
                buf.append(node.getNodeValue());
                break;
            }
            case 2: {
                buf.append(node.getNodeValue());
                break;
            }
            case 7: {
                break;
            }
        }
    }
}

