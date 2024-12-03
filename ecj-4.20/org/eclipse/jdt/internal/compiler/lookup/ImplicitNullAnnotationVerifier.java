/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ParameterNonNullDefaultProvider;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ImplicitNullAnnotationVerifier {
    ImplicitNullAnnotationVerifier buddyImplicitNullAnnotationsVerifier;
    private boolean inheritNullAnnotations;
    protected LookupEnvironment environment;

    public static void ensureNullnessIsKnown(MethodBinding methodBinding, Scope scope) {
        if ((methodBinding.tagBits & 0x1000L) == 0L) {
            LookupEnvironment environment2 = scope.environment();
            new ImplicitNullAnnotationVerifier(environment2, environment2.globalOptions.inheritNullAnnotations).checkImplicitNullAnnotations(methodBinding, null, false, scope);
        }
    }

    public ImplicitNullAnnotationVerifier(LookupEnvironment environment, boolean inheritNullAnnotations) {
        this.buddyImplicitNullAnnotationsVerifier = this;
        this.inheritNullAnnotations = inheritNullAnnotations;
        this.environment = environment;
    }

    ImplicitNullAnnotationVerifier(LookupEnvironment environment) {
        CompilerOptions options = environment.globalOptions;
        this.buddyImplicitNullAnnotationsVerifier = new ImplicitNullAnnotationVerifier(environment, options.inheritNullAnnotations);
        this.inheritNullAnnotations = options.inheritNullAnnotations;
        this.environment = environment;
    }

    public void checkImplicitNullAnnotations(MethodBinding currentMethod, AbstractMethodDeclaration srcMethod, boolean complain, Scope scope) {
        try {
            ReferenceBinding currentType = currentMethod.declaringClass;
            if (currentType.id == 1) {
                return;
            }
            boolean usesTypeAnnotations = scope.environment().usesNullTypeAnnotations();
            boolean needToApplyReturnNonNullDefault = currentMethod.hasNonNullDefaultForReturnType(srcMethod);
            ParameterNonNullDefaultProvider needToApplyParameterNonNullDefault = currentMethod.hasNonNullDefaultForParameter(srcMethod);
            boolean needToApplyNonNullDefault = needToApplyReturnNonNullDefault | needToApplyParameterNonNullDefault.hasAnyNonNullDefault();
            boolean isInstanceMethod = !currentMethod.isConstructor() && !currentMethod.isStatic();
            if (!(needToApplyNonNullDefault || (complain &= isInstanceMethod) || this.inheritNullAnnotations && isInstanceMethod)) {
                return;
            }
            if (isInstanceMethod) {
                int length;
                ArrayList superMethodList = new ArrayList();
                if (currentType instanceof SourceTypeBinding && !currentType.isHierarchyConnected() && !currentType.isAnonymousType()) {
                    ((SourceTypeBinding)currentType).scope.connectTypeHierarchy();
                }
                int paramLen = currentMethod.parameters.length;
                this.findAllOverriddenMethods(currentMethod.original(), currentMethod.selector, paramLen, currentType, new HashSet(), superMethodList);
                InheritedNonNullnessInfo[] inheritedNonNullnessInfos = new InheritedNonNullnessInfo[paramLen + 1];
                int i = 0;
                while (i < paramLen + 1) {
                    inheritedNonNullnessInfos[i] = new InheritedNonNullnessInfo();
                    ++i;
                }
                int i2 = length = superMethodList.size();
                while (--i2 >= 0) {
                    MethodBinding currentSuper = (MethodBinding)superMethodList.get(i2);
                    if ((currentSuper.tagBits & 0x1000L) == 0L) {
                        this.checkImplicitNullAnnotations(currentSuper, null, false, scope);
                    }
                    this.checkNullSpecInheritance(currentMethod, srcMethod, needToApplyReturnNonNullDefault, needToApplyParameterNonNullDefault, complain, currentSuper, null, scope, inheritedNonNullnessInfos);
                    needToApplyNonNullDefault = false;
                }
                InheritedNonNullnessInfo info = inheritedNonNullnessInfos[0];
                if (!info.complained) {
                    long tagBits = 0L;
                    if (info.inheritedNonNullness == Boolean.TRUE) {
                        tagBits = 0x100000000000000L;
                    } else if (info.inheritedNonNullness == Boolean.FALSE) {
                        tagBits = 0x80000000000000L;
                    }
                    if (tagBits != 0L) {
                        if (!usesTypeAnnotations) {
                            currentMethod.tagBits |= tagBits;
                        } else if (!currentMethod.returnType.isBaseType()) {
                            LookupEnvironment env = scope.environment();
                            currentMethod.returnType = env.createAnnotatedType(currentMethod.returnType, env.nullAnnotationsFromTagBits(tagBits));
                        }
                    }
                }
                int i3 = 0;
                while (i3 < paramLen) {
                    info = inheritedNonNullnessInfos[i3 + 1];
                    if (!info.complained && info.inheritedNonNullness != null) {
                        Argument currentArg;
                        Argument argument = currentArg = srcMethod == null ? null : srcMethod.arguments[i3];
                        if (!usesTypeAnnotations) {
                            this.recordArgNonNullness(currentMethod, paramLen, i3, currentArg, info.inheritedNonNullness);
                        } else {
                            this.recordArgNonNullness18(currentMethod, i3, currentArg, info.inheritedNonNullness, scope.environment());
                        }
                    }
                    ++i3;
                }
            }
            if (needToApplyNonNullDefault) {
                if (!usesTypeAnnotations) {
                    currentMethod.fillInDefaultNonNullness(srcMethod, needToApplyReturnNonNullDefault, needToApplyParameterNonNullDefault);
                } else {
                    currentMethod.fillInDefaultNonNullness18(srcMethod, scope.environment());
                }
            }
        }
        finally {
            currentMethod.tagBits |= 0x1000L;
        }
    }

    private void findAllOverriddenMethods(MethodBinding original, char[] selector, int suggestedParameterLength, ReferenceBinding currentType, Set ifcsSeen, List result) {
        if (currentType.id == 1) {
            return;
        }
        ReferenceBinding superclass = currentType.superclass();
        if (superclass == null) {
            return;
        }
        this.collectOverriddenMethods(original, selector, suggestedParameterLength, superclass, ifcsSeen, result);
        ReferenceBinding[] superInterfaces = currentType.superInterfaces();
        int ifcLen = superInterfaces.length;
        int i = 0;
        while (i < ifcLen) {
            ReferenceBinding currentIfc = superInterfaces[i];
            if (ifcsSeen.add(currentIfc.original())) {
                this.collectOverriddenMethods(original, selector, suggestedParameterLength, currentIfc, ifcsSeen, result);
            }
            ++i;
        }
    }

    private void collectOverriddenMethods(MethodBinding original, char[] selector, int suggestedParameterLength, ReferenceBinding superType, Set ifcsSeen, List result) {
        MethodBinding[] ifcMethods = superType.unResolvedMethods();
        int length = ifcMethods.length;
        boolean added = false;
        int i = 0;
        while (i < length) {
            MethodBinding currentMethod = ifcMethods[i];
            if (CharOperation.equals(selector, currentMethod.selector) && currentMethod.doesParameterLengthMatch(suggestedParameterLength) && !currentMethod.isStatic() && MethodVerifier.doesMethodOverride(original, currentMethod, this.environment)) {
                result.add(currentMethod);
                added = true;
            }
            ++i;
        }
        if (!added) {
            this.findAllOverriddenMethods(original, selector, suggestedParameterLength, superType, ifcsSeen, result);
        }
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    void checkNullSpecInheritance(MethodBinding currentMethod, AbstractMethodDeclaration srcMethod, boolean hasReturnNonNullDefault, ParameterNonNullDefaultProvider hasParameterNonNullDefault, boolean shouldComplain, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods, Scope scope, InheritedNonNullnessInfo[] inheritedNonNullnessInfos) {
        block53: {
            block54: {
                block55: {
                    if (currentMethod.declaringClass.id == 1) {
                        return;
                    }
                    if ((inheritedMethod.tagBits & 4096L) == 0L) {
                        this.buddyImplicitNullAnnotationsVerifier.checkImplicitNullAnnotations(inheritedMethod, null, false, scope);
                    }
                    useTypeAnnotations = this.environment.usesNullTypeAnnotations();
                    inheritedNullnessBits = this.getReturnTypeNullnessTagBits(inheritedMethod, useTypeAnnotations);
                    currentNullnessBits = this.getReturnTypeNullnessTagBits(currentMethod, useTypeAnnotations);
                    shouldInherit = this.inheritNullAnnotations;
                    if (currentMethod.returnType == null || currentMethod.returnType.isBaseType()) break block53;
                    if (currentNullnessBits != 0L) break block54;
                    if (!shouldInherit || inheritedNullnessBits == 0L) break block55;
                    if (hasReturnNonNullDefault && shouldComplain && inheritedNullnessBits == 0x80000000000000L) {
                        scope.problemReporter().conflictingNullAnnotations(currentMethod, ((MethodDeclaration)srcMethod).returnType, inheritedMethod);
                    }
                    if (inheritedNonNullnessInfos != null && srcMethod != null) {
                        this.recordDeferredInheritedNullness(scope, ((MethodDeclaration)srcMethod).returnType, inheritedMethod, inheritedNullnessBits == 0x100000000000000L, inheritedNonNullnessInfos[0]);
                    } else {
                        this.applyReturnNullBits(currentMethod, inheritedNullnessBits);
                    }
                    break block53;
                }
                if (hasReturnNonNullDefault && (!useTypeAnnotations || currentMethod.returnType.acceptsNonNullDefault())) {
                    currentNullnessBits = 0x100000000000000L;
                    this.applyReturnNullBits(currentMethod, currentNullnessBits);
                }
            }
            if (!shouldComplain) break block53;
            if ((inheritedNullnessBits & 0x100000000000000L) == 0L || currentNullnessBits == 0x100000000000000L) ** GOTO lbl31
            if (srcMethod != null) {
                scope.problemReporter().illegalReturnRedefinition(srcMethod, inheritedMethod, this.environment.getNonNullAnnotationName());
            } else {
                scope.problemReporter().cannotImplementIncompatibleNullness(scope.referenceContext(), currentMethod, inheritedMethod, useTypeAnnotations);
                return;
lbl31:
                // 1 sources

                if (useTypeAnnotations) {
                    substituteReturnType = null;
                    typeVariables = inheritedMethod.original().typeVariables;
                    if (typeVariables != null && currentMethod.returnType.id != 6) {
                        substitute = this.environment.createParameterizedGenericMethod(currentMethod, typeVariables);
                        substituteReturnType = substitute.returnType;
                    }
                    if (NullAnnotationMatching.analyse(inheritedMethod.returnType, currentMethod.returnType, substituteReturnType, null, 0, null, NullAnnotationMatching.CheckMode.OVERRIDE_RETURN).isAnyMismatch()) {
                        if (srcMethod != null) {
                            scope.problemReporter().illegalReturnRedefinition(srcMethod, inheritedMethod, this.environment.getNonNullAnnotationName());
                        } else {
                            scope.problemReporter().cannotImplementIncompatibleNullness(scope.referenceContext(), currentMethod, inheritedMethod, useTypeAnnotations);
                        }
                        return;
                    }
                }
            }
        }
        substituteParameters = null;
        if (shouldComplain && (typeVariables = currentMethod.original().typeVariables) != Binding.NO_TYPE_VARIABLES) {
            substitute = this.environment.createParameterizedGenericMethod(inheritedMethod, typeVariables);
            substituteParameters = substitute.parameters;
        }
        currentArguments = srcMethod == null ? null : srcMethod.arguments;
        length = 0;
        if (currentArguments != null) {
            length = currentArguments.length;
        }
        if (useTypeAnnotations) {
            length = currentMethod.parameters.length;
        } else if (inheritedMethod.parameterNonNullness != null) {
            length = inheritedMethod.parameterNonNullness.length;
        } else if (currentMethod.parameterNonNullness != null) {
            length = currentMethod.parameterNonNullness.length;
        }
        i = 0;
        while (i < length) {
            block56: {
                block59: {
                    block57: {
                        block58: {
                            if (currentMethod.parameters[i].isBaseType()) break block56;
                            currentArgument = currentArguments == null ? null : currentArguments[i];
                            inheritedNonNullNess = this.getParameterNonNullness(inheritedMethod, i, useTypeAnnotations);
                            currentNonNullNess = this.getParameterNonNullness(currentMethod, i, useTypeAnnotations);
                            if (currentNonNullNess != null) break block57;
                            if (inheritedNonNullNess == null || !shouldInherit) break block58;
                            if (hasParameterNonNullDefault.hasNonNullDefaultForParam(i) && shouldComplain && inheritedNonNullNess == Boolean.FALSE && currentArgument != null) {
                                scope.problemReporter().conflictingNullAnnotations(currentMethod, currentArgument, inheritedMethod);
                            }
                            if (inheritedNonNullnessInfos != null && srcMethod != null) {
                                this.recordDeferredInheritedNullness(scope, srcMethod.arguments[i].type, inheritedMethod, inheritedNonNullNess, inheritedNonNullnessInfos[i + 1]);
                            } else if (!useTypeAnnotations) {
                                this.recordArgNonNullness(currentMethod, length, i, currentArgument, inheritedNonNullNess);
                            } else {
                                this.recordArgNonNullness18(currentMethod, i, currentArgument, inheritedNonNullNess, this.environment);
                            }
                            break block56;
                        }
                        if (hasParameterNonNullDefault.hasNonNullDefaultForParam(i)) {
                            currentNonNullNess = Boolean.TRUE;
                            if (!useTypeAnnotations) {
                                this.recordArgNonNullness(currentMethod, length, i, currentArgument, Boolean.TRUE);
                            } else if (currentMethod.parameters[i].acceptsNonNullDefault()) {
                                this.recordArgNonNullness18(currentMethod, i, currentArgument, Boolean.TRUE, this.environment);
                            } else {
                                currentNonNullNess = null;
                            }
                        }
                    }
                    if (!shouldComplain) break block56;
                    annotationName = inheritedNonNullNess == Boolean.TRUE ? this.environment.getNonNullAnnotationName() : this.environment.getNullableAnnotationName();
                    if (inheritedNonNullNess == Boolean.TRUE || currentNonNullNess != Boolean.TRUE) break block59;
                    if (currentArgument != null) {
                        scope.problemReporter().illegalRedefinitionToNonNullParameter(currentArgument, inheritedMethod.declaringClass, inheritedNonNullNess == null ? null : this.environment.getNullableAnnotationName());
                    } else {
                        scope.problemReporter().cannotImplementIncompatibleNullness(scope.referenceContext(), currentMethod, inheritedMethod, false);
                    }
                    break block56;
                }
                if (currentNonNullNess != null) ** GOTO lbl-1000
                if (inheritedNonNullNess == Boolean.FALSE) {
                    if (currentArgument != null) {
                        scope.problemReporter().parameterLackingNullableAnnotation(currentArgument, inheritedMethod.declaringClass, annotationName);
                    } else {
                        scope.problemReporter().cannotImplementIncompatibleNullness(scope.referenceContext(), currentMethod, inheritedMethod, false);
                    }
                } else if (inheritedNonNullNess == Boolean.TRUE) {
                    if (allInheritedMethods != null) {
                        var27_30 = allInheritedMethods;
                        var26_29 = allInheritedMethods.length;
                        var25_26 = 0;
                        while (var25_26 < var26_29) {
                            one = var27_30[var25_26];
                            if (!TypeBinding.equalsEquals(inheritedMethod.declaringClass, one.declaringClass) || this.getParameterNonNullness(one, i, useTypeAnnotations) == Boolean.TRUE) {
                                ++var25_26;
                                continue;
                            }
                            break;
                        }
                    } else if (currentArgument != null) {
                        scope.problemReporter().parameterLackingNonnullAnnotation(currentArgument, inheritedMethod.declaringClass, annotationName);
                    } else {
                        type = scope.classScope().referenceContext;
                        location /* !! */  = type.superclass != null ? type.superclass : type;
                        scope.problemReporter().inheritedParameterLackingNonnullAnnotation(currentMethod, i + 1, inheritedMethod.declaringClass, location /* !! */ , annotationName);
                    }
                } else if (useTypeAnnotations) {
                    inheritedParameter = inheritedMethod.parameters[i];
                    v0 = substituteParameter = substituteParameters != null ? substituteParameters[i] : null;
                    if (NullAnnotationMatching.analyse(currentMethod.parameters[i], inheritedParameter, substituteParameter, null, 0, null, NullAnnotationMatching.CheckMode.OVERRIDE).isAnyMismatch()) {
                        if (currentArgument != null) {
                            scope.problemReporter().illegalParameterRedefinition(currentArgument, inheritedMethod.declaringClass, inheritedParameter);
                        } else {
                            scope.problemReporter().cannotImplementIncompatibleNullness(scope.referenceContext(), currentMethod, inheritedMethod, false);
                        }
                    }
                }
            }
            ++i;
        }
        if (shouldComplain && useTypeAnnotations && srcMethod != null) {
            currentTypeVariables = currentMethod.typeVariables();
            inheritedTypeVariables = inheritedMethod.typeVariables();
            if (currentTypeVariables != Binding.NO_TYPE_VARIABLES && currentTypeVariables.length == inheritedTypeVariables.length) {
                i = 0;
                while (i < currentTypeVariables.length) {
                    inheritedVariable = inheritedTypeVariables[i];
                    if (NullAnnotationMatching.analyse(inheritedVariable, currentTypeVariables[i], null, null, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK).isAnyMismatch()) {
                        scope.problemReporter().cannotRedefineTypeArgumentNullity(inheritedVariable, inheritedMethod, srcMethod.typeParameters()[i]);
                    }
                    ++i;
                }
            }
        }
    }

    void applyReturnNullBits(MethodBinding method, long nullnessBits) {
        if (this.environment.usesNullTypeAnnotations()) {
            if (!method.returnType.isBaseType()) {
                method.returnType = this.environment.createAnnotatedType(method.returnType, this.environment.nullAnnotationsFromTagBits(nullnessBits));
            }
        } else {
            method.tagBits |= nullnessBits;
        }
    }

    private Boolean getParameterNonNullness(MethodBinding method, int i, boolean useTypeAnnotations) {
        if (useTypeAnnotations) {
            long nullBits;
            TypeBinding parameter = method.parameters[i];
            if (parameter != null && (nullBits = NullAnnotationMatching.validNullTagBits(parameter.tagBits)) != 0L) {
                return nullBits == 0x100000000000000L;
            }
            return null;
        }
        return method.parameterNonNullness == null ? null : method.parameterNonNullness[i];
    }

    private long getReturnTypeNullnessTagBits(MethodBinding method, boolean useTypeAnnotations) {
        if (useTypeAnnotations) {
            if (method.returnType == null) {
                return 0L;
            }
            return NullAnnotationMatching.validNullTagBits(method.returnType.tagBits);
        }
        return method.tagBits & 0x180000000000000L;
    }

    protected void recordDeferredInheritedNullness(Scope scope, ASTNode location, MethodBinding inheritedMethod, Boolean inheritedNonNullness, InheritedNonNullnessInfo nullnessInfo) {
        if (nullnessInfo.inheritedNonNullness != null && nullnessInfo.inheritedNonNullness != inheritedNonNullness) {
            scope.problemReporter().conflictingInheritedNullAnnotations(location, nullnessInfo.inheritedNonNullness, nullnessInfo.annotationOrigin, inheritedNonNullness, inheritedMethod);
            nullnessInfo.complained = true;
        } else {
            nullnessInfo.inheritedNonNullness = inheritedNonNullness;
            nullnessInfo.annotationOrigin = inheritedMethod;
        }
    }

    void recordArgNonNullness(MethodBinding method, int paramCount, int paramIdx, Argument currentArgument, Boolean nonNullNess) {
        if (method.parameterNonNullness == null) {
            method.parameterNonNullness = new Boolean[paramCount];
        }
        method.parameterNonNullness[paramIdx] = nonNullNess;
        if (currentArgument != null) {
            currentArgument.binding.tagBits = currentArgument.binding.tagBits | (nonNullNess != false ? 0x100000000000000L : 0x80000000000000L);
        }
    }

    void recordArgNonNullness18(MethodBinding method, int paramIdx, Argument currentArgument, Boolean nonNullNess, LookupEnvironment env) {
        AnnotationBinding annotationBinding = nonNullNess != false ? env.getNonNullAnnotation() : env.getNullableAnnotation();
        method.parameters[paramIdx] = env.createAnnotatedType(method.parameters[paramIdx], new AnnotationBinding[]{annotationBinding});
        if (currentArgument != null) {
            currentArgument.binding.type = method.parameters[paramIdx];
        }
    }

    static boolean areParametersEqual(MethodBinding one, MethodBinding two) {
        TypeBinding[] oneArgs = one.parameters;
        TypeBinding[] twoArgs = two.parameters;
        if (oneArgs == twoArgs) {
            return true;
        }
        int length = oneArgs.length;
        if (length != twoArgs.length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (!ImplicitNullAnnotationVerifier.areTypesEqual(oneArgs[i], twoArgs[i])) {
                if (oneArgs[i].leafComponentType().isRawType() && oneArgs[i].dimensions() == twoArgs[i].dimensions() && oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())) {
                    if (one.typeVariables != Binding.NO_TYPE_VARIABLES) {
                        return false;
                    }
                    int j = 0;
                    while (j < i) {
                        if (oneArgs[j].leafComponentType().isParameterizedTypeWithActualArguments()) {
                            return false;
                        }
                        ++j;
                    }
                    break;
                }
                return false;
            }
            ++i;
        }
        ++i;
        while (i < length) {
            if (!ImplicitNullAnnotationVerifier.areTypesEqual(oneArgs[i], twoArgs[i])) {
                if (!oneArgs[i].leafComponentType().isRawType() || oneArgs[i].dimensions() != twoArgs[i].dimensions() || !oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())) {
                    return false;
                }
            } else if (oneArgs[i].leafComponentType().isParameterizedTypeWithActualArguments()) {
                return false;
            }
            ++i;
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    static boolean areTypesEqual(TypeBinding one, TypeBinding two) {
        if (TypeBinding.equalsEquals(one, two)) {
            return true;
        }
        switch (one.kind()) {
            case 4: {
                switch (two.kind()) {
                    case 260: 
                    case 1028: {
                        if (!TypeBinding.equalsEquals(one, two.erasure())) break;
                        return true;
                    }
                }
                break;
            }
            case 260: 
            case 1028: {
                switch (two.kind()) {
                    case 4: {
                        if (!TypeBinding.equalsEquals(one.erasure(), two)) break;
                        return true;
                    }
                }
                break;
            }
        }
        if (one.isParameterizedType() && two.isParameterizedType()) {
            return one.isEquivalentTo(two) && two.isEquivalentTo(one);
        }
        return false;
    }

    static class InheritedNonNullnessInfo {
        Boolean inheritedNonNullness;
        MethodBinding annotationOrigin;
        boolean complained;

        InheritedNonNullnessInfo() {
        }
    }
}

