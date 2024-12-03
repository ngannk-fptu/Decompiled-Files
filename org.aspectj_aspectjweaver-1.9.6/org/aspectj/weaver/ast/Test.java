/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.ASTNode;
import org.aspectj.weaver.ast.And;
import org.aspectj.weaver.ast.Call;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.FieldGetCall;
import org.aspectj.weaver.ast.HasAnnotation;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Not;
import org.aspectj.weaver.ast.Or;
import org.aspectj.weaver.ast.Var;

public abstract class Test
extends ASTNode {
    public abstract void accept(ITestVisitor var1);

    public static Test makeAnd(Test a, Test b) {
        if (a == Literal.TRUE) {
            if (b == Literal.TRUE) {
                return a;
            }
            return b;
        }
        if (b == Literal.TRUE) {
            return a;
        }
        if (a == Literal.FALSE || b == Literal.FALSE) {
            return Literal.FALSE;
        }
        return new And(a, b);
    }

    public static Test makeOr(Test a, Test b) {
        if (a == Literal.FALSE) {
            return b;
        }
        if (b == Literal.FALSE) {
            return a;
        }
        if (a == Literal.TRUE || b == Literal.TRUE) {
            return Literal.TRUE;
        }
        return new Or(a, b);
    }

    public static Test makeNot(Test a) {
        if (a instanceof Not) {
            return ((Not)a).getBody();
        }
        if (a == Literal.TRUE) {
            return Literal.FALSE;
        }
        if (a == Literal.FALSE) {
            return Literal.TRUE;
        }
        return new Not(a);
    }

    public static Test makeInstanceof(Var v, ResolvedType ty) {
        if (ty.equals(ResolvedType.OBJECT)) {
            return Literal.TRUE;
        }
        Test e = ty.isAssignableFrom(v.getType()) ? Literal.TRUE : (!ty.isCoerceableFrom(v.getType()) ? Literal.FALSE : new Instanceof(v, ty));
        return e;
    }

    public static Test makeHasAnnotation(Var v, ResolvedType annTy) {
        return new HasAnnotation(v, annTy);
    }

    public static Test makeCall(Member m, Expr[] args) {
        return new Call(m, args);
    }

    public static Test makeFieldGetCall(Member f, Member m, Expr[] args) {
        return new FieldGetCall(f, m, args);
    }
}

