/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.util.ArrayList;
import java.util.List;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public class AstListData
extends SimpleNode {
    public AstListData(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        ArrayList<Object> result = new ArrayList<Object>();
        if (this.children != null) {
            for (SimpleNode child : this.children) {
                result.add(child.getValue(ctx));
            }
        }
        return result;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return List.class;
    }
}

