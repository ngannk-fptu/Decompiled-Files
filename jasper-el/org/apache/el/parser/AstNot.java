/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstNot
extends SimpleNode {
    public AstNot(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Boolean.class;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        Boolean b = AstNot.coerceToBoolean(ctx, obj, true);
        return b == false;
    }
}

