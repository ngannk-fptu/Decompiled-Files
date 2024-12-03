/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.parser;

import org.apache.el.parser.SimpleNode;

public class AstLambdaParameters
extends SimpleNode {
    public AstLambdaParameters(int id) {
        super(id);
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
        result.append(")->");
        return result.toString();
    }
}

