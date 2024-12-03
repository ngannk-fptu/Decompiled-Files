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

public class AstSemicolon
extends SimpleNode {
    public AstSemicolon(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        this.children[0].getValue(ctx);
        return this.children[1].getValue(ctx);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        this.children[0].getType(ctx);
        return this.children[1].getType(ctx);
    }
}

