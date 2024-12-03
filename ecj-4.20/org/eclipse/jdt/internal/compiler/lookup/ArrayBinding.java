/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public final class ArrayBinding
extends TypeBinding {
    public static final FieldBinding ArrayLength = new FieldBinding(TypeConstants.LENGTH, TypeBinding.INT, 17, null, Constant.NotAConstant);
    public TypeBinding leafComponentType;
    public int dimensions;
    LookupEnvironment environment;
    char[] constantPoolName;
    char[] genericTypeSignature;
    public long[] nullTagBitsPerDimension;
    private MethodBinding clone;

    public ArrayBinding(TypeBinding type, int dimensions, LookupEnvironment environment) {
        this.tagBits |= 1L;
        this.leafComponentType = type;
        this.dimensions = dimensions;
        this.environment = environment;
        if (type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
        } else {
            this.tagBits |= type.tagBits & 0x2000000060000880L;
        }
        long mask = type.tagBits & 0x180000000000000L;
        if (mask != 0L) {
            this.nullTagBitsPerDimension = new long[this.dimensions + 1];
            this.nullTagBitsPerDimension[this.dimensions] = mask;
            this.tagBits |= 0x100000L;
        }
    }

    @Override
    public TypeBinding closestMatch() {
        if (this.isValidBinding()) {
            return this;
        }
        TypeBinding leafClosestMatch = this.leafComponentType.closestMatch();
        if (leafClosestMatch == null) {
            return null;
        }
        return this.environment.createArrayType(this.leafComponentType.closestMatch(), this.dimensions);
    }

    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0L) {
            missingTypes = this.leafComponentType.collectMissingTypes(missingTypes);
        }
        return missingTypes;
    }

    @Override
    public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
        if ((this.tagBits & 0x20000000L) == 0L) {
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
            return;
        }
        switch (actualType.kind()) {
            case 68: {
                int actualDim = actualType.dimensions();
                if (actualDim == this.dimensions) {
                    this.leafComponentType.collectSubstitutes(scope, actualType.leafComponentType(), inferenceContext, constraint);
                    break;
                }
                if (actualDim <= this.dimensions) break;
                ArrayBinding actualReducedType = this.environment.createArrayType(actualType.leafComponentType(), actualDim - this.dimensions);
                this.leafComponentType.collectSubstitutes(scope, actualReducedType, inferenceContext, constraint);
                break;
            }
        }
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        return this.leafComponentType.mentionsAny(parameters, idx);
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        this.leafComponentType.collectInferenceVariables(variables);
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        TypeBinding substitutedLeaf = this.leafComponentType.substituteInferenceVariable(var, substituteType);
        if (TypeBinding.notEquals(substitutedLeaf, this.leafComponentType)) {
            return this.environment.createArrayType(substitutedLeaf, this.dimensions, this.typeAnnotations);
        }
        return this;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        char[] brackets = new char[this.dimensions];
        int i = this.dimensions - 1;
        while (i >= 0) {
            brackets[i] = 91;
            --i;
        }
        return CharOperation.concat(brackets, this.leafComponentType.computeUniqueKey(isLeaf));
    }

    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        char[] brackets = new char[this.dimensions];
        int i = this.dimensions - 1;
        while (i >= 0) {
            brackets[i] = 91;
            --i;
        }
        this.constantPoolName = CharOperation.concat(brackets, this.leafComponentType.signature());
        return this.constantPoolName;
    }

    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer brackets = new StringBuffer(this.dimensions * 2);
        int i = this.dimensions;
        while (--i >= 0) {
            brackets.append("[]");
        }
        return String.valueOf(this.leafComponentType.debugName()) + brackets.toString();
    }

    @Override
    public String annotatedDebugName() {
        StringBuffer brackets = new StringBuffer(this.dimensions * 2);
        brackets.append(this.leafComponentType.annotatedDebugName());
        brackets.append(' ');
        AnnotationBinding[] annotations = this.getTypeAnnotations();
        int i = 0;
        int j = -1;
        while (i < this.dimensions) {
            if (annotations != null) {
                if (i != 0) {
                    brackets.append(' ');
                }
                while (++j < annotations.length && annotations[j] != null) {
                    brackets.append(annotations[j]);
                    brackets.append(' ');
                }
            }
            brackets.append("[]");
            ++i;
        }
        return brackets.toString();
    }

    @Override
    public int dimensions() {
        return this.dimensions;
    }

    public TypeBinding elementsType() {
        if (this.dimensions == 1) {
            return this.leafComponentType;
        }
        AnnotationBinding[] oldies = this.getTypeAnnotations();
        AnnotationBinding[] newbies = Binding.NO_ANNOTATIONS;
        int i = 0;
        int length = oldies == null ? 0 : oldies.length;
        while (i < length) {
            if (oldies[i] == null) {
                newbies = new AnnotationBinding[length - i - 1];
                System.arraycopy(oldies, i + 1, newbies, 0, length - i - 1);
                break;
            }
            ++i;
        }
        return this.environment.createArrayType(this.leafComponentType, this.dimensions - 1, newbies);
    }

    @Override
    public TypeBinding erasure() {
        TypeBinding erasedType = this.leafComponentType.erasure();
        if (TypeBinding.notEquals(this.leafComponentType, erasedType)) {
            return this.environment.createArrayType(erasedType, this.dimensions);
        }
        return this;
    }

    @Override
    public ArrayBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        TypeBinding leafType = this.leafComponentType.upwardsProjection(scope, mentionedTypeVariables);
        return scope.environment().createArrayType(leafType, this.dimensions, this.typeAnnotations);
    }

    @Override
    public ArrayBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        TypeBinding leafType = this.leafComponentType.downwardsProjection(scope, mentionedTypeVariables);
        return scope.environment().createArrayType(leafType, this.dimensions, this.typeAnnotations);
    }

    public LookupEnvironment environment() {
        return this.environment;
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            char[] brackets = new char[this.dimensions];
            int i = this.dimensions - 1;
            while (i >= 0) {
                brackets[i] = 91;
                --i;
            }
            this.genericTypeSignature = CharOperation.concat(brackets, this.leafComponentType.genericTypeSignature());
        }
        return this.genericTypeSignature;
    }

    @Override
    public PackageBinding getPackage() {
        return this.leafComponentType.getPackage();
    }

    public int hashCode() {
        return this.leafComponentType == null ? super.hashCode() : this.leafComponentType.hashCode();
    }

    @Override
    public boolean isCompatibleWith(TypeBinding otherType, Scope captureScope) {
        if (ArrayBinding.equalsEquals(this, otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 68: {
                ArrayBinding otherArray = (ArrayBinding)otherType;
                if (otherArray.leafComponentType.isBaseType()) {
                    return false;
                }
                if (this.dimensions == otherArray.dimensions) {
                    return this.leafComponentType.isCompatibleWith(otherArray.leafComponentType);
                }
                if (this.dimensions >= otherArray.dimensions) break;
                return false;
            }
            case 132: {
                return false;
            }
            case 516: 
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 32772: {
                ReferenceBinding[] referenceBindingArray = ((IntersectionTypeBinding18)otherType).intersectingTypes;
                int n = ((IntersectionTypeBinding18)otherType).intersectingTypes.length;
                int n2 = 0;
                while (n2 < n) {
                    ReferenceBinding intersecting = referenceBindingArray[n2];
                    if (!this.isCompatibleWith(intersecting, captureScope)) {
                        return false;
                    }
                    ++n2;
                }
                return true;
            }
            case 4100: {
                if (otherType.isCapture()) {
                    CaptureBinding otherCapture = (CaptureBinding)otherType;
                    TypeBinding otherLowerBound = otherCapture.lowerBound;
                    if (otherLowerBound != null) {
                        if (!otherLowerBound.isArrayType()) {
                            return false;
                        }
                        return this.isCompatibleWith(otherLowerBound, captureScope);
                    }
                }
                return false;
            }
        }
        switch (otherType.leafComponentType().id) {
            case 1: 
            case 36: 
            case 37: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSubtypeOf(TypeBinding otherType, boolean simulatingBugJDK8026527) {
        if (ArrayBinding.equalsEquals(this, otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 68: {
                ArrayBinding otherArray = (ArrayBinding)otherType;
                if (otherArray.leafComponentType.isBaseType()) {
                    return false;
                }
                if (this.dimensions == otherArray.dimensions) {
                    return this.leafComponentType.isSubtypeOf(otherArray.leafComponentType, simulatingBugJDK8026527);
                }
                if (this.dimensions >= otherArray.dimensions) break;
                return false;
            }
            case 132: {
                return false;
            }
            case 32772: {
                ReferenceBinding[] referenceBindingArray = ((IntersectionTypeBinding18)otherType).intersectingTypes;
                int n = ((IntersectionTypeBinding18)otherType).intersectingTypes.length;
                int n2 = 0;
                while (n2 < n) {
                    ReferenceBinding intersecting = referenceBindingArray[n2];
                    if (!this.isSubtypeOf(intersecting, simulatingBugJDK8026527)) {
                        return false;
                    }
                    ++n2;
                }
                return true;
            }
            case 4100: {
                if (!otherType.isCapture()) break;
                CaptureBinding otherCapture = (CaptureBinding)otherType;
                TypeBinding otherLowerBound = otherCapture.lowerBound;
                if (otherLowerBound == null) break;
                if (!otherLowerBound.isArrayType()) {
                    return false;
                }
                return this.isSubtypeOf(otherLowerBound, simulatingBugJDK8026527);
            }
        }
        switch (otherType.leafComponentType().id) {
            case 1: 
            case 36: 
            case 37: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        return this.leafComponentType.isProperType(admitCapture18);
    }

    @Override
    public int kind() {
        return 68;
    }

    @Override
    public TypeBinding leafComponentType() {
        return this.leafComponentType;
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        if (this.nullTagBitsPerDimension == null) {
            return shortNames ? this.shortReadableName() : this.readableName();
        }
        char[][] brackets = new char[this.dimensions][];
        int i = 0;
        while (i < this.dimensions) {
            if ((this.nullTagBitsPerDimension[i] & 0x180000000000000L) != 0L) {
                char[][] fqAnnotationName = (this.nullTagBitsPerDimension[i] & 0x100000000000000L) != 0L ? options.nonNullAnnotationName : options.nullableAnnotationName;
                char[] annotationName = shortNames ? fqAnnotationName[fqAnnotationName.length - 1] : CharOperation.concatWith(fqAnnotationName, '.');
                brackets[i] = new char[annotationName.length + 3];
                brackets[i][0] = 64;
                System.arraycopy(annotationName, 0, brackets[i], 1, annotationName.length);
                brackets[i][annotationName.length + 1] = 91;
                brackets[i][annotationName.length + 2] = 93;
            } else {
                brackets[i] = new char[]{'[', ']'};
            }
            ++i;
        }
        return CharOperation.concat(this.leafComponentType.nullAnnotatedReadableName(options, shortNames), CharOperation.concatWith(brackets, ' '), ' ');
    }

    @Override
    public int problemId() {
        return this.leafComponentType.problemId();
    }

    @Override
    public char[] qualifiedSourceName() {
        char[] brackets = new char[this.dimensions * 2];
        int i = this.dimensions * 2 - 1;
        while (i >= 0) {
            brackets[i] = 93;
            brackets[i - 1] = 91;
            i -= 2;
        }
        return CharOperation.concat(this.leafComponentType.qualifiedSourceName(), brackets);
    }

    @Override
    public char[] readableName() {
        char[] brackets = new char[this.dimensions * 2];
        int i = this.dimensions * 2 - 1;
        while (i >= 0) {
            brackets[i] = 93;
            brackets[i - 1] = 91;
            i -= 2;
        }
        return CharOperation.concat(this.leafComponentType.readableName(), brackets);
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
        this.tagBits |= 0x200000L;
        if (annotations == null || annotations.length == 0) {
            return;
        }
        this.typeAnnotations = annotations;
        if (evalNullAnnotations) {
            long nullTagBits = 0L;
            if (this.nullTagBitsPerDimension == null) {
                this.nullTagBitsPerDimension = new long[this.dimensions + 1];
            }
            int dimension = 0;
            int i = 0;
            int length = annotations.length;
            while (i < length) {
                AnnotationBinding annotation = annotations[i];
                if (annotation != null) {
                    if (annotation.type.hasNullBit(64)) {
                        nullTagBits |= 0x80000000000000L;
                        this.tagBits |= 0x100000L;
                    } else if (annotation.type.hasNullBit(32)) {
                        nullTagBits |= 0x100000000000000L;
                        this.tagBits |= 0x100000L;
                    }
                } else {
                    if (nullTagBits != 0L) {
                        this.nullTagBitsPerDimension[dimension] = nullTagBits;
                        nullTagBits = 0L;
                    }
                    ++dimension;
                }
                ++i;
            }
            this.tagBits |= this.nullTagBitsPerDimension[0];
        }
    }

    @Override
    public char[] shortReadableName() {
        char[] brackets = new char[this.dimensions * 2];
        int i = this.dimensions * 2 - 1;
        while (i >= 0) {
            brackets[i] = 93;
            brackets[i - 1] = 91;
            i -= 2;
        }
        return CharOperation.concat(this.leafComponentType.shortReadableName(), brackets);
    }

    @Override
    public char[] sourceName() {
        char[] brackets = new char[this.dimensions * 2];
        int i = this.dimensions * 2 - 1;
        while (i >= 0) {
            brackets[i] = 93;
            brackets[i - 1] = 91;
            i -= 2;
        }
        return CharOperation.concat(this.leafComponentType.sourceName(), brackets);
    }

    @Override
    public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
        if (this.leafComponentType == unresolvedType) {
            this.leafComponentType = env.convertUnresolvedBinaryToRawType(resolvedType);
            if (this.leafComponentType != resolvedType) {
                this.id = env.createArrayType((TypeBinding)this.leafComponentType, (int)this.dimensions, (AnnotationBinding[])this.typeAnnotations).id;
            }
            this.tagBits |= this.leafComponentType.tagBits & 0x2000000060000080L;
        }
    }

    public String toString() {
        return this.leafComponentType != null ? this.debugName() : "NULL TYPE ARRAY";
    }

    @Override
    public TypeBinding unannotated() {
        return this.hasTypeAnnotations() ? this.environment.getUnannotatedType(this) : this;
    }

    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createArrayType(this.leafComponentType, this.dimensions, newAnnotations);
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        if ((this.tagBits & 0x2000000000000000L) == 0L) {
            return this;
        }
        TypeBinding leafType = this.leafComponentType.uncapture(scope);
        return scope.environment().createArrayType(leafType, this.dimensions, this.typeAnnotations);
    }

    @Override
    public boolean acceptsNonNullDefault() {
        return true;
    }

    @Override
    public long updateTagBits() {
        if (this.leafComponentType != null) {
            this.tagBits |= this.leafComponentType.updateTagBits();
        }
        return super.updateTagBits();
    }

    public MethodBinding getCloneMethod(final MethodBinding originalMethod) {
        if (this.clone != null) {
            return this.clone;
        }
        MethodBinding method = new MethodBinding(){

            @Override
            public char[] signature(ClassFile classFile) {
                return originalMethod.signature();
            }
        };
        method.modifiers = originalMethod.modifiers;
        method.selector = originalMethod.selector;
        method.declaringClass = originalMethod.declaringClass;
        method.typeVariables = Binding.NO_TYPE_VARIABLES;
        method.parameters = originalMethod.parameters;
        method.thrownExceptions = Binding.NO_EXCEPTIONS;
        method.tagBits = originalMethod.tagBits;
        TypeBinding typeBinding = method.returnType = this.environment.globalOptions.sourceLevel >= 0x310000L ? this : originalMethod.returnType;
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (this.environment.usesNullTypeAnnotations()) {
                method.returnType = this.environment.createAnnotatedType(method.returnType, new AnnotationBinding[]{this.environment.getNonNullAnnotation()});
            } else {
                method.tagBits |= 0x100000000000000L;
            }
        }
        if ((method.returnType.tagBits & 0x80L) != 0L) {
            method.tagBits |= 0x80L;
        }
        this.clone = method;
        return this.clone;
    }

    public static boolean isArrayClone(TypeBinding receiverType, MethodBinding binding) {
        if (receiverType instanceof ArrayBinding) {
            MethodBinding clone = ((ArrayBinding)receiverType).clone;
            return clone != null && binding == clone;
        }
        return false;
    }
}

