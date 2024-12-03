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

public abstract class BooleanNode
extends SimpleNode {
    public BooleanNode(int i) {
        super(i);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Boolean.class;
    }
}

