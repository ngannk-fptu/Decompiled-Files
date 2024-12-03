/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocArgumentExpression
extends Expression {
    public char[] token;
    public Argument argument;

    public JavadocArgumentExpression(char[] name, int startPos, int endPos, TypeReference typeRef) {
        this.token = name;
        this.sourceStart = startPos;
        this.sourceEnd = endPos;
        long pos = ((long)startPos << 32) + (long)endPos;
        this.argument = new Argument(name, pos, typeRef, 0);
        this.bits |= 0x8000;
    }

    private TypeBinding internalResolveType(Scope scope) {
        TypeReference typeRef;
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            return this.resolvedType.isValidBinding() ? this.resolvedType : null;
        }
        if (this.argument != null && (typeRef = this.argument.type) != null) {
            ReferenceBinding enclosingType;
            typeRef.resolvedType = this.resolvedType = typeRef.getTypeBinding(scope);
            if (this.resolvedType == null) {
                return null;
            }
            if (typeRef instanceof SingleTypeReference && this.resolvedType.leafComponentType().enclosingType() != null && scope.compilerOptions().complianceLevel <= 0x300000L) {
                scope.problemReporter().javadocInvalidMemberTypeQualification(this.sourceStart, this.sourceEnd, scope.getDeclarationModifiers());
            } else if (typeRef instanceof QualifiedTypeReference && (enclosingType = this.resolvedType.leafComponentType().enclosingType()) != null) {
                int compoundLength = 2;
                while ((enclosingType = enclosingType.enclosingType()) != null) {
                    ++compoundLength;
                }
                int typeNameLength = typeRef.getTypeName().length;
                if (typeNameLength != compoundLength && typeNameLength != compoundLength + this.resolvedType.getPackage().compoundName.length) {
                    scope.problemReporter().javadocInvalidMemberTypeQualification(typeRef.sourceStart, typeRef.sourceEnd, scope.getDeclarationModifiers());
                }
            }
            if (!this.resolvedType.isValidBinding()) {
                scope.problemReporter().javadocInvalidType(typeRef, this.resolvedType, scope.getDeclarationModifiers());
                return null;
            }
            if (this.isTypeUseDeprecated(this.resolvedType, scope)) {
                scope.problemReporter().javadocDeprecatedType(this.resolvedType, typeRef, scope.getDeclarationModifiers());
            }
            this.resolvedType = scope.environment().convertToRawType(this.resolvedType, true);
            return this.resolvedType;
        }
        return null;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.argument == null) {
            if (this.token != null) {
                output.append(this.token);
            }
        } else {
            this.argument.print(indent, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope scope) {
        if (this.argument != null) {
            this.argument.resolve(scope);
        }
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        return this.internalResolveType(scope);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope) {
        return this.internalResolveType(scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope) && this.argument != null) {
            this.argument.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope blockScope) {
        if (visitor.visit(this, blockScope) && this.argument != null) {
            this.argument.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}

