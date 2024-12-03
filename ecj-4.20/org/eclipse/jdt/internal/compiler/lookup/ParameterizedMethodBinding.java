/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ParameterizedMethodBinding
extends MethodBinding {
    protected MethodBinding originalMethod;

    public ParameterizedMethodBinding(final ParameterizedTypeBinding parameterizedDeclaringClass, MethodBinding originalMethod) {
        block24: {
            super(originalMethod.modifiers, originalMethod.selector, originalMethod.returnType, originalMethod.parameters, originalMethod.thrownExceptions, parameterizedDeclaringClass);
            this.originalMethod = originalMethod;
            this.tagBits = originalMethod.tagBits & 0xFFFFFFFFFFFFFF7FL;
            this.parameterNonNullness = originalMethod.parameterNonNullness;
            this.defaultNullness = originalMethod.defaultNullness;
            final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
            Substitution substitution = null;
            final int length = originalVariables.length;
            final boolean isStatic = originalMethod.isStatic();
            if (length == 0) {
                this.typeVariables = Binding.NO_TYPE_VARIABLES;
                if (!isStatic) {
                    substitution = parameterizedDeclaringClass;
                }
            } else {
                TypeVariableBinding originalVariable;
                final TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
                int i = 0;
                while (i < length) {
                    originalVariable = originalVariables[i];
                    substitutedVariables[i] = new TypeVariableBinding(originalVariable.sourceName, this, originalVariable.rank, parameterizedDeclaringClass.environment);
                    substitutedVariables[i].tagBits |= originalVariable.tagBits & 0x180000000100000L;
                    ++i;
                }
                this.typeVariables = substitutedVariables;
                substitution = new Substitution(){

                    @Override
                    public LookupEnvironment environment() {
                        return parameterizedDeclaringClass.environment;
                    }

                    @Override
                    public boolean isRawSubstitution() {
                        return !isStatic && parameterizedDeclaringClass.isRawSubstitution();
                    }

                    @Override
                    public TypeBinding substitute(TypeVariableBinding typeVariable) {
                        if (typeVariable.rank < length && TypeBinding.equalsEquals(originalVariables[typeVariable.rank], typeVariable)) {
                            TypeVariableBinding substitute = substitutedVariables[typeVariable.rank];
                            return typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType((TypeBinding)substitute, typeVariable.getTypeAnnotations()) : substitute;
                        }
                        if (!isStatic) {
                            return parameterizedDeclaringClass.substitute(typeVariable);
                        }
                        return typeVariable;
                    }
                };
                i = 0;
                while (i < length) {
                    originalVariable = originalVariables[i];
                    TypeVariableBinding substitutedVariable = substitutedVariables[i];
                    TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
                    ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
                    if (originalVariable.firstBound != null) {
                        TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                        substitutedVariable.setFirstBound(firstBound);
                    }
                    switch (substitutedSuperclass.kind()) {
                        case 68: {
                            substitutedVariable.setSuperClass(parameterizedDeclaringClass.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null));
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                        default: {
                            if (substitutedSuperclass.isInterface()) {
                                substitutedVariable.setSuperClass(parameterizedDeclaringClass.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                                int interfaceCount = substitutedInterfaces.length;
                                ReferenceBinding[] referenceBindingArray = substitutedInterfaces;
                                substitutedInterfaces = new ReferenceBinding[interfaceCount + 1];
                                System.arraycopy(referenceBindingArray, 0, substitutedInterfaces, 1, interfaceCount);
                                substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                                substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                                break;
                            }
                            substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        }
                    }
                    ++i;
                }
            }
            if (substitution != null) {
                this.returnType = Scope.substitute(substitution, this.returnType);
                this.parameters = Scope.substitute(substitution, this.parameters);
                this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
                if (this.thrownExceptions == null) {
                    this.thrownExceptions = Binding.NO_EXCEPTIONS;
                }
                if (parameterizedDeclaringClass.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                    long returnNullBits = NullAnnotationMatching.validNullTagBits(this.returnType.tagBits);
                    if (returnNullBits != 0L) {
                        this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        this.tagBits |= returnNullBits;
                    }
                    int parametersLen = this.parameters.length;
                    int i = 0;
                    while (i < parametersLen) {
                        long paramTagBits = NullAnnotationMatching.validNullTagBits(this.parameters[i].tagBits);
                        if (paramTagBits != 0L) {
                            if (this.parameterNonNullness == null) {
                                this.parameterNonNullness = new Boolean[parametersLen];
                            }
                            this.parameterNonNullness[i] = paramTagBits == 0x100000000000000L;
                        }
                        ++i;
                    }
                }
            }
            if ((this.tagBits & 0x80L) == 0L) {
                if ((this.returnType.tagBits & 0x80L) != 0L) {
                    this.tagBits |= 0x80L;
                } else {
                    int i = 0;
                    int max = this.parameters.length;
                    while (i < max) {
                        if ((this.parameters[i].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break block24;
                        }
                        ++i;
                    }
                    i = 0;
                    max = this.thrownExceptions.length;
                    while (i < max) {
                        if ((this.thrownExceptions[i].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break;
                        }
                        ++i;
                    }
                }
            }
        }
    }

    public ParameterizedMethodBinding(ReferenceBinding declaringClass, MethodBinding originalMethod, char[][] alternateParamaterNames, final LookupEnvironment environment) {
        block18: {
            super(originalMethod.modifiers, originalMethod.selector, originalMethod.returnType, originalMethod.parameters, originalMethod.thrownExceptions, declaringClass);
            this.originalMethod = originalMethod;
            this.tagBits = originalMethod.tagBits & 0xFFFFFFFFFFFFFF7FL;
            this.parameterNonNullness = originalMethod.parameterNonNullness;
            this.defaultNullness = originalMethod.defaultNullness;
            final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
            Substitution substitution = null;
            final int length = originalVariables.length;
            if (length == 0) {
                this.typeVariables = Binding.NO_TYPE_VARIABLES;
            } else {
                TypeVariableBinding originalVariable;
                final TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
                int i = 0;
                while (i < length) {
                    originalVariable = originalVariables[i];
                    substitutedVariables[i] = new TypeVariableBinding(alternateParamaterNames == null ? originalVariable.sourceName : alternateParamaterNames[i], this, originalVariable.rank, environment);
                    substitutedVariables[i].tagBits |= originalVariable.tagBits & 0x180000000100000L;
                    ++i;
                }
                this.typeVariables = substitutedVariables;
                substitution = new Substitution(){

                    @Override
                    public LookupEnvironment environment() {
                        return environment;
                    }

                    @Override
                    public boolean isRawSubstitution() {
                        return false;
                    }

                    @Override
                    public TypeBinding substitute(TypeVariableBinding typeVariable) {
                        if (typeVariable.rank < length && TypeBinding.equalsEquals(originalVariables[typeVariable.rank], typeVariable)) {
                            TypeVariableBinding substitute = substitutedVariables[typeVariable.rank];
                            return typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType((TypeBinding)substitute, typeVariable.getTypeAnnotations()) : substitute;
                        }
                        return typeVariable;
                    }
                };
                i = 0;
                while (i < length) {
                    originalVariable = originalVariables[i];
                    TypeVariableBinding substitutedVariable = substitutedVariables[i];
                    TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
                    ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
                    if (originalVariable.firstBound != null) {
                        TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                        substitutedVariable.setFirstBound(firstBound);
                    }
                    switch (substitutedSuperclass.kind()) {
                        case 68: {
                            substitutedVariable.setSuperClass(environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null));
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                        default: {
                            if (substitutedSuperclass.isInterface()) {
                                substitutedVariable.setSuperClass(environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                                int interfaceCount = substitutedInterfaces.length;
                                ReferenceBinding[] referenceBindingArray = substitutedInterfaces;
                                substitutedInterfaces = new ReferenceBinding[interfaceCount + 1];
                                System.arraycopy(referenceBindingArray, 0, substitutedInterfaces, 1, interfaceCount);
                                substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                                substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                                break;
                            }
                            substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        }
                    }
                    ++i;
                }
            }
            if (substitution != null) {
                this.returnType = Scope.substitute(substitution, this.returnType);
                this.parameters = Scope.substitute(substitution, this.parameters);
                this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
                if (this.thrownExceptions == null) {
                    this.thrownExceptions = Binding.NO_EXCEPTIONS;
                }
            }
            if ((this.tagBits & 0x80L) == 0L) {
                if ((this.returnType.tagBits & 0x80L) != 0L) {
                    this.tagBits |= 0x80L;
                } else {
                    int i = 0;
                    int max = this.parameters.length;
                    while (i < max) {
                        if ((this.parameters[i].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break block18;
                        }
                        ++i;
                    }
                    i = 0;
                    max = this.thrownExceptions.length;
                    while (i < max) {
                        if ((this.thrownExceptions[i].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break;
                        }
                        ++i;
                    }
                }
            }
        }
    }

    public ParameterizedMethodBinding() {
    }

    public static ParameterizedMethodBinding instantiateGetClass(TypeBinding receiverType, MethodBinding originalMethod, Scope scope) {
        ParameterizedMethodBinding method = new ParameterizedMethodBinding();
        method.modifiers = originalMethod.modifiers;
        method.selector = originalMethod.selector;
        method.declaringClass = originalMethod.declaringClass;
        method.typeVariables = Binding.NO_TYPE_VARIABLES;
        method.originalMethod = originalMethod;
        method.parameters = originalMethod.parameters;
        method.thrownExceptions = originalMethod.thrownExceptions;
        method.tagBits = originalMethod.tagBits;
        ReferenceBinding genericClassType = scope.getJavaLangClass();
        LookupEnvironment environment = scope.environment();
        TypeBinding rawType = environment.convertToRawType(receiverType.erasure(), false);
        if (environment.usesNullTypeAnnotations()) {
            rawType = environment.createAnnotatedType(rawType, new AnnotationBinding[]{environment.getNonNullAnnotation()});
        }
        method.returnType = environment.createParameterizedType(genericClassType, new TypeBinding[]{environment.createWildcard(genericClassType, 0, rawType, null, 1)}, null);
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (environment.usesNullTypeAnnotations()) {
                method.returnType = environment.createAnnotatedType(method.returnType, new AnnotationBinding[]{environment.getNonNullAnnotation()});
            } else {
                method.tagBits |= 0x100000000000000L;
            }
        }
        if ((method.returnType.tagBits & 0x80L) != 0L) {
            method.tagBits |= 0x80L;
        }
        return method;
    }

    @Override
    public boolean hasSubstitutedParameters() {
        return this.parameters != this.originalMethod.parameters;
    }

    @Override
    public boolean hasSubstitutedReturnType() {
        return this.returnType != this.originalMethod.returnType;
    }

    @Override
    public MethodBinding original() {
        return this.originalMethod.original();
    }

    @Override
    public MethodBinding shallowOriginal() {
        return this.originalMethod;
    }
}

