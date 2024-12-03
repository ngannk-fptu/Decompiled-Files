/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.BooleanNode;

public final class AstAnd
extends BooleanNode {
    public AstAnd(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        Boolean b = AstAnd.coerceToBoolean(ctx, obj, true);
        if (!b.booleanValue()) {
            return b;
        }
        obj = this.children[1].getValue(ctx);
        b = AstAnd.coerceToBoolean(ctx, obj, true);
        return b;
    }
}

