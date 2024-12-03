/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.TemplateNumber;

public class ASTGENode
extends SimpleNode {
    public ASTGENode(int id) {
        super(id);
    }

    public ASTGENode(Parser p, int id) {
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
        if (left == null || right == null) {
            String msg = (left == null ? "Left" : "Right") + " side (" + this.jjtGetChild(left == null ? 0 : 1).literal() + ") of '>=' operation has null value at " + Log.formatFileString(this);
            if (this.rsvc.getBoolean("runtime.references.strict", false)) {
                throw new VelocityException(msg);
            }
            this.log.error(msg);
            return false;
        }
        if (left instanceof TemplateNumber) {
            left = ((TemplateNumber)left).getAsNumber();
        }
        if (right instanceof TemplateNumber) {
            right = ((TemplateNumber)right).getAsNumber();
        }
        if (!(left instanceof Number) || !(right instanceof Number)) {
            String msg = (!(left instanceof Number) ? "Left" : "Right") + " side of '>=' operation is not a Number at " + Log.formatFileString(this);
            if (this.rsvc.getBoolean("runtime.references.strict", false)) {
                throw new VelocityException(msg);
            }
            this.log.error(msg);
            return false;
        }
        return MathUtils.compare((Number)left, (Number)right) >= 0;
    }

    @Override
    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        boolean val = this.evaluate(context);
        return val ? Boolean.TRUE : Boolean.FALSE;
    }
}

