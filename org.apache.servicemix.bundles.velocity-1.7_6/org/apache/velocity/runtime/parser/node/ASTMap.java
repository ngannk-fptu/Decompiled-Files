/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.util.LinkedHashMap;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTMap
extends SimpleNode {
    public ASTMap(int id) {
        super(id);
    }

    public ASTMap(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        int size = this.jjtGetNumChildren();
        LinkedHashMap<Object, Object> objectMap = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < size; i += 2) {
            SimpleNode keyNode = (SimpleNode)this.jjtGetChild(i);
            SimpleNode valueNode = (SimpleNode)this.jjtGetChild(i + 1);
            Object key = keyNode == null ? null : keyNode.value(context);
            Object value = valueNode == null ? null : valueNode.value(context);
            objectMap.put(key, value);
        }
        return objectMap;
    }
}

