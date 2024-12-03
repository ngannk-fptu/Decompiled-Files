/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ParameterizedQualifiedTypeReference
extends ArrayQualifiedTypeReference {
    public TypeReference[][] typeArguments;
    ReferenceBinding[] typesPerToken;

    public ParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, int dim, long[] positions) {
        super(tokens, dim, positions);
        this.typeArguments = typeArguments;
        int i = 0;
        int max = typeArguments.length;
        block0: while (i < max) {
            TypeReference[] typeArgumentsOnTypeComponent = typeArguments[i];
            if (typeArgumentsOnTypeComponent != null) {
                int j = 0;
                int max2 = typeArgumentsOnTypeComponent.length;
                while (j < max2) {
                    if ((typeArgumentsOnTypeComponent[j].bits & 0x100000) != 0) {
                        this.bits |= 0x100000;
                        break block0;
                    }
                    ++j;
                }
            }
            ++i;
        }
    }

    public ParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, int dim, Annotation[][] annotationsOnDimensions, long[] positions) {
        this(tokens, typeArguments, dim, positions);
        this.setAnnotationsOnDimensions(annotationsOnDimensions);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
    }

    @Override
    public void checkBounds(Scope scope) {
        if (this.resolvedType == null || !this.resolvedType.isValidBinding()) {
            return;
        }
        this.checkBounds((ReferenceBinding)this.resolvedType.leafComponentType(), scope, this.typeArguments.length - 1);
    }

    public void checkBounds(ReferenceBinding type, Scope scope, int index) {
        ParameterizedTypeBinding parameterizedType;
        ReferenceBinding currentType;
        TypeVariableBinding[] typeVariables;
        ReferenceBinding enclosingType;
        if (index > 0 && (enclosingType = this.typesPerToken[index - 1]) != null) {
            this.checkBounds(enclosingType, scope, index - 1);
        }
        if (type.isParameterizedTypeWithActualArguments() && (typeVariables = (currentType = (parameterizedType = (ParameterizedTypeBinding)type).genericType()).typeVariables()) != null) {
            parameterizedType.boundCheck(scope, this.typeArguments[index]);
        }
    }

    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        int totalDimensions = this.dimensions() + additionalDimensions;
        Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        ParameterizedQualifiedTypeReference pqtr = new ParameterizedQualifiedTypeReference(this.tokens, this.typeArguments, totalDimensions, allAnnotations, this.sourcePositions);
        pqtr.annotations = this.annotations;
        pqtr.bits |= this.bits & 0x100000;
        if (!isVarargs) {
            pqtr.extendedDimensions = additionalDimensions;
        }
        return pqtr;
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
                    TypeReference[] arguments = this.typeArguments[i];
                    if (arguments != null) {
                        int j = 0;
                        while (j < arguments.length) {
                            if (arguments[j].hasNullTypeAnnotation(position)) {
                                return true;
                            }
                            ++j;
                        }
                    }
                    ++i;
                }
            }
        }
        return false;
    }

    @Override
    public char[][] getParameterizedTypeName() {
        int length = this.tokens.length;
        char[][] qParamName = new char[length][];
        int i = 0;
        while (i < length) {
            TypeReference[] arguments = this.typeArguments[i];
            if (arguments == null) {
                qParamName[i] = this.tokens[i];
            } else {
                StringBuffer buffer = new StringBuffer(5);
                buffer.append(this.tokens[i]);
                buffer.append('<');
                int j = 0;
                int argLength = arguments.length;
                while (j < argLength) {
                    if (j > 0) {
                        buffer.append(',');
                    }
                    buffer.append(CharOperation.concatWith(arguments[j].getParameterizedTypeName(), '.'));
                    ++j;
                }
                buffer.append('>');
                int nameLength = buffer.length();
                qParamName[i] = new char[nameLength];
                buffer.getChars(0, nameLength, qParamName[i], 0);
            }
            ++i;
        }
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
            qParamName[length - 1] = CharOperation.concat(qParamName[length - 1], dimChars);
        }
        return qParamName;
    }

    @Override
    public TypeReference[][] getTypeArguments() {
        return this.typeArguments;
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        return null;
    }

    private TypeBinding internalResolveType(Scope scope, boolean checkBounds, int location) {
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
        TypeBinding type = this.internalResolveLeafType(scope, checkBounds);
        this.createArrayType(scope);
        this.resolveAnnotations(scope, location);
        if (this.dimensions > 0) {
            this.resolvedType = ArrayTypeReference.maybeMarkArrayContentsNonNull(scope, this.resolvedType, this.sourceStart, this.dimensions, null);
        }
        if (this.typeArguments != null) {
            this.checkIllegalNullAnnotations(scope, this.typeArguments[this.typeArguments.length - 1]);
        }
        return type == null ? type : this.resolvedType;
    }

    private TypeBinding internalResolveLeafType(Scope scope, boolean checkBounds) {
        boolean isClassScope = scope.kind == 3;
        Binding binding = scope.getPackage(this.tokens);
        if (binding != null && !binding.isValidBinding()) {
            this.resolvedType = (ReferenceBinding)binding;
            this.reportInvalidType(scope);
            int i = 0;
            int max = this.tokens.length;
            while (i < max) {
                TypeReference[] args = this.typeArguments[i];
                if (args != null) {
                    int argLength = args.length;
                    int j = 0;
                    while (j < argLength) {
                        TypeReference typeArgument = args[j];
                        if (isClassScope) {
                            typeArgument.resolveType((ClassScope)scope);
                        } else {
                            typeArgument.resolveType((BlockScope)scope, checkBounds);
                        }
                        ++j;
                    }
                }
                ++i;
            }
            return null;
        }
        PackageBinding packageBinding = binding == null ? null : (PackageBinding)binding;
        this.rejectAnnotationsOnPackageQualifiers(scope, packageBinding);
        boolean typeIsConsistent = true;
        ReferenceBinding qualifyingType = null;
        int max = this.tokens.length;
        this.typesPerToken = new ReferenceBinding[max];
        int i = packageBinding == null ? 0 : packageBinding.compoundName.length;
        while (i < max) {
            TypeReference[] args;
            this.findNextTypeBinding(i, scope, packageBinding);
            if (!this.resolvedType.isValidBinding()) {
                this.reportInvalidType(scope);
                int j = i;
                while (j < max) {
                    args = this.typeArguments[j];
                    if (args != null) {
                        int argLength = args.length;
                        int k = 0;
                        while (k < argLength) {
                            TypeReference typeArgument = args[k];
                            if (isClassScope) {
                                typeArgument.resolveType((ClassScope)scope);
                            } else {
                                typeArgument.resolveType((BlockScope)scope);
                            }
                            ++k;
                        }
                    }
                    ++j;
                }
                return null;
            }
            ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
            if (qualifyingType == null) {
                qualifyingType = currentType.enclosingType();
                if (qualifyingType != null && currentType.hasEnclosingInstanceContext()) {
                    qualifyingType = scope.environment().convertToParameterizedType(qualifyingType);
                }
            } else {
                ReferenceBinding enclosingType;
                if (this.annotations != null) {
                    ParameterizedQualifiedTypeReference.rejectAnnotationsOnStaticMemberQualififer(scope, currentType, this.annotations[i - 1]);
                }
                if (typeIsConsistent && currentType.isStatic() && (qualifyingType.isParameterizedTypeWithActualArguments() || qualifyingType.isGenericType())) {
                    scope.problemReporter().staticMemberOfParameterizedType(this, currentType, qualifyingType, i);
                    typeIsConsistent = false;
                    qualifyingType = qualifyingType.actualType();
                }
                if ((enclosingType = currentType.enclosingType()) != null && TypeBinding.notEquals(enclosingType.erasure(), qualifyingType.erasure())) {
                    qualifyingType = enclosingType;
                }
            }
            if ((args = this.typeArguments[i]) != null) {
                TypeVariableBinding[] typeVariables;
                int argLength;
                TypeReference keep = null;
                if (isClassScope) {
                    keep = ((ClassScope)scope).superTypeReference;
                    ((ClassScope)scope).superTypeReference = null;
                }
                boolean isDiamond = (argLength = args.length) == 0 && i == max - 1 && (this.bits & 0x80000) != 0;
                TypeBinding[] argTypes = new TypeBinding[argLength];
                boolean argHasError = false;
                ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
                int j = 0;
                while (j < argLength) {
                    TypeBinding argType;
                    TypeReference arg = args[j];
                    TypeBinding typeBinding = argType = isClassScope ? arg.resolveTypeArgument((ClassScope)scope, currentOriginal, j) : arg.resolveTypeArgument((BlockScope)scope, currentOriginal, j);
                    if (argType == null) {
                        argHasError = true;
                    } else {
                        argTypes[j] = argType;
                    }
                    ++j;
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
                    if (scope.compilerOptions().originalSourceLevel >= 0x310000L) {
                        scope.problemReporter().nonGenericTypeCannotBeParameterized(i, this, currentType, argTypes);
                        return null;
                    }
                    this.resolvedType = qualifyingType != null && qualifyingType.isParameterizedType() ? scope.environment().createParameterizedType(currentOriginal, null, qualifyingType) : currentType;
                    return this.resolvedType;
                }
                if (argLength != typeVariables.length && !isDiamond) {
                    scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes, i);
                    return null;
                }
                if (typeIsConsistent) {
                    if (!currentType.hasEnclosingInstanceContext()) {
                        if (qualifyingType != null && qualifyingType.isRawType()) {
                            this.typesPerToken[i - 1] = qualifyingType = qualifyingType.actualType();
                        }
                    } else {
                        ReferenceBinding actualEnclosing = currentType.enclosingType();
                        if (actualEnclosing != null && actualEnclosing.isRawType()) {
                            scope.problemReporter().rawMemberTypeCannotBeParameterized(this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
                            typeIsConsistent = false;
                        }
                    }
                }
                ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, qualifyingType);
                if (!isDiamond) {
                    if (checkBounds) {
                        parameterizedType.boundCheck(scope, args);
                    } else {
                        scope.deferBoundCheck(this);
                    }
                } else {
                    parameterizedType.arguments = ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS;
                }
                qualifyingType = parameterizedType;
            } else {
                ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
                if (isClassScope && ((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
                    return null;
                }
                if (currentOriginal.isGenericType()) {
                    if (typeIsConsistent && qualifyingType != null && qualifyingType.isParameterizedType() && currentOriginal.hasEnclosingInstanceContext()) {
                        scope.problemReporter().parameterizedMemberTypeMissingArguments(this, scope.environment().createParameterizedType(currentOriginal, null, qualifyingType), i);
                        typeIsConsistent = false;
                    }
                    qualifyingType = scope.environment().createRawType(currentOriginal, qualifyingType);
                } else {
                    qualifyingType = scope.environment().maybeCreateParameterizedType(currentOriginal, qualifyingType);
                }
            }
            if (this.isTypeUseDeprecated(qualifyingType, scope)) {
                this.reportDeprecatedType(qualifyingType, scope, i);
            }
            this.resolvedType = qualifyingType;
            this.typesPerToken[i] = qualifyingType;
            this.recordResolution(scope.environment(), this.resolvedType);
            ++i;
        }
        return this.resolvedType;
    }

    private void createArrayType(Scope scope) {
        if (this.dimensions > 0) {
            if (this.dimensions > 255) {
                scope.problemReporter().tooManyDimensions(this);
            }
            this.resolvedType = scope.createArrayType(this.resolvedType, this.dimensions);
        }
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        int i;
        int length = this.tokens.length;
        int i2 = 0;
        while (i2 < length - 1) {
            if (this.annotations != null && this.annotations[i2] != null) {
                ParameterizedQualifiedTypeReference.printAnnotations(this.annotations[i2], output);
                output.append(' ');
            }
            output.append(this.tokens[i2]);
            TypeReference[] typeArgument = this.typeArguments[i2];
            if (typeArgument != null) {
                output.append('<');
                int typeArgumentLength = typeArgument.length;
                if (typeArgumentLength > 0) {
                    int max = typeArgumentLength - 1;
                    int j = 0;
                    while (j < max) {
                        typeArgument[j].print(0, output);
                        output.append(", ");
                        ++j;
                    }
                    typeArgument[max].print(0, output);
                }
                output.append('>');
            }
            output.append('.');
            ++i2;
        }
        if (this.annotations != null && this.annotations[length - 1] != null) {
            output.append(" ");
            ParameterizedQualifiedTypeReference.printAnnotations(this.annotations[length - 1], output);
            output.append(' ');
        }
        output.append(this.tokens[length - 1]);
        TypeReference[] typeArgument = this.typeArguments[length - 1];
        if (typeArgument != null) {
            output.append('<');
            int typeArgumentLength = typeArgument.length;
            if (typeArgumentLength > 0) {
                int max = typeArgumentLength - 1;
                int j = 0;
                while (j < max) {
                    typeArgument[j].print(0, output);
                    output.append(", ");
                    ++j;
                }
                typeArgument[max].print(0, output);
            }
            output.append('>');
        }
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if ((this.bits & 0x4000) != 0) {
            i = 0;
            while (i < this.dimensions - 1) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ParameterizedQualifiedTypeReference.printAnnotations(annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
                ++i;
            }
            if (annotationsOnDimensions != null && annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(" ");
                ParameterizedQualifiedTypeReference.printAnnotations(annotationsOnDimensions[this.dimensions - 1], output);
                output.append(" ");
            }
            output.append("...");
        } else {
            i = 0;
            while (i < this.dimensions) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ParameterizedQualifiedTypeReference.printAnnotations(annotationsOnDimensions[i], output);
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
        return this.internalResolveType(scope, checkBounds, location);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope, int location) {
        return this.internalResolveType(scope, false, location);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int max;
            Annotation[][] annotationsOnDimensions;
            int i;
            if (this.annotations != null) {
                int annotationsLevels = this.annotations.length;
                i = 0;
                while (i < annotationsLevels) {
                    int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                    int j = 0;
                    while (j < annotationsLength) {
                        this.annotations[i][j].traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
            if ((annotationsOnDimensions = this.getAnnotationsOnDimensions(true)) != null) {
                i = 0;
                max = annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = annotationsOnDimensions[i];
                    int j = 0;
                    int max2 = annotations2 == null ? 0 : annotations2.length;
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
                if (this.typeArguments[i] != null) {
                    int j = 0;
                    int max2 = this.typeArguments[i].length;
                    while (j < max2) {
                        this.typeArguments[i][j].traverse(visitor, scope);
                        ++j;
                    }
                }
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
                int annotationsLevels = this.annotations.length;
                i = 0;
                while (i < annotationsLevels) {
                    int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                    int j = 0;
                    while (j < annotationsLength) {
                        this.annotations[i][j].traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
            if ((annotationsOnDimensions = this.getAnnotationsOnDimensions(true)) != null) {
                i = 0;
                max = annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = annotationsOnDimensions[i];
                    int j = 0;
                    int max2 = annotations2 == null ? 0 : annotations2.length;
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
                if (this.typeArguments[i] != null) {
                    int j = 0;
                    int max2 = this.typeArguments[i].length;
                    while (j < max2) {
                        this.typeArguments[i][j].traverse(visitor, scope);
                        ++j;
                    }
                }
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }
}

