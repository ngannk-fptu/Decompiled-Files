/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public abstract class SelectionTransformer<T> {
    protected final XMLParserHelper xmlParserHelper;

    public SelectionTransformer(XMLParserHelper xmlParserHelper) {
        this.xmlParserHelper = xmlParserHelper;
    }

    public abstract boolean transform(Document var1, TextMatch var2, T var3) throws SAXException;

    protected Node findAncestorByName(Node node, String name) {
        while (node.getParentNode() != null) {
            if (!this.isElementWithName(node = node.getParentNode(), name)) continue;
            return node;
        }
        return null;
    }

    protected List<Node> findChildrenByName(Node node, String name) {
        ArrayList<Node> resultNodes = new ArrayList<Node>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            if (!this.isElementWithName(childNode, name)) continue;
            resultNodes.add(childNode);
        }
        return resultNodes;
    }

    private boolean isElementWithName(Node node, String name) {
        return node.getNodeType() == 1 && node.getNodeName().equals(name);
    }

    protected void insertContentAtIndexInTextNode(Document document, Node node, int inNodeIndex, String xmlInsertion) throws SAXException {
        this.insertContentAtIndexInTextNode(document, node, inNodeIndex, this.xmlParserHelper.parseDocumentFragment(document, xmlInsertion));
    }

    protected void insertContentAtIndexInTextNode(Document document, Node node, int inNodeIndex, Node fragment) throws SAXException {
        Node parentNode = node.getParentNode();
        String nodeText = node.getNodeValue();
        String before = nodeText.substring(0, inNodeIndex);
        String after = nodeText.substring(inNodeIndex);
        if (!before.isEmpty()) {
            Text beforeNode = document.createTextNode(before);
            parentNode.insertBefore(beforeNode, node);
        }
        parentNode.insertBefore(fragment, node);
        if (!after.isEmpty()) {
            Text afterNode = document.createTextNode(after);
            parentNode.insertBefore(afterNode, node);
        }
        parentNode.removeChild(node);
    }
}

