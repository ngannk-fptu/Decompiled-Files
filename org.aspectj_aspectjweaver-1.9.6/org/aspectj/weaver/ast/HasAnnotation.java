/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;

public class HasAnnotation
extends Test {
    private Var v;
    private ResolvedType annType;

    public HasAnnotation(Var v, ResolvedType annType) {
        this.v = v;
        this.annType = annType;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "(" + this.v + " has annotation @" + this.annType + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof HasAnnotation) {
            HasAnnotation o = (HasAnnotation)other;
            return o.v.equals(this.v) && o.annType.equals(this.annType);
        }
        return false;
    }

    public int hashCode() {
        return this.v.hashCode() * 37 + this.annType.hashCode();
    }

    public Var getVar() {
        return this.v;
    }

    public UnresolvedType getAnnotationType() {
        return this.annType;
    }
}

