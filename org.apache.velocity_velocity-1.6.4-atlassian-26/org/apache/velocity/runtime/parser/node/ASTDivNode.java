/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MathException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ASTMathNode;
import org.apache.velocity.runtime.parser.node.MathUtils;

public class ASTDivNode
extends ASTMathNode {
    public ASTDivNode(int id) {
        super(id);
    }

    public ASTDivNode(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Number perform(Number left, Number right, InternalContextAdapter context) {
        if (MathUtils.isZero(right)) {
            String msg = "Right side of division operation is zero. Must be non-zero. " + this.getLocation(context);
            if (this.strictMode) {
                this.log.error(msg);
                throw new MathException(msg);
            }
            this.log.debug(msg);
            return null;
        }
        return MathUtils.divide(left, right);
    }
}

