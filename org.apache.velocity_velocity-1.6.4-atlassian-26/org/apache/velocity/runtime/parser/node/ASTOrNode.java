/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTOrNode
extends SimpleNode {
    public ASTOrNode(int id) {
        super(id);
    }

    public ASTOrNode(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        return this.evaluate(context) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException {
        Node left = this.jjtGetChild(0);
        Node right = this.jjtGetChild(1);
        if (left != null && left.evaluate(context)) {
            return true;
        }
        return right != null && right.evaluate(context);
    }
}

