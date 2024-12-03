/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.Styles;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;
import com.atlassian.renderer.wysiwyg.WysiwygNodeConverter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeContext {
    private final Node node;
    private Node previousSibling;
    private Styles styles;
    private final ListContext listContext;
    private final boolean inTable;
    private final boolean inListItem;
    private final boolean ignoreText;
    private final boolean escapeWikiMarkup;
    private final boolean inHeading;

    public NodeContext(Node node, Node previousSibling, Styles styles, ListContext listContext, boolean inTable, boolean inListItem, boolean ignoreText, boolean escapeWikiMarkup) {
        this.node = node;
        this.previousSibling = previousSibling;
        this.styles = styles;
        this.listContext = listContext;
        this.inTable = inTable;
        this.inListItem = inListItem;
        this.inHeading = false;
        this.ignoreText = ignoreText;
        this.escapeWikiMarkup = escapeWikiMarkup;
    }

    private NodeContext(Node node, Node previousSibling, Styles styles, ListContext listContext, boolean inTable, boolean inListItem, boolean inHeading, boolean ignoreText, boolean escapeWikiMarkup) {
        this.node = node;
        this.previousSibling = previousSibling;
        this.styles = styles;
        this.listContext = listContext;
        this.inTable = inTable;
        this.inListItem = inListItem;
        this.inHeading = inHeading;
        this.ignoreText = ignoreText;
        this.escapeWikiMarkup = escapeWikiMarkup;
    }

    public NodeContext getFirstChildNodeContext() {
        if (this.node.getFirstChild() == null) {
            return null;
        }
        return new Builder(this).node(this.node.getFirstChild()).previousSibling(null).build();
    }

    public NodeContext getFirstChildNodeContextPreservingPreviousSibling() {
        if (this.node.getFirstChild() == null) {
            return null;
        }
        return new Builder(this).node(this.node.getFirstChild()).build();
    }

    public NodeContext getNodeContextForNextChild(NodeContext child) {
        if (child.getNode().getNextSibling() == null) {
            return null;
        }
        return new Builder(this).node(child.getNode().getNextSibling()).previousSibling(child.getNode()).build();
    }

    public NodeContext getNodeContextForNextChildPreservingPreviousSibling(NodeContext child) {
        if (child.getNode().getNextSibling() == null) {
            return null;
        }
        return new Builder(this).node(child.getNode().getNextSibling()).previousSibling(child.getPreviousSibling()).build();
    }

    public String invokeConvert(WysiwygNodeConverter wysiwygNodeConverter, WysiwygConverter wysiwygConverter) {
        return wysiwygNodeConverter.convertXHtmlToWikiMarkup(this.previousSibling, this.node, wysiwygConverter, this.styles, this.listContext, this.inTable, this.inListItem, this.ignoreText);
    }

    public boolean hasClass(String className) {
        String[] nodeClasses;
        String nodeClassesAttr = this.getAttribute("class");
        if (nodeClassesAttr == null) {
            return false;
        }
        for (String nodeClass : nodeClasses = nodeClassesAttr.split(" ")) {
            if (!nodeClass.equals(className)) continue;
            return true;
        }
        return false;
    }

    public Node getNode() {
        return this.node;
    }

    public Node getPreviousSibling() {
        return this.previousSibling;
    }

    public Styles getStyles() {
        return this.styles;
    }

    public ListContext getListContext() {
        return this.listContext;
    }

    public boolean isInTable() {
        return this.inTable;
    }

    public boolean isInListItem() {
        return this.inListItem;
    }

    public boolean isInHeading() {
        return this.inHeading;
    }

    public boolean isIgnoreText() {
        return this.ignoreText;
    }

    public boolean isEscapeWikiMarkup() {
        return this.escapeWikiMarkup;
    }

    public boolean getBooleanAttributeValue(String attributeName, boolean defaultValue) {
        String attributeValue = this.getAttribute(attributeName);
        return String.valueOf(!defaultValue).equalsIgnoreCase(attributeValue) ? !defaultValue : defaultValue;
    }

    public String getAttribute(String name) {
        NamedNodeMap map = this.node.getAttributes();
        if (map == null) {
            return null;
        }
        Node n = this.node.getAttributes().getNamedItem(name);
        return n != null ? n.getNodeValue() : null;
    }

    public String getNodeName() {
        return this.node.getNodeName().toLowerCase();
    }

    public boolean hasNodeName(String nodeName) {
        return this.node.getNodeName().equalsIgnoreCase(nodeName);
    }

    public static class Builder {
        private Node node;
        private Node previousSibling;
        private Styles styles;
        private ListContext listContext;
        private boolean inTable;
        private boolean inListItem;
        private boolean inHeading;
        private boolean ignoreText;
        private boolean escapeWikiMarkup;

        public Builder(Node node) {
            this.node = node;
            this.previousSibling = null;
            this.styles = new Styles();
        }

        public Builder(NodeContext originalNodeContext) {
            this.node = originalNodeContext.node;
            this.previousSibling = originalNodeContext.previousSibling;
            this.styles = new Styles(this.node, originalNodeContext.getStyles());
            this.listContext = originalNodeContext.listContext;
            this.inTable = originalNodeContext.inTable;
            this.inListItem = originalNodeContext.inListItem;
            this.inHeading = originalNodeContext.inHeading;
            this.ignoreText = originalNodeContext.ignoreText;
            this.escapeWikiMarkup = originalNodeContext.escapeWikiMarkup;
        }

        public NodeContext build() {
            return new NodeContext(this.node, this.previousSibling, this.styles, this.listContext, this.inTable, this.inListItem, this.inHeading, this.ignoreText, this.escapeWikiMarkup);
        }

        public Builder node(Node node) {
            this.node = node;
            this.styles = new Styles(node, this.styles);
            return this;
        }

        public Builder previousSibling(Node previousSibling) {
            this.previousSibling = previousSibling;
            return this;
        }

        public Builder ignoreText(boolean ignoreText) {
            this.ignoreText = ignoreText;
            return this;
        }

        public Builder escapeWikiMarkup(boolean escapeWikiMarkup) {
            this.escapeWikiMarkup = escapeWikiMarkup;
            return this;
        }

        public Builder addStyle(String style) {
            this.styles = new Styles(style, this.styles);
            return this;
        }

        public Builder styles(Styles styles) {
            this.styles = styles;
            return this;
        }

        public Builder listContext(ListContext listContext) {
            this.listContext = listContext;
            return this;
        }

        public Builder inListItem(boolean inListItem) {
            this.inListItem = inListItem;
            return this;
        }

        public Builder inTable(boolean inTable) {
            this.inTable = inTable;
            return this;
        }

        public Builder inHeading(boolean inHeading) {
            this.inHeading = inHeading;
            return this;
        }
    }
}

