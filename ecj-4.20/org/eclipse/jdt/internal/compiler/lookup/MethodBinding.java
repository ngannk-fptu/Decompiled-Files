/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterNonNullDefaultProvider;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class MethodBinding
extends Binding {
    public int modifiers;
    public char[] selector;
    public TypeBinding returnType;
    public TypeBinding[] parameters;
    public TypeBinding receiver;
    public ReferenceBinding[] thrownExceptions;
    public ReferenceBinding declaringClass;
    public TypeVariableBinding[] typeVariables = Binding.NO_TYPE_VARIABLES;
    char[] signature;
    public long tagBits;
    protected AnnotationBinding[] typeAnnotations = Binding.NO_ANNOTATIONS;
    public Boolean[] parameterNonNullness;
    public int defaultNullness;
    public char[][] parameterNames = Binding.NO_PARAMETER_NAMES;

    protected MethodBinding() {
    }

    public MethodBinding(int modifiers, char[] selector, TypeBinding returnType, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass) {
        this.modifiers = modifiers;
        this.selector = selector;
        this.returnType = returnType;
        this.parameters = parameters == null || parameters.length == 0 ? Binding.NO_PARAMETERS : parameters;
        this.thrownExceptions = thrownExceptions == null || thrownExceptions.length == 0 ? Binding.NO_EXCEPTIONS : thrownExceptions;
        this.declaringClass = declaringClass;
        if (this.declaringClass != null && this.declaringClass.isStrictfp() && !this.isNative() && !this.isAbstract()) {
            this.modifiers |= 0x800;
        }
    }

    public MethodBinding(int modifiers, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass) {
        this(modifiers, TypeConstants.INIT, TypeBinding.VOID, parameters, thrownExceptions, declaringClass);
    }

    public MethodBinding(MethodBinding initialMethodBinding, ReferenceBinding declaringClass) {
        this.modifiers = initialMethodBinding.modifiers;
        this.selector = initialMethodBinding.selector;
        this.returnType = initialMethodBinding.returnType;
        this.parameters = initialMethodBinding.parameters;
        this.thrownExceptions = initialMethodBinding.thrownExceptions;
        this.declaringClass = declaringClass;
        declaringClass.storeAnnotationHolder(this, initialMethodBinding.declaringClass.retrieveAnnotationHolder(initialMethodBinding, true));
    }

    public final boolean areParameterErasuresEqual(MethodBinding method) {
        TypeBinding[] args = method.parameters;
        if (this.parameters == args) {
            return true;
        }
        int length = this.parameters.length;
        if (length != args.length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (TypeBinding.notEquals(this.parameters[i], args[i]) && TypeBinding.notEquals(this.parameters[i].erasure(), args[i].erasure())) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public final boolean areParametersCompatibleWith(TypeBinding[] arguments) {
        int argLength;
        int paramLength = this.parameters.length;
        int lastIndex = argLength = arguments.length;
        if (this.isVarargs()) {
            TypeBinding varArgType;
            lastIndex = paramLength - 1;
            if (paramLength == argLength) {
                varArgType = this.parameters[lastIndex];
                TypeBinding lastArgument = arguments[lastIndex];
                if (TypeBinding.notEquals(varArgType, lastArgument) && !lastArgument.isCompatibleWith(varArgType)) {
                    return false;
                }
            } else if (paramLength < argLength) {
                varArgType = ((ArrayBinding)this.parameters[lastIndex]).elementsType();
                int i = lastIndex;
                while (i < argLength) {
                    if (TypeBinding.notEquals(varArgType, arguments[i]) && !arguments[i].isCompatibleWith(varArgType)) {
                        return false;
                    }
                    ++i;
                }
            } else if (lastIndex != argLength) {
                return false;
            }
        }
        int i = 0;
        while (i < lastIndex) {
            if (TypeBinding.notEquals(this.parameters[i], arguments[i]) && !arguments[i].isCompatibleWith(this.parameters[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public final boolean areParametersEqual(MethodBinding method) {
        TypeBinding[] args = method.parameters;
        if (this.parameters == args) {
            return true;
        }
        int length = this.parameters.length;
        if (length != args.length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (TypeBinding.notEquals(this.parameters[i], args[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public final boolean areTypeVariableErasuresEqual(MethodBinding method) {
        TypeVariableBinding[] vars = method.typeVariables;
        if (this.typeVariables == vars) {
            return true;
        }
        int length = this.typeVariables.length;
        if (length != vars.length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (TypeBinding.notEquals(this.typeVariables[i], vars[i]) && TypeBinding.notEquals(this.typeVariables[i].erasure(), vars[i].erasure())) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public MethodBinding asRawMethod(LookupEnvironment env) {
        if (this.typeVariables == Binding.NO_TYPE_VARIABLES) {
            return this;
        }
        int length = this.typeVariables.length;
        TypeBinding[] arguments = new TypeBinding[length];
        int i = 0;
        while (i < length) {
            arguments[i] = this.makeRawArgument(env, this.typeVariables[i]);
            ++i;
        }
        return env.createParameterizedGenericMethod(this, arguments);
    }

    private TypeBinding makeRawArgument(LookupEnvironment env, TypeVariableBinding var) {
        if (var.boundsCount() <= 1) {
            TypeBinding upperBound = var.upperBound();
            if (upperBound.isTypeVariable()) {
                return this.makeRawArgument(env, (TypeVariableBinding)upperBound);
            }
            return env.convertToRawType(upperBound, false);
        }
        ReferenceBinding[] itsSuperinterfaces = var.superInterfaces();
        int superLength = itsSuperinterfaces.length;
        TypeBinding rawFirstBound = null;
        TypeBinding[] rawOtherBounds = null;
        if (var.boundsCount() == superLength) {
            rawFirstBound = env.convertToRawType(itsSuperinterfaces[0], false);
            rawOtherBounds = new TypeBinding[superLength - 1];
            int s = 1;
            while (s < superLength) {
                rawOtherBounds[s - 1] = env.convertToRawType(itsSuperinterfaces[s], false);
                ++s;
            }
        } else {
            rawFirstBound = env.convertToRawType(var.superclass(), false);
            rawOtherBounds = new TypeBinding[superLength];
            int s = 0;
            while (s < superLength) {
                rawOtherBounds[s] = env.convertToRawType(itsSuperinterfaces[s], false);
                ++s;
            }
        }
        return env.createWildcard(null, 0, rawFirstBound, rawOtherBounds, 1);
    }

    public final boolean canBeSeenBy(InvocationSite invocationSite, Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this.declaringClass)) {
            return true;
        }
        if (this.isProtected()) {
            if (invocationType.fPackage == this.declaringClass.fPackage) {
                return true;
            }
            return invocationSite.isSuperAccess();
        }
        if (this.isPrivate()) {
            ReferenceBinding outerInvocationType = invocationType;
            ReferenceBinding temp = outerInvocationType.enclosingType();
            while (temp != null) {
                outerInvocationType = temp;
                temp = temp.enclosingType();
            }
            ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
            temp = outerDeclaringClass.enclosingType();
            while (temp != null) {
                outerDeclaringClass = temp;
                temp = temp.enclosingType();
            }
            return TypeBinding.equalsEquals(outerInvocationType, outerDeclaringClass);
        }
        return invocationType.fPackage == this.declaringClass.fPackage;
    }

    public final boolean canBeSeenBy(PackageBinding invocationPackage) {
        if (this.isPublic()) {
            return true;
        }
        if (this.isPrivate()) {
            return false;
        }
        return invocationPackage == this.declaringClass.getPackage();
    }

    public final boolean canBeSeenBy(TypeBinding receiverType, InvocationSite invocationSite, Scope scope) {
        SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (this.declaringClass.isInterface() && this.isStatic() && !this.isPrivate()) {
            if (scope.compilerOptions().sourceLevel < 0x340000L) {
                return false;
            }
            return (invocationSite.isTypeAccess() || invocationSite.receiverIsImplicitThis()) && TypeBinding.equalsEquals(receiverType, this.declaringClass);
        }
        if (this.isPublic()) {
            return true;
        }
        if (TypeBinding.equalsEquals(invocationType, this.declaringClass) && TypeBinding.equalsEquals(invocationType, receiverType)) {
            return true;
        }
        if (invocationType == null) {
            return !this.isPrivate() && scope.getCurrentPackage() == this.declaringClass.fPackage;
        }
        if (this.isProtected()) {
            if (TypeBinding.equalsEquals(invocationType, this.declaringClass)) {
                return true;
            }
            if (invocationType.fPackage == this.declaringClass.fPackage) {
                return true;
            }
            ReferenceBinding currentType = invocationType;
            TypeBinding receiverErasure = receiverType.erasure();
            ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
            int depth = 0;
            do {
                if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
                    if (invocationSite.isSuperAccess()) {
                        return true;
                    }
                    if (receiverType instanceof ArrayBinding) {
                        return false;
                    }
                    if (this.isStatic()) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                    if (TypeBinding.equalsEquals(currentType, receiverErasure) || receiverErasure.findSuperTypeOriginatingFrom(currentType) != null) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                }
                ++depth;
            } while ((currentType = currentType.enclosingType()) != null);
            return false;
        }
        if (this.isPrivate()) {
            if (!(!TypeBinding.notEquals(receiverType, this.declaringClass) || scope.compilerOptions().complianceLevel <= 0x320000L && receiverType.isTypeVariable() && ((TypeVariableBinding)receiverType).isErasureBoundTo(this.declaringClass.erasure()))) {
                return false;
            }
            if (TypeBinding.notEquals(invocationType, this.declaringClass)) {
                ReferenceBinding outerInvocationType = invocationType;
                ReferenceBinding temp = outerInvocationType.enclosingType();
                while (temp != null) {
                    outerInvocationType = temp;
                    temp = temp.enclosingType();
                }
                ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
                temp = outerDeclaringClass.enclosingType();
                while (temp != null) {
                    outerDeclaringClass = temp;
                    temp = temp.enclosingType();
                }
                if (TypeBinding.notEquals(outerInvocationType, outerDeclaringClass)) {
                    return false;
                }
            }
            return true;
        }
        PackageBinding declaringPackage = this.declaringClass.fPackage;
        if (invocationType.fPackage != declaringPackage) {
            return false;
        }
        if (receiverType instanceof ArrayBinding) {
            return false;
        }
        TypeBinding originalDeclaringClass = this.declaringClass.original();
        ReferenceBinding currentType = (ReferenceBinding)receiverType;
        do {
            if (currentType.isCapture() ? TypeBinding.equalsEquals(originalDeclaringClass, currentType.erasure().original()) : TypeBinding.equalsEquals(originalDeclaringClass, currentType.original())) {
                return true;
            }
            PackageBinding currentPackage = currentType.fPackage;
            if (currentType.isCapture() || currentPackage == null || currentPackage == declaringPackage) continue;
            return false;
        } while ((currentType = currentType.superclass()) != null);
        return false;
    }

    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0L) {
            missingTypes = this.returnType.collectMissingTypes(missingTypes);
            int i = 0;
            int max = this.parameters.length;
            while (i < max) {
                missingTypes = this.parameters[i].collectMissingTypes(missingTypes);
                ++i;
            }
            i = 0;
            max = this.thrownExceptions.length;
            while (i < max) {
                missingTypes = this.thrownExceptions[i].collectMissingTypes(missingTypes);
                ++i;
            }
            i = 0;
            max = this.typeVariables.length;
            while (i < max) {
                TypeVariableBinding variable = this.typeVariables[i];
                missingTypes = variable.superclass().collectMissingTypes(missingTypes);
                ReferenceBinding[] interfaces = variable.superInterfaces();
                int j = 0;
                int length = interfaces.length;
                while (j < length) {
                    missingTypes = interfaces[j].collectMissingTypes(missingTypes);
                    ++j;
                }
                ++i;
            }
        }
        return missingTypes;
    }

    public MethodBinding computeSubstitutedMethod(MethodBinding method, LookupEnvironment env) {
        int length = this.typeVariables.length;
        TypeVariableBinding[] vars = method.typeVariables;
        if (length != vars.length) {
            return null;
        }
        ParameterizedGenericMethodBinding substitute = env.createParameterizedGenericMethod(method, this.typeVariables);
        int i = 0;
        while (i < length) {
            if (!this.typeVariables[i].isInterchangeableWith(vars[i], substitute)) {
                return null;
            }
            ++i;
        }
        return substitute;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        boolean addThrownExceptions;
        boolean isGeneric;
        char[] declaringKey = this.declaringClass.computeUniqueKey(false);
        int declaringLength = declaringKey.length;
        int selectorLength = this.selector == TypeConstants.INIT ? 0 : this.selector.length;
        char[] sig = this.genericSignature();
        boolean bl = isGeneric = sig != null;
        if (!isGeneric) {
            sig = this.signature();
        }
        int signatureLength = sig.length;
        int thrownExceptionsLength = this.thrownExceptions.length;
        int thrownExceptionsSignatureLength = 0;
        char[][] thrownExceptionsSignatures = null;
        boolean bl2 = addThrownExceptions = thrownExceptionsLength > 0 && (!isGeneric || CharOperation.lastIndexOf('^', sig) < 0);
        if (addThrownExceptions) {
            thrownExceptionsSignatures = new char[thrownExceptionsLength][];
            int i = 0;
            while (i < thrownExceptionsLength) {
                if (this.thrownExceptions[i] != null) {
                    thrownExceptionsSignatures[i] = this.thrownExceptions[i].signature();
                    thrownExceptionsSignatureLength += thrownExceptionsSignatures[i].length + 1;
                }
                ++i;
            }
        }
        char[] uniqueKey = new char[declaringLength + 1 + selectorLength + signatureLength + thrownExceptionsSignatureLength];
        int index = 0;
        System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
        index = declaringLength;
        uniqueKey[index++] = 46;
        System.arraycopy(this.selector, 0, uniqueKey, index, selectorLength);
        System.arraycopy(sig, 0, uniqueKey, index += selectorLength, signatureLength);
        if (thrownExceptionsSignatureLength > 0) {
            index += signatureLength;
            int i = 0;
            while (i < thrownExceptionsLength) {
                char[] thrownExceptionSignature = thrownExceptionsSignatures[i];
                if (thrownExceptionSignature != null) {
                    uniqueKey[index++] = 124;
                    int length = thrownExceptionSignature.length;
                    System.arraycopy(thrownExceptionSignature, 0, uniqueKey, index, length);
                    index += length;
                }
                ++i;
            }
        }
        return uniqueKey;
    }

    public final char[] constantPoolName() {
        return this.selector;
    }

    protected void fillInDefaultNonNullness(AbstractMethodDeclaration sourceMethod, boolean needToApplyReturnNonNullDefault, ParameterNonNullDefaultProvider needToApplyParameterNonNullDefault) {
        if (this.parameterNonNullness == null) {
            this.parameterNonNullness = new Boolean[this.parameters.length];
        }
        boolean added = false;
        int length = this.parameterNonNullness.length;
        int i = 0;
        while (i < length) {
            if (needToApplyParameterNonNullDefault.hasNonNullDefaultForParam(i) && !this.parameters[i].isBaseType()) {
                if (this.parameterNonNullness[i] == null) {
                    added = true;
                    this.parameterNonNullness[i] = Boolean.TRUE;
                    if (sourceMethod != null) {
                        sourceMethod.arguments[i].binding.tagBits |= 0x100000000000000L;
                    }
                } else if (sourceMethod != null && this.parameterNonNullness[i].booleanValue()) {
                    sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, i);
                }
            }
            ++i;
        }
        if (added) {
            this.tagBits |= 0x400L;
        }
        if (!needToApplyReturnNonNullDefault) {
            return;
        }
        if (this.returnType != null && !this.returnType.isBaseType() && (this.tagBits & 0x180000000000000L) == 0L) {
            this.tagBits |= 0x100000000000000L;
        } else if (sourceMethod != null && (this.tagBits & 0x100000000000000L) != 0L) {
            sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, -1);
        }
    }

    protected void fillInDefaultNonNullness18(AbstractMethodDeclaration sourceMethod, LookupEnvironment env) {
        MethodBinding original = this.original();
        if (original == null) {
            return;
        }
        ParameterNonNullDefaultProvider hasNonNullDefaultForParameter = this.hasNonNullDefaultForParameter(sourceMethod);
        if (hasNonNullDefaultForParameter.hasAnyNonNullDefault()) {
            boolean added = false;
            int length = this.parameters.length;
            int i = 0;
            while (i < length) {
                if (hasNonNullDefaultForParameter.hasNonNullDefaultForParam(i)) {
                    TypeBinding parameter = this.parameters[i];
                    if (original.parameters[i].acceptsNonNullDefault()) {
                        long existing = parameter.tagBits & 0x180000000000000L;
                        if (existing == 0L) {
                            added = true;
                            if (!parameter.isBaseType()) {
                                this.parameters[i] = env.createAnnotatedType(parameter, new AnnotationBinding[]{env.getNonNullAnnotation()});
                                if (sourceMethod != null) {
                                    sourceMethod.arguments[i].binding.type = this.parameters[i];
                                }
                            }
                        } else if (sourceMethod != null && (parameter.tagBits & 0x100000000000000L) != 0L && sourceMethod.arguments[i].hasNullTypeAnnotation(TypeReference.AnnotationPosition.MAIN_TYPE)) {
                            sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, i);
                        }
                    }
                }
                ++i;
            }
            if (added) {
                this.tagBits |= 0x400L;
            }
        }
        if (original.returnType != null && this.hasNonNullDefaultForReturnType(sourceMethod) && original.returnType.acceptsNonNullDefault()) {
            if ((this.returnType.tagBits & 0x180000000000000L) == 0L) {
                this.returnType = env.createAnnotatedType(this.returnType, new AnnotationBinding[]{env.getNonNullAnnotation()});
            } else if (sourceMethod instanceof MethodDeclaration && (this.returnType.tagBits & 0x100000000000000L) != 0L && ((MethodDeclaration)sourceMethod).hasNullTypeAnnotation(TypeReference.AnnotationPosition.MAIN_TYPE)) {
                sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, -1);
            }
        }
    }

    public MethodBinding findOriginalInheritedMethod(MethodBinding inheritedMethod) {
        MethodBinding inheritedOriginal = inheritedMethod.original();
        TypeBinding superType = this.declaringClass.findSuperTypeOriginatingFrom(inheritedOriginal.declaringClass);
        if (superType == null || !(superType instanceof ReferenceBinding)) {
            return null;
        }
        if (TypeBinding.notEquals(inheritedOriginal.declaringClass, superType)) {
            MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(inheritedOriginal.selector, inheritedOriginal.parameters.length);
            int m = 0;
            int l = superMethods.length;
            while (m < l) {
                if (superMethods[m].original() == inheritedOriginal) {
                    return superMethods[m];
                }
                ++m;
            }
        }
        return inheritedOriginal;
    }

    public char[] genericSignature() {
        int length;
        int i;
        if ((this.modifiers & 0x40000000) == 0) {
            return null;
        }
        StringBuffer sig = new StringBuffer(10);
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            sig.append('<');
            i = 0;
            length = this.typeVariables.length;
            while (i < length) {
                sig.append(this.typeVariables[i].genericSignature());
                ++i;
            }
            sig.append('>');
        }
        sig.append('(');
        i = 0;
        length = this.parameters.length;
        while (i < length) {
            sig.append(this.parameters[i].genericTypeSignature());
            ++i;
        }
        sig.append(')');
        if (this.returnType != null) {
            sig.append(this.returnType.genericTypeSignature());
        }
        boolean needExceptionSignatures = false;
        length = this.thrownExceptions.length;
        int i2 = 0;
        while (i2 < length) {
            if ((this.thrownExceptions[i2].modifiers & 0x40000000) != 0) {
                needExceptionSignatures = true;
                break;
            }
            ++i2;
        }
        if (needExceptionSignatures) {
            i2 = 0;
            while (i2 < length) {
                sig.append('^');
                sig.append(this.thrownExceptions[i2].genericTypeSignature());
                ++i2;
            }
        }
        int sigLength = sig.length();
        char[] genericSignature = new char[sigLength];
        sig.getChars(0, sigLength, genericSignature, 0);
        return genericSignature;
    }

    public final int getAccessFlags() {
        return this.modifiers & 0x1FFFF;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        MethodBinding originalMethod = this.original();
        return originalMethod.declaringClass.retrieveAnnotations(originalMethod);
    }

    @Override
    public long getAnnotationTagBits() {
        ClassScope scope;
        MethodBinding originalMethod = this.original();
        if ((originalMethod.tagBits & 0x200000000L) == 0L && originalMethod.declaringClass instanceof SourceTypeBinding && (scope = ((SourceTypeBinding)originalMethod.declaringClass).scope) != null) {
            Binding target;
            long nullDefaultBits;
            TypeDeclaration typeDecl = scope.referenceContext;
            AbstractMethodDeclaration methodDecl = typeDecl.declarationOf(originalMethod);
            if (methodDecl != null) {
                ASTNode.resolveAnnotations((BlockScope)methodDecl.scope, methodDecl.annotations, originalMethod);
            }
            CompilerOptions options = scope.compilerOptions();
            if (options.isAnnotationBasedNullAnalysisEnabled && (nullDefaultBits = (long)this.defaultNullness) != 0L && this.declaringClass instanceof SourceTypeBinding && (target = scope.checkRedundantDefaultNullness(this.defaultNullness, typeDecl.declarationSourceStart)) != null) {
                methodDecl.scope.problemReporter().nullDefaultAnnotationIsRedundant(methodDecl, methodDecl.annotations, target);
            }
        }
        return originalMethod.tagBits;
    }

    public Object getDefaultValue() {
        AnnotationHolder holder;
        MethodBinding originalMethod = this.original();
        if ((originalMethod.tagBits & 0x800000000000000L) == 0L) {
            if (originalMethod.declaringClass instanceof SourceTypeBinding) {
                AbstractMethodDeclaration methodDeclaration;
                SourceTypeBinding sourceType = (SourceTypeBinding)originalMethod.declaringClass;
                if (sourceType.scope != null && (methodDeclaration = originalMethod.sourceMethod()) != null && methodDeclaration.isAnnotationMethod()) {
                    methodDeclaration.resolve(sourceType.scope);
                }
            }
            originalMethod.tagBits |= 0x800000000000000L;
        }
        return (holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true)) == null ? null : holder.getDefaultValue();
    }

    public AnnotationBinding[][] getParameterAnnotations() {
        AnnotationBinding[][] allParameterAnnotations;
        int length = this.parameters.length;
        if (length == 0) {
            return null;
        }
        MethodBinding originalMethod = this.original();
        AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
        AnnotationBinding[][] annotationBindingArray = allParameterAnnotations = holder == null ? null : holder.getParameterAnnotations();
        if (allParameterAnnotations == null && (this.tagBits & 0x400L) != 0L) {
            allParameterAnnotations = new AnnotationBinding[length][];
            if (this.declaringClass instanceof SourceTypeBinding) {
                SourceTypeBinding sourceType = (SourceTypeBinding)this.declaringClass;
                if (sourceType.scope != null) {
                    AbstractMethodDeclaration methodDecl = sourceType.scope.referenceType().declarationOf(originalMethod);
                    int i = 0;
                    while (i < length) {
                        Argument argument = methodDecl.arguments[i];
                        if (argument.annotations != null) {
                            ASTNode.resolveAnnotations((BlockScope)methodDecl.scope, argument.annotations, argument.binding);
                            allParameterAnnotations[i] = argument.binding.getAnnotations();
                        } else {
                            allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
                        }
                        ++i;
                    }
                } else {
                    int i = 0;
                    while (i < length) {
                        allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
                        ++i;
                    }
                }
            } else {
                int i = 0;
                while (i < length) {
                    allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
                    ++i;
                }
            }
            this.setParameterAnnotations(allParameterAnnotations);
        }
        return allParameterAnnotations;
    }

    public TypeVariableBinding getTypeVariable(char[] variableName) {
        int i = this.typeVariables.length;
        while (--i >= 0) {
            if (!CharOperation.equals(this.typeVariables[i].sourceName, variableName)) continue;
            return this.typeVariables[i];
        }
        return null;
    }

    public TypeVariableBinding[] getAllTypeVariables(boolean isDiamond) {
        TypeVariableBinding[] allTypeVariables = this.typeVariables;
        if (isDiamond) {
            TypeVariableBinding[] classTypeVariables = this.declaringClass.typeVariables();
            int l1 = allTypeVariables.length;
            int l2 = classTypeVariables.length;
            if (l1 == 0) {
                allTypeVariables = classTypeVariables;
            } else if (l2 != 0) {
                TypeVariableBinding[] typeVariableBindingArray = allTypeVariables;
                allTypeVariables = new TypeVariableBinding[l1 + l2];
                System.arraycopy(typeVariableBindingArray, 0, allTypeVariables, 0, l1);
                System.arraycopy(classTypeVariables, 0, allTypeVariables, l1, l2);
            }
        }
        return allTypeVariables;
    }

    public boolean hasSubstitutedParameters() {
        return false;
    }

    public boolean hasSubstitutedReturnType() {
        return false;
    }

    public final boolean isAbstract() {
        return (this.modifiers & 0x400) != 0;
    }

    public final boolean isBridge() {
        return (this.modifiers & 0x40) != 0;
    }

    public final boolean isConstructor() {
        return this.selector == TypeConstants.INIT;
    }

    public final boolean isCompactConstructor() {
        return (this.modifiers & 0x800000) != 0;
    }

    public final boolean isDefault() {
        return !this.isPublic() && !this.isProtected() && !this.isPrivate();
    }

    public final boolean isDefaultAbstract() {
        return (this.modifiers & 0x80000) != 0;
    }

    public boolean isDefaultMethod() {
        return (this.modifiers & 0x10000) != 0;
    }

    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0;
    }

    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public final boolean isImplementing() {
        return (this.modifiers & 0x20000000) != 0;
    }

    public final boolean isMain() {
        TypeBinding paramType;
        return this.selector.length == 4 && CharOperation.equals(this.selector, TypeConstants.MAIN) && (this.modifiers & 9) != 0 && TypeBinding.VOID == this.returnType && this.parameters.length == 1 && (paramType = this.parameters[0]).dimensions() == 1 && paramType.leafComponentType().id == 11;
    }

    public final boolean isNative() {
        return (this.modifiers & 0x100) != 0;
    }

    public final boolean isOverriding() {
        return (this.modifiers & 0x10000000) != 0;
    }

    public final boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public final boolean isOrEnclosedByPrivateType() {
        if ((this.modifiers & 2) != 0) {
            return true;
        }
        return this.declaringClass != null && this.declaringClass.isOrEnclosedByPrivateType();
    }

    public final boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public final boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public final boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public final boolean isStrictfp() {
        return (this.modifiers & 0x800) != 0;
    }

    public final boolean isSynchronized() {
        return (this.modifiers & 0x20) != 0;
    }

    public final boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0;
    }

    public boolean isVarargs() {
        return (this.modifiers & 0x80) != 0;
    }

    public boolean isParameterizedGeneric() {
        return false;
    }

    public boolean isPolymorphic() {
        return false;
    }

    public final boolean isViewedAsDeprecated() {
        return (this.modifiers & 0x300000) != 0;
    }

    @Override
    public final int kind() {
        return 8;
    }

    public MethodBinding original() {
        return this;
    }

    public MethodBinding shallowOriginal() {
        return this.original();
    }

    public MethodBinding genericMethod() {
        return this;
    }

    @Override
    public char[] readableName() {
        StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        if (this.isConstructor()) {
            buffer.append(this.declaringClass.sourceName());
        } else {
            buffer.append(this.selector);
        }
        buffer.append('(');
        if (this.parameters != Binding.NO_PARAMETERS) {
            int i = 0;
            int length = this.parameters.length;
            while (i < length) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.parameters[i].sourceName());
                ++i;
            }
        }
        buffer.append(')');
        return buffer.toString().toCharArray();
    }

    public final AnnotationBinding[] getTypeAnnotations() {
        return this.typeAnnotations;
    }

    public void setTypeAnnotations(AnnotationBinding[] annotations) {
        this.typeAnnotations = annotations;
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, boolean forceStore) {
        this.declaringClass.storeAnnotations(this, annotations, forceStore);
    }

    public void setAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
        this.declaringClass.storeAnnotationHolder(this, AnnotationHolder.storeAnnotations(annotations, parameterAnnotations, defaultValue, optionalEnv));
    }

    public void setDefaultValue(Object defaultValue) {
        MethodBinding originalMethod = this.original();
        originalMethod.tagBits |= 0x800000000000000L;
        AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
        if (holder == null) {
            this.setAnnotations(null, null, defaultValue, null);
        } else {
            this.setAnnotations(holder.getAnnotations(), holder.getParameterAnnotations(), defaultValue, null);
        }
    }

    public void setParameterAnnotations(AnnotationBinding[][] parameterAnnotations) {
        AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
        if (holder == null) {
            this.setAnnotations(null, parameterAnnotations, null, null);
        } else {
            this.setAnnotations(holder.getAnnotations(), parameterAnnotations, holder.getDefaultValue(), null);
        }
    }

    protected final void setSelector(char[] selector) {
        this.selector = selector;
        this.signature = null;
    }

    @Override
    public char[] shortReadableName() {
        StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        if (this.isConstructor()) {
            buffer.append(this.declaringClass.shortReadableName());
        } else {
            buffer.append(this.selector);
        }
        buffer.append('(');
        if (this.parameters != Binding.NO_PARAMETERS) {
            int i = 0;
            int length = this.parameters.length;
            while (i < length) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.parameters[i].shortReadableName());
                ++i;
            }
        }
        buffer.append(')');
        int nameLength = buffer.length();
        char[] shortReadableName = new char[nameLength];
        buffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    public final char[] signature() {
        boolean needSynthetics;
        if (this.signature != null) {
            return this.signature;
        }
        StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        buffer.append('(');
        TypeBinding[] targetParameters = this.parameters;
        boolean isConstructor = this.isConstructor();
        if (isConstructor && this.declaringClass.isEnum()) {
            buffer.append(ConstantPool.JavaLangStringSignature);
            buffer.append(TypeBinding.INT.signature());
        }
        boolean bl = needSynthetics = isConstructor && this.declaringClass.isNestedType();
        if (needSynthetics) {
            ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                int i = 0;
                int count = syntheticArgumentTypes.length;
                while (i < count) {
                    buffer.append(syntheticArgumentTypes[i].signature());
                    ++i;
                }
            }
            if (this instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            int i = 0;
            while (i < targetParameters.length) {
                buffer.append(targetParameters[i].signature());
                ++i;
            }
        }
        if (needSynthetics) {
            SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
            int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
            int i = 0;
            while (i < count) {
                buffer.append(syntheticOuterArguments[i].type.signature());
                ++i;
            }
            i = targetParameters.length;
            int extraLength = this.parameters.length;
            while (i < extraLength) {
                buffer.append(this.parameters[i].signature());
                ++i;
            }
        }
        buffer.append(')');
        if (this.returnType != null) {
            buffer.append(this.returnType.signature());
        }
        int nameLength = buffer.length();
        this.signature = new char[nameLength];
        buffer.getChars(0, nameLength, this.signature, 0);
        return this.signature;
    }

    public char[] signature(ClassFile classFile) {
        boolean needSynthetics;
        if (this.signature != null) {
            if ((this.tagBits & 0x800L) != 0L) {
                boolean needSynthetics2;
                boolean isConstructor = this.isConstructor();
                TypeBinding[] targetParameters = this.parameters;
                boolean bl = needSynthetics2 = isConstructor && this.declaringClass.isNestedType() && !this.declaringClass.isStatic();
                if (needSynthetics2) {
                    ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
                    if (syntheticArgumentTypes != null) {
                        int i = 0;
                        int count = syntheticArgumentTypes.length;
                        while (i < count) {
                            ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
                            if ((syntheticArgumentType.tagBits & 0x800L) != 0L) {
                                Util.recordNestedType(classFile, syntheticArgumentType);
                            }
                            ++i;
                        }
                    }
                    if (this instanceof SyntheticMethodBinding) {
                        targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
                    }
                }
                if (targetParameters != Binding.NO_PARAMETERS) {
                    int i = 0;
                    int max = targetParameters.length;
                    while (i < max) {
                        TypeBinding targetParameter = targetParameters[i];
                        TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
                        if ((leafTargetParameterType.tagBits & 0x800L) != 0L) {
                            Util.recordNestedType(classFile, leafTargetParameterType);
                        }
                        ++i;
                    }
                }
                if (needSynthetics2) {
                    int i = targetParameters.length;
                    int extraLength = this.parameters.length;
                    while (i < extraLength) {
                        TypeBinding parameter = this.parameters[i];
                        TypeBinding leafParameterType = parameter.leafComponentType();
                        if ((leafParameterType.tagBits & 0x800L) != 0L) {
                            Util.recordNestedType(classFile, leafParameterType);
                        }
                        ++i;
                    }
                }
                if (this.returnType != null) {
                    TypeBinding ret = this.returnType.leafComponentType();
                    if ((ret.tagBits & 0x800L) != 0L) {
                        Util.recordNestedType(classFile, ret);
                    }
                }
            }
            return this.signature;
        }
        StringBuffer buffer = new StringBuffer((this.parameters.length + 1) * 20);
        buffer.append('(');
        TypeBinding[] targetParameters = this.parameters;
        boolean isConstructor = this.isConstructor();
        if (isConstructor && this.declaringClass.isEnum()) {
            buffer.append(ConstantPool.JavaLangStringSignature);
            buffer.append(TypeBinding.INT.signature());
        }
        boolean bl = needSynthetics = isConstructor && this.declaringClass.isNestedType() && !this.declaringClass.isStatic();
        if (needSynthetics) {
            ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                int i = 0;
                int count = syntheticArgumentTypes.length;
                while (i < count) {
                    ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
                    if ((syntheticArgumentType.tagBits & 0x800L) != 0L) {
                        this.tagBits |= 0x800L;
                        Util.recordNestedType(classFile, syntheticArgumentType);
                    }
                    buffer.append(syntheticArgumentType.signature());
                    ++i;
                }
            }
            if (this instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            int i = 0;
            int max = targetParameters.length;
            while (i < max) {
                TypeBinding targetParameter = targetParameters[i];
                TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
                if ((leafTargetParameterType.tagBits & 0x800L) != 0L) {
                    this.tagBits |= 0x800L;
                    Util.recordNestedType(classFile, leafTargetParameterType);
                }
                buffer.append(targetParameter.signature());
                ++i;
            }
        }
        if (needSynthetics) {
            SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
            int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
            int i = 0;
            while (i < count) {
                buffer.append(syntheticOuterArguments[i].type.signature());
                ++i;
            }
            i = targetParameters.length;
            int extraLength = this.parameters.length;
            while (i < extraLength) {
                TypeBinding parameter = this.parameters[i];
                TypeBinding leafParameterType = parameter.leafComponentType();
                if ((leafParameterType.tagBits & 0x800L) != 0L) {
                    this.tagBits |= 0x800L;
                    Util.recordNestedType(classFile, leafParameterType);
                }
                buffer.append(parameter.signature());
                ++i;
            }
        }
        buffer.append(')');
        if (this.returnType != null) {
            TypeBinding ret = this.returnType.leafComponentType();
            if ((ret.tagBits & 0x800L) != 0L) {
                this.tagBits |= 0x800L;
                Util.recordNestedType(classFile, ret);
            }
            buffer.append(this.returnType.signature());
        }
        int nameLength = buffer.length();
        this.signature = new char[nameLength];
        buffer.getChars(0, nameLength, this.signature, 0);
        return this.signature;
    }

    public final int sourceEnd() {
        AbstractMethodDeclaration method = this.sourceMethod();
        if (method == null) {
            if (this.declaringClass instanceof SourceTypeBinding) {
                return ((SourceTypeBinding)this.declaringClass).sourceEnd();
            }
            return 0;
        }
        return method.sourceEnd;
    }

    public AbstractMethodDeclaration sourceMethod() {
        AbstractMethodDeclaration[] methods;
        SourceTypeBinding sourceType;
        if (this.isSynthetic()) {
            return null;
        }
        try {
            sourceType = (SourceTypeBinding)this.declaringClass;
        }
        catch (ClassCastException classCastException) {
            return null;
        }
        AbstractMethodDeclaration[] abstractMethodDeclarationArray = methods = sourceType.scope != null ? sourceType.scope.referenceContext.methods : null;
        if (methods != null) {
            int i = methods.length;
            while (--i >= 0) {
                if (this != methods[i].binding) continue;
                return methods[i];
            }
        }
        return null;
    }

    public LambdaExpression sourceLambda() {
        return null;
    }

    public RecordComponent sourceRecordComponent() {
        return null;
    }

    public final int sourceStart() {
        AbstractMethodDeclaration method = this.sourceMethod();
        if (method == null) {
            if (this.declaringClass instanceof SourceTypeBinding) {
                return ((SourceTypeBinding)this.declaringClass).sourceStart();
            }
            return 0;
        }
        return method.sourceStart;
    }

    public MethodBinding tiebreakMethod() {
        return this;
    }

    public String toString() {
        int length;
        int i;
        StringBuffer output = new StringBuffer(10);
        if ((this.modifiers & 0x2000000) != 0) {
            output.append("[unresolved] ");
        }
        ASTNode.printModifiers(this.modifiers, output);
        output.append(this.returnType != null ? this.returnType.debugName() : "<no type>");
        output.append(" ");
        output.append(this.selector != null ? new String(this.selector) : "<no selector>");
        output.append("(");
        if (this.parameters != null) {
            if (this.parameters != Binding.NO_PARAMETERS) {
                i = 0;
                length = this.parameters.length;
                while (i < length) {
                    if (i > 0) {
                        output.append(", ");
                    }
                    output.append(this.parameters[i] != null ? this.parameters[i].debugName() : "<no argument type>");
                    ++i;
                }
            }
        } else {
            output.append("<no argument types>");
        }
        output.append(") ");
        if (this.thrownExceptions != null) {
            if (this.thrownExceptions != Binding.NO_EXCEPTIONS) {
                output.append("throws ");
                i = 0;
                length = this.thrownExceptions.length;
                while (i < length) {
                    if (i > 0) {
                        output.append(", ");
                    }
                    output.append(this.thrownExceptions[i] != null ? this.thrownExceptions[i].debugName() : "<no exception type>");
                    ++i;
                }
            }
        } else {
            output.append("<no exception types>");
        }
        return output.toString();
    }

    public TypeVariableBinding[] typeVariables() {
        return this.typeVariables;
    }

    public boolean hasNonNullDefaultForReturnType(AbstractMethodDeclaration srcMethod) {
        return this.hasNonNullDefaultFor(16, srcMethod, srcMethod == null ? -1 : srcMethod.declarationSourceStart);
    }

    static int getNonNullByDefaultValue(AnnotationBinding annotation) {
        ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();
        if (elementValuePairs == null || elementValuePairs.length == 0) {
            ReferenceBinding annotationType = annotation.getAnnotationType();
            if (annotationType == null) {
                return 0;
            }
            MethodBinding[] annotationMethods = annotationType.methods();
            if (annotationMethods != null && annotationMethods.length == 1) {
                Object value = annotationMethods[0].getDefaultValue();
                return Annotation.nullLocationBitsFromAnnotationValue(value);
            }
            return 56;
        }
        if (elementValuePairs.length > 0) {
            int nullness = 0;
            int i = 0;
            while (i < elementValuePairs.length) {
                nullness |= Annotation.nullLocationBitsFromAnnotationValue(elementValuePairs[i].getValue());
                ++i;
            }
            return nullness;
        }
        return 2;
    }

    public ParameterNonNullDefaultProvider hasNonNullDefaultForParameter(AbstractMethodDeclaration srcMethod) {
        int len = this.parameters.length;
        boolean[] result = new boolean[len];
        boolean trueFound = false;
        boolean falseFound = false;
        int i = 0;
        while (i < len) {
            boolean b;
            AnnotationBinding[][] parameterAnnotations;
            int nonNullByDefaultValue;
            int start = srcMethod == null || srcMethod.arguments == null || srcMethod.arguments.length == 0 ? -1 : srcMethod.arguments[i].declarationSourceStart;
            int n = nonNullByDefaultValue = srcMethod != null && start >= 0 ? srcMethod.scope.localNonNullByDefaultValue(start) : 0;
            if (nonNullByDefaultValue == 0 && (parameterAnnotations = this.getParameterAnnotations()) != null) {
                AnnotationBinding[] annotationBindings;
                AnnotationBinding[] annotationBindingArray = annotationBindings = parameterAnnotations[i];
                int n2 = annotationBindings.length;
                int n3 = 0;
                while (n3 < n2) {
                    AnnotationBinding annotationBinding = annotationBindingArray[n3];
                    ReferenceBinding annotationType = annotationBinding.getAnnotationType();
                    if (annotationType.hasNullBit(128)) {
                        nonNullByDefaultValue |= MethodBinding.getNonNullByDefaultValue(annotationBinding);
                    }
                    ++n3;
                }
            }
            if (b = nonNullByDefaultValue != 0 ? (nonNullByDefaultValue & 8) != 0 : this.hasNonNullDefaultFor(8, srcMethod, start)) {
                trueFound = true;
            } else {
                falseFound = true;
            }
            result[i] = b;
            ++i;
        }
        if (trueFound && falseFound) {
            return new ParameterNonNullDefaultProvider.MixedProvider(result);
        }
        return trueFound ? ParameterNonNullDefaultProvider.TRUE_PROVIDER : ParameterNonNullDefaultProvider.FALSE_PROVIDER;
    }

    private boolean hasNonNullDefaultFor(int location, AbstractMethodDeclaration srcMethod, int start) {
        if ((this.modifiers & 0x4000000) != 0) {
            return false;
        }
        if (this.defaultNullness != 0) {
            return (this.defaultNullness & location) != 0;
        }
        return this.declaringClass.hasNonNullDefaultFor(location, start);
    }

    public boolean redeclaresPublicObjectMethod(Scope scope) {
        ReferenceBinding javaLangObject = scope.getJavaLangObject();
        MethodBinding[] methods = javaLangObject.getMethods(this.selector);
        int i = 0;
        int length = methods == null ? 0 : methods.length;
        while (i < length) {
            MethodBinding method = methods[i];
            if (method.isPublic() && !method.isStatic() && method.parameters.length == this.parameters.length && MethodVerifier.doesMethodOverride(this, method, scope.environment())) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public boolean isVoidMethod() {
        return this.returnType == TypeBinding.VOID;
    }

    public boolean doesParameterLengthMatch(int suggestedParameterLength) {
        int len = this.parameters.length;
        return len <= suggestedParameterLength || this.isVarargs() && len == suggestedParameterLength + 1;
    }

    public void updateTypeVariableBinding(TypeVariableBinding previousBinding, TypeVariableBinding updatedBinding) {
        TypeVariableBinding[] bindings = this.typeVariables;
        if (bindings != null) {
            int i = 0;
            while (i < bindings.length) {
                if (bindings[i] == previousBinding) {
                    bindings[i] = updatedBinding;
                }
                ++i;
            }
        }
    }
}

