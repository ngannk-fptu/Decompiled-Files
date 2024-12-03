/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public interface InvocationSite {
    public TypeBinding[] genericTypeArguments();

    public boolean isSuperAccess();

    public boolean isQualifiedSuper();

    public boolean isTypeAccess();

    public void setActualReceiverType(ReferenceBinding var1);

    public void setDepth(int var1);

    public void setFieldIndex(int var1);

    public int sourceEnd();

    public int sourceStart();

    default public int nameSourceStart() {
        return this.sourceStart();
    }

    default public int nameSourceEnd() {
        return this.sourceEnd();
    }

    public TypeBinding invocationTargetType();

    public boolean receiverIsImplicitThis();

    public boolean checkingPotentialCompatibility();

    public void acceptPotentiallyCompatibleMethods(MethodBinding[] var1);

    default public LocalVariableBinding[] getPatternVariablesWhenTrue() {
        return null;
    }

    public InferenceContext18 freshInferenceContext(Scope var1);

    public ExpressionContext getExpressionContext();

    public static class EmptyWithAstNode
    implements InvocationSite {
        ASTNode node;

        public EmptyWithAstNode(ASTNode node) {
            this.node = node;
        }

        @Override
        public TypeBinding[] genericTypeArguments() {
            return null;
        }

        @Override
        public boolean isSuperAccess() {
            return false;
        }

        @Override
        public boolean isTypeAccess() {
            return false;
        }

        @Override
        public void setActualReceiverType(ReferenceBinding receiverType) {
        }

        @Override
        public void setDepth(int depth) {
        }

        @Override
        public void setFieldIndex(int depth) {
        }

        @Override
        public int sourceEnd() {
            return this.node.sourceEnd;
        }

        @Override
        public int sourceStart() {
            return this.node.sourceStart;
        }

        @Override
        public TypeBinding invocationTargetType() {
            return null;
        }

        @Override
        public boolean receiverIsImplicitThis() {
            return false;
        }

        @Override
        public InferenceContext18 freshInferenceContext(Scope scope) {
            return null;
        }

        @Override
        public ExpressionContext getExpressionContext() {
            return ExpressionContext.VANILLA_CONTEXT;
        }

        @Override
        public boolean isQualifiedSuper() {
            return false;
        }

        @Override
        public boolean checkingPotentialCompatibility() {
            return false;
        }

        @Override
        public void acceptPotentiallyCompatibleMethods(MethodBinding[] methods) {
        }
    }
}

