/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.parser;

import java.util.ArrayList;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstMethodParameters
extends SimpleNode {
    public AstMethodParameters(int id) {
        super(id);
    }

    public Object[] getParameters(EvaluationContext ctx) {
        ArrayList<Object> params = new ArrayList<Object>();
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            params.add(this.jjtGetChild(i).getValue(ctx));
        }
        return params.toArray(new Object[0]);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        if (this.children != null) {
            for (SimpleNode n : this.children) {
                result.append(((Object)n).toString());
                result.append(',');
            }
        }
        result.append(')');
        return result.toString();
    }
}

