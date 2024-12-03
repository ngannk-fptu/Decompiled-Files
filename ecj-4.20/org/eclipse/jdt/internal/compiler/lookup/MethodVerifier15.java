/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ParameterNonNullDefaultProvider;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.util.Sorting;

class MethodVerifier15
extends MethodVerifier {
    MethodVerifier15(LookupEnvironment environment) {
        super(environment);
    }

    @Override
    protected boolean canOverridingMethodDifferInErasure(MethodBinding overridingMethod, MethodBinding inheritedMethod) {
        if (overridingMethod.areParameterErasuresEqual(inheritedMethod)) {
            return false;
        }
        return !overridingMethod.declaringClass.isRawType();
    }

    @Override
    boolean canSkipInheritedMethods() {
        if (this.type.superclass() != null && (this.type.superclass().isAbstract() || this.type.superclass().isParameterizedType())) {
            return false;
        }
        return this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
    }

    @Override
    boolean canSkipInheritedMethods(MethodBinding one, MethodBinding two) {
        return two == null || TypeBinding.equalsEquals(one.declaringClass, two.declaringClass) && !one.declaringClass.isParameterizedType();
    }

    @Override
    void checkConcreteInheritedMethod(MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        super.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
        boolean analyseNullAnnotations = this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        AbstractMethodDeclaration srcMethod = null;
        if (analyseNullAnnotations && this.type.equals(concreteMethod.declaringClass)) {
            srcMethod = concreteMethod.sourceMethod();
        }
        boolean hasReturnNonNullDefault = analyseNullAnnotations && concreteMethod.hasNonNullDefaultForReturnType(srcMethod);
        ParameterNonNullDefaultProvider hasParameterNonNullDefault = analyseNullAnnotations ? concreteMethod.hasNonNullDefaultForParameter(srcMethod) : ParameterNonNullDefaultProvider.FALSE_PROVIDER;
        int i = 0;
        int l = abstractMethods.length;
        while (i < l) {
            MethodBinding abstractMethod = abstractMethods[i];
            if (concreteMethod.isVarargs() != abstractMethod.isVarargs()) {
                this.problemReporter().varargsConflict(concreteMethod, abstractMethod, this.type);
            }
            MethodBinding originalInherited = abstractMethod.original();
            if (TypeBinding.notEquals(originalInherited.returnType, concreteMethod.returnType) && !this.isAcceptableReturnTypeOverride(concreteMethod, abstractMethod)) {
                this.problemReporter().unsafeReturnTypeOverride(concreteMethod, originalInherited, this.type);
            }
            if (originalInherited.declaringClass.isInterface() && (TypeBinding.equalsEquals(concreteMethod.declaringClass, this.type.superclass) && this.type.superclass.isParameterizedType() && !this.areMethodsCompatible(concreteMethod, originalInherited) || this.type.superclass.erasure().findSuperTypeOriginatingFrom(originalInherited.declaringClass) == null)) {
                this.type.addSyntheticBridgeMethod(originalInherited, concreteMethod.original());
            }
            if (analyseNullAnnotations && !concreteMethod.isStatic() && !abstractMethod.isStatic()) {
                this.checkNullSpecInheritance(concreteMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, true, abstractMethod, abstractMethods, this.type.scope, null);
            }
            ++i;
        }
    }

    @Override
    void checkForBridgeMethod(MethodBinding currentMethod, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods) {
        SyntheticMethodBinding bridge;
        if (currentMethod.isVarargs() != inheritedMethod.isVarargs()) {
            this.problemReporter(currentMethod).varargsConflict(currentMethod, inheritedMethod, this.type);
        }
        MethodBinding originalInherited = inheritedMethod.original();
        if (TypeBinding.notEquals(originalInherited.returnType, currentMethod.returnType) && !this.isAcceptableReturnTypeOverride(currentMethod, inheritedMethod)) {
            this.problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, originalInherited, this.type);
        }
        if ((bridge = this.type.addSyntheticBridgeMethod(originalInherited, currentMethod.original())) != null) {
            int i = 0;
            int l = allInheritedMethods == null ? 0 : allInheritedMethods.length;
            while (i < l) {
                if (allInheritedMethods[i] != null && this.detectInheritedNameClash(originalInherited, allInheritedMethods[i].original())) {
                    return;
                }
                ++i;
            }
            MethodBinding[] current = (MethodBinding[])this.currentMethods.get(bridge.selector);
            int i2 = current.length - 1;
            while (i2 >= 0) {
                MethodBinding thisMethod = current[i2];
                if (thisMethod.areParameterErasuresEqual(bridge) && TypeBinding.equalsEquals(thisMethod.returnType.erasure(), bridge.returnType.erasure())) {
                    this.problemReporter(thisMethod).methodNameClash(thisMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : inheritedMethod.original(), 1);
                    return;
                }
                --i2;
            }
        }
    }

    void checkForNameClash(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        if (inheritedMethod.isStatic() || currentMethod.isStatic()) {
            MethodBinding original = inheritedMethod.original();
            if (this.type.scope.compilerOptions().complianceLevel >= 0x330000L && currentMethod.areParameterErasuresEqual(original)) {
                this.problemReporter(currentMethod).methodNameClashHidden(currentMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : original);
            }
            return;
        }
        if (!this.detectNameClash(currentMethod, inheritedMethod, false)) {
            TypeBinding[] currentParams = currentMethod.parameters;
            int length = currentParams.length;
            TypeBinding[] inheritedParams = inheritedMethod.parameters;
            if (length != inheritedParams.length) {
                return;
            }
            int i = 0;
            while (i < length) {
                if (TypeBinding.notEquals(currentParams[i], inheritedParams[i]) && (currentParams[i].isBaseType() != inheritedParams[i].isBaseType() || !inheritedParams[i].isCompatibleWith(currentParams[i]))) {
                    return;
                }
                ++i;
            }
            ReferenceBinding[] interfacesToVisit = null;
            int nextPosition = 0;
            ReferenceBinding superType = inheritedMethod.declaringClass;
            ReferenceBinding[] itsInterfaces = superType.superInterfaces();
            if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                nextPosition = itsInterfaces.length;
                interfacesToVisit = itsInterfaces;
            }
            superType = superType.superclass();
            while (superType != null && superType.isValidBinding()) {
                MethodBinding[] methods = superType.getMethods(currentMethod.selector);
                int m = 0;
                int n = methods.length;
                while (m < n) {
                    MethodBinding substitute = this.computeSubstituteMethod(methods[m], currentMethod);
                    if (substitute != null && !this.isSubstituteParameterSubsignature(currentMethod, substitute) && this.detectNameClash(currentMethod, substitute, true)) {
                        return;
                    }
                    ++m;
                }
                itsInterfaces = superType.superInterfaces();
                if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (interfacesToVisit == null) {
                        interfacesToVisit = itsInterfaces;
                        nextPosition = interfacesToVisit.length;
                    } else {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block26: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block26;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                superType = superType.superclass();
            }
            int i2 = 0;
            while (i2 < nextPosition) {
                superType = interfacesToVisit[i2];
                if (superType.isValidBinding()) {
                    MethodBinding[] methods = superType.getMethods(currentMethod.selector);
                    int m = 0;
                    int n = methods.length;
                    while (m < n) {
                        MethodBinding substitute = this.computeSubstituteMethod(methods[m], currentMethod);
                        if (substitute != null && !this.isSubstituteParameterSubsignature(currentMethod, substitute) && this.detectNameClash(currentMethod, substitute, true)) {
                            return;
                        }
                        ++m;
                    }
                    itsInterfaces = superType.superInterfaces();
                    if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block27: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block27;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                ++i2;
            }
        }
    }

    void checkInheritedMethods(MethodBinding inheritedMethod, MethodBinding otherInheritedMethod) {
        if (inheritedMethod.isStatic()) {
            return;
        }
        if (this.environment.globalOptions.complianceLevel < 0x330000L && inheritedMethod.declaringClass.isInterface()) {
            return;
        }
        this.detectInheritedNameClash(inheritedMethod.original(), otherInheritedMethod.original());
    }

    @Override
    void checkInheritedMethods(MethodBinding[] methods, int length, boolean[] isOverridden, boolean[] isInherited) {
        boolean continueInvestigation = true;
        MethodBinding concreteMethod = null;
        MethodBinding abstractSuperClassMethod = null;
        boolean playingTrump = false;
        int i = 0;
        while (i < length) {
            if (!methods[i].declaringClass.isInterface() && TypeBinding.notEquals(methods[i].declaringClass, this.type) && methods[i].isAbstract()) {
                abstractSuperClassMethod = methods[i];
                break;
            }
            ++i;
        }
        i = 0;
        while (i < length) {
            block12: {
                block14: {
                    block13: {
                        if (!isInherited[i] || methods[i].isAbstract()) break block12;
                        if (!methods[i].isDefaultMethod() || abstractSuperClassMethod == null || !MethodVerifier15.areParametersEqual(abstractSuperClassMethod, methods[i]) || concreteMethod != null) break block13;
                        playingTrump = true;
                        break block12;
                    }
                    playingTrump = false;
                    if (concreteMethod == null) break block14;
                    if (isOverridden[i] && this.areMethodsCompatible(concreteMethod, methods[i]) || TypeBinding.equalsEquals(concreteMethod.declaringClass, methods[i].declaringClass) && concreteMethod.typeVariables.length != methods[i].typeVariables.length && (concreteMethod.typeVariables == Binding.NO_TYPE_VARIABLES && concreteMethod.original() == methods[i] || methods[i].typeVariables == Binding.NO_TYPE_VARIABLES && methods[i].original() == concreteMethod)) break block12;
                    this.problemReporter().duplicateInheritedMethods(this.type, concreteMethod, methods[i], this.environment.globalOptions.sourceLevel >= 0x340000L);
                    continueInvestigation = false;
                }
                concreteMethod = methods[i];
            }
            ++i;
        }
        if (continueInvestigation) {
            if (playingTrump) {
                if (!this.type.isAbstract()) {
                    this.problemReporter().abstractMethodMustBeImplemented(this.type, abstractSuperClassMethod);
                    return;
                }
            } else if (concreteMethod != null && concreteMethod.isDefaultMethod() && this.environment.globalOptions.complianceLevel >= 0x340000L && !this.checkInheritedDefaultMethods(methods, isOverridden, length)) {
                return;
            }
            super.checkInheritedMethods(methods, length, isOverridden, isInherited);
        }
    }

    boolean checkInheritedDefaultMethods(MethodBinding[] methods, boolean[] isOverridden, int length) {
        if (length < 2) {
            return true;
        }
        boolean ok = true;
        int i = 0;
        while (i < length) {
            if (methods[i].isDefaultMethod() && !isOverridden[i]) {
                int j = 0;
                while (j < length) {
                    if (j != i && !isOverridden[j] && this.isMethodSubsignature(methods[i], methods[j]) && !this.doesMethodOverride(methods[i], methods[j]) && !this.doesMethodOverride(methods[j], methods[i])) {
                        this.problemReporter().inheritedDefaultMethodConflictsWithOtherInherited(this.type, methods[i], methods[j]);
                        ok = false;
                        break;
                    }
                    ++j;
                }
            }
            ++i;
        }
        return ok;
    }

    @Override
    boolean checkInheritedReturnTypes(MethodBinding method, MethodBinding otherMethod) {
        if (this.areReturnTypesCompatible(method, otherMethod)) {
            return true;
        }
        if (this.isUnsafeReturnTypeOverride(method, otherMethod)) {
            if (!method.declaringClass.implementsInterface(otherMethod.declaringClass, false)) {
                this.problemReporter(method).unsafeReturnTypeOverride(method, otherMethod, this.type);
            }
            return true;
        }
        return false;
    }

    @Override
    void checkAgainstInheritedMethods(MethodBinding currentMethod, MethodBinding[] methods, int length, MethodBinding[] allInheritedMethods) {
        super.checkAgainstInheritedMethods(currentMethod, methods, length, allInheritedMethods);
        CompilerOptions options = this.environment.globalOptions;
        if (options.isAnnotationBasedNullAnalysisEnabled && (currentMethod.tagBits & 0x1000L) == 0L) {
            AbstractMethodDeclaration srcMethod = null;
            if (this.type.equals(currentMethod.declaringClass)) {
                srcMethod = currentMethod.sourceMethod();
            }
            boolean hasReturnNonNullDefault = currentMethod.hasNonNullDefaultForReturnType(srcMethod);
            ParameterNonNullDefaultProvider hasParameterNonNullDefault = currentMethod.hasNonNullDefaultForParameter(srcMethod);
            int i = length;
            while (--i >= 0) {
                if (currentMethod.isStatic() || methods[i].isStatic()) continue;
                this.checkNullSpecInheritance(currentMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, true, methods[i], methods, this.type.scope, null);
            }
        }
    }

    @Override
    void checkNullSpecInheritance(MethodBinding currentMethod, AbstractMethodDeclaration srcMethod, boolean hasReturnNonNullDefault, ParameterNonNullDefaultProvider hasParameterNonNullDefault, boolean complain, MethodBinding inheritedMethod, MethodBinding[] allInherited, Scope scope, ImplicitNullAnnotationVerifier.InheritedNonNullnessInfo[] inheritedNonNullnessInfos) {
        if (!(hasReturnNonNullDefault || hasParameterNonNullDefault.hasAnyNonNullDefault() || (complain &= !currentMethod.isConstructor()) || this.environment.globalOptions.inheritNullAnnotations)) {
            currentMethod.tagBits |= 0x1000L;
            return;
        }
        if (TypeBinding.notEquals(currentMethod.declaringClass, this.type) && (currentMethod.tagBits & 0x1000L) == 0L) {
            this.buddyImplicitNullAnnotationsVerifier.checkImplicitNullAnnotations(currentMethod, srcMethod, complain, scope);
        }
        super.checkNullSpecInheritance(currentMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, complain, inheritedMethod, allInherited, scope, inheritedNonNullnessInfos);
    }

    void reportRawReferences() {
        CompilerOptions compilerOptions = this.type.scope.compilerOptions();
        if (compilerOptions.sourceLevel < 0x310000L || compilerOptions.reportUnavoidableGenericTypeProblems) {
            return;
        }
        Object[] methodArray = this.currentMethods.valueTable;
        int s = methodArray.length;
        while (--s >= 0) {
            if (methodArray[s] == null) continue;
            MethodBinding[] current = (MethodBinding[])methodArray[s];
            int i = 0;
            int length = current.length;
            while (i < length) {
                MethodBinding currentMethod = current[i];
                if ((currentMethod.modifiers & 0x30000000) == 0) {
                    AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
                    if (methodDecl == null) {
                        return;
                    }
                    TypeBinding[] parameterTypes = currentMethod.parameters;
                    Argument[] arguments = methodDecl.arguments;
                    int j = 0;
                    int size = currentMethod.parameters.length;
                    while (j < size) {
                        TypeBinding parameterType = parameterTypes[j];
                        Argument arg = arguments[j];
                        if (parameterType.leafComponentType().isRawType() && compilerOptions.getSeverity(0x20010000) != 256 && (arg.type.bits & 0x40000000) == 0) {
                            methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
                        }
                        ++j;
                    }
                    if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration) {
                        TypeReference returnType = ((MethodDeclaration)methodDecl).returnType;
                        TypeBinding methodType = currentMethod.returnType;
                        if (returnType != null && methodType.leafComponentType().isRawType() && compilerOptions.getSeverity(0x20010000) != 256 && (returnType.bits & 0x40000000) == 0) {
                            methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
                        }
                    }
                }
                ++i;
            }
        }
    }

    @Override
    public void reportRawReferences(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        CompilerOptions compilerOptions = this.type.scope.compilerOptions();
        if (compilerOptions.sourceLevel < 0x310000L || compilerOptions.reportUnavoidableGenericTypeProblems) {
            return;
        }
        AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
        if (methodDecl == null) {
            return;
        }
        TypeBinding[] parameterTypes = currentMethod.parameters;
        TypeBinding[] inheritedParameterTypes = inheritedMethod.parameters;
        Argument[] arguments = methodDecl.arguments;
        int j = 0;
        int size = currentMethod.parameters.length;
        while (j < size) {
            TypeBinding parameterType = parameterTypes[j];
            TypeBinding inheritedParameterType = inheritedParameterTypes[j];
            Argument arg = arguments[j];
            if (parameterType.leafComponentType().isRawType()) {
                if (inheritedParameterType.leafComponentType().isRawType()) {
                    arg.binding.tagBits |= 0x200L;
                } else if (compilerOptions.getSeverity(0x20010000) != 256 && (arg.type.bits & 0x40000000) == 0) {
                    methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
                }
            }
            ++j;
        }
        TypeReference returnType = null;
        if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration && (returnType = ((MethodDeclaration)methodDecl).returnType) != null) {
            TypeBinding inheritedMethodType = inheritedMethod.returnType;
            TypeBinding methodType = currentMethod.returnType;
            if (methodType.leafComponentType().isRawType() && !inheritedMethodType.leafComponentType().isRawType() && (returnType.bits & 0x40000000) == 0 && compilerOptions.getSeverity(0x20010000) != 256) {
                methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
            }
        }
    }

    @Override
    void checkMethods() {
        boolean mustImplementAbstractMethods = this.mustImplementAbstractMethods();
        boolean skipInheritedMethods = mustImplementAbstractMethods && this.canSkipInheritedMethods();
        boolean isOrEnclosedByPrivateType = this.type.isOrEnclosedByPrivateType();
        char[][] methodSelectors = this.inheritedMethods.keyTable;
        int s = methodSelectors.length;
        while (--s >= 0) {
            MethodBinding inheritedMethod;
            int i;
            int i2;
            int length;
            if (methodSelectors[s] == null) continue;
            MethodBinding[] current = (MethodBinding[])this.currentMethods.get(methodSelectors[s]);
            MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
            inherited = Sorting.concreteFirst(inherited, inherited.length);
            if (current == null && !isOrEnclosedByPrivateType) {
                length = inherited.length;
                i2 = 0;
                while (i2 < length) {
                    inherited[i2].original().modifiers |= 0x8000000;
                    ++i2;
                }
            }
            if (current == null && this.type.isPublic()) {
                length = inherited.length;
                i2 = 0;
                while (i2 < length) {
                    MethodBinding inheritedMethod2 = inherited[i2];
                    if (inheritedMethod2.isPublic() && !inheritedMethod2.declaringClass.isInterface() && !inheritedMethod2.declaringClass.isPublic()) {
                        this.type.addSyntheticBridgeMethod(inheritedMethod2.original());
                    }
                    ++i2;
                }
            }
            if (current == null && skipInheritedMethods) continue;
            if (inherited.length == 1 && current == null) {
                if (!mustImplementAbstractMethods || !inherited[0].isAbstract()) continue;
                this.checkAbstractMethod(inherited[0]);
                continue;
            }
            int index = -1;
            int inheritedLength = inherited.length;
            MethodBinding[] matchingInherited = new MethodBinding[inheritedLength];
            MethodBinding[] foundMatch = new MethodBinding[inheritedLength];
            boolean[] skip = new boolean[inheritedLength];
            boolean[] isOverridden = new boolean[inheritedLength];
            boolean[] isInherited = new boolean[inheritedLength];
            Arrays.fill(isInherited, true);
            if (current != null) {
                i = 0;
                int length1 = current.length;
                while (i < length1) {
                    MethodBinding currentMethod = current[i];
                    MethodBinding[] nonMatchingInherited = null;
                    int j = 0;
                    while (j < inheritedLength) {
                        MethodBinding inheritedMethod3 = this.computeSubstituteMethod(inherited[j], currentMethod);
                        if (inheritedMethod3 != null) {
                            if (foundMatch[j] == null && this.isSubstituteParameterSubsignature(currentMethod, inheritedMethod3)) {
                                isOverridden[j] = skip[j] = MethodVerifier15.couldMethodOverride(currentMethod, inheritedMethod3);
                                matchingInherited[++index] = inheritedMethod3;
                                foundMatch[j] = currentMethod;
                            } else {
                                this.checkForNameClash(currentMethod, inheritedMethod3);
                                if (inheritedLength > 1) {
                                    if (nonMatchingInherited == null) {
                                        nonMatchingInherited = new MethodBinding[inheritedLength];
                                    }
                                    nonMatchingInherited[j] = inheritedMethod3;
                                }
                            }
                        }
                        ++j;
                    }
                    if (index >= 0) {
                        this.checkAgainstInheritedMethods(currentMethod, matchingInherited, index + 1, nonMatchingInherited);
                        while (index >= 0) {
                            matchingInherited[index--] = null;
                        }
                    }
                    ++i;
                }
            }
            i = 0;
            while (i < inheritedLength) {
                MethodBinding matchMethod = foundMatch[i];
                if (matchMethod == null && current != null && this.type.isPublic() && (inheritedMethod = inherited[i]).isPublic() && !inheritedMethod.declaringClass.isInterface() && !inheritedMethod.declaringClass.isPublic()) {
                    this.type.addSyntheticBridgeMethod(inheritedMethod.original());
                }
                if (!isOrEnclosedByPrivateType && matchMethod == null && current != null) {
                    inherited[i].original().modifiers |= 0x8000000;
                }
                inheritedMethod = inherited[i];
                int j = i + 1;
                while (j < inheritedLength) {
                    MethodBinding otherInheritedMethod = inherited[j];
                    if (matchMethod == foundMatch[j] && matchMethod != null || this.canSkipInheritedMethods(inheritedMethod, otherInheritedMethod) || !TypeBinding.notEquals(inheritedMethod.declaringClass, otherInheritedMethod.declaringClass) || this.isSkippableOrOverridden(inheritedMethod, otherInheritedMethod, skip, isOverridden, isInherited, j) || this.isSkippableOrOverridden(otherInheritedMethod, inheritedMethod, skip, isOverridden, isInherited, i)) {
                        // empty if block
                    }
                    ++j;
                }
                ++i;
            }
            i = 0;
            while (i < inheritedLength) {
                MethodBinding matchMethod = foundMatch[i];
                if (!skip[i]) {
                    inheritedMethod = inherited[i];
                    if (matchMethod == null) {
                        matchingInherited[++index] = inheritedMethod;
                    }
                    int j = i + 1;
                    while (j < inheritedLength) {
                        if (foundMatch[j] == null) {
                            MethodBinding otherInheritedMethod = inherited[j];
                            if (!(matchMethod == foundMatch[j] && matchMethod != null || this.canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))) {
                                MethodBinding replaceMatch = this.findReplacedMethod(inheritedMethod, otherInheritedMethod);
                                if (replaceMatch != null) {
                                    matchingInherited[++index] = replaceMatch;
                                    skip[j] = true;
                                } else {
                                    replaceMatch = this.findReplacedMethod(otherInheritedMethod, inheritedMethod);
                                    if (replaceMatch != null) {
                                        matchingInherited[++index] = replaceMatch;
                                        skip[j] = true;
                                    } else if (matchMethod == null) {
                                        this.checkInheritedMethods(inheritedMethod, otherInheritedMethod);
                                    }
                                }
                            }
                        }
                        ++j;
                    }
                    if (index != -1) {
                        if (index > 0) {
                            boolean[] matchingIsInherited;
                            boolean[] matchingIsOverridden;
                            int length2 = index + 1;
                            if (length2 != inheritedLength) {
                                matchingIsOverridden = new boolean[length2];
                                matchingIsInherited = new boolean[length2];
                                int j2 = 0;
                                while (j2 < length2) {
                                    int k = 0;
                                    while (k < inheritedLength) {
                                        if (matchingInherited[j2] == inherited[k]) {
                                            matchingIsOverridden[j2] = isOverridden[k];
                                            matchingIsInherited[j2] = isInherited[k];
                                            break;
                                        }
                                        ++k;
                                    }
                                    ++j2;
                                }
                            } else {
                                matchingIsOverridden = isOverridden;
                                matchingIsInherited = isInherited;
                            }
                            this.checkInheritedMethods(matchingInherited, length2, matchingIsOverridden, matchingIsInherited);
                        } else if (mustImplementAbstractMethods && matchingInherited[0].isAbstract() && matchMethod == null) {
                            this.checkAbstractMethod(matchingInherited[0]);
                        }
                        while (index >= 0) {
                            matchingInherited[index--] = null;
                        }
                    }
                }
                ++i;
            }
        }
    }

    boolean isSkippableOrOverridden(MethodBinding specific, MethodBinding general, boolean[] skip, boolean[] isOverridden, boolean[] isInherited, int idx) {
        boolean specificIsInterface = specific.declaringClass.isInterface();
        boolean generalIsInterface = general.declaringClass.isInterface();
        if (!specificIsInterface && generalIsInterface) {
            if (!specific.isAbstract() && this.isParameterSubsignature(specific, general)) {
                isInherited[idx] = false;
                return true;
            }
            if (this.isInterfaceMethodImplemented(general, specific, general.declaringClass)) {
                skip[idx] = true;
                isOverridden[idx] = true;
                return true;
            }
        } else if (specificIsInterface == generalIsInterface && specific.declaringClass.isCompatibleWith(general.declaringClass) && this.isMethodSubsignature(specific, general)) {
            skip[idx] = true;
            isOverridden[idx] = true;
            return true;
        }
        return false;
    }

    MethodBinding findReplacedMethod(MethodBinding specific, MethodBinding general) {
        MethodBinding generalSubstitute = this.computeSubstituteMethod(general, specific);
        if (generalSubstitute != null && (!specific.isAbstract() || general.isAbstract() || general.isDefaultMethod() && specific.declaringClass.isClass()) && this.isSubstituteParameterSubsignature(specific, generalSubstitute)) {
            return generalSubstitute;
        }
        return null;
    }

    /*
     * Unable to fully structure code
     */
    void checkTypeVariableMethods(TypeParameter typeParameter) {
        methodSelectors = this.inheritedMethods.keyTable;
        s = methodSelectors.length;
        block0: while (--s >= 0) {
            if (methodSelectors[s] == null || (inherited = (MethodBinding[])this.inheritedMethods.valueTable[s]).length == 1) continue;
            index = -1;
            matchingInherited = new MethodBinding[inherited.length];
            i = 0;
            length = inherited.length;
            ** GOTO lbl49
            {
                matchingInherited[index--] = null;
                do {
                    if (index >= 0) continue block1;
                    inheritedMethod = inherited[i];
                    if (inheritedMethod != null) {
                        matchingInherited[++index] = inheritedMethod;
                        j = i + 1;
                        while (j < length) {
                            otherInheritedMethod = inherited[j];
                            if (!this.canSkipInheritedMethods(inheritedMethod, otherInheritedMethod) && (otherInheritedMethod = this.computeSubstituteMethod(otherInheritedMethod, inheritedMethod)) != null && this.isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod)) {
                                matchingInherited[++index] = otherInheritedMethod;
                                inherited[j] = null;
                            }
                            ++j;
                        }
                    }
                    if (index > 0) {
                        first = matchingInherited[0];
                        count = index + 1;
                        while (--count > 0) {
                            match = matchingInherited[count];
                            interfaceMethod = null;
                            implementation = null;
                            if (first.declaringClass.isInterface()) {
                                interfaceMethod = first;
                            } else if (first.declaringClass.isClass()) {
                                implementation = first;
                            }
                            if (match.declaringClass.isInterface()) {
                                interfaceMethod = match;
                            } else if (match.declaringClass.isClass()) {
                                implementation = match;
                            }
                            if (interfaceMethod != null && implementation != null && !implementation.isAbstract() && !this.isAsVisible(implementation, interfaceMethod)) {
                                this.problemReporter().inheritedMethodReducesVisibility(typeParameter, implementation, new MethodBinding[]{interfaceMethod});
                            }
                            if (!this.areReturnTypesCompatible(first, match) && (!first.declaringClass.isInterface() || !match.declaringClass.isInterface() || !this.areReturnTypesCompatible(match, first))) break;
                        }
                        if (count > 0) {
                            this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(typeParameter, matchingInherited, index + 1);
                            continue block0;
                        }
                    }
                    ++i;
lbl49:
                    // 2 sources

                } while (i < length);
            }
        }
    }

    boolean detectInheritedNameClash(MethodBinding inherited, MethodBinding otherInherited) {
        if (!inherited.areParameterErasuresEqual(otherInherited)) {
            return false;
        }
        if (TypeBinding.notEquals(inherited.returnType.erasure(), otherInherited.returnType.erasure())) {
            return false;
        }
        if (TypeBinding.notEquals(inherited.declaringClass.erasure(), otherInherited.declaringClass.erasure())) {
            if (inherited.declaringClass.findSuperTypeOriginatingFrom(otherInherited.declaringClass) != null) {
                return false;
            }
            if (otherInherited.declaringClass.findSuperTypeOriginatingFrom(inherited.declaringClass) != null) {
                return false;
            }
        }
        this.problemReporter().inheritedMethodsHaveNameClash(this.type, inherited, otherInherited);
        return true;
    }

    boolean detectNameClash(MethodBinding current, MethodBinding inherited, boolean treatAsSynthetic) {
        MethodBinding[] currentNamesakes;
        MethodBinding methodToCheck = inherited;
        MethodBinding original = methodToCheck.original();
        if (!current.areParameterErasuresEqual(original)) {
            return false;
        }
        int severity = 1;
        if (this.environment.globalOptions.complianceLevel == 0x320000L && TypeBinding.notEquals(current.returnType.erasure(), original.returnType.erasure())) {
            severity = 0;
        }
        if (!treatAsSynthetic && (currentNamesakes = (MethodBinding[])this.currentMethods.get(inherited.selector)).length > 1) {
            int i = 0;
            int length = currentNamesakes.length;
            while (i < length) {
                MethodBinding currentMethod = currentNamesakes[i];
                if (currentMethod != current && this.doesMethodOverride(currentMethod, inherited)) {
                    methodToCheck = currentMethod;
                    break;
                }
                ++i;
            }
        }
        if (!current.areParameterErasuresEqual(original = methodToCheck.original())) {
            return false;
        }
        original = inherited.original();
        this.problemReporter(current).methodNameClash(current, inherited.declaringClass.isRawType() ? inherited : original, severity);
        return severity != 0;
    }

    boolean doTypeVariablesClash(MethodBinding one, MethodBinding substituteTwo) {
        return one.typeVariables != Binding.NO_TYPE_VARIABLES && !(substituteTwo instanceof ParameterizedGenericMethodBinding);
    }

    @Override
    SimpleSet findSuperinterfaceCollisions(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding[] itsInterfaces = superInterfaces;
        if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
            nextPosition = itsInterfaces.length;
            interfacesToVisit = itsInterfaces;
        }
        boolean isInconsistent = this.type.isHierarchyInconsistent();
        ReferenceBinding superType = superclass;
        while (superType != null && superType.isValidBinding()) {
            isInconsistent |= superType.isHierarchyInconsistent();
            itsInterfaces = superType.superInterfaces();
            if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                } else {
                    int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                        interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                        System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                    }
                    int a = 0;
                    while (a < itsLength) {
                        block22: {
                            ReferenceBinding next = itsInterfaces[a];
                            int b = 0;
                            while (b < nextPosition) {
                                if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++b;
                                    continue;
                                }
                                break block22;
                            }
                            interfacesToVisit[nextPosition++] = next;
                        }
                        ++a;
                    }
                }
            }
            superType = superType.superclass();
        }
        int i = 0;
        while (i < nextPosition) {
            superType = interfacesToVisit[i];
            if (superType.isValidBinding()) {
                isInconsistent |= superType.isHierarchyInconsistent();
                itsInterfaces = superType.superInterfaces();
                if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                    int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                        interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                        System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                    }
                    int a = 0;
                    while (a < itsLength) {
                        block23: {
                            ReferenceBinding next = itsInterfaces[a];
                            int b = 0;
                            while (b < nextPosition) {
                                if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++b;
                                    continue;
                                }
                                break block23;
                            }
                            interfacesToVisit[nextPosition++] = next;
                        }
                        ++a;
                    }
                }
            }
            ++i;
        }
        if (!isInconsistent) {
            return null;
        }
        SimpleSet copy = null;
        int i2 = 0;
        while (i2 < nextPosition) {
            ReferenceBinding current = interfacesToVisit[i2];
            if (current.isValidBinding()) {
                TypeBinding erasure = current.erasure();
                int j = i2 + 1;
                while (j < nextPosition) {
                    ReferenceBinding next = interfacesToVisit[j];
                    if (next.isValidBinding() && TypeBinding.equalsEquals(next.erasure(), erasure)) {
                        if (copy == null) {
                            copy = new SimpleSet(nextPosition);
                        }
                        copy.add(interfacesToVisit[i2]);
                        copy.add(interfacesToVisit[j]);
                    }
                    ++j;
                }
            }
            ++i2;
        }
        return copy;
    }

    boolean isAcceptableReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        if (inheritedMethod.declaringClass.isRawType()) {
            return true;
        }
        MethodBinding originalInherited = inheritedMethod.original();
        TypeBinding originalInheritedReturnType = originalInherited.returnType.leafComponentType();
        if (originalInheritedReturnType.isParameterizedTypeWithActualArguments()) {
            return !currentMethod.returnType.leafComponentType().isRawType();
        }
        TypeBinding currentReturnType = currentMethod.returnType.leafComponentType();
        switch (currentReturnType.kind()) {
            case 4100: {
                if (!TypeBinding.equalsEquals(currentReturnType, inheritedMethod.returnType.leafComponentType())) break;
                return true;
            }
        }
        return !originalInheritedReturnType.isTypeVariable() || ((TypeVariableBinding)originalInheritedReturnType).declaringElement != originalInherited;
    }

    @Override
    boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, MethodBinding existingMethod, ReferenceBinding superType) {
        if (inheritedMethod.original() != inheritedMethod && existingMethod.declaringClass.isInterface()) {
            return false;
        }
        if ((inheritedMethod = this.computeSubstituteMethod(inheritedMethod, existingMethod)) == null || !this.doesMethodOverride(existingMethod, inheritedMethod)) {
            return false;
        }
        return TypeBinding.equalsEquals(inheritedMethod.returnType, existingMethod.returnType) || TypeBinding.notEquals(this.type, existingMethod.declaringClass) && !existingMethod.declaringClass.isInterface() && this.areReturnTypesCompatible(existingMethod, inheritedMethod);
    }

    @Override
    public boolean isMethodSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
        MethodBinding inheritedOriginal;
        if (!CharOperation.equals(method.selector, inheritedMethod.selector)) {
            return false;
        }
        if (method.declaringClass.isParameterizedType()) {
            method = method.original();
        }
        return this.isParameterSubsignature(method, (inheritedOriginal = method.findOriginalInheritedMethod(inheritedMethod)) == null ? inheritedMethod : inheritedOriginal);
    }

    boolean isUnsafeReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        if (TypeBinding.equalsEquals(currentMethod.returnType, inheritedMethod.returnType.erasure())) {
            TypeBinding[] currentParams = currentMethod.parameters;
            TypeBinding[] inheritedParams = inheritedMethod.parameters;
            int i = 0;
            int l = currentParams.length;
            while (i < l) {
                if (!MethodVerifier15.areTypesEqual(currentParams[i], inheritedParams[i])) {
                    return true;
                }
                ++i;
            }
        }
        return currentMethod.typeVariables == Binding.NO_TYPE_VARIABLES && inheritedMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES && currentMethod.returnType.erasure().findSuperTypeOriginatingFrom(inheritedMethod.returnType.erasure()) != null;
    }

    @Override
    boolean reportIncompatibleReturnTypeError(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        if (this.isUnsafeReturnTypeOverride(currentMethod, inheritedMethod)) {
            this.problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, inheritedMethod, this.type);
            return false;
        }
        return super.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod);
    }

    @Override
    void verify() {
        if (this.type.isAnnotationType()) {
            this.type.detectAnnotationCycle();
        }
        super.verify();
        this.reportRawReferences();
        int i = this.type.typeVariables.length;
        while (--i >= 0) {
            TypeVariableBinding var = this.type.typeVariables[i];
            if (var.superInterfaces == Binding.NO_SUPERINTERFACES || var.superInterfaces.length == 1 && var.superclass.id == 1) continue;
            this.currentMethods = new HashtableOfObject(0);
            ReferenceBinding superclass = var.superclass();
            if (superclass.kind() == 4100) {
                superclass = (ReferenceBinding)superclass.erasure();
            }
            ReferenceBinding[] itsInterfaces = var.superInterfaces();
            ReferenceBinding[] superInterfaces = new ReferenceBinding[itsInterfaces.length];
            int j = itsInterfaces.length;
            while (--j >= 0) {
                ReferenceBinding referenceBinding = superInterfaces[j] = itsInterfaces[j].kind() == 4100 ? (ReferenceBinding)itsInterfaces[j].erasure() : itsInterfaces[j];
            }
            this.computeInheritedMethods(superclass, superInterfaces);
            this.checkTypeVariableMethods(this.type.scope.referenceContext.typeParameters[i]);
        }
    }
}

