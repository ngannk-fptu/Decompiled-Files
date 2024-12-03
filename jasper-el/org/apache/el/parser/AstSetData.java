/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.util.HashSet;
import java.util.Set;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public class AstSetData
extends SimpleNode {
    public AstSetData(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        HashSet<Object> result = new HashSet<Object>();
        if (this.children != null) {
            for (SimpleNode child : this.children) {
                result.add(child.getValue(ctx));
            }
        }
        return result;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Set.class;
    }
}

