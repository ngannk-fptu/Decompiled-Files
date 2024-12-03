/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import org.apache.bcel.verifier.structurals.LocalVariables;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.apache.bcel.verifier.structurals.UninitializedObjectType;

public class Frame {
    @Deprecated
    protected static UninitializedObjectType _this;
    private final LocalVariables locals;
    private final OperandStack stack;

    public static UninitializedObjectType getThis() {
        return _this;
    }

    public static void setThis(UninitializedObjectType _this) {
        Frame._this = _this;
    }

    public Frame(int maxLocals, int maxStack) {
        this.locals = new LocalVariables(maxLocals);
        this.stack = new OperandStack(maxStack);
    }

    public Frame(LocalVariables locals, OperandStack stack) {
        this.locals = locals;
        this.stack = stack;
    }

    protected Object clone() {
        return new Frame(this.locals.getClone(), this.stack.getClone());
    }

    public boolean equals(Object o) {
        if (!(o instanceof Frame)) {
            return false;
        }
        Frame f = (Frame)o;
        return this.stack.equals(f.stack) && this.locals.equals(f.locals);
    }

    public Frame getClone() {
        return (Frame)this.clone();
    }

    public LocalVariables getLocals() {
        return this.locals;
    }

    public OperandStack getStack() {
        return this.stack;
    }

    public int hashCode() {
        return this.stack.hashCode() ^ this.locals.hashCode();
    }

    public String toString() {
        String s = "Local Variables:\n";
        s = s + this.locals;
        s = s + "OperandStack:\n";
        s = s + this.stack;
        return s;
    }
}

