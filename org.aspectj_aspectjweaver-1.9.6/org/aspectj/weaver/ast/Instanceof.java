/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;

public class Instanceof
extends Test {
    Var var;
    UnresolvedType type;

    public Instanceof(Var left, UnresolvedType right) {
        this.var = left;
        this.type = right;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "(" + this.var + " instanceof " + this.type + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof Instanceof) {
            Instanceof o = (Instanceof)other;
            return o.var.equals(this.var) && o.type.equals(this.type);
        }
        return false;
    }

    public int hashCode() {
        return this.var.hashCode() * 37 + this.type.hashCode();
    }

    public Var getVar() {
        return this.var;
    }

    public UnresolvedType getType() {
        return this.type;
    }
}

