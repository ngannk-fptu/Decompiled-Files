/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.util.Sorting;

public abstract class MethodVerifier
extends ImplicitNullAnnotationVerifier {
    SourceTypeBinding type = null;
    HashtableOfObject inheritedMethods = null;
    HashtableOfObject currentMethods = null;
    HashtableOfObject inheritedOverriddenMethods = null;

    MethodVerifier(LookupEnvironment environment) {
        super(environment);
    }

    boolean areMethodsCompatible(MethodBinding one, MethodBinding two) {
        return MethodVerifier.areMethodsCompatible(one, two, this.environment);
    }

    static boolean areMethodsCompatible(MethodBinding one, MethodBinding two, LookupEnvironment environment) {
        two = (one = one.original()).findOriginalInheritedMethod(two);
        if (two == null) {
            return false;
        }
        return MethodVerifier.isParameterSubsignature(one, two, environment);
    }

    boolean areReturnTypesCompatible(MethodBinding one, MethodBinding two) {
        return MethodVerifier.areReturnTypesCompatible(one, two, this.type.scope.environment());
    }

    public static boolean areReturnTypesCompatible(MethodBinding one, MethodBinding two, LookupEnvironment environment) {
        if (TypeBinding.equalsEquals(one.returnType, two.returnType)) {
            return true;
        }
        if (environment.globalOptions.sourceLevel >= 0x310000L) {
            if (one.returnType.isBaseType()) {
                return false;
            }
            if (!one.declaringClass.isInterface() && one.declaringClass.id == 1) {
                return two.returnType.isCompatibleWith(one.returnType);
            }
            return one.returnType.isCompatibleWith(two.returnType);
        }
        return MethodVerifier.areTypesEqual(one.returnType.erasure(), two.returnType.erasure());
    }

    boolean canSkipInheritedMethods() {
        if (this.type.superclass() != null && this.type.superclass().isAbstract()) {
            return false;
        }
        return this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
    }

    boolean canSkipInheritedMethods(MethodBinding one, MethodBinding two) {
        return two == null || TypeBinding.equalsEquals(one.declaringClass, two.declaringClass);
    }

    void checkAbstractMethod(MethodBinding abstractMethod) {
        if (this.mustImplementAbstractMethod(abstractMethod.declaringClass)) {
            TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
            if (typeDeclaration != null) {
                MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(abstractMethod);
                missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
            } else {
                this.problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
            }
        }
    }

    void checkAgainstInheritedMethods(MethodBinding currentMethod, MethodBinding[] methods, int length, MethodBinding[] allInheritedMethods) {
        if (this.type.isAnnotationType()) {
            this.problemReporter().annotationCannotOverrideMethod(currentMethod, methods[length - 1]);
            return;
        }
        CompilerOptions options = this.type.scope.compilerOptions();
        int[] overriddenInheritedMethods = length > 1 ? this.findOverriddenInheritedMethods(methods, length) : null;
        int i = length;
        block0: while (--i >= 0) {
            MethodBinding inheritedMethod = methods[i];
            if (overriddenInheritedMethods == null || overriddenInheritedMethods[i] == 0) {
                if (currentMethod.isStatic() != inheritedMethod.isStatic()) {
                    this.problemReporter(currentMethod).staticAndInstanceConflict(currentMethod, inheritedMethod);
                    continue;
                }
                if (inheritedMethod.isAbstract()) {
                    currentMethod.modifiers = inheritedMethod.declaringClass.isInterface() ? (currentMethod.modifiers |= 0x20000000) : (currentMethod.modifiers |= 0x30000000);
                } else if (inheritedMethod.isPublic() || !this.type.isInterface()) {
                    if (currentMethod.isDefaultMethod() && !inheritedMethod.isFinal() && inheritedMethod.declaringClass.id == 1) {
                        this.problemReporter(currentMethod).defaultMethodOverridesObjectMethod(currentMethod);
                    } else {
                        currentMethod.modifiers = inheritedMethod.isDefaultMethod() ? (currentMethod.modifiers |= 0x20000000) : (currentMethod.modifiers |= 0x10000000);
                    }
                }
                if (!this.areReturnTypesCompatible(currentMethod, inheritedMethod) && (currentMethod.returnType.tagBits & 0x80L) == 0L && this.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod)) continue;
                this.reportRawReferences(currentMethod, inheritedMethod);
                if (currentMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
                    this.checkExceptions(currentMethod, inheritedMethod);
                }
                if (inheritedMethod.isFinal()) {
                    this.problemReporter(currentMethod).finalMethodCannotBeOverridden(currentMethod, inheritedMethod);
                }
                if (!this.isAsVisible(currentMethod, inheritedMethod)) {
                    this.problemReporter(currentMethod).visibilityConflict(currentMethod, inheritedMethod);
                }
                if (inheritedMethod.isSynchronized() && !currentMethod.isSynchronized()) {
                    this.problemReporter(currentMethod).missingSynchronizedOnInheritedMethod(currentMethod, inheritedMethod);
                }
                if (options.reportDeprecationWhenOverridingDeprecatedMethod && inheritedMethod.isViewedAsDeprecated() && (!currentMethod.isViewedAsDeprecated() || options.reportDeprecationInsideDeprecatedCode)) {
                    ReferenceBinding declaringClass = inheritedMethod.declaringClass;
                    if (declaringClass.isInterface()) {
                        int j = length;
                        while (--j >= 0) {
                            if (i != j && methods[j].declaringClass.implementsInterface(declaringClass, false)) continue block0;
                        }
                    }
                    this.problemReporter(currentMethod).overridesDeprecatedMethod(currentMethod, inheritedMethod);
                }
            }
            if (inheritedMethod.isStatic() || inheritedMethod.isFinal()) continue;
            this.checkForBridgeMethod(currentMethod, inheritedMethod, allInheritedMethods);
        }
        MethodBinding[] overridden = (MethodBinding[])this.inheritedOverriddenMethods.get(currentMethod.selector);
        if (overridden != null) {
            int i2 = overridden.length;
            while (--i2 >= 0) {
                MethodBinding inheritedMethod = overridden[i2];
                if (!this.isParameterSubsignature(currentMethod, inheritedMethod) || inheritedMethod.isStatic() || inheritedMethod.isFinal()) continue;
                this.checkForBridgeMethod(currentMethod, inheritedMethod, allInheritedMethods);
            }
        }
    }

    void addBridgeMethodCandidate(MethodBinding overriddenMethod) {
        MethodBinding[] existing = (MethodBinding[])this.inheritedOverriddenMethods.get(overriddenMethod.selector);
        if (existing == null) {
            existing = new MethodBinding[]{overriddenMethod};
        } else {
            int length = existing.length;
            MethodBinding[] methodBindingArray = existing;
            existing = new MethodBinding[length + 1];
            System.arraycopy(methodBindingArray, 0, existing, 0, length);
            existing[length] = overriddenMethod;
        }
        this.inheritedOverriddenMethods.put(overriddenMethod.selector, existing);
    }

    public void reportRawReferences(MethodBinding currentMethod, MethodBinding inheritedMethod) {
    }

    void checkConcreteInheritedMethod(MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        if (concreteMethod.isStatic()) {
            this.problemReporter().staticInheritedMethodConflicts(this.type, concreteMethod, abstractMethods);
        }
        if (!concreteMethod.isPublic()) {
            int index = 0;
            int length = abstractMethods.length;
            if (concreteMethod.isProtected()) {
                while (index < length) {
                    if (!abstractMethods[index].isPublic()) {
                        ++index;
                        continue;
                    }
                    break;
                }
            } else if (concreteMethod.isDefault()) {
                while (index < length) {
                    if (abstractMethods[index].isDefault()) {
                        ++index;
                        continue;
                    }
                    break;
                }
            }
            if (index < length) {
                this.problemReporter().inheritedMethodReducesVisibility(this.type, concreteMethod, abstractMethods);
            }
        }
        if (concreteMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
            int i = abstractMethods.length;
            while (--i >= 0) {
                this.checkExceptions(concreteMethod, abstractMethods[i]);
            }
        }
        if (concreteMethod.isOrEnclosedByPrivateType()) {
            concreteMethod.original().modifiers |= 0x8000000;
        }
    }

    void checkExceptions(MethodBinding newMethod, MethodBinding inheritedMethod) {
        ReferenceBinding[] newExceptions = this.resolvedExceptionTypesFor(newMethod);
        ReferenceBinding[] inheritedExceptions = this.resolvedExceptionTypesFor(inheritedMethod);
        int i = newExceptions.length;
        while (--i >= 0) {
            ReferenceBinding newException = newExceptions[i];
            int j = inheritedExceptions.length;
            while (--j > -1 && !this.isSameClassOrSubclassOf(newException, inheritedExceptions[j])) {
            }
            if (j != -1 || newException.isUncheckedException(false) || (newException.tagBits & 0x80L) != 0L) continue;
            this.problemReporter(newMethod).incompatibleExceptionInThrowsClause(this.type, newMethod, inheritedMethod, newException);
        }
    }

    void checkForBridgeMethod(MethodBinding currentMethod, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods) {
    }

    void checkForMissingHashCodeMethod() {
        MethodBinding hashCodeMethod;
        MethodBinding[] choices = this.type.getMethods(TypeConstants.EQUALS);
        boolean overridesEquals = false;
        int i = choices.length;
        while (!overridesEquals && --i >= 0) {
            boolean bl = overridesEquals = choices[i].parameters.length == 1 && choices[i].parameters[0].id == 1;
        }
        if (overridesEquals && (hashCodeMethod = this.type.getExactMethod(TypeConstants.HASHCODE, Binding.NO_PARAMETERS, null)) != null && hashCodeMethod.declaringClass.id == 1) {
            this.problemReporter().shouldImplementHashcode(this.type);
        }
    }

    void checkForRedundantSuperinterfaces(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
        if (superInterfaces == Binding.NO_SUPERINTERFACES) {
            return;
        }
        SimpleSet interfacesToCheck = new SimpleSet(superInterfaces.length);
        SimpleSet redundantInterfaces = null;
        int i = 0;
        int l = superInterfaces.length;
        while (i < l) {
            ReferenceBinding toCheck = superInterfaces[i];
            int j = 0;
            while (j < l) {
                block23: {
                    ReferenceBinding implementedInterface;
                    block25: {
                        block24: {
                            implementedInterface = superInterfaces[j];
                            if (i == j || !toCheck.implementsInterface(implementedInterface, true)) break block23;
                            if (redundantInterfaces != null) break block24;
                            redundantInterfaces = new SimpleSet(3);
                            break block25;
                        }
                        if (redundantInterfaces.includes(implementedInterface)) break block23;
                    }
                    redundantInterfaces.add(implementedInterface);
                    TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
                    int r = 0;
                    int rl = refs.length;
                    while (r < rl) {
                        if (TypeBinding.equalsEquals(refs[r].resolvedType, toCheck)) {
                            this.problemReporter().redundantSuperInterface(this.type, refs[j], implementedInterface, toCheck);
                            break;
                        }
                        ++r;
                    }
                }
                ++j;
            }
            interfacesToCheck.add(toCheck);
            ++i;
        }
        ReferenceBinding[] itsInterfaces = null;
        SimpleSet inheritedInterfaces = new SimpleSet(5);
        Object superType = superclass;
        while (superType != null && ((Binding)superType).isValidBinding()) {
            block26: {
                itsInterfaces = ((ReferenceBinding)superType).superInterfaces();
                if (itsInterfaces == Binding.NO_SUPERINTERFACES) break block26;
                int i2 = 0;
                int l2 = itsInterfaces.length;
                while (i2 < l2) {
                    block21: {
                        ReferenceBinding inheritedInterface;
                        block27: {
                            block29: {
                                block28: {
                                    inheritedInterface = itsInterfaces[i2];
                                    if (inheritedInterfaces.includes(inheritedInterface) || !inheritedInterface.isValidBinding()) break block21;
                                    if (!interfacesToCheck.includes(inheritedInterface)) break block27;
                                    if (redundantInterfaces != null) break block28;
                                    redundantInterfaces = new SimpleSet(3);
                                    break block29;
                                }
                                if (redundantInterfaces.includes(inheritedInterface)) break block21;
                            }
                            redundantInterfaces.add(inheritedInterface);
                            TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
                            int r = 0;
                            int rl = refs.length;
                            while (r < rl) {
                                if (TypeBinding.equalsEquals(refs[r].resolvedType, inheritedInterface)) {
                                    this.problemReporter().redundantSuperInterface(this.type, refs[r], inheritedInterface, (ReferenceBinding)superType);
                                    break block21;
                                }
                                ++r;
                            }
                            break block21;
                        }
                        inheritedInterfaces.add(inheritedInterface);
                    }
                    ++i2;
                }
            }
            superType = ((ReferenceBinding)superType).superclass();
        }
        int nextPosition = inheritedInterfaces.elementSize;
        if (nextPosition == 0) {
            return;
        }
        Object[] interfacesToVisit = new ReferenceBinding[nextPosition];
        inheritedInterfaces.asArray(interfacesToVisit);
        int i3 = 0;
        while (i3 < nextPosition) {
            block30: {
                superType = interfacesToVisit[i3];
                itsInterfaces = ((ReferenceBinding)superType).superInterfaces();
                if (itsInterfaces == Binding.NO_SUPERINTERFACES) break block30;
                int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    Object[] objectArray = interfacesToVisit;
                    interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                    System.arraycopy(objectArray, 0, interfacesToVisit, 0, nextPosition);
                }
                int a = 0;
                while (a < itsLength) {
                    block22: {
                        ReferenceBinding inheritedInterface;
                        block31: {
                            block33: {
                                block32: {
                                    inheritedInterface = itsInterfaces[a];
                                    if (inheritedInterfaces.includes(inheritedInterface) || !inheritedInterface.isValidBinding()) break block22;
                                    if (!interfacesToCheck.includes(inheritedInterface)) break block31;
                                    if (redundantInterfaces != null) break block32;
                                    redundantInterfaces = new SimpleSet(3);
                                    break block33;
                                }
                                if (redundantInterfaces.includes(inheritedInterface)) break block22;
                            }
                            redundantInterfaces.add(inheritedInterface);
                            TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
                            int r = 0;
                            int rl = refs.length;
                            while (r < rl) {
                                if (TypeBinding.equalsEquals(refs[r].resolvedType, inheritedInterface)) {
                                    this.problemReporter().redundantSuperInterface(this.type, refs[r], inheritedInterface, (ReferenceBinding)superType);
                                    break block22;
                                }
                                ++r;
                            }
                            break block22;
                        }
                        inheritedInterfaces.add(inheritedInterface);
                        interfacesToVisit[nextPosition++] = inheritedInterface;
                    }
                    ++a;
                }
            }
            ++i3;
        }
    }

    void checkInheritedMethods(MethodBinding[] methods, int length, boolean[] isOverridden, boolean[] isInherited) {
        MethodBinding concreteMethod;
        MethodBinding methodBinding = concreteMethod = this.type.isInterface() || methods[0].isAbstract() ? null : methods[0];
        if (concreteMethod == null) {
            boolean noMatch;
            MethodBinding bestAbstractMethod = length == 1 ? methods[0] : this.findBestInheritedAbstractOrDefaultMethod(methods, length);
            boolean bl = noMatch = bestAbstractMethod == null;
            if (noMatch) {
                bestAbstractMethod = methods[0];
            }
            if (this.mustImplementAbstractMethod(bestAbstractMethod.declaringClass)) {
                TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
                MethodBinding superclassAbstractMethod = methods[0];
                if (superclassAbstractMethod == bestAbstractMethod || superclassAbstractMethod.declaringClass.isInterface()) {
                    if (typeDeclaration != null) {
                        MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
                        missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
                    } else {
                        this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
                    }
                } else if (typeDeclaration != null) {
                    MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
                    missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
                } else {
                    this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
                }
            } else if (noMatch) {
                this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length, isOverridden);
            }
            return;
        }
        if (length < 2) {
            return;
        }
        int index = length;
        while (--index > 0 && this.checkInheritedReturnTypes(concreteMethod, methods[index])) {
        }
        if (index > 0) {
            MethodBinding bestAbstractMethod = this.findBestInheritedAbstractOrDefaultMethod(methods, length);
            if (bestAbstractMethod == null) {
                this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length, isOverridden);
            } else {
                this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, concreteMethod);
            }
            return;
        }
        MethodBinding[] abstractMethods = new MethodBinding[length - 1];
        index = 0;
        int i = 0;
        while (i < length) {
            if (methods[i].isAbstract() || methods[i] != concreteMethod && methods[i].isDefaultMethod()) {
                abstractMethods[index++] = methods[i];
            }
            ++i;
        }
        if (index == 0) {
            return;
        }
        if (index < abstractMethods.length) {
            MethodBinding[] methodBindingArray = abstractMethods;
            abstractMethods = new MethodBinding[index];
            System.arraycopy(methodBindingArray, 0, abstractMethods, 0, index);
        }
        this.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
    }

    boolean checkInheritedReturnTypes(MethodBinding method, MethodBinding otherMethod) {
        if (this.areReturnTypesCompatible(method, otherMethod)) {
            return true;
        }
        return !(this.type.isInterface() || !method.declaringClass.isClass() && this.type.implementsInterface(method.declaringClass, false) || !otherMethod.declaringClass.isClass() && this.type.implementsInterface(otherMethod.declaringClass, false));
    }

    abstract void checkMethods();

    void checkPackagePrivateAbstractMethod(MethodBinding abstractMethod) {
        PackageBinding necessaryPackage = abstractMethod.declaringClass.fPackage;
        if (necessaryPackage == this.type.fPackage) {
            return;
        }
        ReferenceBinding superType = this.type.superclass();
        char[] selector = abstractMethod.selector;
        do {
            if (!superType.isValidBinding()) {
                return;
            }
            if (!superType.isAbstract()) {
                return;
            }
            if (necessaryPackage != superType.fPackage) continue;
            MethodBinding[] methods = superType.getMethods(selector);
            int m = methods.length;
            while (--m >= 0) {
                MethodBinding method = methods[m];
                if (method.isPrivate() || method.isConstructor() || method.isDefaultAbstract() || !this.areMethodsCompatible(method, abstractMethod)) continue;
                return;
            }
        } while (TypeBinding.notEquals(superType = superType.superclass(), abstractMethod.declaringClass));
        this.problemReporter().abstractMethodCannotBeOverridden(this.type, abstractMethod);
    }

    void computeInheritedMethods() {
        ReferenceBinding superclass = this.type.isInterface() ? this.type.scope.getJavaLangObject() : this.type.superclass();
        this.computeInheritedMethods(superclass, this.type.superInterfaces());
        this.checkForRedundantSuperinterfaces(superclass, this.type.superInterfaces());
    }

    void computeInheritedMethods(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
        int i;
        this.inheritedMethods = new HashtableOfObject(51);
        this.inheritedOverriddenMethods = new HashtableOfObject(11);
        ReferenceBinding superType = superclass;
        HashtableOfObject nonVisibleDefaultMethods = new HashtableOfObject(3);
        while (superType != null && superType.isValidBinding()) {
            MethodBinding[] methods = superType.unResolvedMethods();
            int m = methods.length;
            block1: while (--m >= 0) {
                MethodBinding[] current;
                int length;
                MethodBinding[] existingMethods;
                MethodBinding inheritedMethod;
                block28: {
                    inheritedMethod = methods[m];
                    if (inheritedMethod.isPrivate() || inheritedMethod.isConstructor() || inheritedMethod.isDefaultAbstract()) continue;
                    existingMethods = (MethodBinding[])this.inheritedMethods.get(inheritedMethod.selector);
                    if (existingMethods == null) break block28;
                    int i2 = 0;
                    length = existingMethods.length;
                    while (i2 < length) {
                        block29: {
                            MethodBinding existingMethod;
                            block30: {
                                block31: {
                                    existingMethod = existingMethods[i2];
                                    if (!TypeBinding.notEquals(existingMethod.declaringClass, inheritedMethod.declaringClass) || !this.areMethodsCompatible(existingMethod, inheritedMethod) || this.canOverridingMethodDifferInErasure(existingMethod, inheritedMethod)) break block29;
                                    if (!inheritedMethod.isDefault()) break block30;
                                    if (!inheritedMethod.isAbstract()) break block31;
                                    this.checkPackagePrivateAbstractMethod(inheritedMethod);
                                    break block30;
                                }
                                if (existingMethod.declaringClass.fPackage != inheritedMethod.declaringClass.fPackage && this.type.fPackage == inheritedMethod.declaringClass.fPackage && !this.areReturnTypesCompatible(inheritedMethod, existingMethod)) break block29;
                            }
                            if (!TypeBinding.notEquals(inheritedMethod.returnType.erasure(), existingMethod.returnType.erasure()) || !this.areReturnTypesCompatible(existingMethod, inheritedMethod)) continue block1;
                            this.addBridgeMethodCandidate(inheritedMethod);
                            continue block1;
                        }
                        ++i2;
                    }
                }
                if (!inheritedMethod.isDefault() || inheritedMethod.declaringClass.fPackage == this.type.fPackage) {
                    if (existingMethods == null) {
                        existingMethods = new MethodBinding[]{inheritedMethod};
                    } else {
                        int length2 = existingMethods.length;
                        MethodBinding[] methodBindingArray = existingMethods;
                        existingMethods = new MethodBinding[length2 + 1];
                        System.arraycopy(methodBindingArray, 0, existingMethods, 0, length2);
                        existingMethods[length2] = inheritedMethod;
                    }
                    this.inheritedMethods.put(inheritedMethod.selector, existingMethods);
                    continue;
                }
                MethodBinding[] nonVisible = (MethodBinding[])nonVisibleDefaultMethods.get(inheritedMethod.selector);
                if (nonVisible != null && inheritedMethod.isAbstract()) {
                    i = 0;
                    int l = nonVisible.length;
                    while (i < l) {
                        if (this.areMethodsCompatible(nonVisible[i], inheritedMethod)) continue block1;
                        ++i;
                    }
                }
                if (nonVisible == null) {
                    nonVisible = new MethodBinding[]{inheritedMethod};
                } else {
                    length = nonVisible.length;
                    MethodBinding[] methodBindingArray = nonVisible;
                    nonVisible = new MethodBinding[length + 1];
                    System.arraycopy(methodBindingArray, 0, nonVisible, 0, length);
                    nonVisible[length] = inheritedMethod;
                }
                nonVisibleDefaultMethods.put(inheritedMethod.selector, nonVisible);
                if (inheritedMethod.isAbstract() && !this.type.isAbstract()) {
                    this.problemReporter().abstractMethodCannotBeOverridden(this.type, inheritedMethod);
                }
                if ((current = (MethodBinding[])this.currentMethods.get(inheritedMethod.selector)) == null || inheritedMethod.isStatic()) continue;
                int i3 = 0;
                int length3 = current.length;
                while (i3 < length3) {
                    if (!current[i3].isStatic() && this.areMethodsCompatible(current[i3], inheritedMethod)) {
                        this.problemReporter().overridesPackageDefaultMethod(current[i3], inheritedMethod);
                        continue block1;
                    }
                    ++i3;
                }
            }
            superType = superType.superclass();
        }
        ArrayList superIfcList = new ArrayList();
        HashSet seenTypes = new HashSet();
        this.collectAllDistinctSuperInterfaces(superInterfaces, seenTypes, superIfcList);
        ReferenceBinding currentSuper = superclass;
        while (currentSuper != null && currentSuper.id != 1) {
            this.collectAllDistinctSuperInterfaces(currentSuper.superInterfaces(), seenTypes, superIfcList);
            currentSuper = currentSuper.superclass();
        }
        if (superIfcList.size() == 0) {
            return;
        }
        if (superIfcList.size() == 1) {
            superInterfaces = new ReferenceBinding[]{(ReferenceBinding)superIfcList.get(0)};
        } else {
            superInterfaces = superIfcList.toArray(new ReferenceBinding[superIfcList.size()]);
            superInterfaces = Sorting.sortTypes(superInterfaces);
        }
        SimpleSet skip = this.findSuperinterfaceCollisions(superclass, superInterfaces);
        int len = superInterfaces.length;
        i = len - 1;
        while (i >= 0) {
            superType = superInterfaces[i];
            if (superType.isValidBinding() && (skip == null || !skip.includes(superType))) {
                MethodBinding[] methods = superType.unResolvedMethods();
                int m = methods.length;
                block7: while (--m >= 0) {
                    MethodBinding inheritedMethod = methods[m];
                    if (inheritedMethod.isStatic() || inheritedMethod.isPrivate()) continue;
                    MethodBinding[] existingMethods = (MethodBinding[])this.inheritedMethods.get(inheritedMethod.selector);
                    if (existingMethods == null) {
                        existingMethods = new MethodBinding[]{inheritedMethod};
                    } else {
                        int length = existingMethods.length;
                        int e = 0;
                        while (e < length) {
                            if (this.isInterfaceMethodImplemented(inheritedMethod, existingMethods[e], superType)) {
                                if (TypeBinding.notEquals(inheritedMethod.returnType.erasure(), existingMethods[e].returnType.erasure())) {
                                    this.addBridgeMethodCandidate(inheritedMethod);
                                }
                                if (!this.canOverridingMethodDifferInErasure(existingMethods[e], inheritedMethod)) continue block7;
                            }
                            ++e;
                        }
                        MethodBinding[] methodBindingArray = existingMethods;
                        existingMethods = new MethodBinding[length + 1];
                        System.arraycopy(methodBindingArray, 0, existingMethods, 0, length);
                        existingMethods[length] = inheritedMethod;
                    }
                    this.inheritedMethods.put(inheritedMethod.selector, existingMethods);
                }
            }
            --i;
        }
    }

    void collectAllDistinctSuperInterfaces(ReferenceBinding[] superInterfaces, Set seen, List result) {
        int length = superInterfaces.length;
        int i = 0;
        while (i < length) {
            ReferenceBinding superInterface = superInterfaces[i];
            if (seen.add(superInterface)) {
                result.add(superInterface);
                this.collectAllDistinctSuperInterfaces(superInterface.superInterfaces(), seen, result);
            }
            ++i;
        }
    }

    protected boolean canOverridingMethodDifferInErasure(MethodBinding overridingMethod, MethodBinding inheritedMethod) {
        return false;
    }

    void computeMethods() {
        MethodBinding[] methods = this.type.methods();
        int size = methods.length;
        this.currentMethods = new HashtableOfObject(size == 0 ? 1 : size);
        int m = size;
        while (--m >= 0) {
            MethodBinding method = methods[m];
            if (method.isConstructor() || method.isDefaultAbstract()) continue;
            MethodBinding[] existingMethods = (MethodBinding[])this.currentMethods.get(method.selector);
            if (existingMethods == null) {
                existingMethods = new MethodBinding[1];
            } else {
                MethodBinding[] methodBindingArray = existingMethods;
                existingMethods = new MethodBinding[existingMethods.length + 1];
                System.arraycopy(methodBindingArray, 0, existingMethods, 0, existingMethods.length - 1);
            }
            existingMethods[existingMethods.length - 1] = method;
            this.currentMethods.put(method.selector, existingMethods);
        }
    }

    MethodBinding computeSubstituteMethod(MethodBinding inheritedMethod, MethodBinding currentMethod) {
        return MethodVerifier.computeSubstituteMethod(inheritedMethod, currentMethod, this.environment);
    }

    public static MethodBinding computeSubstituteMethod(MethodBinding inheritedMethod, MethodBinding currentMethod, LookupEnvironment environment) {
        TypeVariableBinding[] inheritedTypeVariables;
        int inheritedLength;
        if (inheritedMethod == null) {
            return null;
        }
        if (currentMethod.parameters.length != inheritedMethod.parameters.length) {
            return null;
        }
        if (currentMethod.declaringClass instanceof BinaryTypeBinding) {
            ((BinaryTypeBinding)currentMethod.declaringClass).resolveTypesFor(currentMethod);
        }
        if (inheritedMethod.declaringClass instanceof BinaryTypeBinding) {
            ((BinaryTypeBinding)inheritedMethod.declaringClass).resolveTypesFor(inheritedMethod);
        }
        if ((inheritedLength = (inheritedTypeVariables = inheritedMethod.typeVariables).length) == 0) {
            return inheritedMethod;
        }
        TypeVariableBinding[] typeVariables = currentMethod.typeVariables;
        int length = typeVariables.length;
        if (length == 0) {
            return inheritedMethod.asRawMethod(environment);
        }
        if (length != inheritedLength) {
            return inheritedMethod;
        }
        TypeBinding[] arguments = new TypeBinding[length];
        System.arraycopy(typeVariables, 0, arguments, 0, length);
        ParameterizedGenericMethodBinding substitute = environment.createParameterizedGenericMethod(inheritedMethod, arguments);
        int i = 0;
        while (i < inheritedLength) {
            block17: {
                TypeVariableBinding typeVariable;
                TypeVariableBinding inheritedTypeVariable;
                block16: {
                    block15: {
                        inheritedTypeVariable = inheritedTypeVariables[i];
                        typeVariable = (TypeVariableBinding)arguments[i];
                        if (!TypeBinding.equalsEquals(typeVariable.firstBound, inheritedTypeVariable.firstBound)) break block15;
                        if (typeVariable.firstBound != null) break block16;
                        break block17;
                    }
                    if (typeVariable.firstBound != null && inheritedTypeVariable.firstBound != null && typeVariable.firstBound.isClass() != inheritedTypeVariable.firstBound.isClass()) {
                        return inheritedMethod;
                    }
                }
                if (TypeBinding.notEquals(Scope.substitute((Substitution)substitute, inheritedTypeVariable.superclass), typeVariable.superclass)) {
                    return inheritedMethod;
                }
                int interfaceLength = inheritedTypeVariable.superInterfaces.length;
                ReferenceBinding[] interfaces = typeVariable.superInterfaces;
                if (interfaceLength != interfaces.length) {
                    return inheritedMethod;
                }
                int j = 0;
                while (j < interfaceLength) {
                    block14: {
                        TypeBinding superType = Scope.substitute((Substitution)substitute, inheritedTypeVariable.superInterfaces[j]);
                        int k = 0;
                        while (k < interfaceLength) {
                            if (!TypeBinding.equalsEquals(superType, interfaces[k])) {
                                ++k;
                                continue;
                            }
                            break block14;
                        }
                        return inheritedMethod;
                    }
                    ++j;
                }
            }
            ++i;
        }
        return substitute;
    }

    static boolean couldMethodOverride(MethodBinding method, MethodBinding inheritedMethod) {
        if (!CharOperation.equals(method.selector, inheritedMethod.selector)) {
            return false;
        }
        if (method == inheritedMethod || method.isStatic() || inheritedMethod.isStatic()) {
            return false;
        }
        if (inheritedMethod.isPrivate()) {
            return false;
        }
        if (inheritedMethod.isDefault() && method.declaringClass.getPackage() != inheritedMethod.declaringClass.getPackage()) {
            return false;
        }
        if (!method.isPublic()) {
            if (inheritedMethod.isPublic()) {
                return false;
            }
            if (inheritedMethod.isProtected() && !method.isProtected()) {
                return false;
            }
        }
        return true;
    }

    public boolean doesMethodOverride(MethodBinding method, MethodBinding inheritedMethod) {
        return MethodVerifier.doesMethodOverride(method, inheritedMethod, this.environment);
    }

    public static boolean doesMethodOverride(MethodBinding method, MethodBinding inheritedMethod, LookupEnvironment environment) {
        return MethodVerifier.couldMethodOverride(method, inheritedMethod) && MethodVerifier.areMethodsCompatible(method, inheritedMethod, environment);
    }

    SimpleSet findSuperinterfaceCollisions(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
        return null;
    }

    MethodBinding findBestInheritedAbstractOrDefaultMethod(MethodBinding[] methods, int length) {
        int i = 0;
        while (i < length) {
            block5: {
                MethodBinding method = methods[i];
                if (method.isAbstract() || method.isDefaultMethod()) {
                    int j = 0;
                    while (j < length) {
                        if (i != j && !this.checkInheritedReturnTypes(method, methods[j])) {
                            if (this.type.isInterface() && methods[j].declaringClass.id == 1) {
                                return method;
                            }
                            break block5;
                        }
                        ++j;
                    }
                    return method;
                }
            }
            ++i;
        }
        return null;
    }

    int[] findOverriddenInheritedMethods(MethodBinding[] methods, int length) {
        int[] toSkip = null;
        int i = 0;
        ReferenceBinding declaringClass = methods[i].declaringClass;
        if (!declaringClass.isInterface()) {
            ReferenceBinding declaringClass2 = methods[++i].declaringClass;
            while (TypeBinding.equalsEquals(declaringClass, declaringClass2)) {
                if (++i == length) {
                    return null;
                }
                declaringClass2 = methods[i].declaringClass;
            }
            if (!declaringClass2.isInterface()) {
                if (declaringClass.fPackage != declaringClass2.fPackage && methods[i].isDefault()) {
                    return null;
                }
                toSkip = new int[length];
                do {
                    toSkip[i] = -1;
                    if (++i != length) continue;
                    return toSkip;
                } while (!(declaringClass2 = methods[i].declaringClass).isInterface());
            }
        }
        while (i < length) {
            if (toSkip == null || toSkip[i] != -1) {
                declaringClass = methods[i].declaringClass;
                int j = i + 1;
                while (j < length) {
                    ReferenceBinding declaringClass2;
                    if (!(toSkip != null && toSkip[j] == -1 || TypeBinding.equalsEquals(declaringClass, declaringClass2 = methods[j].declaringClass))) {
                        if (declaringClass.implementsInterface(declaringClass2, true)) {
                            if (toSkip == null) {
                                toSkip = new int[length];
                            }
                            toSkip[j] = -1;
                        } else if (declaringClass2.implementsInterface(declaringClass, true)) {
                            if (toSkip == null) {
                                toSkip = new int[length];
                            }
                            toSkip[i] = -1;
                            break;
                        }
                    }
                    ++j;
                }
            }
            ++i;
        }
        return toSkip;
    }

    boolean isAsVisible(MethodBinding newMethod, MethodBinding inheritedMethod) {
        if (inheritedMethod.modifiers == newMethod.modifiers) {
            return true;
        }
        if (newMethod.isPublic()) {
            return true;
        }
        if (inheritedMethod.isPublic()) {
            return false;
        }
        if (newMethod.isProtected()) {
            return true;
        }
        if (inheritedMethod.isProtected()) {
            return false;
        }
        return !newMethod.isPrivate();
    }

    boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, MethodBinding existingMethod, ReferenceBinding superType) {
        return MethodVerifier.areParametersEqual(existingMethod, inheritedMethod) && existingMethod.declaringClass.implementsInterface(superType, true);
    }

    public boolean isMethodSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
        return CharOperation.equals(method.selector, inheritedMethod.selector) && this.isParameterSubsignature(method, inheritedMethod);
    }

    boolean isParameterSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
        return MethodVerifier.isParameterSubsignature(method, inheritedMethod, this.environment);
    }

    static boolean isParameterSubsignature(MethodBinding method, MethodBinding inheritedMethod, LookupEnvironment environment) {
        MethodBinding substitute = MethodVerifier.computeSubstituteMethod(inheritedMethod, method, environment);
        return substitute != null && MethodVerifier.isSubstituteParameterSubsignature(method, substitute, environment);
    }

    boolean isSubstituteParameterSubsignature(MethodBinding method, MethodBinding substituteMethod) {
        return MethodVerifier.isSubstituteParameterSubsignature(method, substituteMethod, this.environment);
    }

    public static boolean isSubstituteParameterSubsignature(MethodBinding method, MethodBinding substituteMethod, LookupEnvironment environment) {
        if (!MethodVerifier.areParametersEqual(method, substituteMethod)) {
            if (substituteMethod.hasSubstitutedParameters() && method.areParameterErasuresEqual(substituteMethod)) {
                return method.typeVariables == Binding.NO_TYPE_VARIABLES && !MethodVerifier.hasGenericParameter(method);
            }
            if (method.declaringClass.isRawType() && substituteMethod.declaringClass.isRawType() && method.hasSubstitutedParameters() && substituteMethod.hasSubstitutedParameters()) {
                return MethodVerifier.areMethodsCompatible(method, substituteMethod, environment);
            }
            return false;
        }
        if (substituteMethod instanceof ParameterizedGenericMethodBinding) {
            if (method.typeVariables != Binding.NO_TYPE_VARIABLES) {
                return !((ParameterizedGenericMethodBinding)substituteMethod).isRaw;
            }
            return !MethodVerifier.hasGenericParameter(method);
        }
        return method.typeVariables == Binding.NO_TYPE_VARIABLES;
    }

    static boolean hasGenericParameter(MethodBinding method) {
        if (method.genericSignature() == null) {
            return false;
        }
        TypeBinding[] params = method.parameters;
        int i = 0;
        int l = params.length;
        while (i < l) {
            int modifiers;
            TypeBinding param = params[i].leafComponentType();
            if (param instanceof ReferenceBinding && ((modifiers = ((ReferenceBinding)param).modifiers) & 0x40000000) != 0) {
                return true;
            }
            ++i;
        }
        return false;
    }

    boolean isSameClassOrSubclassOf(ReferenceBinding testClass, ReferenceBinding superclass) {
        do {
            if (!TypeBinding.equalsEquals(testClass, superclass)) continue;
            return true;
        } while ((testClass = testClass.superclass()) != null);
        return false;
    }

    /*
     * Unable to fully structure code
     */
    boolean mustImplementAbstractMethod(ReferenceBinding declaringClass) {
        block4: {
            block3: {
                if (!this.mustImplementAbstractMethods()) {
                    return false;
                }
                superclass = this.type.superclass();
                if (!declaringClass.isClass()) break block3;
                while (superclass.isAbstract() && TypeBinding.notEquals(superclass, declaringClass)) {
                    superclass = superclass.superclass();
                }
                break block4;
            }
            if (!this.type.implementsInterface(declaringClass, false) || superclass.implementsInterface(declaringClass, true)) ** GOTO lbl13
            return true;
lbl-1000:
            // 1 sources

            {
                superclass = superclass.superclass();
lbl13:
                // 2 sources

                ** while (superclass.isAbstract() && !superclass.implementsInterface((ReferenceBinding)declaringClass, (boolean)false))
            }
        }
        return superclass.isAbstract();
    }

    boolean mustImplementAbstractMethods() {
        return !this.type.isInterface() && !this.type.isAbstract();
    }

    ProblemReporter problemReporter() {
        return this.type.scope.problemReporter();
    }

    ProblemReporter problemReporter(MethodBinding currentMethod) {
        ProblemReporter reporter = this.problemReporter();
        if (TypeBinding.equalsEquals(currentMethod.declaringClass, this.type) && currentMethod.sourceMethod() != null) {
            reporter.referenceContext = currentMethod.sourceMethod();
        }
        return reporter;
    }

    boolean reportIncompatibleReturnTypeError(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        this.problemReporter(currentMethod).incompatibleReturnType(currentMethod, inheritedMethod);
        return true;
    }

    ReferenceBinding[] resolvedExceptionTypesFor(MethodBinding method) {
        ReferenceBinding[] exceptions = method.thrownExceptions;
        if ((method.modifiers & 0x2000000) == 0) {
            return exceptions;
        }
        if (!(method.declaringClass instanceof BinaryTypeBinding)) {
            return Binding.NO_EXCEPTIONS;
        }
        int i = exceptions.length;
        while (--i >= 0) {
            exceptions[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(exceptions[i], this.environment, true);
        }
        return exceptions;
    }

    void verify() {
        this.computeMethods();
        this.computeInheritedMethods();
        this.checkMethods();
        if (this.type.isClass()) {
            this.checkForMissingHashCodeMethod();
        }
    }

    void verify(SourceTypeBinding someType) {
        if (this.type == null) {
            try {
                this.type = someType;
                this.verify();
            }
            finally {
                this.type = null;
            }
        } else {
            this.environment.newMethodVerifier().verify(someType);
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(10);
        buffer.append("MethodVerifier for type: ");
        buffer.append(this.type.readableName());
        buffer.append('\n');
        buffer.append("\t-inherited methods: ");
        buffer.append(this.inheritedMethods);
        return buffer.toString();
    }
}

