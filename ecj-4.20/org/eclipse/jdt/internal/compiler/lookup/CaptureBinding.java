/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class CaptureBinding
extends TypeVariableBinding {
    public TypeBinding lowerBound;
    public WildcardBinding wildcard;
    public int captureID;
    public ReferenceBinding sourceType;
    public int start;
    public int end;
    public ASTNode cud;
    TypeBinding pendingSubstitute;

    public CaptureBinding(WildcardBinding wildcard, ReferenceBinding sourceType, int start, int end, ASTNode cud, int captureID) {
        super(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX, wildcard.environment);
        this.wildcard = wildcard;
        this.modifiers = 0x40000001;
        this.fPackage = wildcard.fPackage;
        this.sourceType = sourceType;
        this.start = start;
        this.end = end;
        this.captureID = captureID;
        this.tagBits |= 0x2000000000000000L;
        this.cud = cud;
        if (wildcard.hasTypeAnnotations()) {
            CaptureBinding unannotated = (CaptureBinding)this.clone(null);
            unannotated.wildcard = (WildcardBinding)this.wildcard.unannotated();
            this.environment.getUnannotatedType(unannotated);
            this.id = unannotated.id;
            this.environment.typeSystem.cacheDerivedType(this, unannotated, this);
            super.setTypeAnnotations(wildcard.getTypeAnnotations(), wildcard.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
            if (wildcard.hasNullTypeAnnotations()) {
                this.tagBits |= 0x100000L;
            }
        } else {
            this.computeId(this.environment);
            if (wildcard.hasNullTypeAnnotations()) {
                this.tagBits |= wildcard.tagBits & 0x180000000000000L | 0x100000L;
            }
        }
    }

    protected CaptureBinding(ReferenceBinding sourceType, char[] sourceName, int start, int end, int captureID, LookupEnvironment environment) {
        super(sourceName, null, 0, environment);
        this.modifiers = 0x40000001;
        this.sourceType = sourceType;
        this.start = start;
        this.end = end;
        this.captureID = captureID;
    }

    public CaptureBinding(CaptureBinding prototype) {
        super(prototype);
        this.wildcard = prototype.wildcard;
        this.sourceType = prototype.sourceType;
        this.start = prototype.start;
        this.end = prototype.end;
        this.captureID = prototype.captureID;
        this.lowerBound = prototype.lowerBound;
        this.tagBits |= prototype.tagBits & 0x2000000000000000L;
        this.cud = prototype.cud;
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return new CaptureBinding(this);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer buffer = new StringBuffer();
        if (isLeaf) {
            buffer.append(this.sourceType.computeUniqueKey(false));
            buffer.append('&');
        }
        buffer.append(TypeConstants.WILDCARD_CAPTURE);
        buffer.append(this.wildcard.computeUniqueKey(false));
        buffer.append(this.end);
        buffer.append(';');
        int length = buffer.length();
        char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public String debugName() {
        if (this.wildcard != null) {
            StringBuffer buffer = new StringBuffer(10);
            AnnotationBinding[] annotations = this.getTypeAnnotations();
            int i = 0;
            int length = annotations == null ? 0 : annotations.length;
            while (i < length) {
                buffer.append(annotations[i]);
                buffer.append(' ');
                ++i;
            }
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.debugName());
            return buffer.toString();
        }
        return super.debugName();
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.inRecursiveFunction) {
            return CharOperation.concat(new char[]{'L'}, CharOperation.concatWith(TypeConstants.JAVA_LANG_OBJECT, '.'), new char[]{';'});
        }
        this.inRecursiveFunction = true;
        try {
            char[] cArray = this.erasure().genericTypeSignature();
            return cArray;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    public void initializeBounds(Scope scope, ParameterizedTypeBinding capturedParameterizedType) {
        ReferenceBinding[] originalVariableInterfaces;
        ReferenceBinding[] substitutedVariableInterfaces;
        boolean is18plus = scope.compilerOptions().complianceLevel >= 0x340000L;
        TypeVariableBinding wildcardVariable = this.wildcard.typeVariable();
        if (wildcardVariable == null) {
            TypeBinding originalWildcardBound = this.wildcard.bound;
            switch (this.wildcard.boundKind) {
                case 1: {
                    TypeBinding capturedWildcardBound;
                    TypeBinding typeBinding = capturedWildcardBound = is18plus ? originalWildcardBound : originalWildcardBound.capture(scope, this.start, this.end);
                    if (originalWildcardBound.isInterface()) {
                        this.setSuperClass(scope.getJavaLangObject());
                        this.setSuperInterfaces(new ReferenceBinding[]{(ReferenceBinding)capturedWildcardBound});
                    } else {
                        if (capturedWildcardBound.isArrayType() || TypeBinding.equalsEquals(capturedWildcardBound, this)) {
                            this.setSuperClass(scope.getJavaLangObject());
                        } else {
                            this.setSuperClass((ReferenceBinding)capturedWildcardBound);
                        }
                        this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    }
                    this.setFirstBound(capturedWildcardBound);
                    if ((capturedWildcardBound.tagBits & 0x20000000L) != 0L) break;
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                    break;
                }
                case 0: {
                    this.setSuperClass(scope.getJavaLangObject());
                    this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                    break;
                }
                case 2: {
                    this.setSuperClass(scope.getJavaLangObject());
                    this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    this.lowerBound = this.wildcard.bound;
                    if ((originalWildcardBound.tagBits & 0x20000000L) != 0L) break;
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                }
            }
            return;
        }
        ReferenceBinding originalVariableSuperclass = wildcardVariable.superclass;
        ReferenceBinding substitutedVariableSuperclass = (ReferenceBinding)Scope.substitute((Substitution)capturedParameterizedType, originalVariableSuperclass);
        if (TypeBinding.equalsEquals(substitutedVariableSuperclass, this)) {
            substitutedVariableSuperclass = originalVariableSuperclass;
        }
        if ((substitutedVariableInterfaces = Scope.substitute((Substitution)capturedParameterizedType, originalVariableInterfaces = wildcardVariable.superInterfaces())) != originalVariableInterfaces) {
            int i = 0;
            int length = substitutedVariableInterfaces.length;
            while (i < length) {
                if (TypeBinding.equalsEquals(substitutedVariableInterfaces[i], this)) {
                    substitutedVariableInterfaces[i] = originalVariableInterfaces[i];
                }
                ++i;
            }
        }
        TypeBinding originalWildcardBound = this.wildcard.bound;
        switch (this.wildcard.boundKind) {
            case 1: {
                TypeBinding capturedWildcardBound;
                TypeBinding typeBinding = capturedWildcardBound = is18plus ? originalWildcardBound : originalWildcardBound.capture(scope, this.start, this.end);
                if (originalWildcardBound.isInterface()) {
                    this.setSuperClass(substitutedVariableSuperclass);
                    if (substitutedVariableInterfaces == Binding.NO_SUPERINTERFACES) {
                        this.setSuperInterfaces(new ReferenceBinding[]{(ReferenceBinding)capturedWildcardBound});
                    } else {
                        int length = substitutedVariableInterfaces.length;
                        ReferenceBinding[] referenceBindingArray = substitutedVariableInterfaces;
                        substitutedVariableInterfaces = new ReferenceBinding[length + 1];
                        System.arraycopy(referenceBindingArray, 0, substitutedVariableInterfaces, 1, length);
                        substitutedVariableInterfaces[0] = (ReferenceBinding)capturedWildcardBound;
                        this.setSuperInterfaces(Scope.greaterLowerBound(substitutedVariableInterfaces));
                    }
                } else {
                    if (capturedWildcardBound.isArrayType() || TypeBinding.equalsEquals(capturedWildcardBound, this)) {
                        this.setSuperClass(substitutedVariableSuperclass);
                    } else {
                        this.setSuperClass((ReferenceBinding)capturedWildcardBound);
                        if (this.superclass.isSuperclassOf(substitutedVariableSuperclass)) {
                            this.setSuperClass(substitutedVariableSuperclass);
                        }
                    }
                    this.setSuperInterfaces(substitutedVariableInterfaces);
                }
                this.setFirstBound(capturedWildcardBound);
                if ((capturedWildcardBound.tagBits & 0x20000000L) != 0L) break;
                this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                break;
            }
            case 0: {
                this.setSuperClass(substitutedVariableSuperclass);
                this.setSuperInterfaces(substitutedVariableInterfaces);
                this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                break;
            }
            case 2: {
                this.setSuperClass(substitutedVariableSuperclass);
                if (TypeBinding.equalsEquals(wildcardVariable.firstBound, substitutedVariableSuperclass) || TypeBinding.equalsEquals(originalWildcardBound, substitutedVariableSuperclass)) {
                    this.setFirstBound(substitutedVariableSuperclass);
                }
                this.setSuperInterfaces(substitutedVariableInterfaces);
                this.lowerBound = originalWildcardBound;
                if ((originalWildcardBound.tagBits & 0x20000000L) != 0L) break;
                this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
            }
        }
        if (scope.environment().usesNullTypeAnnotations()) {
            this.evaluateNullAnnotations(scope, null);
        }
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        if (this.enterRecursiveProjectionFunction()) {
            try {
                int i = 0;
                while (i < mentionedTypeVariables.length) {
                    if (TypeBinding.equalsEquals(this, mentionedTypeVariables[i])) {
                        TypeBinding upperBoundForProjection = this.upperBoundForProjection();
                        ReferenceBinding referenceBinding = ((ReferenceBinding)upperBoundForProjection).upwardsProjection(scope, mentionedTypeVariables);
                        return referenceBinding;
                    }
                    ++i;
                }
                CaptureBinding captureBinding = this;
                return captureBinding;
            }
            finally {
                this.exitRecursiveProjectionFunction();
            }
        }
        return scope.getJavaLangObject();
    }

    public TypeBinding upperBoundForProjection() {
        TypeBinding upperBound = null;
        if (this.wildcard != null) {
            ReferenceBinding[] supers = this.superInterfaces();
            if (this.wildcard.boundKind == 1) {
                if (supers.length > 0) {
                    ReferenceBinding[] allBounds = new ReferenceBinding[supers.length + 1];
                    System.arraycopy(supers, 0, allBounds, 1, supers.length);
                    allBounds[0] = this.superclass();
                    ReferenceBinding[] glbs = Scope.greaterLowerBound(allBounds);
                    upperBound = glbs == null ? new ProblemReferenceBinding(null, null, 10) : (glbs.length == 1 ? glbs[0] : this.environment.createIntersectionType18(glbs));
                } else {
                    upperBound = this.superclass;
                }
            } else {
                boolean superClassIsObject = TypeBinding.equalsEquals(this.superclass(), this.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null));
                if (supers.length == 0) {
                    upperBound = this.superclass();
                } else if (supers.length == 1) {
                    upperBound = superClassIsObject ? supers[0] : this.environment.createIntersectionType18(new ReferenceBinding[]{this.superclass(), supers[0]});
                } else if (superClassIsObject) {
                    upperBound = this.environment.createIntersectionType18(supers);
                } else {
                    ReferenceBinding[] allBounds = new ReferenceBinding[supers.length + 1];
                    System.arraycopy(supers, 0, allBounds, 1, supers.length);
                    allBounds[0] = this.superclass();
                    upperBound = this.environment.createIntersectionType18(allBounds);
                }
            }
        } else {
            upperBound = super.upperBound();
        }
        return upperBound;
    }

    @Override
    public boolean isCapture() {
        return true;
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (CaptureBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        if (this.firstBound != null && this.firstBound.isArrayType() && this.firstBound.isCompatibleWith(otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 516: 
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
        }
        return false;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        if (this.lowerBound != null && !this.lowerBound.isProperType(admitCapture18)) {
            return false;
        }
        if (this.wildcard != null && !this.wildcard.isProperType(admitCapture18)) {
            return false;
        }
        return super.isProperType(admitCapture18);
    }

    @Override
    public char[] readableName() {
        if (this.wildcard != null) {
            StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.readableName());
            int length = buffer.length();
            char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.readableName();
    }

    @Override
    public char[] signableName() {
        if (this.wildcard != null) {
            StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_SIGNABLE_NAME_SUFFIX).append(this.wildcard.readableName());
            int length = buffer.length();
            char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.readableName();
    }

    @Override
    public char[] shortReadableName() {
        if (this.wildcard != null) {
            StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.shortReadableName());
            int length = buffer.length();
            char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.shortReadableName();
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        StringBuffer nameBuffer = new StringBuffer(10);
        this.appendNullAnnotation(nameBuffer, options);
        nameBuffer.append(this.sourceName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.wildcard != null) {
                    nameBuffer.append("of ");
                    nameBuffer.append(this.wildcard.withoutToplevelNullAnnotation().nullAnnotatedReadableName(options, shortNames));
                } else if (this.lowerBound != null) {
                    nameBuffer.append(" super ");
                    nameBuffer.append(this.lowerBound.nullAnnotatedReadableName(options, shortNames));
                } else if (this.firstBound != null) {
                    nameBuffer.append(" extends ");
                    nameBuffer.append(this.firstBound.nullAnnotatedReadableName(options, shortNames));
                    TypeBinding[] otherUpperBounds = this.otherUpperBounds();
                    if (otherUpperBounds != NO_TYPES) {
                        nameBuffer.append(" & ...");
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
    public TypeBinding withoutToplevelNullAnnotation() {
        WildcardBinding newWildcard;
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        if (this.wildcard != null && this.wildcard.hasNullTypeAnnotations() && (newWildcard = (WildcardBinding)this.wildcard.withoutToplevelNullAnnotation()) != this.wildcard) {
            CaptureBinding newCapture = (CaptureBinding)this.environment.getUnannotatedType(this).clone(null);
            if (newWildcard.hasTypeAnnotations()) {
                newCapture.tagBits |= 0x200000L;
            }
            newCapture.wildcard = newWildcard;
            newCapture.superclass = this.superclass;
            newCapture.superInterfaces = this.superInterfaces;
            AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
            return this.environment.createAnnotatedType((TypeBinding)newCapture, newAnnotations);
        }
        return super.withoutToplevelNullAnnotation();
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        if (this.pendingSubstitute != null) {
            return this.pendingSubstitute;
        }
        try {
            TypeBinding substitutedWildcard = this.wildcard.substituteInferenceVariable(var, substituteType);
            if (substitutedWildcard != this.wildcard) {
                CaptureBinding substitute = (CaptureBinding)this.clone(this.enclosingType());
                substitute.wildcard = (WildcardBinding)substitutedWildcard;
                this.pendingSubstitute = substitute;
                if (this.lowerBound != null) {
                    substitute.lowerBound = this.lowerBound.substituteInferenceVariable(var, substituteType);
                }
                if (this.firstBound != null) {
                    substitute.firstBound = this.firstBound.substituteInferenceVariable(var, substituteType);
                }
                if (this.superclass != null) {
                    substitute.superclass = (ReferenceBinding)this.superclass.substituteInferenceVariable(var, substituteType);
                }
                if (this.superInterfaces != null) {
                    int length = this.superInterfaces.length;
                    substitute.superInterfaces = new ReferenceBinding[length];
                    int i = 0;
                    while (i < length) {
                        substitute.superInterfaces[i] = (ReferenceBinding)this.superInterfaces[i].substituteInferenceVariable(var, substituteType);
                        ++i;
                    }
                }
                CaptureBinding captureBinding = substitute;
                return captureBinding;
            }
            CaptureBinding captureBinding = this;
            return captureBinding;
        }
        finally {
            this.pendingSubstitute = null;
        }
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
        super.setTypeAnnotations(annotations, evalNullAnnotations);
        if (annotations != Binding.NO_ANNOTATIONS && this.wildcard != null) {
            this.wildcard = (WildcardBinding)this.wildcard.environment.createAnnotatedType((TypeBinding)this.wildcard, annotations);
        }
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        return this.wildcard.uncapture(scope);
    }

    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        ReferenceBinding result = null;
        if (this.enterRecursiveProjectionFunction()) {
            int i = 0;
            while (i < mentionedTypeVariables.length) {
                if (TypeBinding.equalsEquals(this, mentionedTypeVariables[i])) {
                    if (this.lowerBound == null) break;
                    result = (ReferenceBinding)this.lowerBound.downwardsProjection(scope, mentionedTypeVariables);
                    break;
                }
                ++i;
            }
            this.exitRecursiveProjectionFunction();
        }
        return result;
    }

    @Override
    protected TypeBinding[] getDerivedTypesForDeferredInitialization() {
        TypeBinding[] derived = this.environment.typeSystem.getDerivedTypes(this);
        if (derived.length > 0) {
            int count = 0;
            int i = 0;
            while (i < derived.length) {
                if (derived[i] != null && derived[i].id == this.id) {
                    derived[count++] = derived[i];
                }
                ++i;
            }
            if (count < derived.length) {
                TypeBinding[] typeBindingArray = derived;
                derived = new TypeBinding[count];
                System.arraycopy(typeBindingArray, 0, derived, 0, count);
            }
        }
        return derived;
    }

    @Override
    public String toString() {
        if (this.wildcard != null) {
            StringBuffer buffer = new StringBuffer(10);
            AnnotationBinding[] annotations = this.getTypeAnnotations();
            int i = 0;
            int length = annotations == null ? 0 : annotations.length;
            while (i < length) {
                buffer.append(annotations[i]);
                buffer.append(' ');
                ++i;
            }
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard);
            return buffer.toString();
        }
        return super.toString();
    }

    @Override
    public char[] signature() {
        if (this.signature != null) {
            return this.signature;
        }
        this.signature = this.firstBound instanceof ArrayBinding ? this.constantPoolName() : CharOperation.concat('L', this.constantPoolName(), ';');
        return this.signature;
    }
}

