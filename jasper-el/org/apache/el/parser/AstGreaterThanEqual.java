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

public final class AstGreaterThanEqual
extends BooleanNode {
    public AstGreaterThanEqual(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj1;
        Object obj0 = this.children[0].getValue(ctx);
        if (obj0 == (obj1 = this.children[1].getValue(ctx))) {
            return Boolean.TRUE;
        }
        if (obj0 == null || obj1 == null) {
            return Boolean.FALSE;
        }
        return AstGreaterThanEqual.compare(ctx, obj0, obj1) >= 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}

