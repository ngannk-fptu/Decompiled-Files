/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractCharacterData;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class AbstractText
extends AbstractCharacterData
implements Text {
    @Override
    public Text splitText(int offset) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String v = this.getNodeValue();
        if (offset < 0 || offset >= v.length()) {
            throw this.createDOMException((short)1, "offset", new Object[]{offset});
        }
        Node n = this.getParentNode();
        if (n == null) {
            throw this.createDOMException((short)1, "need.parent", new Object[0]);
        }
        String t1 = v.substring(offset);
        Text t = this.createTextNode(t1);
        Node ns = this.getNextSibling();
        if (ns != null) {
            n.insertBefore(t, ns);
        } else {
            n.appendChild(t);
        }
        this.setNodeValue(v.substring(0, offset));
        return t;
    }

    protected Node getPreviousLogicallyAdjacentTextNode(Node n) {
        Node p;
        Node parent = n.getParentNode();
        for (p = n.getPreviousSibling(); p == null && parent != null && parent.getNodeType() == 5; p = p.getPreviousSibling()) {
            p = parent;
            parent = p.getParentNode();
        }
        while (p != null && p.getNodeType() == 5) {
            p = p.getLastChild();
        }
        if (p == null) {
            return null;
        }
        short nt = p.getNodeType();
        if (nt == 3 || nt == 4) {
            return p;
        }
        return null;
    }

    protected Node getNextLogicallyAdjacentTextNode(Node n) {
        Node p;
        Node parent = n.getParentNode();
        for (p = n.getNextSibling(); p == null && parent != null && parent.getNodeType() == 5; p = p.getNextSibling()) {
            p = parent;
            parent = p.getParentNode();
        }
        while (p != null && p.getNodeType() == 5) {
            p = p.getFirstChild();
        }
        if (p == null) {
            return null;
        }
        short nt = p.getNodeType();
        if (nt == 3 || nt == 4) {
            return p;
        }
        return null;
    }

    @Override
    public String getWholeText() {
        StringBuffer sb = new StringBuffer();
        Node n = this;
        while (n != null) {
            sb.insert(0, n.getNodeValue());
            n = this.getPreviousLogicallyAdjacentTextNode(n);
        }
        n = this.getNextLogicallyAdjacentTextNode(this);
        while (n != null) {
            sb.append(n.getNodeValue());
            n = this.getNextLogicallyAdjacentTextNode(n);
        }
        return sb.toString();
    }

    @Override
    public boolean isElementContentWhitespace() {
        int len = this.nodeValue.length();
        for (int i = 0; i < len; ++i) {
            if (XMLUtilities.isXMLSpace((char)this.nodeValue.charAt(i))) continue;
            return false;
        }
        Node p = this.getParentNode();
        if (p.getNodeType() == 1) {
            String sp = XMLSupport.getXMLSpace((Element)p);
            return !sp.equals("preserve");
        }
        return true;
    }

    @Override
    public Text replaceWholeText(String s) throws DOMException {
        AbstractNode an;
        Node n = this.getPreviousLogicallyAdjacentTextNode(this);
        while (n != null) {
            an = (AbstractNode)n;
            if (an.isReadonly()) {
                throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)n.getNodeType(), n.getNodeName()});
            }
            n = this.getPreviousLogicallyAdjacentTextNode(n);
        }
        n = this.getNextLogicallyAdjacentTextNode(this);
        while (n != null) {
            an = (AbstractNode)n;
            if (an.isReadonly()) {
                throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)n.getNodeType(), n.getNodeName()});
            }
            n = this.getNextLogicallyAdjacentTextNode(n);
        }
        Node parent = this.getParentNode();
        Node n2 = this.getPreviousLogicallyAdjacentTextNode(this);
        while (n2 != null) {
            parent.removeChild(n2);
            n2 = this.getPreviousLogicallyAdjacentTextNode(n2);
        }
        n2 = this.getNextLogicallyAdjacentTextNode(this);
        while (n2 != null) {
            parent.removeChild(n2);
            n2 = this.getNextLogicallyAdjacentTextNode(n2);
        }
        if (this.isReadonly()) {
            Text t = this.createTextNode(s);
            parent.replaceChild(t, this);
            return t;
        }
        this.setNodeValue(s);
        return this;
    }

    @Override
    public String getTextContent() {
        if (this.isElementContentWhitespace()) {
            return "";
        }
        return this.getNodeValue();
    }

    protected abstract Text createTextNode(String var1);
}

