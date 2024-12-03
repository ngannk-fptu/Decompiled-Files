/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class ParameterizedTypeBinding
extends ReferenceBinding
implements Substitution {
    protected ReferenceBinding type;
    public TypeBinding[] arguments;
    public LookupEnvironment environment;
    public char[] genericTypeSignature;
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    public FieldBinding[] fields;
    public ReferenceBinding[] memberTypes;
    public MethodBinding[] methods;
    protected ReferenceBinding enclosingType;

    public ParameterizedTypeBinding(ReferenceBinding type, TypeBinding[] arguments, ReferenceBinding enclosingType, LookupEnvironment environment) {
        this.environment = environment;
        this.enclosingType = enclosingType;
        if (!type.hasEnclosingInstanceContext() && arguments == null && !(this instanceof RawTypeBinding)) {
            throw new IllegalStateException();
        }
        this.initialize(type, arguments);
        if (type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
        }
        if (arguments != null) {
            int i = 0;
            int l = arguments.length;
            while (i < l) {
                if (arguments[i] instanceof UnresolvedReferenceBinding) {
                    ((UnresolvedReferenceBinding)arguments[i]).addWrapper(this, environment);
                }
                if (arguments[i].hasNullTypeAnnotations()) {
                    this.tagBits |= 0x100000L;
                }
                ++i;
            }
        }
        if (enclosingType != null && enclosingType.hasNullTypeAnnotations()) {
            this.tagBits |= 0x100000L;
        }
        this.tagBits |= 0x1000000L;
        this.typeBits = type.typeBits;
    }

    @Override
    public ReferenceBinding actualType() {
        return this.type;
    }

    @Override
    public boolean isParameterizedType() {
        return true;
    }

    public void boundCheck(Scope scope, TypeReference[] argumentReferences) {
        if ((this.tagBits & 0x400000L) == 0L) {
            boolean hasErrors = false;
            TypeVariableBinding[] typeVariables = this.type.typeVariables();
            if (this.arguments != null && typeVariables != null) {
                int i = 0;
                int length = typeVariables.length;
                while (i < length) {
                    TypeConstants.BoundCheckStatus checkStatus = typeVariables[i].boundCheck(this, this.arguments[i], scope, argumentReferences[i]);
                    hasErrors |= checkStatus != TypeConstants.BoundCheckStatus.OK;
                    if (!checkStatus.isOKbyJLS() && (this.arguments[i].tagBits & 0x80L) == 0L) {
                        scope.problemReporter().typeMismatchError(this.arguments[i], typeVariables[i], this.type, (ASTNode)argumentReferences[i]);
                    }
                    ++i;
                }
            }
            if (!hasErrors) {
                this.tagBits |= 0x400000L;
            }
        }
    }

    @Override
    public boolean canBeInstantiated() {
        return (this.tagBits & 0x40000000L) == 0L && super.canBeInstantiated();
    }

    @Override
    public ParameterizedTypeBinding capture(Scope scope, int start, int end) {
        if ((this.tagBits & 0x40000000L) == 0L) {
            return this;
        }
        TypeBinding[] originalArguments = this.arguments;
        int length = originalArguments.length;
        TypeBinding[] capturedArguments = new TypeBinding[length];
        ReferenceBinding contextType = scope.enclosingSourceType();
        if (contextType != null) {
            contextType = contextType.outermostEnclosingType();
        }
        CompilationUnitScope compilationUnitScope = scope.compilationUnitScope();
        CompilationUnitDeclaration cud = compilationUnitScope.referenceContext;
        long sourceLevel = this.environment.globalOptions.sourceLevel;
        boolean needUniqueCapture = sourceLevel >= 0x340000L;
        int i = 0;
        while (i < length) {
            TypeBinding argument = originalArguments[i];
            if (argument.kind() == 516) {
                WildcardBinding wildcard = (WildcardBinding)argument;
                capturedArguments[i] = wildcard.boundKind == 2 && wildcard.bound.id == 1 ? wildcard.bound : (needUniqueCapture ? this.environment.createCapturedWildcard(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID()) : new CaptureBinding(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID()));
            } else {
                capturedArguments[i] = argument;
            }
            ++i;
        }
        ParameterizedTypeBinding capturedParameterizedType = this.environment.createParameterizedType(this.type, capturedArguments, this.enclosingType(), this.typeAnnotations);
        int i2 = 0;
        while (i2 < length) {
            TypeBinding argument = capturedArguments[i2];
            if (argument.isCapture()) {
                ((CaptureBinding)argument).initializeBounds(scope, capturedParameterizedType);
            }
            ++i2;
        }
        return capturedParameterizedType;
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        if ((this.tagBits & 0x2000000000000000L) == 0L) {
            return this;
        }
        int length = this.arguments == null ? 0 : this.arguments.length;
        TypeBinding[] freeTypes = new TypeBinding[length];
        int i = 0;
        while (i < length) {
            freeTypes[i] = this.arguments[i].uncapture(scope);
            ++i;
        }
        return scope.environment().createParameterizedType(this.type, freeTypes, (ReferenceBinding)(this.enclosingType != null ? this.enclosingType.uncapture(scope) : null), this.typeAnnotations);
    }

    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0L) {
            if (this.enclosingType != null) {
                missingTypes = this.enclosingType.collectMissingTypes(missingTypes);
            }
            missingTypes = this.genericType().collectMissingTypes(missingTypes);
            if (this.arguments != null) {
                int i = 0;
                int max = this.arguments.length;
                while (i < max) {
                    missingTypes = this.arguments[i].collectMissingTypes(missingTypes);
                    ++i;
                }
            }
        }
        return missingTypes;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
        if ((this.tagBits & 0x20000000L) == 0L) {
            actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
            if (actualEquivalent != null && actualEquivalent.isRawType()) {
                inferenceContext.isUnchecked = true;
            }
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
            return;
        }
        if (!(actualType instanceof ReferenceBinding)) {
            return;
        }
        switch (constraint) {
            case 0: 
            case 1: {
                formalEquivalent = this;
                actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
                if (actualEquivalent != null) break;
                return;
            }
            default: {
                formalEquivalent = this.findSuperTypeOriginatingFrom(actualType);
                if (formalEquivalent == null) {
                    return;
                }
                actualEquivalent = actualType;
            }
        }
        if ((formalEnclosingType = formalEquivalent.enclosingType()) != null) {
            formalEnclosingType.collectSubstitutes(scope, actualEquivalent.enclosingType(), inferenceContext, constraint);
        }
        if (this.arguments == null) {
            return;
        }
        switch (formalEquivalent.kind()) {
            case 2052: {
                formalArguments = formalEquivalent.typeVariables();
                break;
            }
            case 260: {
                formalArguments = ((ParameterizedTypeBinding)formalEquivalent).arguments;
                break;
            }
            case 1028: {
                if (inferenceContext.depth > 0) {
                    inferenceContext.status = 1;
                }
                return;
            }
            default: {
                return;
            }
        }
        switch (actualEquivalent.kind()) {
            case 2052: {
                actualArguments = actualEquivalent.typeVariables();
                break;
            }
            case 260: {
                actualArguments = ((ParameterizedTypeBinding)actualEquivalent).arguments;
                break;
            }
            case 1028: {
                if (inferenceContext.depth > 0) {
                    inferenceContext.status = 1;
                } else {
                    inferenceContext.isUnchecked = true;
                }
                return;
            }
            default: {
                return;
            }
        }
        ++inferenceContext.depth;
        i = 0;
        length = formalArguments.length;
        while (i < length) {
            block32: {
                block31: {
                    formalArgument = formalArguments[i];
                    actualArgument = actualArguments[i];
                    if (!formalArgument.isWildcard()) break block31;
                    formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, constraint);
                    break block32;
                }
                if (!actualArgument.isWildcard()) ** GOTO lbl-1000
                actualWildcardArgument = (WildcardBinding)actualArgument;
                if (actualWildcardArgument.otherBounds == null) {
                    if (constraint == 2) {
                        switch (actualWildcardArgument.boundKind) {
                            case 1: {
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 2);
                                break;
                            }
                            case 2: {
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 1);
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                } else lbl-1000:
                // 2 sources

                {
                    formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, 0);
                }
            }
            ++i;
        }
        --inferenceContext.depth;
    }

    @Override
    public void computeId() {
        this.id = Integer.MAX_VALUE;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        char[] typeSig;
        ReferenceBinding enclosing;
        StringBuffer sig = new StringBuffer(10);
        if (this.isMemberType() && ((enclosing = this.enclosingType()).isParameterizedType() || enclosing.isRawType())) {
            typeSig = enclosing.computeUniqueKey(false);
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.').append(this.sourceName());
        } else if (this.type.isLocalType()) {
            ReferenceBinding temp;
            LocalTypeBinding localTypeBinding = (LocalTypeBinding)this.type;
            enclosing = localTypeBinding.enclosingType();
            while ((temp = enclosing.enclosingType()) != null) {
                enclosing = temp;
            }
            char[] typeSig2 = enclosing.computeUniqueKey(false);
            sig.append(typeSig2, 0, typeSig2.length - 1);
            sig.append('$');
            sig.append(localTypeBinding.sourceStart);
        } else {
            typeSig = this.type.computeUniqueKey(false);
            sig.append(typeSig, 0, typeSig.length - 1);
        }
        ReferenceBinding captureSourceType = null;
        if (this.arguments != null) {
            sig.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                TypeBinding typeBinding = this.arguments[i];
                sig.append(typeBinding.computeUniqueKey(false));
                if (typeBinding instanceof CaptureBinding) {
                    captureSourceType = ((CaptureBinding)typeBinding).sourceType;
                }
                ++i;
            }
            sig.append('>');
        }
        sig.append(';');
        if (captureSourceType != null && TypeBinding.notEquals(captureSourceType, this.type)) {
            sig.insert(0, "&");
            sig.insert(0, captureSourceType.computeUniqueKey(false));
        }
        int sigLength = sig.length();
        char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public char[] constantPoolName() {
        return this.type.constantPoolName();
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        return new ParameterizedTypeBinding(this.type, this.arguments, (ReferenceBinding)outerType, this.environment);
    }

    public ParameterizedMethodBinding createParameterizedMethod(MethodBinding originalMethod) {
        return new ParameterizedMethodBinding(this, originalMethod);
    }

    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.type instanceof UnresolvedReferenceBinding) {
            nameBuffer.append(this.type);
        } else {
            nameBuffer.append(this.type.sourceName());
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].debugName());
                ++i;
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }

    @Override
    public String annotatedDebugName() {
        StringBuffer nameBuffer = new StringBuffer(super.annotatedDebugName());
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].annotatedDebugName());
                ++i;
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }

    @Override
    public ReferenceBinding enclosingType() {
        if (this.type instanceof UnresolvedReferenceBinding && ((UnresolvedReferenceBinding)this.type).depth() > 0) {
            ((UnresolvedReferenceBinding)this.type).resolve(this.environment, false);
        }
        return this.enclosingType;
    }

    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }

    @Override
    public TypeBinding erasure() {
        return this.type.erasure();
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        TypeBinding[] typeVariables = this.arguments;
        if (typeVariables == null) {
            return this;
        }
        TypeBinding[] a_i_primes = new TypeBinding[typeVariables.length];
        int i = 0;
        int length = typeVariables.length;
        while (i < length) {
            TypeBinding a_i = typeVariables[i];
            int typeVariableKind = a_i.kind();
            if (!a_i.mentionsAny(mentionedTypeVariables, -1)) {
                a_i_primes[i] = a_i;
            } else if (typeVariableKind != 516) {
                TypeBinding l;
                TypeBinding u = a_i.upwardsProjection(scope, mentionedTypeVariables);
                TypeVariableBinding[] g_vars = this.type.typeVariables();
                if (g_vars == null || g_vars.length == 0) {
                    return this;
                }
                TypeBinding b_i = g_vars[i].upperBound();
                a_i_primes[i] = u.id != 1 && (b_i.mentionsAny(typeVariables, -1) || !b_i.isSubtypeOf(u, false)) ? this.environment().createWildcard(this.genericType(), i, u, null, 1) : ((l = a_i.downwardsProjection(scope, mentionedTypeVariables)) != null ? this.environment().createWildcard(this.genericType(), i, l, null, 2) : this.environment().createWildcard(this.genericType(), i, null, null, 0));
            } else {
                WildcardBinding wildcard = (WildcardBinding)a_i;
                if (wildcard.boundKind() == 1) {
                    TypeBinding u = wildcard.bound().upwardsProjection(scope, mentionedTypeVariables);
                    a_i_primes[i] = this.environment().createWildcard(null, 0, u, null, 1);
                } else if (wildcard.boundKind() == 2) {
                    TypeBinding l = wildcard.bound().downwardsProjection(scope, mentionedTypeVariables);
                    a_i_primes[i] = l != null ? this.environment().createWildcard(null, 0, l, null, 2) : this.environment().createWildcard(null, 0, null, null, 0);
                }
            }
            ++i;
        }
        return this.environment.createParameterizedType(this.type, a_i_primes, this.enclosingType);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        TypeBinding[] typeVariables = this.arguments;
        if (typeVariables == null) {
            return this;
        }
        TypeBinding[] a_i_primes = new TypeBinding[typeVariables.length];
        int i = 0;
        int length = typeVariables.length;
        while (i < length) {
            TypeBinding a_i = typeVariables[i];
            int typeVariableKind = a_i.kind();
            if (!a_i.mentionsAny(mentionedTypeVariables, -1)) {
                a_i_primes[i] = a_i;
            } else {
                if (typeVariableKind != 516) {
                    return null;
                }
                WildcardBinding wildcard = (WildcardBinding)a_i;
                if (wildcard.boundKind() == 1) {
                    TypeBinding u = wildcard.bound().downwardsProjection(scope, mentionedTypeVariables);
                    if (u == null) return null;
                    a_i_primes[i] = this.environment().createWildcard(null, 0, u, null, 1);
                } else {
                    if (wildcard.boundKind() != 2) return null;
                    TypeBinding l = wildcard.bound().upwardsProjection(scope, mentionedTypeVariables);
                    a_i_primes[i] = this.environment().createWildcard(null, 0, l, null, 2);
                }
            }
            ++i;
        }
        return this.environment.createParameterizedType(this.type, a_i_primes, this.enclosingType);
    }

    @Override
    public int fieldCount() {
        return this.type.fieldCount();
    }

    @Override
    public FieldBinding[] fields() {
        if ((this.tagBits & 0x2000L) != 0L) {
            return this.fields;
        }
        try {
            FieldBinding[] originalFields = this.type.fields();
            int length = originalFields.length;
            FieldBinding[] parameterizedFields = new FieldBinding[length];
            int i = 0;
            while (i < length) {
                parameterizedFields[i] = new ParameterizedFieldBinding(this, originalFields[i]);
                ++i;
            }
            this.fields = parameterizedFields;
        }
        finally {
            if (this.fields == null) {
                this.fields = Binding.NO_FIELDS;
            }
            this.tagBits |= 0x2000L;
        }
        return this.fields;
    }

    public ReferenceBinding genericType() {
        if (this.type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)this.type).resolve(this.environment, false);
        }
        return this.type;
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            if ((this.modifiers & 0x40000000) == 0) {
                this.genericTypeSignature = this.type.signature();
            } else {
                StringBuffer sig = new StringBuffer(10);
                if (this.isMemberType() && !this.isStatic()) {
                    ReferenceBinding enclosing = this.enclosingType();
                    char[] typeSig = enclosing.genericTypeSignature();
                    sig.append(typeSig, 0, typeSig.length - 1);
                    if ((enclosing.modifiers & 0x40000000) != 0) {
                        sig.append('.');
                    } else {
                        sig.append('$');
                    }
                    sig.append(this.sourceName());
                } else {
                    char[] typeSig = this.type.signature();
                    sig.append(typeSig, 0, typeSig.length - 1);
                }
                if (this.arguments != null) {
                    sig.append('<');
                    int i = 0;
                    int length = this.arguments.length;
                    while (i < length) {
                        sig.append(this.arguments[i].genericTypeSignature());
                        ++i;
                    }
                    sig.append('>');
                }
                sig.append(';');
                int sigLength = sig.length();
                this.genericTypeSignature = new char[sigLength];
                sig.getChars(0, sigLength, this.genericTypeSignature, 0);
            }
        }
        return this.genericTypeSignature;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.type.getAnnotations();
    }

    @Override
    public long getAnnotationTagBits() {
        return this.type.getAnnotationTagBits();
    }

    @Override
    public int getEnclosingInstancesSlotSize() {
        return this.genericType().getEnclosingInstancesSlotSize();
    }

    @Override
    public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
        MethodBinding match;
        block10: {
            int argCount;
            block9: {
                argCount = argumentTypes.length;
                match = null;
                if ((this.tagBits & 0x8000L) == 0L) break block9;
                long range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods);
                if (range < 0L) break block10;
                int imethod = (int)range;
                int end = (int)(range >> 32);
                while (imethod <= end) {
                    block8: {
                        MethodBinding method = this.methods[imethod];
                        if (method.parameters.length == argCount) {
                            TypeBinding[] toMatch = method.parameters;
                            int iarg = 0;
                            while (iarg < argCount) {
                                if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                    ++iarg;
                                    continue;
                                }
                                break block8;
                            }
                            if (match != null) {
                                return null;
                            }
                            match = method;
                        }
                    }
                    ++imethod;
                }
                break block10;
            }
            MethodBinding[] matchingMethods = this.getMethods(TypeConstants.INIT);
            int m = matchingMethods.length;
            block2: while (--m >= 0) {
                MethodBinding method = matchingMethods[m];
                TypeBinding[] toMatch = method.parameters;
                if (toMatch.length != argCount) continue;
                int p = 0;
                while (p < argCount) {
                    if (TypeBinding.notEquals(toMatch[p], argumentTypes[p])) continue block2;
                    ++p;
                }
                if (match != null) {
                    return null;
                }
                match = method;
            }
        }
        return match;
    }

    @Override
    public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
        int argCount = argumentTypes.length;
        boolean foundNothing = true;
        MethodBinding match = null;
        if ((this.tagBits & 0x8000L) != 0L) {
            long range = ReferenceBinding.binarySearch(selector, this.methods);
            if (range >= 0L) {
                int imethod = (int)range;
                int end = (int)(range >> 32);
                while (imethod <= end) {
                    block20: {
                        MethodBinding method = this.methods[imethod];
                        foundNothing = false;
                        if (method.parameters.length == argCount) {
                            TypeBinding[] toMatch = method.parameters;
                            int iarg = 0;
                            while (iarg < argCount) {
                                if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                    ++iarg;
                                    continue;
                                }
                                break block20;
                            }
                            if (match != null) {
                                return null;
                            }
                            match = method;
                        }
                    }
                    ++imethod;
                }
            }
        } else {
            MethodBinding[] matchingMethods = this.getMethods(selector);
            foundNothing = matchingMethods == Binding.NO_METHODS;
            int m = matchingMethods.length;
            block2: while (--m >= 0) {
                MethodBinding method = matchingMethods[m];
                TypeBinding[] toMatch = method.parameters;
                if (toMatch.length != argCount) continue;
                int p = 0;
                while (p < argCount) {
                    if (TypeBinding.notEquals(toMatch[p], argumentTypes[p])) continue block2;
                    ++p;
                }
                if (match != null) {
                    return null;
                }
                match = method;
            }
        }
        if (match != null) {
            if (match.hasSubstitutedParameters()) {
                return null;
            }
            return match;
        }
        if (foundNothing && (this.arguments == null || this.arguments.length <= 1)) {
            if (this.isInterface()) {
                if (this.superInterfaces().length == 1) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    }
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            } else if (this.superclass() != null) {
                if (refScope != null) {
                    refScope.recordTypeReference(this.superclass);
                }
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }

    @Override
    public FieldBinding getField(char[] fieldName, boolean needResolve) {
        FieldBinding originalField;
        if ((this.tagBits & 0x2000L) == 0L && (this.type.tagBits & 0x1000L) != 0L && (originalField = ReferenceBinding.binarySearch(fieldName, this.type.unResolvedFields())) == null) {
            return null;
        }
        this.fields();
        return ReferenceBinding.binarySearch(fieldName, this.fields);
    }

    @Override
    public MethodBinding[] getMethods(char[] selector) {
        long range;
        if (this.methods != null && (range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            int start = (int)range;
            int length = (int)(range >> 32) - start + 1;
            MethodBinding[] result = new MethodBinding[length];
            System.arraycopy(this.methods, start, result, 0, length);
            return result;
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            return Binding.NO_METHODS;
        }
        MethodBinding[] parameterizedMethods = null;
        try {
            MethodBinding[] originalMethods = this.type.getMethods(selector);
            int length = originalMethods.length;
            if (length == 0) {
                MethodBinding[] methodBindingArray = Binding.NO_METHODS;
                return methodBindingArray;
            }
            parameterizedMethods = new MethodBinding[length];
            boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            int i = 0;
            while (i < length) {
                parameterizedMethods[i] = this.createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations) {
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
                }
                ++i;
            }
            if (this.methods == null) {
                MethodBinding[] temp = new MethodBinding[length];
                System.arraycopy(parameterizedMethods, 0, temp, 0, length);
                this.methods = temp;
            } else {
                int total = length + this.methods.length;
                MethodBinding[] temp = new MethodBinding[total];
                System.arraycopy(parameterizedMethods, 0, temp, 0, length);
                System.arraycopy(this.methods, 0, temp, length, this.methods.length);
                if (total > 1) {
                    ReferenceBinding.sortMethods(temp, 0, total);
                }
                this.methods = temp;
            }
            MethodBinding[] methodBindingArray = parameterizedMethods;
            return methodBindingArray;
        }
        finally {
            if (parameterizedMethods == null) {
                parameterizedMethods = Binding.NO_METHODS;
                this.methods = Binding.NO_METHODS;
            }
        }
    }

    @Override
    public int getOuterLocalVariablesSlotSize() {
        return this.genericType().getOuterLocalVariablesSlotSize();
    }

    @Override
    public boolean hasMemberTypes() {
        return this.type.hasMemberTypes();
    }

    @Override
    public boolean hasTypeBit(int bit) {
        TypeBinding erasure = this.erasure();
        if (erasure instanceof ReferenceBinding) {
            return ((ReferenceBinding)erasure).hasTypeBit(bit);
        }
        return false;
    }

    @Override
    protected boolean hasMethodWithNumArgs(char[] selector, int numArgs) {
        return this.type.hasMethodWithNumArgs(selector, numArgs);
    }

    @Override
    public boolean hasValueBasedTypeAnnotation() {
        return this.type.hasValueBasedTypeAnnotation();
    }

    @Override
    public boolean implementsMethod(MethodBinding method) {
        return this.type.implementsMethod(method);
    }

    void initialize(ReferenceBinding someType, TypeBinding[] someArguments) {
        this.type = someType;
        this.sourceName = someType.sourceName;
        this.compoundName = someType.compoundName;
        this.fPackage = someType.fPackage;
        this.fileName = someType.fileName;
        this.modifiers = someType.modifiers & 0xBFFFFFFF;
        if (someArguments != null) {
            this.modifiers |= 0x40000000;
        } else if (this.enclosingType != null) {
            this.modifiers |= this.enclosingType.modifiers & 0x40000000;
            this.tagBits |= this.enclosingType.tagBits & 0x2000000020000080L;
        }
        if (someArguments != null) {
            this.arguments = someArguments;
            int i = 0;
            int length = someArguments.length;
            while (i < length) {
                TypeBinding someArgument = someArguments[i];
                switch (someArgument.kind()) {
                    case 516: {
                        this.tagBits |= 0x40000000L;
                        if (((WildcardBinding)someArgument).boundKind == 0) break;
                        this.tagBits |= 0x800000L;
                        break;
                    }
                    case 8196: {
                        this.tagBits |= 0x40800000L;
                        break;
                    }
                    default: {
                        this.tagBits |= 0x800000L;
                    }
                }
                this.tagBits |= someArgument.tagBits & 0x2000000020000880L;
                ++i;
            }
        }
        this.tagBits |= someType.tagBits & 0x218000000000089CL;
        this.tagBits &= 0xFFFFFFFFFFFF5FFFL;
    }

    protected void initializeArguments() {
    }

    @Override
    void initializeForStaticImports() {
        this.type.initializeForStaticImports();
    }

    @Override
    public boolean isBoundParameterizedType() {
        return (this.tagBits & 0x800000L) != 0L;
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (ParameterizedTypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516: 
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260: {
                ReferenceBinding enclosing;
                ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                if (TypeBinding.notEquals(this.type, otherParamType.type)) {
                    return false;
                }
                if (!this.isStatic() && (enclosing = this.enclosingType()) != null) {
                    ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                    if (otherEnclosing == null) {
                        return false;
                    }
                    if ((otherEnclosing.tagBits & 0x40000000L) == 0L ? TypeBinding.notEquals(enclosing, otherEnclosing) : !enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                        return false;
                    }
                }
                if (this.arguments != ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS) {
                    if (this.arguments == null) {
                        return otherParamType.arguments == null;
                    }
                    int length = this.arguments.length;
                    TypeBinding[] otherArguments = otherParamType.arguments;
                    if (otherArguments == null || otherArguments.length != length) {
                        return false;
                    }
                    int i = 0;
                    while (i < length) {
                        if (!this.arguments[i].isTypeArgumentContainedBy(otherArguments[i])) {
                            return false;
                        }
                        ++i;
                    }
                }
                return true;
            }
            case 1028: {
                return TypeBinding.equalsEquals(this.erasure(), otherType.erasure());
            }
        }
        return TypeBinding.equalsEquals(this.erasure(), otherType);
    }

    @Override
    public boolean isHierarchyConnected() {
        return this.superclass != null && this.superInterfaces != null;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        if (this.arguments != null) {
            int i = 0;
            while (i < this.arguments.length) {
                if (!this.arguments[i].isProperType(admitCapture18)) {
                    return false;
                }
                ++i;
            }
        }
        return super.isProperType(admitCapture18);
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        ReferenceBinding newEnclosing = this.enclosingType;
        if (!this.isStatic() && this.enclosingType != null) {
            newEnclosing = (ReferenceBinding)this.enclosingType.substituteInferenceVariable(var, substituteType);
        }
        if (this.arguments != null) {
            TypeBinding[] newArgs = null;
            int length = this.arguments.length;
            int i = 0;
            while (i < length) {
                TypeBinding oldArg = this.arguments[i];
                TypeBinding newArg = oldArg.substituteInferenceVariable(var, substituteType);
                if (TypeBinding.notEquals(newArg, oldArg)) {
                    if (newArgs == null) {
                        newArgs = new TypeBinding[length];
                        System.arraycopy(this.arguments, 0, newArgs, 0, length);
                    }
                    newArgs[i] = newArg;
                }
                ++i;
            }
            if (newArgs != null) {
                return this.environment.createParameterizedType(this.type, newArgs, newEnclosing);
            }
        } else if (TypeBinding.notEquals(newEnclosing, this.enclosingType)) {
            return this.environment.createParameterizedType(this.type, this.arguments, newEnclosing);
        }
        return this;
    }

    @Override
    public boolean isRawSubstitution() {
        return this.isRawType();
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
        ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.environment.getUnannotatedType(this.type);
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createParameterizedType(unannotatedGenericType, this.arguments, this.enclosingType, newAnnotations);
    }

    @Override
    public int kind() {
        return 260;
    }

    @Override
    public ReferenceBinding[] memberTypes() {
        if (this.memberTypes == null) {
            try {
                ReferenceBinding[] originalMemberTypes = this.type.memberTypes();
                int length = originalMemberTypes.length;
                ReferenceBinding[] parameterizedMemberTypes = new ReferenceBinding[length];
                int i = 0;
                while (i < length) {
                    parameterizedMemberTypes[i] = originalMemberTypes[i].isStatic() ? originalMemberTypes[i] : this.environment.createParameterizedType(originalMemberTypes[i], null, this);
                    ++i;
                }
                this.memberTypes = parameterizedMemberTypes;
            }
            finally {
                if (this.memberTypes == null) {
                    this.memberTypes = Binding.NO_MEMBER_TYPES;
                }
            }
        }
        return this.memberTypes;
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        if (super.mentionsAny(parameters, idx)) {
            return true;
        }
        if (this.arguments != null) {
            int len = this.arguments.length;
            int i = 0;
            while (i < len) {
                if (TypeBinding.notEquals(this.arguments[i], this) && this.arguments[i].mentionsAny(parameters, idx)) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        if (this.arguments != null) {
            int len = this.arguments.length;
            int i = 0;
            while (i < len) {
                if (TypeBinding.notEquals(this.arguments[i], this)) {
                    this.arguments[i].collectInferenceVariables(variables);
                }
                ++i;
            }
        }
        if (!this.isStatic() && this.enclosingType != null) {
            this.enclosingType.collectInferenceVariables(variables);
        }
    }

    @Override
    public MethodBinding[] methods() {
        if ((this.tagBits & 0x8000L) != 0L) {
            return this.methods;
        }
        try {
            MethodBinding[] originalMethods = this.type.methods();
            int length = originalMethods.length;
            MethodBinding[] parameterizedMethods = new MethodBinding[length];
            boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            int i = 0;
            while (i < length) {
                parameterizedMethods[i] = this.createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations) {
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
                }
                ++i;
            }
            this.methods = parameterizedMethods;
        }
        finally {
            if (this.methods == null) {
                this.methods = Binding.NO_METHODS;
            }
            this.tagBits |= 0x8000L;
        }
        return this.methods;
    }

    @Override
    public int problemId() {
        return this.type.problemId();
    }

    @Override
    public char[] qualifiedPackageName() {
        return this.type.qualifiedPackageName();
    }

    @Override
    public char[] qualifiedSourceName() {
        return this.type.qualifiedSourceName();
    }

    @Override
    public char[] readableName() {
        return this.readableName(true);
    }

    @Override
    public char[] readableName(boolean showGenerics) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(CharOperation.concat(this.enclosingType().readableName(showGenerics && !this.isStatic()), this.sourceName, '.'));
        } else {
            nameBuffer.append(CharOperation.concatWith(this.type.compoundName, '.'));
        }
        if (showGenerics && this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].readableName());
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    ReferenceBinding resolve() {
        if ((this.tagBits & 0x1000000L) == 0L) {
            return this;
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        ReferenceBinding resolvedType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.environment, false);
        this.tagBits |= resolvedType.tagBits & 0x800L;
        if (this.arguments != null) {
            int argLength = this.arguments.length;
            if ((this.type.tagBits & 0x80L) == 0L) {
                this.tagBits &= 0xFFFFFFFFFFFFFF7FL;
                if (this.enclosingType != null) {
                    this.tagBits |= this.enclosingType.tagBits & 0x80L;
                }
            }
            int i = 0;
            while (i < argLength) {
                TypeBinding resolveType;
                this.arguments[i] = resolveType = BinaryTypeBinding.resolveType(this.arguments[i], this.environment, true);
                this.tagBits |= resolveType.tagBits & 0x880L;
                ++i;
            }
        }
        return this;
    }

    @Override
    public char[] shortReadableName() {
        return this.shortReadableName(true);
    }

    @Override
    public char[] shortReadableName(boolean showGenerics) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(CharOperation.concat(this.enclosingType().shortReadableName(showGenerics && !this.isStatic()), this.sourceName, '.'));
        } else {
            nameBuffer.append(this.type.sourceName);
        }
        if (showGenerics && this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].shortReadableName());
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        if (shortNames) {
            return this.nullAnnotatedShortReadableName(options);
        }
        return this.nullAnnotatedReadableName(options);
    }

    @Override
    char[] nullAnnotatedReadableName(CompilerOptions options) {
        int i;
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, false));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else if (this.type.compoundName != null) {
            int l = this.type.compoundName.length;
            i = 0;
            while (i < l - 1) {
                nameBuffer.append(this.type.compoundName[i]);
                nameBuffer.append('.');
                ++i;
            }
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.type.compoundName[i]);
        } else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null) {
                nameBuffer.append(this.type.sourceName);
            } else {
                nameBuffer.append(this.type.readableName());
            }
        }
        if (this.arguments != null && this.arguments.length > 0 && !this.isRawType()) {
            nameBuffer.append('<');
            i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, false));
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    @Override
    char[] nullAnnotatedShortReadableName(CompilerOptions options) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, true));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null) {
                nameBuffer.append(this.type.sourceName);
            } else {
                nameBuffer.append(this.type.shortReadableName());
            }
        }
        if (this.arguments != null && this.arguments.length > 0 && !this.isRawType()) {
            nameBuffer.append('<');
            int i = 0;
            int length = this.arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, true));
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    @Override
    public char[] signature() {
        if (this.signature == null) {
            this.signature = this.type.signature();
        }
        return this.signature;
    }

    @Override
    public char[] sourceName() {
        return this.type.sourceName();
    }

    @Override
    public TypeBinding substitute(TypeVariableBinding originalVariable) {
        ParameterizedTypeBinding currentType = this;
        while (true) {
            ReferenceBinding enclosing;
            TypeVariableBinding[] typeVariables;
            int length;
            if (originalVariable.rank < (length = (typeVariables = currentType.type.typeVariables()).length) && TypeBinding.equalsEquals(typeVariables[originalVariable.rank], originalVariable)) {
                if (currentType.arguments == null) {
                    currentType.initializeArguments();
                }
                if (currentType.arguments != null) {
                    if (currentType.arguments.length == 0) {
                        return originalVariable;
                    }
                    TypeBinding substitute = currentType.arguments[originalVariable.rank];
                    return originalVariable.combineTypeAnnotations(substitute);
                }
            }
            if (currentType.isStatic() || !((enclosing = currentType.enclosingType()) instanceof ParameterizedTypeBinding)) break;
            currentType = (ParameterizedTypeBinding)enclosing;
        }
        return originalVariable;
    }

    @Override
    public ReferenceBinding superclass() {
        if (this.superclass == null) {
            ReferenceBinding genericSuperclass = this.type.superclass();
            if (genericSuperclass == null) {
                return null;
            }
            this.superclass = (ReferenceBinding)Scope.substitute((Substitution)this, genericSuperclass);
            this.typeBits |= this.superclass.typeBits & 0x713;
            if ((this.typeBits & 3) != 0) {
                this.typeBits |= this.applyCloseableClassWhitelists(this.environment.globalOptions);
            }
        }
        return this.superclass;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
            if (this.type.isHierarchyBeingConnected()) {
                return Binding.NO_SUPERINTERFACES;
            }
            this.superInterfaces = Scope.substitute((Substitution)this, this.type.superInterfaces());
            if (this.superInterfaces != null) {
                int i = this.superInterfaces.length;
                while (--i >= 0) {
                    this.typeBits |= this.superInterfaces[i].typeBits & 0x713;
                    if ((this.typeBits & 3) == 0) continue;
                    this.typeBits |= this.applyCloseableInterfaceWhitelists();
                }
            }
        }
        return this.superInterfaces;
    }

    @Override
    public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
        boolean update = false;
        if (this.type == unresolvedType) {
            this.type = resolvedType;
            update = true;
            ReferenceBinding enclosing = resolvedType.enclosingType();
            if (enclosing != null) {
                ReferenceBinding referenceBinding = this.enclosingType = resolvedType.isStatic() ? enclosing : (ReferenceBinding)env.convertUnresolvedBinaryToRawType(enclosing);
            }
        }
        if (this.arguments != null) {
            int i = 0;
            int l = this.arguments.length;
            while (i < l) {
                if (this.arguments[i] == unresolvedType) {
                    this.arguments[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                    update = true;
                }
                ++i;
            }
        }
        if (update) {
            this.initialize(this.type, this.arguments);
        }
    }

    @Override
    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        return this.genericType().syntheticEnclosingInstanceTypes();
    }

    @Override
    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        return this.genericType().syntheticOuterLocalVariables();
    }

    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer buffer = new StringBuffer(30);
        if (this.type instanceof UnresolvedReferenceBinding) {
            buffer.append(this.debugName());
        } else {
            int length;
            int i;
            if (this.isDeprecated()) {
                buffer.append("deprecated ");
            }
            if (this.isPublic()) {
                buffer.append("public ");
            }
            if (this.isProtected()) {
                buffer.append("protected ");
            }
            if (this.isPrivate()) {
                buffer.append("private ");
            }
            if (this.isAbstract() && this.isClass()) {
                buffer.append("abstract ");
            }
            if (this.isStatic() && this.isNestedType()) {
                buffer.append("static ");
            }
            if (this.isFinal()) {
                buffer.append("final ");
            }
            if (this.isRecord()) {
                buffer.append("record ");
            } else if (this.isEnum()) {
                buffer.append("enum ");
            } else if (this.isAnnotationType()) {
                buffer.append("@interface ");
            } else if (this.isClass()) {
                buffer.append("class ");
            } else {
                buffer.append("interface ");
            }
            buffer.append(this.debugName());
            buffer.append("\n\textends ");
            buffer.append(this.superclass != null ? this.superclass.debugName() : "NULL TYPE");
            if (this.superInterfaces != null) {
                if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    buffer.append("\n\timplements : ");
                    i = 0;
                    length = this.superInterfaces.length;
                    while (i < length) {
                        if (i > 0) {
                            buffer.append(", ");
                        }
                        buffer.append(this.superInterfaces[i] != null ? this.superInterfaces[i].debugName() : "NULL TYPE");
                        ++i;
                    }
                }
            } else {
                buffer.append("NULL SUPERINTERFACES");
            }
            if (this.enclosingType() != null) {
                buffer.append("\n\tenclosing type : ");
                buffer.append(this.enclosingType().debugName());
            }
            if (this.fields != null) {
                if (this.fields != Binding.NO_FIELDS) {
                    buffer.append("\n/*   fields   */");
                    i = 0;
                    length = this.fields.length;
                    while (i < length) {
                        buffer.append('\n').append(this.fields[i] != null ? this.fields[i].toString() : "NULL FIELD");
                        ++i;
                    }
                }
            } else {
                buffer.append("NULL FIELDS");
            }
            if (this.methods != null) {
                if (this.methods != Binding.NO_METHODS) {
                    buffer.append("\n/*   methods   */");
                    i = 0;
                    length = this.methods.length;
                    while (i < length) {
                        buffer.append('\n').append(this.methods[i] != null ? this.methods[i].toString() : "NULL METHOD");
                        ++i;
                    }
                }
            } else {
                buffer.append("NULL METHODS");
            }
            buffer.append("\n\n");
        }
        return buffer.toString();
    }

    @Override
    public TypeVariableBinding[] typeVariables() {
        if (this.arguments == null) {
            return this.type.typeVariables();
        }
        return Binding.NO_TYPE_VARIABLES;
    }

    @Override
    public TypeBinding[] typeArguments() {
        return this.arguments;
    }

    @Override
    public FieldBinding[] unResolvedFields() {
        return this.fields;
    }

    @Override
    protected MethodBinding[] getInterfaceAbstractContracts(Scope scope, boolean replaceWildcards, boolean filterDefaultMethods) throws InvalidInputException {
        if (replaceWildcards) {
            TypeBinding[] types = this.getNonWildcardParameterization(scope);
            if (types == null) {
                return new MethodBinding[]{new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18)};
            }
            int i = 0;
            while (i < types.length) {
                if (TypeBinding.notEquals(types[i], this.arguments[i])) {
                    ParameterizedTypeBinding declaringType = scope.environment().createParameterizedType(this.type, types, this.type.enclosingType());
                    TypeVariableBinding[] typeParameters = this.type.typeVariables();
                    int j = 0;
                    int length = typeParameters.length;
                    while (j < length) {
                        if (!typeParameters[j].boundCheck(declaringType, types[j], scope, null).isOKbyJLS()) {
                            return new MethodBinding[]{new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18)};
                        }
                        ++j;
                    }
                    return declaringType.getInterfaceAbstractContracts(scope, replaceWildcards, filterDefaultMethods);
                }
                ++i;
            }
        }
        return super.getInterfaceAbstractContracts(scope, replaceWildcards, filterDefaultMethods);
    }

    @Override
    public MethodBinding getSingleAbstractMethod(Scope scope, boolean replaceWildcards) {
        return this.getSingleAbstractMethod(scope, replaceWildcards, -1, -1);
    }

    public MethodBinding getSingleAbstractMethod(Scope scope, boolean replaceWildcards, int start, int end) {
        int index;
        int n = replaceWildcards ? (end < 0 ? 0 : 1) : (index = 2);
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        } else {
            this.singleAbstractMethod = new MethodBinding[3];
        }
        if (!this.isValidBinding()) {
            return null;
        }
        ReferenceBinding genericType = this.genericType();
        MethodBinding theAbstractMethod = genericType.getSingleAbstractMethod(scope, replaceWildcards);
        if (theAbstractMethod == null || !theAbstractMethod.isValidBinding()) {
            this.singleAbstractMethod[index] = theAbstractMethod;
            return this.singleAbstractMethod[index];
        }
        ParameterizedTypeBinding declaringType = null;
        TypeBinding[] types = this.arguments;
        if (replaceWildcards) {
            types = this.getNonWildcardParameterization(scope);
            if (types == null) {
                this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18);
                return this.singleAbstractMethod[index];
            }
        } else if (types == null) {
            types = NO_TYPES;
        }
        if (end >= 0) {
            int i = 0;
            int length = types.length;
            while (i < length) {
                types[i] = types[i].capture(scope, start, end);
                ++i;
            }
        }
        declaringType = scope.environment().createParameterizedType(genericType, types, genericType.enclosingType());
        TypeVariableBinding[] typeParameters = genericType.typeVariables();
        int i = 0;
        int length = typeParameters.length;
        while (i < length) {
            if (!typeParameters[i].boundCheck(declaringType, types[i], scope, null).isOKbyJLS()) {
                this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18);
                return this.singleAbstractMethod[index];
            }
            ++i;
        }
        ReferenceBinding substitutedDeclaringType = (ReferenceBinding)declaringType.findSuperTypeOriginatingFrom(theAbstractMethod.declaringClass);
        MethodBinding[] choices = substitutedDeclaringType.getMethods(theAbstractMethod.selector);
        int i2 = 0;
        int length2 = choices.length;
        while (i2 < length2) {
            MethodBinding method = choices[i2];
            if (method.isAbstract() && !method.redeclaresPublicObjectMethod(scope)) {
                if (method.problemId() == 25) {
                    method = ((ProblemMethodBinding)method).closestMatch;
                }
                this.singleAbstractMethod[index] = method;
                break;
            }
            ++i2;
        }
        return this.singleAbstractMethod[index];
    }

    public TypeBinding[] getNonWildcardParameterization(Scope scope) {
        TypeBinding[] typeArguments = this.arguments;
        if (typeArguments == null) {
            return NO_TYPES;
        }
        TypeBinding[] typeParameters = this.genericType().typeVariables();
        TypeBinding[] types = new TypeBinding[typeArguments.length];
        int i = 0;
        int length = typeArguments.length;
        while (i < length) {
            block20: {
                TypeBinding typeArgument;
                block19: {
                    typeArgument = typeArguments[i];
                    if (typeArgument.kind() != 516) break block19;
                    if (typeParameters[i].mentionsAny(typeParameters, i)) {
                        return null;
                    }
                    WildcardBinding wildcard = (WildcardBinding)typeArgument;
                    switch (wildcard.boundKind) {
                        case 1: {
                            int j;
                            TypeBinding[] otherUBounds = wildcard.otherBounds;
                            TypeBinding[] otherBBounds = ((TypeVariableBinding)typeParameters[i]).otherUpperBounds();
                            int len = 1 + (otherUBounds != null ? otherUBounds.length : 0) + otherBBounds.length;
                            if (((TypeVariableBinding)typeParameters[i]).firstBound != null) {
                                ++len;
                            }
                            TypeBinding[] allBounds = new TypeBinding[len];
                            int idx = 0;
                            allBounds[idx++] = wildcard.bound;
                            if (otherUBounds != null) {
                                j = 0;
                                while (j < otherUBounds.length) {
                                    allBounds[idx++] = otherUBounds[j];
                                    ++j;
                                }
                            }
                            if (((TypeVariableBinding)typeParameters[i]).firstBound != null) {
                                allBounds[idx++] = ((TypeVariableBinding)typeParameters[i]).firstBound;
                            }
                            j = 0;
                            while (j < otherBBounds.length) {
                                allBounds[idx++] = otherBBounds[j];
                                ++j;
                            }
                            Object[] glb = Scope.greaterLowerBound(allBounds, null, this.environment);
                            if (glb == null || glb.length == 0) {
                                return null;
                            }
                            if (glb.length == 1) {
                                types[i] = glb[0];
                                break;
                            }
                            try {
                                ReferenceBinding[] refs = new ReferenceBinding[glb.length];
                                System.arraycopy(glb, 0, refs, 0, glb.length);
                                types[i] = this.environment.createIntersectionType18(refs);
                                break;
                            }
                            catch (ArrayStoreException arrayStoreException) {
                                scope.problemReporter().genericInferenceError("Cannot compute glb of " + Arrays.toString(glb), null);
                                return null;
                            }
                        }
                        case 2: {
                            types[i] = wildcard.bound;
                            break;
                        }
                        case 0: {
                            types[i] = ((TypeVariableBinding)typeParameters[i]).firstBound;
                            if (types[i] != null) break block20;
                            types[i] = ((TypeVariableBinding)typeParameters[i]).superclass;
                        }
                        default: {
                            break;
                        }
                        {
                        }
                    }
                    break block20;
                }
                types[i] = typeArgument;
            }
            ++i;
        }
        return types;
    }

    @Override
    public long updateTagBits() {
        if (this.arguments != null) {
            TypeBinding[] typeBindingArray = this.arguments;
            int n = this.arguments.length;
            int n2 = 0;
            while (n2 < n) {
                TypeBinding argument = typeBindingArray[n2];
                this.tagBits |= argument.updateTagBits();
                ++n2;
            }
        }
        return super.updateTagBits();
    }
}

