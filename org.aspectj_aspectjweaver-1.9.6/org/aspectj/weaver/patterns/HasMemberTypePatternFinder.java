/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.aspectj.weaver.patterns.TypePattern;

public class HasMemberTypePatternFinder
extends AbstractPatternNodeVisitor {
    private boolean hasMemberTypePattern = false;

    public HasMemberTypePatternFinder(TypePattern aPattern) {
        aPattern.traverse(this, null);
    }

    @Override
    public Object visit(HasMemberTypePattern node, Object data) {
        this.hasMemberTypePattern = true;
        return null;
    }

    public boolean hasMemberTypePattern() {
        return this.hasMemberTypePattern;
    }
}

