/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ASTMathNode;
import org.apache.velocity.runtime.parser.node.MathUtils;

public class ASTAddNode
extends ASTMathNode {
    public ASTAddNode(int id) {
        super(id);
    }

    public ASTAddNode(Parser p, int id) {
        super(p, id);
    }

    @Override
    protected Object handleSpecial(Object left, Object right, InternalContextAdapter context) {
        if (left instanceof String || right instanceof String) {
            if (left == null) {
                left = this.jjtGetChild(0).literal();
            } else if (right == null) {
                right = this.jjtGetChild(1).literal();
            }
            return left.toString().concat(right.toString());
        }
        return null;
    }

    @Override
    public Number perform(Number left, Number right, InternalContextAdapter context) {
        return MathUtils.add(left, right);
    }
}

