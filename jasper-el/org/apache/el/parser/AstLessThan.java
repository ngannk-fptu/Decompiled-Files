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

public final class AstLessThan
extends BooleanNode {
    public AstLessThan(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        if (obj0 == null) {
            return Boolean.FALSE;
        }
        Object obj1 = this.children[1].getValue(ctx);
        if (obj1 == null) {
            return Boolean.FALSE;
        }
        return AstLessThan.compare(ctx, obj0, obj1) < 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}

