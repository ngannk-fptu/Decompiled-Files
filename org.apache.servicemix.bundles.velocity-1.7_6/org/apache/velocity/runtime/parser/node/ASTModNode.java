/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MathException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ASTMathNode;
import org.apache.velocity.runtime.parser.node.MathUtils;

public class ASTModNode
extends ASTMathNode {
    public ASTModNode(int id) {
        super(id);
    }

    public ASTModNode(Parser p, int id) {
        super(p, id);
    }

    public Number perform(Number left, Number right, InternalContextAdapter context) {
        if (MathUtils.isZero(right)) {
            String msg = "Right side of modulus operation is zero. Must be non-zero. " + this.getLocation(context);
            if (this.strictMode) {
                this.log.error(msg);
                throw new MathException(msg);
            }
            this.log.debug(msg);
            return null;
        }
        return MathUtils.modulo(left, right);
    }
}

