/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstCompositeExpression
extends SimpleNode {
    public AstCompositeExpression(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        StringBuilder sb = new StringBuilder(16);
        Object obj = null;
        if (this.children != null) {
            for (SimpleNode child : this.children) {
                obj = child.getValue(ctx);
                if (obj == null) continue;
                sb.append(ELSupport.coerceToString(ctx, obj));
            }
        }
        return sb.toString();
    }
}

