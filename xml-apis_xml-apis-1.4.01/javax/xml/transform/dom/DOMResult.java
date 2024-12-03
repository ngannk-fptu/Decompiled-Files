/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.dom;

import javax.xml.transform.Result;
import org.w3c.dom.Node;

public class DOMResult
implements Result {
    public static final String FEATURE = "http://javax.xml.transform.dom.DOMResult/feature";
    private Node node = null;
    private Node nextSibling = null;
    private String systemId = null;

    public DOMResult() {
        this.setNode(null);
        this.setNextSibling(null);
        this.setSystemId(null);
    }

    public DOMResult(Node node) {
        this.setNode(node);
        this.setNextSibling(null);
        this.setSystemId(null);
    }

    public DOMResult(Node node, String string) {
        this.setNode(node);
        this.setNextSibling(null);
        this.setSystemId(string);
    }

    public DOMResult(Node node, Node node2) {
        if (node2 != null) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(node2) & 0x10) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.setNode(node);
        this.setNextSibling(node2);
        this.setSystemId(null);
    }

    public DOMResult(Node node, Node node2, String string) {
        if (node2 != null) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(node2) & 0x10) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.setNode(node);
        this.setNextSibling(node2);
        this.setSystemId(string);
    }

    public void setNode(Node node) {
        if (this.nextSibling != null) {
            if (node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(this.nextSibling) & 0x10) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNextSibling(Node node) {
        if (node != null) {
            if (this.node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((this.node.compareDocumentPosition(node) & 0x10) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.nextSibling = node;
    }

    public Node getNextSibling() {
        return this.nextSibling;
    }

    public void setSystemId(String string) {
        this.systemId = string;
    }

    public String getSystemId() {
        return this.systemId;
    }
}

