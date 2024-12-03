/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ParameterizedSingleTypeReference
extends ArrayTypeReference {
    public static final TypeBinding[] DIAMOND_TYPE_ARGUMENTS = new TypeBinding[0];
    public TypeReference[] typeArguments;

    public ParameterizedSingleTypeReference(char[] name, TypeReference[] typeArguments, int dim, long pos) {
        super(name, dim, pos);
        this.originalSourceEnd = this.sourceEnd;
        this.typeArguments = typeArguments;
        int i = 0;
        int max = typeArguments.length;
        while (i < max) {
            if ((typeArguments[i].bits & 0x100000) != 0) {
                this.bits |= 0x100000;
                break;
            }
            ++i;
        }
    }

    public ParameterizedSingleTypeReference(char[] name, TypeReference[] typeArguments, int dim, Annotation[][] annotationsOnDimensions, long pos) {
        this(name, typeArguments, dim, pos);
        this.setAnnotationsOnDimensions(annotationsOnDimensions);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
    }

    @Override
    public void checkBounds(Scope scope) {
        if (this.resolvedType == null) {
            return;
        }
        if (this.resolvedType.leafComponentType() instanceof ParameterizedTypeBinding) {
            ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)this.resolvedType.leafComponentType();
            TypeBinding[] argTypes = parameterizedType.arguments;
            if (argTypes != null) {
                parameterizedType.boundCheck(scope, this.typeArguments);
            }
        }
    }

    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        int totalDimensions = this.dimensions() + additionalDimensions;
        Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.token, this.typeArguments, totalDimensions, allAnnotations, ((long)this.sourceStart << 32) + (long)this.sourceEnd);
        parameterizedSingleTypeReference.annotations = this.annotations;
        parameterizedSingleTypeReference.bits |= this.bits & 0x100000;
        if (!isVarargs) {
            parameterizedSingleTypeReference.extendedDimensions = additionalDimensions;
        }
        return parameterizedSingleTypeReference;
    }

    @Override
    public char[][] getParameterizedTypeName() {
        StringBuffer buffer = new StringBuffer(5);
        buffer.append(this.token).append('<');
        int i = 0;
        int length = this.typeArguments.length;
        while (i < length) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(CharOperation.concatWith(this.typeArguments[i].getParameterizedTypeName(), '.'));
            ++i;
        }
        buffer.append('>');
        int nameLength = buffer.length();
        char[] name = new char[nameLength];
        buffer.getChars(0, nameLength, name, 0);
        int dim = this.dimensions;
        if (dim > 0) {
            char[] dimChars = new char[dim * 2];
            int i2 = 0;
            while (i2 < dim) {
                int index = i2 * 2;
                dimChars[index] = 91;
                dimChars[index + 1] = 93;
                ++i2;
            }
            name = CharOperation.concat(name, dimChars);
        }
        return new char[][]{name};
    }

    @Override
    public TypeReference[][] getTypeArguments() {
        return new TypeReference[][]{this.typeArguments};
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        return null;
    }

    @Override
    public boolean isParameterizedTypeReference() {
        return true;
    }

    @Override
    public boolean hasNullTypeAnnotation(TypeReference.AnnotationPosition position) {
        if (super.hasNullTypeAnnotation(position)) {
            return true;
        }
        if (position == TypeReference.AnnotationPosition.ANY) {
            if (this.resolvedType != null && !this.resolvedType.hasNullTypeAnnotations()) {
                return false;
            }
            if (this.typeArguments != null) {
                int i = 0;
                while (i < this.typeArguments.length) {
                    if (this.typeArguments[i].hasNullTypeAnnotation(position)) {
                        return true;
                    }
                    ++i;
                }
            }
        }
        return false;
    }

    private TypeBinding internalResolveType(Scope scope, ReferenceBinding enclosingType, boolean checkBounds, int location) {
        this.constant = Constant.NotAConstant;
        if ((this.bits & 0x40000) != 0 && this.resolvedType != null) {
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
        this.bits |= 0x40000;
        TypeBinding type = this.internalResolveLeafType(scope, enclosingType, checkBounds);
        if (type == null) {
            this.resolvedType = this.createArrayType(scope, this.resolvedType);
            this.resolveAnnotations(scope, 0);
            return null;
        }
        type = this.createArrayType(scope, type);
        if (!this.resolvedType.isValidBinding() && this.resolvedType.dimensions() == type.dimensions()) {
            this.resolveAnnotations(scope, 0);
            return type;
        }
        this.resolvedType = type;
        this.resolveAnnotations(scope, location);
        if (this.dimensions > 0) {
            this.resolvedType = ArrayTypeReference.maybeMarkArrayContentsNonNull(scope, this.resolvedType, this.sourceStart, this.dimensions, leafType -> {
                TypeBinding typeBinding = this.leafComponentTypeWithoutDefaultNullness = leafType;
            });
        }
        return this.resolvedType;
    }

    private TypeBinding internalResolveLeafType(Scope scope, ReferenceBinding enclosingType, boolean checkBounds) {
        ReferenceBinding actualEnclosing;
        TypeVariableBinding[] typeVariables;
        ReferenceBinding currentType;
        if (enclosingType == null) {
            this.resolvedType = scope.getType(this.token);
            if (this.resolvedType.isValidBinding()) {
                currentType = (ReferenceBinding)this.resolvedType;
            } else {
                this.reportInvalidType(scope);
                switch (this.resolvedType.problemId()) {
                    case 1: 
                    case 2: 
                    case 5: {
                        TypeBinding type = this.resolvedType.closestMatch();
                        if (type instanceof ReferenceBinding) {
                            currentType = (ReferenceBinding)type;
                            break;
                        }
                    }
                    default: {
                        boolean isClassScope = scope.kind == 3;
                        int argLength = this.typeArguments.length;
                        int i = 0;
                        while (i < argLength) {
                            TypeReference typeArgument = this.typeArguments[i];
                            if (isClassScope) {
                                typeArgument.resolveType((ClassScope)scope);
                            } else {
                                typeArgument.resolveType((BlockScope)scope, checkBounds);
                            }
                            ++i;
                        }
                        return null;
                    }
                }
            }
            enclosingType = currentType.enclosingType();
            if (enclosingType != null && currentType.hasEnclosingInstanceContext()) {
                enclosingType = scope.environment().convertToParameterizedType(enclosingType);
            }
        } else {
            ReferenceBinding currentEnclosing;
            currentType = scope.getMemberType(this.token, enclosingType);
            this.resolvedType = currentType;
            if (!this.resolvedType.isValidBinding()) {
                scope.problemReporter().invalidEnclosingType(this, currentType, enclosingType);
                return null;
            }
            if (this.isTypeUseDeprecated(currentType, scope)) {
                scope.problemReporter().deprecatedType(currentType, this);
            }
            if ((currentEnclosing = currentType.enclosingType()) != null && TypeBinding.notEquals(currentEnclosing.erasure(), enclosingType.erasure())) {
                enclosingType = currentEnclosing;
            }
        }
        boolean isClassScope = scope.kind == 3;
        TypeReference keep = null;
        if (isClassScope) {
            keep = ((ClassScope)scope).superTypeReference;
            ((ClassScope)scope).superTypeReference = null;
        }
        boolean isDiamond = (this.bits & 0x80000) != 0;
        int argLength = this.typeArguments.length;
        TypeBinding[] argTypes = new TypeBinding[argLength];
        boolean argHasError = false;
        ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
        int i = 0;
        while (i < argLength) {
            TypeReference typeArgument = this.typeArguments[i];
            TypeBinding argType = isClassScope ? typeArgument.resolveTypeArgument((ClassScope)scope, currentOriginal, i) : typeArgument.resolveTypeArgument((BlockScope)scope, currentOriginal, i);
            this.bits |= typeArgument.bits & 0x100000;
            if (argType == null) {
                argHasError = true;
            } else {
                argTypes[i] = argType;
            }
            ++i;
        }
        if (argHasError) {
            return null;
        }
        if (isClassScope) {
            ((ClassScope)scope).superTypeReference = keep;
            if (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
                return null;
            }
        }
        if ((typeVariables = currentOriginal.typeVariables()) == Binding.NO_TYPE_VARIABLES) {
            boolean isCompliant15;
            boolean bl = isCompliant15 = scope.compilerOptions().originalSourceLevel >= 0x310000L;
            if ((currentOriginal.tagBits & 0x80L) == 0L && isCompliant15) {
                this.resolvedType = currentType;
                scope.problemReporter().nonGenericTypeCannotBeParameterized(0, this, currentType, argTypes);
                return null;
            }
            if (!isCompliant15) {
                if (!this.resolvedType.isValidBinding()) {
                    return currentType;
                }
                this.resolvedType = currentType;
                return this.resolvedType;
            }
        } else if (argLength != typeVariables.length) {
            if (!isDiamond) {
                scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes);
                return null;
            }
        } else if (!currentType.isStatic() && (actualEnclosing = currentType.enclosingType()) != null && actualEnclosing.isRawType()) {
            scope.problemReporter().rawMemberTypeCannotBeParameterized(this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
            return null;
        }
        ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, enclosingType);
        if (!isDiamond) {
            if (checkBounds) {
                parameterizedType.boundCheck(scope, this.typeArguments);
            } else {
                scope.deferBoundCheck(this);
            }
        } else {
            parameterizedType.arguments = DIAMOND_TYPE_ARGUMENTS;
        }
        if (this.isTypeUseDeprecated(parameterizedType, scope)) {
            this.reportDeprecatedType(parameterizedType, scope);
        }
        this.checkIllegalNullAnnotations(scope, this.typeArguments);
        if (!this.resolvedType.isValidBinding()) {
            return parameterizedType;
        }
        this.resolvedType = parameterizedType;
        return this.resolvedType;
    }

    private TypeBinding createArrayType(Scope scope, TypeBinding type) {
        if (this.dimensions > 0) {
            if (this.dimensions > 255) {
                scope.problemReporter().tooManyDimensions(this);
            }
            return scope.createArrayType(type, this.dimensions);
        }
        return type;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        int i;
        if (this.annotations != null && this.annotations[0] != null) {
            ParameterizedSingleTypeReference.printAnnotations(this.annotations[0], output);
            output.append(' ');
        }
        output.append(this.token);
        output.append("<");
        int length = this.typeArguments.length;
        if (length > 0) {
            int max = length - 1;
            i = 0;
            while (i < max) {
                this.typeArguments[i].print(0, output);
                output.append(", ");
                ++i;
            }
            this.typeArguments[max].print(0, output);
        }
        output.append(">");
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if ((this.bits & 0x4000) != 0) {
            i = 0;
            while (i < this.dimensions - 1) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ParameterizedSingleTypeReference.printAnnotations(annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
                ++i;
            }
            if (annotationsOnDimensions != null && annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(" ");
                ParameterizedSingleTypeReference.printAnnotations(annotationsOnDimensions[this.dimensions - 1], output);
                output.append(" ");
            }
            output.append("...");
        } else {
            i = 0;
            while (i < this.dimensions) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ParameterizedSingleTypeReference.printAnnotations(annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
                ++i;
            }
        }
        return output;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope, boolean checkBounds, int location) {
        return this.internalResolveType(scope, null, checkBounds, location);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope, int location) {
        return this.internalResolveType(scope, null, false, location);
    }

    @Override
    public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
        return this.internalResolveType(scope, enclosingType, true, 0);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int max;
            Annotation[][] annotationsOnDimensions;
            int i;
            if (this.annotations != null) {
                Annotation[] typeAnnotations = this.annotations[0];
                i = 0;
                int length = typeAnnotations == null ? 0 : typeAnnotations.length;
                while (i < length) {
                    typeAnnotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if ((annotationsOnDimensions = this.getAnnotationsOnDimensions(true)) != null) {
                i = 0;
                max = annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = annotationsOnDimensions[i];
                    if (annotations2 != null) {
                        int j = 0;
                        int max2 = annotations2.length;
                        while (j < max2) {
                            Annotation annotation = annotations2[j];
                            annotation.traverse(visitor, scope);
                            ++j;
                        }
                    }
                    ++i;
                }
            }
            i = 0;
            max = this.typeArguments.length;
            while (i < max) {
                this.typeArguments[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            int max;
            Annotation[][] annotationsOnDimensions;
            int i;
            if (this.annotations != null) {
                Annotation[] typeAnnotations = this.annotations[0];
                i = 0;
                int length = typeAnnotations == null ? 0 : typeAnnotations.length;
                while (i < length) {
                    typeAnnotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if ((annotationsOnDimensions = this.getAnnotationsOnDimensions(true)) != null) {
                i = 0;
                max = annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = annotationsOnDimensions[i];
                    int j = 0;
                    int max2 = annotations2.length;
                    while (j < max2) {
                        Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
            i = 0;
            max = this.typeArguments.length;
            while (i < max) {
                this.typeArguments[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }
}

