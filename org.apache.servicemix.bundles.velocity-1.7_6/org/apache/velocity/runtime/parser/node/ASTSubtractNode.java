/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ASTMathNode;
import org.apache.velocity.runtime.parser.node.MathUtils;

public class ASTSubtractNode
extends ASTMathNode {
    public ASTSubtractNode(int id) {
        super(id);
    }

    public ASTSubtractNode(Parser p, int id) {
        super(p, id);
    }

    public Number perform(Number left, Number right, InternalContextAdapter context) {
        return MathUtils.subtract(left, right);
    }
}

