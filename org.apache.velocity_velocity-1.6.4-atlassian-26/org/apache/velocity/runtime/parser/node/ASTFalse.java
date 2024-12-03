/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTFalse
extends SimpleNode {
    private static Boolean value = Boolean.FALSE;

    public ASTFalse(int id) {
        super(id);
    }

    public ASTFalse(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean evaluate(InternalContextAdapter context) {
        return false;
    }

    @Override
    public Object value(InternalContextAdapter context) {
        return value;
    }
}

