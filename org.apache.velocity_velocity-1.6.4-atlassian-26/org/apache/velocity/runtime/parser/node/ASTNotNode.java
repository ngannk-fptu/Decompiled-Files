/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTNotNode
extends SimpleNode {
    public ASTNotNode(int id) {
        super(id);
    }

    public ASTNotNode(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException {
        return !this.jjtGetChild(0).evaluate(context);
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        return this.jjtGetChild(0).evaluate(context) ? Boolean.FALSE : Boolean.TRUE;
    }
}

