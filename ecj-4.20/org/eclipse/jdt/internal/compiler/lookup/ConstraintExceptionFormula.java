/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintFormula;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ConstraintExceptionFormula
extends ConstraintFormula {
    FunctionalExpression left;

    public ConstraintExceptionFormula(FunctionalExpression left, TypeBinding type) {
        this.left = left;
        this.right = type;
        this.relation = 7;
    }

    @Override
    public Object reduce(InferenceContext18 inferenceContext) {
        int i;
        int nParam;
        Scope scope = inferenceContext.scope;
        if (!this.right.isFunctionalInterface(scope)) {
            return FALSE;
        }
        MethodBinding sam = this.right.getSingleAbstractMethod(scope, true);
        if (sam == null) {
            return FALSE;
        }
        if (this.left instanceof LambdaExpression) {
            if (((LambdaExpression)this.left).argumentsTypeElided()) {
                nParam = sam.parameters.length;
                i = 0;
                while (i < nParam) {
                    if (!sam.parameters[i].isProperType(true)) {
                        return FALSE;
                    }
                    ++i;
                }
            }
            if (sam.returnType != TypeBinding.VOID && !sam.returnType.isProperType(true)) {
                return FALSE;
            }
        } else if (!((ReferenceExpression)this.left).isExactMethodReference()) {
            nParam = sam.parameters.length;
            i = 0;
            while (i < nParam) {
                if (!sam.parameters[i].isProperType(true)) {
                    return FALSE;
                }
                ++i;
            }
            if (sam.returnType != TypeBinding.VOID && !sam.returnType.isProperType(true)) {
                return FALSE;
            }
        }
        ReferenceBinding[] thrown = sam.thrownExceptions;
        InferenceVariable[] e = new InferenceVariable[thrown.length];
        int n = 0;
        int i2 = 0;
        while (i2 < thrown.length) {
            if (!((TypeBinding)thrown[i2]).isProperType(true)) {
                e[n++] = (InferenceVariable)thrown[i2];
            }
            ++i2;
        }
        if (n == 0) {
            return TRUE;
        }
        TypeBinding[] ePrime = null;
        if (this.left instanceof LambdaExpression) {
            LambdaExpression lambda = ((LambdaExpression)this.left).resolveExpressionExpecting(this.right, inferenceContext.scope, inferenceContext);
            if (lambda == null) {
                return TRUE;
            }
            Set<TypeBinding> ePrimeSet = lambda.getThrownExceptions();
            ePrime = ePrimeSet.toArray(new TypeBinding[ePrimeSet.size()]);
        } else {
            MethodBinding method;
            ReferenceExpression referenceExpression = ((ReferenceExpression)this.left).resolveExpressionExpecting(this.right, scope, inferenceContext);
            MethodBinding methodBinding = method = referenceExpression != null ? referenceExpression.binding : null;
            if (method != null) {
                ePrime = method.thrownExceptions;
            }
        }
        if (ePrime == null) {
            return TRUE;
        }
        int m = ePrime.length;
        ArrayList<ConstraintTypeFormula> result = new ArrayList<ConstraintTypeFormula>();
        int i3 = 0;
        while (i3 < m) {
            block26: {
                if (!ePrime[i3].isUncheckedException(false)) {
                    int j = 0;
                    while (j < thrown.length) {
                        if (!((TypeBinding)thrown[j]).isProperType(true) || !ePrime[i3].isCompatibleWith(thrown[j])) {
                            ++j;
                            continue;
                        }
                        break block26;
                    }
                    j = 0;
                    while (j < n) {
                        result.add(ConstraintTypeFormula.create(ePrime[i3], e[j], 2));
                        ++j;
                    }
                }
            }
            ++i3;
        }
        int j = 0;
        while (j < n) {
            inferenceContext.currentBounds.inThrows.add(e[j].prototype());
            ++j;
        }
        return result.toArray(new ConstraintFormula[result.size()]);
    }

    @Override
    Collection<InferenceVariable> inputVariables(InferenceContext18 context) {
        if (this.left instanceof LambdaExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList((InferenceVariable)this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                LambdaExpression lambda = (LambdaExpression)this.left;
                MethodBinding sam = this.right.getSingleAbstractMethod(context.scope, true);
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                if (lambda.argumentsTypeElided()) {
                    int len = sam.parameters.length;
                    int i = 0;
                    while (i < len) {
                        sam.parameters[i].collectInferenceVariables(variables);
                        ++i;
                    }
                }
                if (sam.returnType != TypeBinding.VOID) {
                    sam.returnType.collectInferenceVariables(variables);
                }
                return variables;
            }
        } else if (this.left instanceof ReferenceExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList((InferenceVariable)this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                MethodBinding sam = this.right.getSingleAbstractMethod(context.scope, true);
                HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
                int len = sam.parameters.length;
                int i = 0;
                while (i < len) {
                    sam.parameters[i].collectInferenceVariables(variables);
                    ++i;
                }
                sam.returnType.collectInferenceVariables(variables);
                return variables;
            }
        }
        return EMPTY_VARIABLE_LIST;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append('\u27e8');
        this.left.printExpression(4, buf);
        buf.append(" \u2286throws ");
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}

