/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.Operators;

public class ExecutionContext {
    private final Operators operators;
    private final Stack<Object> stack = new Stack();

    public ExecutionContext(Operators operatorSet) {
        this.operators = operatorSet;
    }

    public Stack<Object> getStack() {
        return this.stack;
    }

    public Operators getOperators() {
        return this.operators;
    }

    public Number popNumber() {
        return (Number)this.stack.pop();
    }

    public int popInt() {
        return (Integer)this.stack.pop();
    }

    public float popReal() {
        return ((Number)this.stack.pop()).floatValue();
    }
}

