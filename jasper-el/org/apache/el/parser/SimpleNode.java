/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 *  javax.el.MethodInfo
 *  javax.el.PropertyNotWritableException
 *  javax.el.ValueReference
 */
package org.apache.el.parser;

import java.util.Arrays;
import javax.el.ELException;
import javax.el.MethodInfo;
import javax.el.PropertyNotWritableException;
import javax.el.ValueReference;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.ELParserTreeConstants;
import org.apache.el.parser.Node;
import org.apache.el.parser.NodeVisitor;
import org.apache.el.util.MessageFactory;

public abstract class SimpleNode
extends ELSupport
implements Node {
    protected SimpleNode parent;
    protected SimpleNode[] children;
    protected final int id;
    protected String image;

    public SimpleNode(int i) {
        this.id = i;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        this.parent = (SimpleNode)n;
    }

    @Override
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (this.children == null) {
            this.children = new SimpleNode[i + 1];
        } else if (i >= this.children.length) {
            SimpleNode[] c = new SimpleNode[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = (SimpleNode)n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    public String toString() {
        if (this.image != null) {
            return ELParserTreeConstants.jjtNodeName[this.id] + "[" + this.image + "]";
        }
        return ELParserTreeConstants.jjtNodeName[this.id];
    }

    @Override
    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        return true;
    }

    @Override
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        throw new PropertyNotWritableException(MessageFactory.get("error.syntax.set"));
    }

    @Override
    public void accept(NodeVisitor visitor) throws Exception {
        visitor.visit(this);
        if (this.children != null && this.children.length > 0) {
            for (SimpleNode child : this.children) {
                child.accept(visitor);
            }
        }
    }

    @Override
    public Object invoke(EvaluationContext ctx, Class<?>[] paramTypes, Object[] paramValues) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodInfo getMethodInfo(EvaluationContext ctx, Class<?>[] paramTypes) throws ELException {
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.children);
        result = 31 * result + this.id;
        result = 31 * result + (this.image == null ? 0 : this.image.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SimpleNode)) {
            return false;
        }
        SimpleNode other = (SimpleNode)obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.image == null ? other.image != null : !this.image.equals(other.image)) {
            return false;
        }
        return Arrays.equals(this.children, other.children);
    }

    @Override
    public ValueReference getValueReference(EvaluationContext ctx) {
        return null;
    }

    @Override
    public boolean isParametersProvided() {
        return false;
    }
}

