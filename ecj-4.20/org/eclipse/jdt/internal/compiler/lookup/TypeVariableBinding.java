/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceSubstitution;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBound;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class TypeVariableBinding
extends ReferenceBinding {
    public Binding declaringElement;
    public int rank;
    public TypeBinding firstBound;
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    public char[] genericTypeSignature;
    LookupEnvironment environment;
    boolean inRecursiveFunction = false;
    boolean inRecursiveProjectionFunction = false;

    public TypeVariableBinding(char[] sourceName, Binding declaringElement, int rank, LookupEnvironment environment) {
        this.sourceName = sourceName;
        this.declaringElement = declaringElement;
        this.rank = rank;
        this.modifiers = 0x40000001;
        this.tagBits |= 0x20000000L;
        this.environment = environment;
        this.typeBits = 0x8000000;
        this.computeId(environment);
    }

    protected TypeVariableBinding(char[] sourceName, LookupEnvironment environment) {
        this.sourceName = sourceName;
        this.modifiers = 0x40000001;
        this.tagBits |= 0x20000000L;
        this.environment = environment;
        this.typeBits = 0x8000000;
    }

    public TypeVariableBinding(TypeVariableBinding prototype) {
        super(prototype);
        this.declaringElement = prototype.declaringElement;
        this.rank = prototype.rank;
        this.firstBound = prototype.firstBound;
        this.superclass = prototype.superclass;
        if (prototype.superInterfaces != null) {
            int len = prototype.superInterfaces.length;
            if (len > 0) {
                this.superInterfaces = new ReferenceBinding[len];
                System.arraycopy(prototype.superInterfaces, 0, this.superInterfaces, 0, len);
            } else {
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
            }
        }
        this.genericTypeSignature = prototype.genericTypeSignature;
        this.environment = prototype.environment;
        prototype.tagBits |= 0x800000L;
        this.tagBits &= 0xFFFFFFFFFF7FFFFFL;
    }

    public TypeConstants.BoundCheckStatus boundCheck(Substitution substitution, TypeBinding argumentType, Scope scope, ASTNode location) {
        TypeBinding bound;
        TypeConstants.BoundCheckStatus code = this.internalBoundCheck(substitution, argumentType, scope, location);
        if (code == TypeConstants.BoundCheckStatus.MISMATCH && argumentType instanceof TypeVariableBinding && scope != null && (bound = ((TypeVariableBinding)argumentType).firstBound) instanceof ParameterizedTypeBinding) {
            TypeConstants.BoundCheckStatus code2 = this.boundCheck(substitution, bound.capture(scope, -1, -1), scope, location);
            return code.betterOf(code2);
        }
        return code;
    }

    private TypeConstants.BoundCheckStatus internalBoundCheck(Substitution substitution, TypeBinding argumentType, Scope scope, ASTNode location) {
        long nullBits;
        boolean checkNullAnnotations;
        boolean hasSubstitution;
        if (argumentType == TypeBinding.NULL || TypeBinding.equalsEquals(argumentType, this)) {
            return TypeConstants.BoundCheckStatus.OK;
        }
        boolean bl = hasSubstitution = substitution != null;
        if (!(argumentType instanceof ReferenceBinding) && !argumentType.isArrayType()) {
            return TypeConstants.BoundCheckStatus.MISMATCH;
        }
        if (this.superclass == null) {
            return TypeConstants.BoundCheckStatus.OK;
        }
        TypeConstants.BoundCheckStatus nullStatus = TypeConstants.BoundCheckStatus.OK;
        boolean bl2 = checkNullAnnotations = scope.environment().usesNullTypeAnnotations() && (location == null || (location.bits & 0x8000) == 0);
        if (argumentType.kind() == 516) {
            WildcardBinding wildcard = (WildcardBinding)argumentType;
            switch (wildcard.boundKind) {
                case 1: {
                    boolean checkedAsOK = false;
                    TypeBinding wildcardBound = wildcard.bound;
                    if (TypeBinding.equalsEquals(wildcardBound, this)) {
                        checkedAsOK = true;
                    }
                    boolean isArrayBound = wildcardBound.isArrayType();
                    if (!wildcardBound.isInterface()) {
                        ReferenceBinding substitutedSuperType;
                        TypeBinding typeBinding = substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
                        if (!checkedAsOK && substitutedSuperType.id != 1) {
                            if (isArrayBound) {
                                if (!wildcardBound.isCompatibleWith(substitutedSuperType, scope)) {
                                    return TypeConstants.BoundCheckStatus.MISMATCH;
                                }
                            } else {
                                TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
                                if (match != null) {
                                    if (substitutedSuperType.isProvablyDistinct(match)) {
                                        return TypeConstants.BoundCheckStatus.MISMATCH;
                                    }
                                } else {
                                    match = substitutedSuperType.findSuperTypeOriginatingFrom(wildcardBound);
                                    if (match != null) {
                                        if (match.isProvablyDistinct(wildcardBound)) {
                                            return TypeConstants.BoundCheckStatus.MISMATCH;
                                        }
                                    } else {
                                        if (this.denotesRelevantSuperClass(wildcardBound) && this.denotesRelevantSuperClass(substitutedSuperType)) {
                                            return TypeConstants.BoundCheckStatus.MISMATCH;
                                        }
                                        if (Scope.greaterLowerBound(new TypeBinding[]{substitutedSuperType, wildcardBound}, scope, this.environment) == null) {
                                            return TypeConstants.BoundCheckStatus.MISMATCH;
                                        }
                                    }
                                }
                            }
                        }
                        if (checkNullAnnotations && argumentType.hasNullTypeAnnotations()) {
                            nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType, substitution, location, nullStatus);
                        }
                    }
                    boolean mustImplement = isArrayBound || ((ReferenceBinding)wildcardBound).isFinal();
                    int i = 0;
                    int length = this.superInterfaces.length;
                    while (i < length) {
                        TypeBinding match;
                        ReferenceBinding substitutedSuperType;
                        TypeBinding typeBinding = substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
                        if (!checkedAsOK && (isArrayBound ? !wildcardBound.isCompatibleWith(substitutedSuperType, scope) : ((match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType)) != null ? substitutedSuperType.isProvablyDistinct(match) : mustImplement))) {
                            return TypeConstants.BoundCheckStatus.MISMATCH;
                        }
                        if (checkNullAnnotations && argumentType.hasNullTypeAnnotations()) {
                            nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType, substitution, location, nullStatus);
                        }
                        ++i;
                    }
                    if (nullStatus == null) break;
                    return nullStatus;
                }
                case 2: {
                    TypeConstants.BoundCheckStatus status;
                    if (wildcard.bound.isTypeVariable() && ((TypeVariableBinding)wildcard.bound).superclass.id == 1) {
                        return this.nullBoundCheck(scope, argumentType, null, substitution, location, nullStatus);
                    }
                    TypeBinding bound = wildcard.bound;
                    if (checkNullAnnotations && this.environment.containsNullTypeAnnotation(wildcard.typeAnnotations)) {
                        bound = this.environment.createAnnotatedType(bound.withoutToplevelNullAnnotation(), wildcard.getTypeAnnotations());
                    }
                    if ((status = this.boundCheck(substitution, bound, scope, null)) == TypeConstants.BoundCheckStatus.NULL_PROBLEM && location != null) {
                        scope.problemReporter().nullityMismatchTypeArgument(this, wildcard, location);
                    }
                    return status;
                }
                case 0: {
                    if (!checkNullAnnotations || !argumentType.hasNullTypeAnnotations()) break;
                    return this.nullBoundCheck(scope, argumentType, null, substitution, location, nullStatus);
                }
            }
            return TypeConstants.BoundCheckStatus.OK;
        }
        boolean unchecked = false;
        if (this.superclass.id != 1) {
            ReferenceBinding substitutedSuperType;
            TypeBinding typeBinding = substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
            if (TypeBinding.notEquals(substitutedSuperType, argumentType)) {
                if (!argumentType.isCompatibleWith(substitutedSuperType, scope)) {
                    return TypeConstants.BoundCheckStatus.MISMATCH;
                }
                TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
                if (match != null && match.isRawType() && substitutedSuperType.isBoundParameterizedType()) {
                    unchecked = true;
                }
            }
            if (checkNullAnnotations) {
                nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType, substitution, location, nullStatus);
            }
        }
        int i = 0;
        int length = this.superInterfaces.length;
        while (i < length) {
            ReferenceBinding substitutedSuperType;
            TypeBinding typeBinding = substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
            if (TypeBinding.notEquals(substitutedSuperType, argumentType)) {
                if (!argumentType.isCompatibleWith(substitutedSuperType, scope)) {
                    return TypeConstants.BoundCheckStatus.MISMATCH;
                }
                TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
                if (match != null && match.isRawType() && substitutedSuperType.isBoundParameterizedType()) {
                    unchecked = true;
                }
            }
            if (checkNullAnnotations) {
                nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType, substitution, location, nullStatus);
            }
            ++i;
        }
        if (checkNullAnnotations && nullStatus != TypeConstants.BoundCheckStatus.NULL_PROBLEM && (nullBits = this.tagBits & 0x180000000000000L) != 0L && nullBits != (argumentType.tagBits & 0x180000000000000L)) {
            if (location != null) {
                scope.problemReporter().nullityMismatchTypeArgument(this, argumentType, location);
            }
            nullStatus = TypeConstants.BoundCheckStatus.NULL_PROBLEM;
        }
        return unchecked ? TypeConstants.BoundCheckStatus.UNCHECKED : (nullStatus != null ? nullStatus : TypeConstants.BoundCheckStatus.OK);
    }

    private TypeConstants.BoundCheckStatus nullBoundCheck(Scope scope, TypeBinding argumentType, TypeBinding substitutedSuperType, Substitution substitution, ASTNode location, TypeConstants.BoundCheckStatus previousStatus) {
        NullAnnotationMatching status = NullAnnotationMatching.analyse(this, argumentType, substitutedSuperType, substitution, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK);
        if (status.isAnyMismatch() && !status.isAnnotatedToUnannotated()) {
            if (location != null) {
                scope.problemReporter().nullityMismatchTypeArgument(this, argumentType, location);
            }
            return TypeConstants.BoundCheckStatus.NULL_PROBLEM;
        }
        return previousStatus;
    }

    boolean denotesRelevantSuperClass(TypeBinding type) {
        if (!type.isTypeVariable() && !type.isInterface() && type.id != 1) {
            return true;
        }
        ReferenceBinding aSuperClass = type.superclass();
        return aSuperClass != null && aSuperClass.id != 1 && !aSuperClass.isTypeVariable();
    }

    public int boundsCount() {
        if (this.firstBound == null) {
            return 0;
        }
        if (this.firstBound.isInterface()) {
            return this.superInterfaces.length;
        }
        return this.superInterfaces.length + 1;
    }

    @Override
    public boolean canBeInstantiated() {
        return false;
    }

    @Override
    public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
        int variableConstraint;
        if (this.declaringElement != inferenceContext.genericMethod) {
            return;
        }
        switch (actualType.kind()) {
            case 132: {
                if (actualType == TypeBinding.NULL) {
                    return;
                }
                TypeBinding boxedType = scope.environment().computeBoxingType(actualType);
                if (boxedType == actualType) {
                    return;
                }
                actualType = boxedType;
                break;
            }
            case 516: 
            case 65540: {
                return;
            }
        }
        switch (constraint) {
            case 0: {
                variableConstraint = 0;
                break;
            }
            case 1: {
                variableConstraint = 2;
                break;
            }
            default: {
                variableConstraint = 1;
            }
        }
        inferenceContext.recordSubstitute(this, actualType, variableConstraint);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer buffer = new StringBuffer();
        Binding declaring = this.declaringElement;
        if (!isLeaf && declaring.kind() == 8) {
            MethodBinding methodBinding = (MethodBinding)declaring;
            ReferenceBinding declaringClass = methodBinding.declaringClass;
            buffer.append(declaringClass.computeUniqueKey(false));
            buffer.append(':');
            MethodBinding[] methods = declaringClass.methods();
            if (methods != null) {
                int i = 0;
                int length = methods.length;
                while (i < length) {
                    MethodBinding binding = methods[i];
                    if (binding == methodBinding) {
                        buffer.append(i);
                        break;
                    }
                    ++i;
                }
            }
        } else {
            buffer.append(declaring.computeUniqueKey(false));
            buffer.append(':');
        }
        buffer.append(this.genericTypeSignature());
        int length = buffer.length();
        char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public char[] constantPoolName() {
        if (this.firstBound != null) {
            return this.firstBound.constantPoolName();
        }
        return this.superclass.constantPoolName();
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return new TypeVariableBinding(this);
    }

    @Override
    public String annotatedDebugName() {
        StringBuffer buffer = new StringBuffer(10);
        buffer.append(super.annotatedDebugName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    buffer.append(" extends ").append(this.superclass.annotatedDebugName());
                }
                if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                        buffer.append(" extends ");
                    }
                    int i = 0;
                    int length = this.superInterfaces.length;
                    while (i < length) {
                        if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                            buffer.append(" & ");
                        }
                        buffer.append(this.superInterfaces[i].annotatedDebugName());
                        ++i;
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
        }
        return buffer.toString();
    }

    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return super.annotatedDebugName();
        }
        return new String(this.sourceName);
    }

    @Override
    public TypeBinding erasure() {
        if (this.firstBound != null) {
            return this.firstBound.erasure();
        }
        return this.superclass;
    }

    public char[] genericSignature() {
        int interfaceLength;
        StringBuffer sig = new StringBuffer(10);
        sig.append(this.sourceName).append(':');
        int n = interfaceLength = this.superInterfaces == null ? 0 : this.superInterfaces.length;
        if ((interfaceLength == 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) && this.superclass != null) {
            sig.append(this.superclass.genericTypeSignature());
        }
        int i = 0;
        while (i < interfaceLength) {
            sig.append(':').append(this.superInterfaces[i].genericTypeSignature());
            ++i;
        }
        int sigLength = sig.length();
        char[] genericSignature = new char[sigLength];
        sig.getChars(0, sigLength, genericSignature, 0);
        return genericSignature;
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature != null) {
            return this.genericTypeSignature;
        }
        this.genericTypeSignature = CharOperation.concat('T', this.sourceName, ';');
        return this.genericTypeSignature;
    }

    TypeBound[] getTypeBounds(InferenceVariable variable, InferenceSubstitution theta) {
        int n = this.boundsCount();
        if (n == 0) {
            return NO_TYPE_BOUNDS;
        }
        TypeBound[] bounds = new TypeBound[n];
        int idx = 0;
        if (!this.firstBound.isInterface()) {
            bounds[idx++] = TypeBound.createBoundOrDependency(theta, this.firstBound, variable);
        }
        int i = 0;
        while (i < this.superInterfaces.length) {
            bounds[idx++] = TypeBound.createBoundOrDependency(theta, this.superInterfaces[i], variable);
            ++i;
        }
        return bounds;
    }

    boolean hasOnlyRawBounds() {
        if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass) && !this.superclass.isRawType()) {
            return false;
        }
        if (this.superInterfaces != null) {
            int i = 0;
            int l = this.superInterfaces.length;
            while (i < l) {
                if (!this.superInterfaces[i].isRawType()) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

    @Override
    public boolean hasTypeBit(int bit) {
        if (this.typeBits == 0x8000000) {
            this.typeBits = 0;
            if (this.superclass != null && this.superclass.hasTypeBit(-134217729)) {
                this.typeBits |= this.superclass.typeBits & 0x713;
            }
            if (this.superInterfaces != null) {
                int i = 0;
                int l = this.superInterfaces.length;
                while (i < l) {
                    if (this.superInterfaces[i].hasTypeBit(-134217729)) {
                        this.typeBits |= this.superInterfaces[i].typeBits & 0x713;
                    }
                    ++i;
                }
            }
        }
        return (this.typeBits & bit) != 0;
    }

    public boolean isErasureBoundTo(TypeBinding type) {
        if (TypeBinding.equalsEquals(this.superclass.erasure(), type)) {
            return true;
        }
        int i = 0;
        int length = this.superInterfaces.length;
        while (i < length) {
            if (TypeBinding.equalsEquals(this.superInterfaces[i].erasure(), type)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public boolean isHierarchyConnected() {
        return (this.modifiers & 0x2000000) == 0;
    }

    public boolean isInterchangeableWith(TypeVariableBinding otherVariable, Substitution substitute) {
        if (TypeBinding.equalsEquals(this, otherVariable)) {
            return true;
        }
        int length = this.superInterfaces.length;
        if (length != otherVariable.superInterfaces.length) {
            return false;
        }
        if (TypeBinding.notEquals(this.superclass, Scope.substitute(substitute, otherVariable.superclass))) {
            return false;
        }
        int i = 0;
        while (i < length) {
            block6: {
                TypeBinding superType = Scope.substitute(substitute, otherVariable.superInterfaces[i]);
                int j = 0;
                while (j < length) {
                    if (!TypeBinding.equalsEquals(superType, this.superInterfaces[j])) {
                        ++j;
                        continue;
                    }
                    break block6;
                }
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean isSubtypeOf(TypeBinding other, boolean simulatingBugJDK8026527) {
        if (this.isSubTypeOfRTL(other)) {
            return true;
        }
        if (this.firstBound != null && this.firstBound.isSubtypeOf(other, simulatingBugJDK8026527)) {
            return true;
        }
        if (this.superclass != null && this.superclass.isSubtypeOf(other, simulatingBugJDK8026527)) {
            return true;
        }
        if (this.superInterfaces != null) {
            int i = 0;
            int l = this.superInterfaces.length;
            while (i < l) {
                if (this.superInterfaces[i].isSubtypeOf(other, false)) {
                    return true;
                }
                ++i;
            }
        }
        return other.id == 1;
    }

    @Override
    public boolean enterRecursiveFunction() {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        return true;
    }

    @Override
    public void exitRecursiveFunction() {
        this.inRecursiveFunction = false;
    }

    public boolean enterRecursiveProjectionFunction() {
        if (this.inRecursiveProjectionFunction) {
            return false;
        }
        this.inRecursiveProjectionFunction = true;
        return true;
    }

    public void exitRecursiveProjectionFunction() {
        this.inRecursiveProjectionFunction = false;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.superclass != null && !this.superclass.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superInterfaces != null) {
                int i = 0;
                int l = this.superInterfaces.length;
                while (i < l) {
                    if (!this.superInterfaces[i].isProperType(admitCapture18)) {
                        return false;
                    }
                    ++i;
                }
            }
            return true;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        if (this.inRecursiveFunction) {
            return this;
        }
        this.inRecursiveFunction = true;
        try {
            boolean haveSubstitution = false;
            ReferenceBinding currentSuperclass = this.superclass;
            if (currentSuperclass != null) {
                currentSuperclass = (ReferenceBinding)currentSuperclass.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(currentSuperclass, this.superclass);
            }
            ReferenceBinding[] currentSuperInterfaces = null;
            if (this.superInterfaces != null) {
                int length = this.superInterfaces.length;
                if (haveSubstitution) {
                    currentSuperInterfaces = new ReferenceBinding[length];
                    System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces, 0, length);
                }
                int i = 0;
                while (i < length) {
                    ReferenceBinding currentSuperInterface = this.superInterfaces[i];
                    if (currentSuperInterface != null && TypeBinding.notEquals(currentSuperInterface = (ReferenceBinding)currentSuperInterface.substituteInferenceVariable(var, substituteType), this.superInterfaces[i])) {
                        if (currentSuperInterfaces == null) {
                            currentSuperInterfaces = new ReferenceBinding[length];
                            System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces, 0, length);
                        }
                        currentSuperInterfaces[i] = currentSuperInterface;
                        haveSubstitution = true;
                    }
                    ++i;
                }
            }
            if (haveSubstitution) {
                TypeVariableBinding newVar = new TypeVariableBinding(this.sourceName, this.declaringElement, this.rank, this.environment);
                newVar.superclass = currentSuperclass;
                newVar.superInterfaces = currentSuperInterfaces;
                newVar.tagBits = this.tagBits;
                TypeVariableBinding typeVariableBinding = newVar;
                return typeVariableBinding;
            }
            TypeVariableBinding typeVariableBinding = this;
            return typeVariableBinding;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    public boolean isTypeVariable() {
        return true;
    }

    @Override
    public int kind() {
        return 4100;
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        try {
            if (super.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.superclass != null && this.superclass.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.superInterfaces != null) {
                int j = 0;
                while (j < this.superInterfaces.length) {
                    if (this.superInterfaces[j].mentionsAny(parameters, idx)) {
                        return true;
                    }
                    ++j;
                }
            }
            return false;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        if (this.inRecursiveFunction) {
            return;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.superclass != null) {
                this.superclass.collectInferenceVariables(variables);
            }
            if (this.superInterfaces != null) {
                int j = 0;
                while (j < this.superInterfaces.length) {
                    this.superInterfaces[j].collectInferenceVariables(variables);
                    ++j;
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    public TypeBinding[] otherUpperBounds() {
        if (this.firstBound == null) {
            return Binding.NO_TYPES;
        }
        if (TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
            return this.superInterfaces;
        }
        int otherLength = this.superInterfaces.length - 1;
        if (otherLength > 0) {
            TypeBinding[] otherBounds = new TypeBinding[otherLength];
            System.arraycopy(this.superInterfaces, 1, otherBounds, 0, otherLength);
            return otherBounds;
        }
        return Binding.NO_TYPES;
    }

    @Override
    public char[] readableName() {
        return this.sourceName;
    }

    ReferenceBinding resolve() {
        ReferenceBinding[] interfaces;
        int length;
        if ((this.modifiers & 0x2000000) == 0) {
            return this;
        }
        long nullTagBits = this.tagBits & 0x180000000000000L;
        ReferenceBinding oldSuperclass = this.superclass;
        ReferenceBinding oldFirstInterface = null;
        if (this.superclass != null) {
            ReferenceBinding resolveType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.superclass, this.environment, true);
            this.tagBits |= resolveType.tagBits & 0x800L;
            long superNullTagBits = resolveType.tagBits & 0x180000000000000L;
            if (superNullTagBits != 0L && nullTagBits == 0L && (superNullTagBits & 0x100000000000000L) != 0L) {
                nullTagBits = superNullTagBits;
            }
            this.setSuperClass(resolveType);
        }
        if ((length = (interfaces = this.superInterfaces).length) != 0) {
            oldFirstInterface = interfaces[0];
            int i = length;
            while (--i >= 0) {
                ReferenceBinding resolveType = (ReferenceBinding)BinaryTypeBinding.resolveType(interfaces[i], this.environment, true);
                this.tagBits |= resolveType.tagBits & 0x800L;
                long superNullTagBits = resolveType.tagBits & 0x180000000000000L;
                if (superNullTagBits != 0L && nullTagBits == 0L && (superNullTagBits & 0x100000000000000L) != 0L) {
                    nullTagBits = superNullTagBits;
                }
                interfaces[i] = resolveType;
            }
        }
        if (nullTagBits != 0L) {
            this.tagBits |= nullTagBits | 0x100000L;
        }
        if (this.firstBound != null) {
            if (TypeBinding.equalsEquals(this.firstBound, oldSuperclass)) {
                this.setFirstBound(this.superclass);
            } else if (TypeBinding.equalsEquals(this.firstBound, oldFirstInterface)) {
                this.setFirstBound(interfaces[0]);
            }
        }
        this.modifiers &= 0xFDFFFFFF;
        return this;
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
        if (this.getClass() == TypeVariableBinding.class) {
            this.environment.typeSystem.forceRegisterAsDerived(this);
        } else {
            this.environment.getUnannotatedType(this);
        }
        super.setTypeAnnotations(annotations, evalNullAnnotations);
    }

    @Override
    public char[] shortReadableName() {
        return this.readableName();
    }

    @Override
    public ReferenceBinding superclass() {
        return this.superclass;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        return this.superInterfaces;
    }

    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer buffer = new StringBuffer(10);
        buffer.append('<').append(this.sourceName);
        if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
            buffer.append(" extends ").append(this.superclass.debugName());
        }
        if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
            if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                buffer.append(" extends ");
            }
            int i = 0;
            int length = this.superInterfaces.length;
            while (i < length) {
                if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    buffer.append(" & ");
                }
                buffer.append(this.superInterfaces[i].debugName());
                ++i;
            }
        }
        buffer.append('>');
        return buffer.toString();
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        StringBuffer nameBuffer = new StringBuffer(10);
        this.appendNullAnnotation(nameBuffer, options);
        nameBuffer.append(this.sourceName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    nameBuffer.append(" extends ").append(this.superclass.nullAnnotatedReadableName(options, shortNames));
                }
                if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                        nameBuffer.append(" extends ");
                    }
                    int i = 0;
                    int length = this.superInterfaces.length;
                    while (i < length) {
                        if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                            nameBuffer.append(" & ");
                        }
                        nameBuffer.append(this.superInterfaces[i].nullAnnotatedReadableName(options, shortNames));
                        ++i;
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    @Override
    protected void appendNullAnnotation(StringBuffer nameBuffer, CompilerOptions options) {
        int oldSize = nameBuffer.length();
        super.appendNullAnnotation(nameBuffer, options);
        if (oldSize == nameBuffer.length() && this.hasNullTypeAnnotations()) {
            TypeVariableBinding prototype;
            TypeVariableBinding[] typeVariables = null;
            if (this.declaringElement instanceof ReferenceBinding) {
                typeVariables = ((ReferenceBinding)this.declaringElement).typeVariables();
            } else if (this.declaringElement instanceof MethodBinding) {
                typeVariables = ((MethodBinding)this.declaringElement).typeVariables();
            }
            if (typeVariables != null && typeVariables.length > this.rank && (prototype = typeVariables[this.rank]) != this) {
                prototype.appendNullAnnotation(nameBuffer, options);
            }
        }
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
        TypeBinding unannotated = this.environment.getUnannotatedType(this);
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        if (newAnnotations.length > 0) {
            return this.environment.createAnnotatedType(unannotated, newAnnotations);
        }
        return unannotated;
    }

    public TypeBinding upperBound() {
        if (this.firstBound != null) {
            return this.firstBound;
        }
        return this.superclass;
    }

    public TypeBinding[] allUpperBounds() {
        if (this.superclass == null) {
            return this.superInterfaces;
        }
        if (this.superInterfaces == null || this.superInterfaces.length == 0) {
            return new TypeBinding[]{this.superclass};
        }
        int nInterfaces = this.superInterfaces.length;
        TypeBinding[] all = Arrays.copyOf(this.superInterfaces, nInterfaces + 1);
        all[nInterfaces] = this.superclass;
        return all;
    }

    public void evaluateNullAnnotations(Scope scope, TypeParameter parameter) {
        int length;
        ReferenceBinding[] interfaces;
        long superNullTagBits;
        long nullTagBits = NullAnnotationMatching.validNullTagBits(this.tagBits);
        if (this.firstBound != null && this.firstBound.isValidBinding() && (superNullTagBits = NullAnnotationMatching.validNullTagBits(this.firstBound.tagBits)) != 0L) {
            if (nullTagBits == 0L) {
                if ((superNullTagBits & 0x100000000000000L) != 0L) {
                    nullTagBits = superNullTagBits;
                }
            } else if (superNullTagBits != nullTagBits && parameter != null) {
                this.firstBound = this.nullMismatchOnBound(parameter, this.firstBound, superNullTagBits, nullTagBits, scope);
            }
        }
        if ((interfaces = this.superInterfaces) != null && (length = interfaces.length) != 0) {
            int i = length;
            while (--i >= 0) {
                ReferenceBinding resolveType = interfaces[i];
                long superNullTagBits2 = NullAnnotationMatching.validNullTagBits(resolveType.tagBits);
                if (superNullTagBits2 == 0L) continue;
                if (nullTagBits == 0L) {
                    if ((superNullTagBits2 & 0x100000000000000L) == 0L) continue;
                    nullTagBits = superNullTagBits2;
                    continue;
                }
                if (superNullTagBits2 == nullTagBits || parameter == null) continue;
                interfaces[i] = (ReferenceBinding)this.nullMismatchOnBound(parameter, resolveType, superNullTagBits2, nullTagBits, scope);
            }
        }
        if (nullTagBits != 0L) {
            this.tagBits |= nullTagBits | 0x100000L;
        }
    }

    private TypeBinding nullMismatchOnBound(TypeParameter parameter, TypeBinding boundType, long superNullTagBits, long nullTagBits, Scope scope) {
        TypeReference bound = this.findBound(boundType, parameter);
        Annotation ann = bound.findAnnotation(superNullTagBits);
        if (ann != null) {
            scope.problemReporter().contradictoryNullAnnotationsOnBounds(ann, nullTagBits);
            this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
        } else {
            return boundType.withoutToplevelNullAnnotation();
        }
        return boundType;
    }

    private TypeReference findBound(TypeBinding bound, TypeParameter parameter) {
        if (parameter.type != null && TypeBinding.equalsEquals(parameter.type.resolvedType, bound)) {
            return parameter.type;
        }
        TypeReference[] bounds = parameter.bounds;
        if (bounds != null) {
            int i = 0;
            while (i < bounds.length) {
                if (TypeBinding.equalsEquals(bounds[i].resolvedType, bound)) {
                    return bounds[i];
                }
                ++i;
            }
        }
        return null;
    }

    public TypeBinding setFirstBound(TypeBinding firstBound) {
        this.firstBound = firstBound;
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                annotatedType.firstBound = firstBound;
                ++i;
            }
        }
        if (firstBound != null && firstBound.hasNullTypeAnnotations()) {
            this.tagBits |= 0x100000L;
        }
        return firstBound;
    }

    public ReferenceBinding setSuperClass(ReferenceBinding superclass) {
        this.superclass = superclass;
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                annotatedType.superclass = superclass;
                ++i;
            }
        }
        return superclass;
    }

    public ReferenceBinding[] setSuperInterfaces(ReferenceBinding[] superInterfaces) {
        this.superInterfaces = superInterfaces;
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                annotatedType.superInterfaces = superInterfaces;
                ++i;
            }
        }
        return superInterfaces;
    }

    protected TypeBinding[] getDerivedTypesForDeferredInitialization() {
        return this.environment.getAnnotatedTypes(this);
    }

    public TypeBinding combineTypeAnnotations(TypeBinding substitute) {
        if (this.hasTypeAnnotations()) {
            if (this.hasRelevantTypeUseNullAnnotations()) {
                substitute = substitute.withoutToplevelNullAnnotation();
            }
            if (this.typeAnnotations != Binding.NO_ANNOTATIONS) {
                return this.environment.createAnnotatedType(substitute, this.typeAnnotations);
            }
        }
        return substitute;
    }

    private boolean hasRelevantTypeUseNullAnnotations() {
        TypeVariableBinding[] parameters;
        if (this.declaringElement instanceof ReferenceBinding) {
            parameters = ((ReferenceBinding)this.declaringElement).original().typeVariables();
        } else if (this.declaringElement instanceof MethodBinding) {
            parameters = ((MethodBinding)this.declaringElement).original().typeVariables;
        } else {
            throw new IllegalStateException("Unexpected declaring element:" + String.valueOf(this.declaringElement.readableName()));
        }
        TypeVariableBinding parameter = parameters[this.rank];
        long currentNullBits = this.tagBits & 0x180000000000000L;
        long declarationNullBits = parameter.tagBits & 0x180000000000000L;
        return (currentNullBits & (declarationNullBits ^ 0xFFFFFFFFFFFFFFFFL)) != 0L;
    }

    @Override
    public boolean acceptsNonNullDefault() {
        return false;
    }

    @Override
    public long updateTagBits() {
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null) {
                    this.tagBits |= this.superclass.updateTagBits();
                }
                if (this.superInterfaces != null) {
                    ReferenceBinding[] referenceBindingArray = this.superInterfaces;
                    int n = this.superInterfaces.length;
                    int n2 = 0;
                    while (n2 < n) {
                        ReferenceBinding superIfc = referenceBindingArray[n2];
                        this.tagBits |= superIfc.updateTagBits();
                        ++n2;
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
        }
        return super.updateTagBits();
    }

    @Override
    public boolean isFreeTypeVariable() {
        return this.environment.usesNullTypeAnnotations() && this.environment.globalOptions.pessimisticNullAnalysisForFreeTypeVariablesEnabled && (this.tagBits & 0x180000000000000L) == 0L;
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }

    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }
}

