/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public interface IPolyExpression {
    public void setExpressionContext(ExpressionContext var1);

    public ExpressionContext getExpressionContext();

    public void setExpectedType(TypeBinding var1);

    public TypeBinding invocationTargetType();

    public boolean isPotentiallyCompatibleWith(TypeBinding var1, Scope var2);

    public boolean isCompatibleWith(TypeBinding var1, Scope var2);

    public boolean isBoxingCompatibleWith(TypeBinding var1, Scope var2);

    public boolean sIsMoreSpecific(TypeBinding var1, TypeBinding var2, Scope var3);

    public boolean isPertinentToApplicability(TypeBinding var1, MethodBinding var2);

    public boolean isPolyExpression(MethodBinding var1);

    public boolean isPolyExpression();

    public boolean isFunctionalType();

    public Expression[] getPolyExpressions();

    public TypeBinding resolveType(BlockScope var1);

    public Expression resolveExpressionExpecting(TypeBinding var1, Scope var2, InferenceContext18 var3);
}

