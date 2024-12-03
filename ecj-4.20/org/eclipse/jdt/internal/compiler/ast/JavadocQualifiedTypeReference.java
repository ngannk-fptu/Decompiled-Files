/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.IJavadocTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocQualifiedTypeReference
extends QualifiedTypeReference
implements IJavadocTypeReference {
    public int tagSourceStart;
    public int tagSourceEnd;
    public PackageBinding packageBinding;

    public JavadocQualifiedTypeReference(char[][] sources, long[] pos, int tagStart, int tagEnd) {
        super(sources, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= 0x8000;
    }

    private TypeBinding internalResolveType(Scope scope, boolean checkBounds) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            return this.resolvedType.isValidBinding() ? this.resolvedType : this.resolvedType.closestMatch();
        }
        this.resolvedType = this.getTypeBinding(scope);
        TypeBinding type = this.resolvedType;
        if (type == null) {
            return null;
        }
        if (!type.isValidBinding()) {
            Binding binding = scope.getTypeOrPackage(this.tokens);
            if (binding instanceof PackageBinding) {
                this.packageBinding = (PackageBinding)binding;
            } else {
                this.reportInvalidType(scope);
            }
            return null;
        }
        if (type.isGenericType() || type.isParameterizedType()) {
            this.resolvedType = scope.environment().convertToRawType(type, true);
        }
        return this.resolvedType;
    }

    @Override
    protected void reportDeprecatedType(TypeBinding type, Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }

    @Override
    protected void reportDeprecatedType(TypeBinding type, Scope scope, int index) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers(), index);
    }

    @Override
    protected void reportInvalidType(Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
    }

    @Override
    public TypeBinding resolveType(BlockScope blockScope, boolean checkBounds, int location) {
        return this.internalResolveType((Scope)blockScope, checkBounds);
    }

    @Override
    public TypeBinding resolveType(ClassScope classScope, int location) {
        return this.internalResolveType((Scope)classScope, false);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public int getTagSourceStart() {
        return this.tagSourceStart;
    }

    @Override
    public int getTagSourceEnd() {
        return this.tagSourceEnd;
    }
}

