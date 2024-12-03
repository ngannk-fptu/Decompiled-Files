/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.TemplateNumber;

public class ASTNENode
extends SimpleNode {
    public ASTNENode(int id) {
        super(id);
    }

    public ASTNENode(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException {
        Object left = this.jjtGetChild(0).value(context);
        Object right = this.jjtGetChild(1).value(context);
        if (left instanceof TemplateNumber) {
            left = ((TemplateNumber)left).getAsNumber();
        }
        if (right instanceof TemplateNumber) {
            right = ((TemplateNumber)right).getAsNumber();
        }
        if (left instanceof Number && right instanceof Number) {
            return MathUtils.compare((Number)left, (Number)right) != 0;
        }
        if (left != null && right != null && (left.getClass().isAssignableFrom(right.getClass()) || right.getClass().isAssignableFrom(left.getClass()))) {
            return !left.equals(right);
        }
        left = left == null ? null : left.toString();
        Object object = right = right == null ? null : right.toString();
        if (left == null && right == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Both right (" + this.getLiteral(false) + " and left " + this.getLiteral(true) + " sides of '!=' operation returned null.If references, they may not be in the context." + this.getLocation(context));
            }
            return false;
        }
        if (left == null || right == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((left == null ? "Left" : "Right") + " side (" + this.getLiteral(left == null) + ") of '!=' operation has null value. If it is a reference, it may not be in the context or its toString() returned null. " + this.getLocation(context));
            }
            return true;
        }
        return !left.equals(right);
    }

    private String getLiteral(boolean left) {
        return this.jjtGetChild(left ? 0 : 1).literal();
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        boolean val = this.evaluate(context);
        return val ? Boolean.TRUE : Boolean.FALSE;
    }
}

