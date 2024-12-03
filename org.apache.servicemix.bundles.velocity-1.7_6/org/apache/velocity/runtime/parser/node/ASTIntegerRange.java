/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.util.ArrayList;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTIntegerRange
extends SimpleNode {
    public ASTIntegerRange(int id) {
        super(id);
    }

    public ASTIntegerRange(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        Object left = this.jjtGetChild(0).value(context);
        Object right = this.jjtGetChild(1).value(context);
        if (left == null || right == null) {
            this.log.error((left == null ? "Left" : "Right") + " side of range operator [n..m] has null value." + " Operation not possible. " + Log.formatFileString(this));
            return null;
        }
        if (!(left instanceof Number) || !(right instanceof Number)) {
            this.log.error((!(left instanceof Number) ? "Left" : "Right") + " side of range operator is not a valid type. " + "Currently only integers (1,2,3...) and the Number type are supported. " + Log.formatFileString(this));
            return null;
        }
        int l = ((Number)left).intValue();
        int r = ((Number)right).intValue();
        int nbrElements = Math.abs(l - r);
        int delta = l >= r ? -1 : 1;
        ArrayList<Integer> elements = new ArrayList<Integer>(++nbrElements);
        int value = l;
        for (int i = 0; i < nbrElements; ++i) {
            elements.add(new Integer(value));
            value += delta;
        }
        return elements;
    }
}

