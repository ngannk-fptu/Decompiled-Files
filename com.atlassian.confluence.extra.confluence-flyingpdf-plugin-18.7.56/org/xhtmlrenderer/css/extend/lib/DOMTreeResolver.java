/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend.lib;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.extend.TreeResolver;

public class DOMTreeResolver
implements TreeResolver {
    @Override
    public Object getParentElement(Object element) {
        Node parent = ((Element)element).getParentNode();
        if (parent.getNodeType() != 1) {
            parent = null;
        }
        return parent;
    }

    @Override
    public Object getPreviousSiblingElement(Object element) {
        Node sibling;
        for (sibling = ((Element)element).getPreviousSibling(); sibling != null && sibling.getNodeType() != 1; sibling = sibling.getPreviousSibling()) {
        }
        if (sibling == null || sibling.getNodeType() != 1) {
            return null;
        }
        return sibling;
    }

    @Override
    public String getElementName(Object element) {
        String name = ((Element)element).getLocalName();
        if (name == null) {
            name = ((Element)element).getNodeName();
        }
        return name;
    }

    @Override
    public boolean isFirstChildElement(Object element) {
        Node currentChild;
        Node parent = ((Element)element).getParentNode();
        for (currentChild = parent.getFirstChild(); currentChild != null && currentChild.getNodeType() != 1; currentChild = currentChild.getNextSibling()) {
        }
        return currentChild == element;
    }

    @Override
    public boolean isLastChildElement(Object element) {
        Node currentChild;
        Node parent = ((Element)element).getParentNode();
        for (currentChild = parent.getLastChild(); currentChild != null && currentChild.getNodeType() != 1; currentChild = currentChild.getPreviousSibling()) {
        }
        return currentChild == element;
    }

    @Override
    public boolean matchesElement(Object element, String namespaceURI, String name) {
        Element e = (Element)element;
        String localName = e.getLocalName();
        String eName = localName == null ? e.getNodeName() : localName;
        if (namespaceURI != null) {
            return name.equals(localName) && namespaceURI.equals(e.getNamespaceURI());
        }
        if (namespaceURI == "") {
            return name.equals(eName) && e.getNamespaceURI() == null;
        }
        return name.equals(eName);
    }

    @Override
    public int getPositionOfElement(Object element) {
        Node parent = ((Element)element).getParentNode();
        NodeList nl = parent.getChildNodes();
        int elt_count = 0;
        for (int i = 0; i < nl.getLength(); ++i) {
            if (nl.item(i).getNodeType() != 1) continue;
            if (nl.item(i) == element) {
                return elt_count;
            }
            ++elt_count;
        }
        return -1;
    }
}

