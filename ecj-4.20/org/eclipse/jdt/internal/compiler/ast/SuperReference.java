/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SuperReference
extends ThisReference {
    public SuperReference(int sourceStart, int sourceEnd) {
        super(sourceStart, sourceEnd);
    }

    public static ExplicitConstructorCall implicitSuperConstructorCall() {
        return new ExplicitConstructorCall(1);
    }

    @Override
    public boolean isImplicitThis() {
        return false;
    }

    @Override
    public boolean isSuper() {
        return true;
    }

    @Override
    public boolean isUnqualifiedSuper() {
        return true;
    }

    @Override
    public boolean isThis() {
        return false;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        return output.append("super");
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        this.constant = Constant.NotAConstant;
        ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
        if (!this.checkAccess(scope, enclosingReceiverType)) {
            return null;
        }
        if (enclosingReceiverType.id == 1) {
            scope.problemReporter().cannotUseSuperInJavaLangObject(this);
            return null;
        }
        this.resolvedType = enclosingReceiverType.superclass();
        return this.resolvedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}

