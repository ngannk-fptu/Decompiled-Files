/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.xpath;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.jaxen.BaseXPath;
import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;

public class DocumentNavigator
extends DefaultNavigator {
    private static final long serialVersionUID = 7325116153349780805L;

    public XPath parseXPath(String xpath) throws SAXPathException {
        return new BaseXPath(xpath, this);
    }

    public String getElementNamespaceUri(Object object) {
        OMElement attr = (OMElement)object;
        return attr.getQName().getNamespaceURI();
    }

    public String getElementName(Object object) {
        return ((OMElement)object).getLocalName();
    }

    public String getElementQName(Object object) {
        OMElement attr = (OMElement)object;
        String prefix = null;
        OMNamespace namespace = attr.getNamespace();
        if (namespace != null) {
            prefix = namespace.getPrefix();
        }
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + namespace.getNamespaceURI();
    }

    public String getAttributeNamespaceUri(Object object) {
        OMAttribute attr = (OMAttribute)object;
        return attr.getQName().getNamespaceURI();
    }

    public String getAttributeName(Object object) {
        return ((OMAttribute)object).getLocalName();
    }

    public String getAttributeQName(Object object) {
        OMAttribute attr = (OMAttribute)object;
        String prefix = attr.getNamespace().getPrefix();
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + attr.getLocalName();
    }

    public boolean isDocument(Object object) {
        return object instanceof OMDocument;
    }

    public boolean isElement(Object object) {
        return object instanceof OMElement;
    }

    public boolean isAttribute(Object object) {
        return object instanceof OMAttribute;
    }

    public boolean isNamespace(Object object) {
        return object instanceof OMNamespace;
    }

    public boolean isComment(Object object) {
        return object instanceof OMComment;
    }

    public boolean isText(Object object) {
        return object instanceof OMText;
    }

    public boolean isProcessingInstruction(Object object) {
        return object instanceof OMProcessingInstruction;
    }

    public String getCommentStringValue(Object object) {
        return ((OMComment)object).getValue();
    }

    public String getElementStringValue(Object object) {
        if (this.isElement(object)) {
            return this.getStringValue((OMElement)object, new StringBuffer()).toString();
        }
        return null;
    }

    private StringBuffer getStringValue(OMNode node, StringBuffer buffer) {
        if (this.isText(node)) {
            buffer.append(((OMText)node).getText());
        } else if (node instanceof OMElement) {
            Iterator children = ((OMElement)node).getChildren();
            while (children.hasNext()) {
                this.getStringValue((OMNode)children.next(), buffer);
            }
        }
        return buffer;
    }

    public String getAttributeStringValue(Object object) {
        return ((OMAttribute)object).getAttributeValue();
    }

    public String getNamespaceStringValue(Object object) {
        return ((OMNamespace)object).getNamespaceURI();
    }

    public String getTextStringValue(Object object) {
        return ((OMText)object).getText();
    }

    public String getNamespacePrefix(Object object) {
        return ((OMNamespace)object).getPrefix();
    }

    public Iterator getChildAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMContainer) {
            return ((OMContainer)contextNode).getChildren();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getDescendantAxisIterator(Object object) throws UnsupportedAxisException {
        return super.getDescendantAxisIterator(object);
    }

    public Iterator getAttributeAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (this.isElement(contextNode)) {
            return ((OMElement)contextNode).getAllAttributes();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getNamespaceAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (!(contextNode instanceof OMContainer) || !(contextNode instanceof OMElement)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        OMContainer omContextNode = (OMContainer)contextNode;
        ArrayList<OMNamespaceEx> nsList = new ArrayList<OMNamespaceEx>();
        HashSet<String> prefixes = new HashSet<String>();
        OMContainer context = omContextNode;
        while (context != null && !(context instanceof OMDocument)) {
            OMElement element = (OMElement)context;
            ArrayList declaredNS = new ArrayList();
            Iterator i = element.getAllDeclaredNamespaces();
            while (i != null && i.hasNext()) {
                declaredNS.add(i.next());
            }
            declaredNS.add(element.getNamespace());
            Iterator iter = element.getAllAttributes();
            while (iter != null && iter.hasNext()) {
                OMAttribute attr = (OMAttribute)iter.next();
                OMNamespace namespace = attr.getNamespace();
                if (namespace == null) continue;
                declaredNS.add(namespace);
            }
            iter = declaredNS.iterator();
            while (iter != null && iter.hasNext()) {
                String prefix;
                OMNamespace namespace = (OMNamespace)iter.next();
                if (namespace == null || (prefix = namespace.getPrefix()) == null || prefixes.contains(prefix)) continue;
                prefixes.add(prefix);
                nsList.add(new OMNamespaceEx(namespace, context));
            }
            context = ((OMElement)context).getParent();
        }
        nsList.add(new OMNamespaceEx(omContextNode.getOMFactory().createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml"), omContextNode));
        return nsList.iterator();
    }

    public Iterator getSelfAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getSelfAxisIterator(contextNode);
    }

    public Iterator getDescendantOrSelfAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getDescendantOrSelfAxisIterator(contextNode);
    }

    public Iterator getAncestorOrSelfAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getAncestorOrSelfAxisIterator(contextNode);
    }

    public Iterator getParentAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMNode) {
            return new SingleObjectIterator(((OMNode)contextNode).getParent());
        }
        if (contextNode instanceof OMNamespaceEx) {
            return new SingleObjectIterator(((OMNamespaceEx)contextNode).getParent());
        }
        if (contextNode instanceof OMAttribute) {
            return new SingleObjectIterator(((OMAttribute)contextNode).getOwner());
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getAncestorAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getAncestorAxisIterator(contextNode);
    }

    public Iterator getFollowingSiblingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        ArrayList<Object> list = new ArrayList<Object>();
        if (contextNode != null && contextNode instanceof OMNode) {
            while (contextNode != null && contextNode instanceof OMNode) {
                if ((contextNode = ((OMNode)contextNode).getNextOMSibling()) == null) continue;
                list.add(contextNode);
            }
        }
        return list.iterator();
    }

    public Iterator getPrecedingSiblingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        ArrayList<Object> list = new ArrayList<Object>();
        if (contextNode != null && contextNode instanceof OMNode) {
            while (contextNode != null && contextNode instanceof OMNode) {
                if ((contextNode = ((OMNode)contextNode).getPreviousOMSibling()) == null) continue;
                list.add(contextNode);
            }
        }
        return list.iterator();
    }

    public Iterator getFollowingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getFollowingAxisIterator(contextNode);
    }

    public Iterator getPrecedingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return super.getPrecedingAxisIterator(contextNode);
    }

    public Object getDocument(String uri) throws FunctionCallException {
        InputStream in = null;
        try {
            if (uri.indexOf(58) == -1) {
                in = new FileInputStream(uri);
            } else {
                URL url = new URL(uri);
                in = url.openStream();
            }
            return new StAXOMBuilder(StAXUtils.createXMLStreamReader(in)).getDocument();
        }
        catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {
                    // empty catch block
                }
            }
            throw new FunctionCallException(e);
        }
    }

    public Object getElementById(Object contextNode, String elementId) {
        return super.getElementById(contextNode, elementId);
    }

    public Object getDocumentNode(Object contextNode) {
        if (contextNode instanceof OMDocument) {
            return contextNode;
        }
        OMContainer parent = ((OMNode)contextNode).getParent();
        if (parent == null) {
            return contextNode;
        }
        return this.getDocumentNode(parent);
    }

    public String translateNamespacePrefixToUri(String prefix, Object element) {
        return super.translateNamespacePrefixToUri(prefix, element);
    }

    public String getProcessingInstructionTarget(Object object) {
        return ((OMProcessingInstruction)object).getTarget();
    }

    public String getProcessingInstructionData(Object object) {
        return ((OMProcessingInstruction)object).getValue();
    }

    public short getNodeType(Object node) {
        return super.getNodeType(node);
    }

    public Object getParentNode(Object contextNode) throws UnsupportedAxisException {
        if (contextNode == null || contextNode instanceof OMDocument) {
            return null;
        }
        if (contextNode instanceof OMAttribute) {
            return ((OMAttribute)contextNode).getOwner();
        }
        if (contextNode instanceof OMNamespaceEx) {
            return ((OMNamespaceEx)contextNode).getParent();
        }
        return ((OMNode)contextNode).getParent();
    }

    class OMNamespaceEx
    implements OMNamespace {
        final OMNamespace originalNsp;
        final OMContainer parent;

        OMNamespaceEx(OMNamespace nsp, OMContainer parent) {
            this.originalNsp = nsp;
            this.parent = parent;
        }

        public boolean equals(String uri, String prefix) {
            return this.originalNsp.equals(uri, prefix);
        }

        public String getPrefix() {
            return this.originalNsp.getPrefix();
        }

        public String getName() {
            return this.originalNsp.getNamespaceURI();
        }

        public String getNamespaceURI() {
            return this.originalNsp.getNamespaceURI();
        }

        public OMContainer getParent() {
            return this.parent;
        }
    }
}

