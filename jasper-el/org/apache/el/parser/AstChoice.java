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

public final class AstChoice
extends SimpleNode {
    public AstChoice(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Object val = this.getValue(ctx);
        return val != null ? val.getClass() : null;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Boolean b0 = AstChoice.coerceToBoolean(ctx, obj0, true);
        return this.children[b0 != false ? 1 : 2].getValue(ctx);
    }
}

