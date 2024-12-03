/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.dom;

import javax.xml.transform.Source;
import org.w3c.dom.Node;

public class DOMSource
implements Source {
    private Node node;
    private String systemID;
    public static final String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature";

    public DOMSource() {
    }

    public DOMSource(Node node) {
        this.setNode(node);
    }

    public DOMSource(Node node, String string) {
        this.setNode(node);
        this.setSystemId(string);
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    public void setSystemId(String string) {
        this.systemID = string;
    }

    public String getSystemId() {
        return this.systemID;
    }
}

