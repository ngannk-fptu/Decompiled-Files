/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class IntersectionCastTypeReference
extends TypeReference {
    public TypeReference[] typeReferences;

    public IntersectionCastTypeReference(TypeReference[] typeReferences) {
        this.typeReferences = typeReferences;
        this.sourceStart = typeReferences[0].sourceStart;
        int length = typeReferences.length;
        this.sourceEnd = typeReferences[length - 1].sourceEnd;
        int i = 0;
        int max = typeReferences.length;
        while (i < max) {
            if ((typeReferences[i].bits & 0x100000) != 0) {
                this.bits |= 0x100000;
                break;
            }
            ++i;
        }
    }

    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char[] getLastToken() {
        return null;
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        return null;
    }

    @Override
    public TypeReference[] getTypeReferences() {
        return this.typeReferences;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public TypeBinding resolveType(BlockScope scope, boolean checkBounds, int location) {
        length = this.typeReferences.length;
        intersectingTypes = new ReferenceBinding[length];
        hasError = false;
        typeCount = 0;
        i = 0;
        while (i < length) {
            block17: {
                block19: {
                    block20: {
                        block18: {
                            typeReference = this.typeReferences[i];
                            type = typeReference.resolveType(scope, checkBounds, location);
                            if (type != null && (type.tagBits & 128L) == 0L) break block18;
                            hasError = true;
                            break block17;
                        }
                        if (i != 0) break block19;
                        if (!type.isBaseType()) break block20;
                        scope.problemReporter().onlyReferenceTypesInIntersectionCast(typeReference);
                        hasError = true;
                        break block17;
                    }
                    if (!type.isArrayType()) ** GOTO lbl-1000
                    scope.problemReporter().illegalArrayTypeInIntersectionCast(typeReference);
                    hasError = true;
                    break block17;
                }
                if (!type.isInterface()) {
                    scope.problemReporter().boundMustBeAnInterface(typeReference, type);
                    hasError = true;
                } else lbl-1000:
                // 2 sources

                {
                    j = 0;
                    while (j < typeCount) {
                        priorType = intersectingTypes[j];
                        if (TypeBinding.equalsEquals(priorType, type)) {
                            scope.problemReporter().duplicateBoundInIntersectionCast(typeReference);
                            hasError = true;
                        } else if (priorType.isInterface()) {
                            if (TypeBinding.equalsEquals(type.findSuperTypeOriginatingFrom(priorType), priorType)) {
                                intersectingTypes[j] = (ReferenceBinding)type;
                                break block17;
                            }
                            if (TypeBinding.equalsEquals(priorType.findSuperTypeOriginatingFrom(type), type)) break block17;
                        }
                        ++j;
                    }
                    intersectingTypes[typeCount++] = (ReferenceBinding)type;
                }
            }
            ++i;
        }
        if (hasError) {
            return null;
        }
        if (typeCount != length) {
            if (typeCount == 1) {
                this.resolvedType = intersectingTypes[0];
                return this.resolvedType;
            }
            v0 = intersectingTypes;
            intersectingTypes = new ReferenceBinding[typeCount];
            System.arraycopy(v0, 0, intersectingTypes, 0, typeCount);
        }
        intersectionType = (IntersectionTypeBinding18)scope.environment().createIntersectionType18(intersectingTypes);
        itsSuperclass = null;
        interfaces = intersectingTypes;
        firstType = intersectingTypes[0];
        if (firstType.isClass()) {
            itsSuperclass = firstType.superclass();
            interfaces = new ReferenceBinding[typeCount - 1];
            System.arraycopy(intersectingTypes, 1, interfaces, 0, typeCount - 1);
        }
        invocations = new HashMap<K, V>(2);
        i = 0;
        interfaceCount = interfaces.length;
        while (i < interfaceCount) {
            one = interfaces[i];
            if (!(one == null || itsSuperclass != null && scope.hasErasedCandidatesCollisions(itsSuperclass, one, invocations, intersectionType, this))) {
                j = 0;
                while (j < i) {
                    two = interfaces[j];
                    if (two != null && scope.hasErasedCandidatesCollisions(one, two, invocations, intersectionType, this)) break;
                    ++j;
                }
            }
            ++i;
        }
        if ((intersectionType.tagBits & 131072L) != 0L) {
            return null;
        }
        this.resolvedType = intersectionType;
        return this.resolvedType;
    }

    @Override
    public char[][] getTypeName() {
        return this.typeReferences[0].getTypeName();
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int length = this.typeReferences == null ? 0 : this.typeReferences.length;
            int i = 0;
            while (i < length) {
                this.typeReferences[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        throw new UnsupportedOperationException("Unexpected traversal request: IntersectionTypeReference in class scope");
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        int length = this.typeReferences == null ? 0 : this.typeReferences.length;
        IntersectionCastTypeReference.printIndent(indent, output);
        int i = 0;
        while (i < length) {
            this.typeReferences[i].printExpression(0, output);
            if (i != length - 1) {
                output.append(" & ");
            }
            ++i;
        }
        return output;
    }
}

