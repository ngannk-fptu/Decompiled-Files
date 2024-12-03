/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class SingleTypeReference
extends TypeReference {
    public char[] token;

    public SingleTypeReference(char[] source, long pos) {
        this.token = source;
        this.sourceStart = (int)(pos >>> 32);
        this.sourceEnd = (int)(pos & 0xFFFFFFFFL);
    }

    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        int totalDimensions = this.dimensions() + additionalDimensions;
        Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        ArrayTypeReference arrayTypeReference = new ArrayTypeReference(this.token, totalDimensions, allAnnotations, ((long)this.sourceStart << 32) + (long)this.sourceEnd);
        arrayTypeReference.annotations = this.annotations;
        arrayTypeReference.bits |= this.bits & 0x100000;
        if (!isVarargs) {
            arrayTypeReference.extendedDimensions = additionalDimensions;
        }
        return arrayTypeReference;
    }

    @Override
    public char[] getLastToken() {
        return this.token;
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        this.resolvedType = scope.getType(this.token);
        if (this.resolvedType instanceof TypeVariableBinding) {
            TypeVariableBinding typeVariable = (TypeVariableBinding)this.resolvedType;
            if (typeVariable.declaringElement instanceof SourceTypeBinding) {
                scope.tagAsAccessingEnclosingInstanceStateOf((ReferenceBinding)typeVariable.declaringElement, true);
            }
        } else if (this.resolvedType instanceof LocalTypeBinding) {
            LocalTypeBinding localType = (LocalTypeBinding)this.resolvedType;
            MethodScope methodScope = scope.methodScope();
            if (methodScope != null && !methodScope.isStatic) {
                methodScope.tagAsAccessingEnclosingInstanceStateOf(localType, false);
            }
        }
        if (scope.kind == 3 && this.resolvedType.isValidBinding() && ((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)) {
            return null;
        }
        return this.resolvedType;
    }

    @Override
    public char[][] getTypeName() {
        return new char[][]{this.token};
    }

    @Override
    public boolean isBaseTypeReference() {
        return this.token == BYTE || this.token == SHORT || this.token == INT || this.token == LONG || this.token == FLOAT || this.token == DOUBLE || this.token == CHAR || this.token == BOOLEAN || this.token == NULL || this.token == VOID;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.annotations != null && this.annotations[0] != null) {
            SingleTypeReference.printAnnotations(this.annotations[0], output);
            output.append(' ');
        }
        return output.append(this.token);
    }

    public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
        this.resolvedType = scope.getMemberType(this.token, enclosingType);
        boolean hasError = false;
        this.resolveAnnotations(scope, 0);
        TypeBinding memberType = this.resolvedType;
        if (!memberType.isValidBinding()) {
            hasError = true;
            scope.problemReporter().invalidEnclosingType(this, memberType, enclosingType);
            memberType = ((ReferenceBinding)memberType).closestMatch();
            if (memberType == null) {
                return null;
            }
        }
        if (this.isTypeUseDeprecated(memberType, scope)) {
            this.reportDeprecatedType(memberType, scope);
        }
        if ((memberType = scope.environment().convertToRawType(memberType, false)).isRawType() && (this.bits & 0x40000000) == 0 && scope.compilerOptions().getSeverity(0x20010000) != 256) {
            scope.problemReporter().rawTypeReference(this, memberType);
        }
        if (hasError) {
            return memberType;
        }
        this.resolvedType = memberType;
        return this.resolvedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            Annotation[] typeAnnotations = this.annotations[0];
            int i = 0;
            int length = typeAnnotations == null ? 0 : typeAnnotations.length;
            while (i < length) {
                typeAnnotations[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            Annotation[] typeAnnotations = this.annotations[0];
            int i = 0;
            int length = typeAnnotations == null ? 0 : typeAnnotations.length;
            while (i < length) {
                typeAnnotations[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }
}

