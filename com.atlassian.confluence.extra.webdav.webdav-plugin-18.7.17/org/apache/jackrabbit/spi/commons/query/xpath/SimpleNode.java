/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import java.io.PrintStream;
import org.apache.jackrabbit.spi.commons.query.xpath.Node;
import org.apache.jackrabbit.spi.commons.query.xpath.Token;
import org.apache.jackrabbit.spi.commons.query.xpath.XPath;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathTreeConstants;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathVisitor;

public class SimpleNode
implements Node {
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected XPath parser;
    protected String m_value;

    public SimpleNode(int i) {
        this.id = i;
    }

    public SimpleNode(XPath p, int i) {
        this(i);
        this.parser = p;
    }

    public static Node jjtCreate(XPath p, int id) {
        return new SimpleNode(p, id);
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        this.parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (this.children == null) {
            this.children = new Node[i + 1];
        } else if (i >= this.children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    @Override
    public Object jjtAccept(XPathVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object childrenAccept(XPathVisitor visitor, Object data) {
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                data = this.children[i].jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public String toString() {
        return XPathTreeConstants.jjtNodeName[this.id];
    }

    public String toString(String prefix) {
        return prefix + this.toString();
    }

    public void dump(String prefix) {
        this.dump(prefix, System.out);
    }

    public void dump(String prefix, PrintStream ps) {
        ps.print(this.toString(prefix));
        this.printValue(ps);
        ps.println();
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                SimpleNode n = (SimpleNode)this.children[i];
                if (n == null) continue;
                n.dump(prefix + "   ", ps);
            }
        }
    }

    public void processToken(Token t) {
        this.m_value = t.image;
    }

    public void printValue(PrintStream ps) {
        if (null != this.m_value) {
            ps.print(" " + this.m_value);
        }
    }

    public int getId() {
        return this.id;
    }

    public String getValue() {
        return this.m_value;
    }
}

