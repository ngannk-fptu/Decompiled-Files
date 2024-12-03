/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;

class IfFinder
extends AbstractPatternNodeVisitor {
    boolean hasIf = false;

    IfFinder() {
    }

    @Override
    public Object visit(IfPointcut node, Object data) {
        if (!node.alwaysFalse() && !node.alwaysTrue()) {
            this.hasIf = true;
        }
        return node;
    }

    @Override
    public Object visit(AndPointcut node, Object data) {
        if (!this.hasIf) {
            node.getLeft().accept(this, data);
        }
        if (!this.hasIf) {
            node.getRight().accept(this, data);
        }
        return node;
    }

    @Override
    public Object visit(NotPointcut node, Object data) {
        if (!this.hasIf) {
            node.getNegatedPointcut().accept(this, data);
        }
        return node;
    }

    @Override
    public Object visit(OrPointcut node, Object data) {
        if (!this.hasIf) {
            node.getLeft().accept(this, data);
        }
        if (!this.hasIf) {
            node.getRight().accept(this, data);
        }
        return node;
    }
}

