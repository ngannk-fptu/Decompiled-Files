/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MathException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.TemplateNumber;

public abstract class ASTMathNode
extends SimpleNode {
    protected boolean strictMode = false;

    public ASTMathNode(int id) {
        super(id);
    }

    public ASTMathNode(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.strictMode = this.rsvc.getBoolean("runtime.strict.math", false);
        return data;
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        Object right;
        Object left = this.jjtGetChild(0).value(context);
        Object special = this.handleSpecial(left, right = this.jjtGetChild(1).value(context), context);
        if (special != null) {
            return special;
        }
        if (left instanceof TemplateNumber) {
            left = ((TemplateNumber)left).getAsNumber();
        }
        if (right instanceof TemplateNumber) {
            right = ((TemplateNumber)right).getAsNumber();
        }
        if (!(left instanceof Number) || !(right instanceof Number)) {
            boolean wrongright = left instanceof Number;
            boolean wrongtype = wrongright ? right != null : left != null;
            String msg = (wrongright ? "Right" : "Left") + " side of math operation (" + this.jjtGetChild(wrongright ? 1 : 0).literal() + ") " + (wrongtype ? "is not a Number. " : "has a null value. ") + this.getLocation(context);
            if (this.strictMode) {
                this.log.error(msg);
                throw new MathException(msg);
            }
            this.log.debug(msg);
            return null;
        }
        return this.perform((Number)left, (Number)right, context);
    }

    protected Object handleSpecial(Object left, Object right, InternalContextAdapter context) {
        return null;
    }

    public abstract Number perform(Number var1, Number var2, InternalContextAdapter var3);
}

