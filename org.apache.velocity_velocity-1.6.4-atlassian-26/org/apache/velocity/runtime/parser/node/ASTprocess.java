/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTprocess
extends SimpleNode {
    public ASTprocess(int id) {
        super(id);
    }

    public ASTprocess(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

