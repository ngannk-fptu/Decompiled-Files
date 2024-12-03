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

public class AstAssign
extends SimpleNode {
    public AstAssign(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return value;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return this.children[1].getType(ctx);
    }
}

