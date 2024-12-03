/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.XBLOMContentElement
 *  org.apache.batik.dom.AbstractDocument
 */
package org.apache.batik.bridge.svg12;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.bridge.svg12.AbstractContentSelector;
import org.apache.batik.bridge.svg12.ContentManager;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathPatternContentSelector
extends AbstractContentSelector {
    protected NSPrefixResolver prefixResolver = new NSPrefixResolver();
    protected XPathExpression xpath;
    protected SelectedNodes selectedContent;
    protected String expression;

    public XPathPatternContentSelector(ContentManager cm, XBLOMContentElement content, Element bound, String selector) {
        super(cm, content, bound);
        this.expression = selector;
        this.parse();
    }

    protected void parse() {
        try {
            XPath xPathAPI = XPathFactory.newInstance().newXPath();
            xPathAPI.setNamespaceContext(this.prefixResolver);
            this.xpath = xPathAPI.compile(this.expression);
        }
        catch (XPathExpressionException te) {
            AbstractDocument doc = (AbstractDocument)this.contentElement.getOwnerDocument();
            throw doc.createXPathException((short)51, "xpath.invalid.expression", new Object[]{this.expression, te.getMessage()});
        }
    }

    @Override
    public NodeList getSelectedContent() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
        }
        return this.selectedContent;
    }

    @Override
    boolean update() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
            return true;
        }
        this.parse();
        return this.selectedContent.update();
    }

    protected class NSPrefixResolver
    implements NamespaceContext {
        protected NSPrefixResolver() {
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return XPathPatternContentSelector.this.contentElement.lookupNamespaceURI(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }

    protected class SelectedNodes
    implements NodeList {
        protected ArrayList nodes = new ArrayList(10);

        public SelectedNodes() {
            this.update();
        }

        protected boolean update() {
            ArrayList oldNodes = (ArrayList)this.nodes.clone();
            this.nodes.clear();
            for (Node n = XPathPatternContentSelector.this.boundElement.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.update(n);
            }
            int nodesSize = this.nodes.size();
            if (oldNodes.size() != nodesSize) {
                return true;
            }
            for (int i = 0; i < nodesSize; ++i) {
                if (oldNodes.get(i) == this.nodes.get(i)) continue;
                return true;
            }
            return false;
        }

        protected boolean descendantSelected(Node n) {
            for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!XPathPatternContentSelector.this.isSelected(n) && !this.descendantSelected(n)) continue;
                return true;
            }
            return false;
        }

        protected void update(Node n) {
            if (!XPathPatternContentSelector.this.isSelected(n)) {
                try {
                    Double matchScore = (Double)XPathPatternContentSelector.this.xpath.evaluate(n, XPathConstants.NUMBER);
                    if (matchScore != null) {
                        if (!this.descendantSelected(n)) {
                            this.nodes.add(n);
                        }
                    } else {
                        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                            this.update(n);
                        }
                    }
                }
                catch (XPathExpressionException te) {
                    AbstractDocument doc = (AbstractDocument)XPathPatternContentSelector.this.contentElement.getOwnerDocument();
                    throw doc.createXPathException((short)51, "xpath.error", new Object[]{XPathPatternContentSelector.this.expression, te.getMessage()});
                }
            }
        }

        @Override
        public Node item(int index) {
            if (index < 0 || index >= this.nodes.size()) {
                return null;
            }
            return (Node)this.nodes.get(index);
        }

        @Override
        public int getLength() {
            return this.nodes.size();
        }
    }
}

