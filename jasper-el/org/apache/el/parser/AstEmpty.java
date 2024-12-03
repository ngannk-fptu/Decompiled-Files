/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.util.Collection;
import java.util.Map;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstEmpty
extends SimpleNode {
    public AstEmpty(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Boolean.class;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        if (obj == null) {
            return Boolean.TRUE;
        }
        if (obj instanceof String) {
            return ((String)obj).length() == 0;
        }
        if (obj instanceof Object[]) {
            return ((Object[])obj).length == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map)obj).isEmpty();
        }
        return Boolean.FALSE;
    }
}

