/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.AndTypePattern;
import org.aspectj.weaver.patterns.AnyWithAnnotationTypePattern;
import org.aspectj.weaver.patterns.NotTypePattern;
import org.aspectj.weaver.patterns.OrTypePattern;
import org.aspectj.weaver.patterns.WildAnnotationTypePattern;
import org.aspectj.weaver.patterns.WildTypePattern;

public class WildChildFinder
extends AbstractPatternNodeVisitor {
    private boolean wildChild;

    public boolean containedWildChild() {
        return this.wildChild;
    }

    @Override
    public Object visit(WildAnnotationTypePattern node, Object data) {
        node.getTypePattern().accept(this, data);
        return node;
    }

    @Override
    public Object visit(WildTypePattern node, Object data) {
        this.wildChild = true;
        return super.visit(node, data);
    }

    @Override
    public Object visit(AndTypePattern node, Object data) {
        node.getLeft().accept(this, data);
        if (!this.wildChild) {
            node.getRight().accept(this, data);
        }
        return node;
    }

    @Override
    public Object visit(OrTypePattern node, Object data) {
        node.getLeft().accept(this, data);
        if (!this.wildChild) {
            node.getRight().accept(this, data);
        }
        return node;
    }

    @Override
    public Object visit(NotTypePattern node, Object data) {
        node.getNegatedPattern().accept(this, data);
        return node;
    }

    @Override
    public Object visit(AnyWithAnnotationTypePattern node, Object data) {
        node.getAnnotationPattern().accept(this, data);
        return node;
    }
}

