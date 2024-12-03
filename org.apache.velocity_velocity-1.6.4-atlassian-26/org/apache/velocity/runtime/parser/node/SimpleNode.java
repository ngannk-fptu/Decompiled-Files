/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.NodeUtils;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

public class SimpleNode
implements Node {
    protected RuntimeServices rsvc = null;
    protected Log log = null;
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Parser parser;
    protected int info;
    public boolean state;
    protected boolean invalid = false;
    protected Token first;
    protected Token last;
    protected String templateName;

    public SimpleNode(int i) {
        this.id = i;
    }

    public SimpleNode(Parser p, int i) {
        this(i);
        this.parser = p;
        this.templateName = this.parser.currentTemplateName;
    }

    @Override
    public void jjtOpen() {
        this.first = this.parser.getToken(1);
    }

    @Override
    public void jjtClose() {
        this.last = this.parser.getToken(0);
    }

    public void setFirstToken(Token t) {
        this.first = t;
    }

    @Override
    public Token getFirstToken() {
        return this.first;
    }

    @Override
    public Token getLastToken() {
        return this.last;
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
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(ParserVisitor visitor, Object data) {
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                this.children[i].jjtAccept(visitor, data);
            }
        }
        return data;
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

    protected String getLocation(InternalContextAdapter context) {
        return Log.formatFileString(this);
    }

    @Override
    public String literal() {
        if (this.first == this.last) {
            return NodeUtils.tokenLiteral(this.first);
        }
        Token t = this.first;
        StrBuilder sb = new StrBuilder(NodeUtils.tokenLiteral(t));
        while (t != this.last) {
            t = t.next;
            sb.append(NodeUtils.tokenLiteral(t));
        }
        return sb.toString();
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        this.rsvc = (RuntimeServices)data;
        this.log = this.rsvc.getLog();
        int k = this.jjtGetNumChildren();
        for (int i = 0; i < k; ++i) {
            this.jjtGetChild(i).init(context, data);
        }
        return data;
    }

    @Override
    public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException {
        return false;
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        return null;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException {
        int k = this.jjtGetNumChildren();
        for (int i = 0; i < k; ++i) {
            this.jjtGetChild(i).render(context, writer);
        }
        return true;
    }

    @Override
    public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException {
        return null;
    }

    @Override
    public int getType() {
        return this.id;
    }

    @Override
    public void setInfo(int info) {
        this.info = info;
    }

    @Override
    public int getInfo() {
        return this.info;
    }

    @Override
    public void setInvalid() {
        this.invalid = true;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public int getLine() {
        return this.first.beginLine;
    }

    @Override
    public int getColumn() {
        return this.first.beginColumn;
    }

    public String toString() {
        StrBuilder tokens = new StrBuilder();
        Token t = this.getFirstToken();
        while (t != null) {
            tokens.append("[").append(t.image).append("]");
            if (t.next != null) {
                if (t.equals(this.getLastToken())) break;
                tokens.append(", ");
            }
            t = t.next;
        }
        return new ToStringBuilder((Object)this).append("id", this.getType()).append("info", this.getInfo()).append("invalid", this.isInvalid()).append("children", this.jjtGetNumChildren()).append("tokens", (Object)tokens).toString();
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }
}

