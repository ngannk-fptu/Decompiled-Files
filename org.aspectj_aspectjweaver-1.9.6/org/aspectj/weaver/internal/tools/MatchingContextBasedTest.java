/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.internal.tools;

import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.MatchingContext;

public class MatchingContextBasedTest
extends Test {
    private final ContextBasedMatcher matcher;

    public MatchingContextBasedTest(ContextBasedMatcher pc) {
        this.matcher = pc;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public boolean matches(MatchingContext context) {
        return this.matcher.matchesDynamically(context);
    }
}

