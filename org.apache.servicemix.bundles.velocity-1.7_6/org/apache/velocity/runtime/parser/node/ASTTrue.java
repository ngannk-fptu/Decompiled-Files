/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTTrue
extends SimpleNode {
    private static Boolean value = Boolean.TRUE;

    public ASTTrue(int id) {
        super(id);
    }

    public ASTTrue(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean evaluate(InternalContextAdapter context) {
        return true;
    }

    public Object value(InternalContextAdapter context) {
        return value;
    }
}

