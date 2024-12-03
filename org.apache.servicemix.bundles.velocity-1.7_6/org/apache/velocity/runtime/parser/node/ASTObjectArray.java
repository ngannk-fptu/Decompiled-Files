/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.util.ArrayList;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTObjectArray
extends SimpleNode {
    public ASTObjectArray(int id) {
        super(id);
    }

    public ASTObjectArray(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        int size = this.jjtGetNumChildren();
        ArrayList<Object> objectArray = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            objectArray.add(this.jjtGetChild(i).value(context));
        }
        return objectArray;
    }
}

