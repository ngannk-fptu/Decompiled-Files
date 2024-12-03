/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.IJavadocTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocSingleTypeReference
extends SingleTypeReference
implements IJavadocTypeReference {
    public int tagSourceStart;
    public int tagSourceEnd;
    public PackageBinding packageBinding;

    public JavadocSingleTypeReference(char[] source, long pos, int tagStart, int tagEnd) {
        super(source, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= 0x8000;
    }

    @Override
    protected TypeBinding internalResolveType(Scope scope, int location) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            if (this.resolvedType.isValidBinding()) {
                return this.resolvedType;
            }
            switch (this.resolvedType.problemId()) {
                case 1: 
                case 2: 
                case 5: {
                    TypeBinding type = this.resolvedType.closestMatch();
                    return type;
                }
            }
            return null;
        }
        this.resolvedType = this.getTypeBinding(scope);
        if (this.resolvedType instanceof LocalTypeBinding) {
            LocalTypeBinding localType = (LocalTypeBinding)this.resolvedType;
            if (localType.scope != null && localType.scope.parent == scope) {
                this.resolvedType = new ProblemReferenceBinding(new char[][]{localType.sourceName}, (ReferenceBinding)this.resolvedType, 1);
            }
        }
        if (this.resolvedType == null) {
            return null;
        }
        if (!this.resolvedType.isValidBinding()) {
            char[][] tokens = new char[][]{this.token};
            Binding binding = scope.getTypeOrPackage(tokens);
            if (binding instanceof PackageBinding) {
                this.packageBinding = (PackageBinding)binding;
            } else {
                TypeBinding closestMatch;
                if (this.resolvedType.problemId() == 7 && (closestMatch = this.resolvedType.closestMatch()) != null && closestMatch.isTypeVariable()) {
                    this.resolvedType = closestMatch;
                    return this.resolvedType;
                }
                this.reportInvalidType(scope);
            }
            return null;
        }
        if (this.isTypeUseDeprecated(this.resolvedType, scope)) {
            this.reportDeprecatedType(this.resolvedType, scope);
        }
        if (this.resolvedType.isGenericType() || this.resolvedType.isParameterizedType()) {
            this.resolvedType = scope.environment().convertToRawType(this.resolvedType, true);
        }
        return this.resolvedType;
    }

    @Override
    protected void reportDeprecatedType(TypeBinding type, Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }

    @Override
    protected void reportInvalidType(Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
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

