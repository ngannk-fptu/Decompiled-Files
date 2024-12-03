/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserTreeConstants;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.Node;

public class SimpleNode
implements Node {
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected JCRSQLParser parser;

    public SimpleNode(int i) {
        this.id = i;
    }

    public SimpleNode(JCRSQLParser p, int i) {
        this(i);
        this.parser = p;
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
    public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object childrenAccept(JCRSQLParserVisitor visitor, Object data) {
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                this.children[i].jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public String toString() {
        return JCRSQLParserTreeConstants.jjtNodeName[this.id];
    }

    public String toString(String prefix) {
        return prefix + this.toString();
    }

    public void dump(String prefix) {
        System.out.println(this.toString(prefix));
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                SimpleNode n = (SimpleNode)this.children[i];
                if (n == null) continue;
                n.dump(prefix + " ");
            }
        }
    }
}

