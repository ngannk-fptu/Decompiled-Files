/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;

public final class Literal
extends Test {
    boolean noTest;
    boolean val;
    public static final Literal TRUE = new Literal(true, false);
    public static final Literal FALSE = new Literal(false, false);

    private Literal(boolean val, boolean noTest) {
        this.val = val;
        this.noTest = noTest;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return this.noTest ? "NO_TEST" : (this.val ? "TRUE" : "FALSE");
    }
}

