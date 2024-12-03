/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.WildTypePattern;

public class HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor
extends AbstractPatternNodeVisitor {
    boolean ohYesItHas = false;

    @Override
    public Object visit(ExactTypePattern node, Object data) {
        UnresolvedType theExactType = node.getExactType();
        if (theExactType.isParameterizedType()) {
            this.ohYesItHas = true;
        }
        return data;
    }

    @Override
    public Object visit(WildTypePattern node, Object data) {
        if (node.getUpperBound() != null) {
            this.ohYesItHas = true;
        }
        if (node.getLowerBound() != null) {
            this.ohYesItHas = true;
        }
        if (node.getTypeParameters().size() != 0) {
            this.ohYesItHas = true;
        }
        return data;
    }

    public boolean wellHasItThen() {
        return this.ohYesItHas;
    }
}

