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

public class AstConcatenation
extends SimpleNode {
    public AstConcatenation(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        String s1 = AstConcatenation.coerceToString(ctx, this.children[0].getValue(ctx));
        String s2 = AstConcatenation.coerceToString(ctx, this.children[1].getValue(ctx));
        return s1 + s2;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }
}

