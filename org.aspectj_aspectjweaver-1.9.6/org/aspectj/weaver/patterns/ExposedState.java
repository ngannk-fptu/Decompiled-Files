/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.Arrays;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Var;

public class ExposedState {
    public static final boolean[] NO_ERRONEOUS_VARS = new boolean[0];
    public Var[] vars;
    private boolean[] erroneousVars;
    private Expr aspectInstance;
    private UnresolvedType[] expectedVarTypes;
    private ResolvedType concreteAspect;

    public ExposedState(int size) {
        if (size == 0) {
            this.vars = Var.NONE;
            this.erroneousVars = NO_ERRONEOUS_VARS;
        } else {
            this.vars = new Var[size];
            this.erroneousVars = new boolean[size];
        }
    }

    public ExposedState(Member signature) {
        this(signature.getParameterTypes().length);
        this.expectedVarTypes = new UnresolvedType[signature.getParameterTypes().length];
        if (this.expectedVarTypes.length > 0) {
            for (int i = 0; i < signature.getParameterTypes().length; ++i) {
                this.expectedVarTypes[i] = signature.getParameterTypes()[i];
            }
        }
    }

    public boolean isFullySetUp() {
        for (int i = 0; i < this.vars.length; ++i) {
            if (this.vars[i] != null) continue;
            return false;
        }
        return true;
    }

    public void set(int i, Var var) {
        ResolvedType expected;
        if (!(this.expectedVarTypes == null || (expected = this.expectedVarTypes[i].resolve(var.getType().getWorld())).equals(ResolvedType.OBJECT) || expected.isAssignableFrom(var.getType()) || var.getType().isCoerceableFrom(expected))) {
            return;
        }
        this.vars[i] = var;
    }

    public Var get(int i) {
        return this.vars[i];
    }

    public int size() {
        return this.vars.length;
    }

    public Expr getAspectInstance() {
        return this.aspectInstance;
    }

    public void setAspectInstance(Expr aspectInstance) {
        this.aspectInstance = aspectInstance;
    }

    public String toString() {
        return "ExposedState(#Vars=" + this.vars.length + ",Vars=" + Arrays.asList(this.vars) + ",AspectInstance=" + this.aspectInstance + ")";
    }

    public void setErroneousVar(int formalIndex) {
        this.erroneousVars[formalIndex] = true;
    }

    public boolean isErroneousVar(int formalIndex) {
        return this.erroneousVars[formalIndex];
    }

    public void setConcreteAspect(ResolvedType concreteAspect) {
        this.concreteAspect = concreteAspect;
    }

    public ResolvedType getConcreteAspect() {
        return this.concreteAspect;
    }
}

