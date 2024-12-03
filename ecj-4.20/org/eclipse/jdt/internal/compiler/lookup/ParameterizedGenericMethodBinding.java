/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BoundSet;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceFailureException;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ParameterizedGenericMethodBinding
extends ParameterizedMethodBinding
implements Substitution {
    public TypeBinding[] typeArguments;
    protected LookupEnvironment environment;
    public boolean inferredReturnType;
    public boolean wasInferred;
    public boolean isRaw;
    private MethodBinding tiebreakMethod;
    public boolean inferredWithUncheckedConversion;
    public TypeBinding targetType;

    public static MethodBinding computeCompatibleMethod(MethodBinding originalMethod, TypeBinding[] arguments, Scope scope, InvocationSite invocationSite) {
        int length;
        ParameterizedGenericMethodBinding methodSubstitute;
        LookupEnvironment environment = scope.environment();
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(originalMethod, scope);
        }
        TypeVariableBinding[] typeVariables = originalMethod.typeVariables;
        TypeBinding[] substitutes = invocationSite.genericTypeArguments();
        InferenceContext inferenceContext = null;
        TypeBinding[] uncheckedArguments = null;
        if (substitutes != null) {
            if (substitutes.length != typeVariables.length) {
                return new ProblemMethodBinding(originalMethod, originalMethod.selector, substitutes, 11);
            }
            methodSubstitute = environment.createParameterizedGenericMethod(originalMethod, substitutes);
        } else {
            TypeBinding expectedType;
            TypeBinding[] parameters = originalMethod.parameters;
            CompilerOptions compilerOptions = scope.compilerOptions();
            if (compilerOptions.sourceLevel >= 0x340000L) {
                return ParameterizedGenericMethodBinding.computeCompatibleMethod18(originalMethod, arguments, scope, invocationSite);
            }
            inferenceContext = new InferenceContext(originalMethod);
            methodSubstitute = ParameterizedGenericMethodBinding.inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
            if (methodSubstitute == null) {
                return null;
            }
            if (inferenceContext.hasUnresolvedTypeArgument()) {
                if (inferenceContext.isUnchecked) {
                    length = inferenceContext.substitutes.length;
                    uncheckedArguments = new TypeBinding[length];
                    System.arraycopy(inferenceContext.substitutes, 0, uncheckedArguments, 0, length);
                }
                if (methodSubstitute.returnType != TypeBinding.VOID) {
                    TypeBinding expectedType2 = invocationSite.invocationTargetType();
                    if (expectedType2 != null) {
                        inferenceContext.hasExplicitExpectedType = true;
                    } else {
                        expectedType2 = scope.getJavaLangObject();
                    }
                    inferenceContext.expectedType = expectedType2;
                }
                if ((methodSubstitute = methodSubstitute.inferFromExpectedType(scope, inferenceContext)) == null) {
                    return null;
                }
            } else if (compilerOptions.sourceLevel == 0x330000L && methodSubstitute.returnType != TypeBinding.VOID && (expectedType = invocationSite.invocationTargetType()) != null && !originalMethod.returnType.mentionsAny(originalMethod.parameters, -1)) {
                TypeBinding uncaptured = methodSubstitute.returnType.uncapture(scope);
                if (!methodSubstitute.returnType.isCompatibleWith(expectedType) && expectedType.isCompatibleWith(uncaptured)) {
                    InferenceContext oldContext = inferenceContext;
                    inferenceContext = new InferenceContext(originalMethod);
                    originalMethod.returnType.collectSubstitutes(scope, expectedType, inferenceContext, 1);
                    ParameterizedGenericMethodBinding substitute = ParameterizedGenericMethodBinding.inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
                    if (substitute != null && substitute.returnType.isCompatibleWith(expectedType)) {
                        if (scope.parameterCompatibilityLevel((MethodBinding)substitute, arguments, false) > -1) {
                            methodSubstitute = substitute;
                        } else {
                            inferenceContext = oldContext;
                        }
                    } else {
                        inferenceContext = oldContext;
                    }
                }
            }
        }
        Substitution substitution = null;
        substitution = inferenceContext != null ? new LingeringTypeVariableEliminator(typeVariables, inferenceContext.substitutes, scope) : methodSubstitute;
        int i = 0;
        length = typeVariables.length;
        while (i < length) {
            TypeVariableBinding typeVariable = typeVariables[i];
            TypeBinding substitute = methodSubstitute.typeArguments[i];
            TypeBinding substituteForChecks = substitute instanceof TypeVariableBinding ? substitute : Scope.substitute((Substitution)new LingeringTypeVariableEliminator(typeVariables, null, scope), substitute);
            if (uncheckedArguments == null || uncheckedArguments[i] != null) {
                switch (typeVariable.boundCheck(substitution, substituteForChecks, scope, null)) {
                    case MISMATCH: {
                        int argLength = arguments.length;
                        TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2];
                        System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
                        augmentedArguments[argLength] = substitute;
                        augmentedArguments[argLength + 1] = typeVariable;
                        return new ProblemMethodBinding(methodSubstitute, originalMethod.selector, augmentedArguments, 10);
                    }
                    case UNCHECKED: {
                        methodSubstitute.tagBits |= 0x100L;
                        break;
                    }
                }
            }
            ++i;
        }
        return methodSubstitute;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static MethodBinding computeCompatibleMethod18(MethodBinding originalMethod, TypeBinding[] arguments, Scope scope, InvocationSite invocationSite) {
        TypeBinding[] typeVariables = originalMethod.typeVariables;
        if (invocationSite.checkingPotentialCompatibility()) {
            return scope.environment().createParameterizedGenericMethod(originalMethod, typeVariables);
        }
        ParameterizedGenericMethodBinding methodSubstitute = null;
        InferenceContext18 infCtx18 = invocationSite.freshInferenceContext(scope);
        if (infCtx18 == null) {
            return originalMethod;
        }
        TypeBinding[] parameters = originalMethod.parameters;
        CompilerOptions compilerOptions = scope.compilerOptions();
        boolean invocationTypeInferred = false;
        boolean requireBoxing = false;
        boolean allArgumentsAreProper = true;
        TypeBinding[] argumentsCopy = new TypeBinding[arguments.length];
        int i = 0;
        int length = arguments.length;
        int parametersLength = parameters.length;
        while (i < length) {
            TypeBinding parameter = i < parametersLength ? parameters[i] : parameters[parametersLength - 1];
            TypeBinding argument = arguments[i];
            allArgumentsAreProper &= argument.isProperType(true);
            if (argument.isPrimitiveType() != parameter.isPrimitiveType()) {
                argumentsCopy[i] = scope.environment().computeBoxingType(argument);
                requireBoxing = true;
            } else {
                argumentsCopy[i] = argument;
            }
            ++i;
        }
        arguments = argumentsCopy;
        LookupEnvironment environment = scope.environment();
        InferenceContext18 previousContext = environment.currentInferenceContext;
        if (previousContext == null) {
            environment.currentInferenceContext = infCtx18;
        }
        try {
            boolean isDiamond;
            BoundSet provisionalResult = null;
            BoundSet result = null;
            boolean isPolyExpression = invocationSite instanceof Expression && ((Expression)((Object)invocationSite)).isTrulyExpression() && ((Expression)((Object)invocationSite)).isPolyExpression(originalMethod);
            boolean bl = isDiamond = isPolyExpression && originalMethod.isConstructor();
            if (arguments.length == parameters.length) {
                infCtx18.inferenceKind = requireBoxing ? 2 : 1;
                infCtx18.inferInvocationApplicability(originalMethod, arguments, isDiamond);
                result = infCtx18.solve(true);
            }
            if (result == null && originalMethod.isVarargs()) {
                infCtx18 = invocationSite.freshInferenceContext(scope);
                infCtx18.inferenceKind = 3;
                infCtx18.inferInvocationApplicability(originalMethod, arguments, isDiamond);
                result = infCtx18.solve(true);
            }
            if (result == null) {
                return null;
            }
            if (!infCtx18.isResolved(result)) return null;
            infCtx18.stepCompleted = 1;
            TypeBinding expectedType = invocationSite.invocationTargetType();
            boolean hasReturnProblem = false;
            if (expectedType != null || !invocationSite.getExpressionContext().definesTargetType() || !isPolyExpression) {
                provisionalResult = result;
                result = infCtx18.inferInvocationType(expectedType, invocationSite, originalMethod);
                invocationTypeInferred = infCtx18.stepCompleted == 3;
                if (hasReturnProblem |= result == null) {
                    result = provisionalResult;
                }
            }
            if (result == null) return null;
            TypeBinding[] solutions = infCtx18.getSolutions((TypeVariableBinding[])typeVariables, invocationSite, result);
            if (solutions == null) return null;
            methodSubstitute = scope.environment().createParameterizedGenericMethod(originalMethod, solutions, infCtx18.usesUncheckedConversion, hasReturnProblem, expectedType);
            if (invocationSite instanceof Invocation && allArgumentsAreProper && (expectedType == null || expectedType.isProperType(true))) {
                infCtx18.forwardResults(result, (Invocation)invocationSite, methodSubstitute, expectedType);
            }
            try {
                MethodBinding problemMethod;
                if (hasReturnProblem && (problemMethod = infCtx18.getReturnProblemMethodIfNeeded(expectedType, methodSubstitute)) instanceof ProblemMethodBinding) {
                    MethodBinding methodBinding = problemMethod;
                    return methodBinding;
                }
                if (invocationTypeInferred) {
                    if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                        NullAnnotationMatching.checkForContradictions(methodSubstitute, invocationSite, scope);
                    }
                    if ((problemMethod = methodSubstitute.boundCheck18(scope, arguments, invocationSite)) != null) {
                        MethodBinding methodBinding = problemMethod;
                        return methodBinding;
                    }
                } else {
                    methodSubstitute = new PolyParameterizedGenericMethodBinding(methodSubstitute);
                }
            }
            finally {
                if (allArgumentsAreProper) {
                    if (invocationSite instanceof Invocation) {
                        ((Invocation)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                    } else if (invocationSite instanceof ReferenceExpression) {
                        ((ReferenceExpression)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                    }
                }
            }
            ParameterizedGenericMethodBinding parameterizedGenericMethodBinding = methodSubstitute;
            return parameterizedGenericMethodBinding;
        }
        catch (InferenceFailureException e) {
            scope.problemReporter().genericInferenceError(e.getMessage(), invocationSite);
            return null;
        }
        finally {
            environment.currentInferenceContext = previousContext;
        }
    }

    MethodBinding boundCheck18(Scope scope, TypeBinding[] arguments, InvocationSite site) {
        ParameterizedGenericMethodBinding substitution = this;
        ParameterizedGenericMethodBinding methodSubstitute = this;
        TypeVariableBinding[] originalTypeVariables = this.originalMethod.typeVariables;
        int i = 0;
        int length = originalTypeVariables.length;
        while (i < length) {
            TypeVariableBinding typeVariable = originalTypeVariables[i];
            TypeBinding substitute = methodSubstitute.typeArguments[i];
            ASTNode location = site instanceof ASTNode ? (ASTNode)((Object)site) : null;
            switch (typeVariable.boundCheck(substitution, substitute, scope, location)) {
                case MISMATCH: {
                    int argLength = arguments.length;
                    TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2];
                    System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
                    augmentedArguments[argLength] = substitute;
                    augmentedArguments[argLength + 1] = typeVariable;
                    return new ProblemMethodBinding(methodSubstitute, this.originalMethod.selector, augmentedArguments, 10);
                }
                case UNCHECKED: {
                    methodSubstitute.tagBits |= 0x100L;
                    break;
                }
            }
            ++i;
        }
        return null;
    }

    /*
     * Unable to fully structure code
     */
    private static ParameterizedGenericMethodBinding inferFromArgumentTypes(Scope scope, MethodBinding originalMethod, TypeBinding[] arguments, TypeBinding[] parameters, InferenceContext inferenceContext) {
        block17: {
            block16: {
                block19: {
                    block18: {
                        if (!originalMethod.isVarargs()) break block16;
                        paramLength = parameters.length;
                        minArgLength = paramLength - 1;
                        argLength = arguments.length;
                        i = 0;
                        while (i < minArgLength) {
                            parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, 1);
                            if (inferenceContext.status == 1) {
                                return null;
                            }
                            ++i;
                        }
                        if (minArgLength >= argLength) break block17;
                        varargType = parameters[minArgLength];
                        lastArgument = arguments[minArgLength];
                        if (paramLength != argLength) break block18;
                        if (lastArgument == TypeBinding.NULL) break block19;
                        switch (lastArgument.dimensions()) {
                            case 0: {
                                ** break;
                            }
                            case 1: {
                                if (lastArgument.leafComponentType().isBaseType()) ** break;
                                break block19;
                            }
                            default: {
                                break block19;
                            }
                        }
                    }
                    varargType = ((ArrayBinding)varargType).elementsType();
                }
                i = minArgLength;
                while (i < argLength) {
                    varargType.collectSubstitutes(scope, arguments[i], inferenceContext, 1);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                    ++i;
                }
                break block17;
            }
            paramLength = parameters.length;
            i = 0;
            while (i < paramLength) {
                parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, 1);
                if (inferenceContext.status == 1) {
                    return null;
                }
                ++i;
            }
        }
        if (!ParameterizedGenericMethodBinding.resolveSubstituteConstraints(scope, originalVariables = originalMethod.typeVariables, inferenceContext, false)) {
            return null;
        }
        actualSubstitutes = inferredSustitutes = inferenceContext.substitutes;
        i = 0;
        varLength = originalVariables.length;
        while (i < varLength) {
            if (inferredSustitutes[i] == null) {
                if (actualSubstitutes == inferredSustitutes) {
                    actualSubstitutes = new TypeBinding[varLength];
                    System.arraycopy(inferredSustitutes, 0, actualSubstitutes, 0, i);
                }
                actualSubstitutes[i] = originalVariables[i];
            } else if (actualSubstitutes != inferredSustitutes) {
                actualSubstitutes[i] = inferredSustitutes[i];
            }
            ++i;
        }
        paramMethod = scope.environment().createParameterizedGenericMethod(originalMethod, actualSubstitutes);
        return paramMethod;
    }

    private static boolean resolveSubstituteConstraints(Scope scope, TypeVariableBinding[] typeVariables, InferenceContext inferenceContext, boolean considerEXTENDSConstraints) {
        TypeBinding[] bounds;
        TypeBinding substitute;
        TypeVariableBinding current;
        TypeBinding[] substitutes = inferenceContext.substitutes;
        int varLength = typeVariables.length;
        int i = 0;
        while (i < varLength) {
            TypeBinding[] equalSubstitutes;
            current = typeVariables[i];
            substitute = substitutes[i];
            if (substitute == null && (equalSubstitutes = inferenceContext.getSubstitutes(current, 0)) != null) {
                int j = 0;
                int equalLength = equalSubstitutes.length;
                block1: while (j < equalLength) {
                    TypeBinding equalSubstitute = equalSubstitutes[j];
                    if (equalSubstitute != null) {
                        if (TypeBinding.equalsEquals(equalSubstitute, current)) {
                            int k = j + 1;
                            while (k < equalLength) {
                                equalSubstitute = equalSubstitutes[k];
                                if (TypeBinding.notEquals(equalSubstitute, current) && equalSubstitute != null) {
                                    substitutes[i] = equalSubstitute;
                                    break block1;
                                }
                                ++k;
                            }
                            substitutes[i] = current;
                            break;
                        }
                        substitutes[i] = equalSubstitute;
                        break;
                    }
                    ++j;
                }
            }
            ++i;
        }
        if (inferenceContext.hasUnresolvedTypeArgument()) {
            i = 0;
            while (i < varLength) {
                current = typeVariables[i];
                substitute = substitutes[i];
                if (substitute == null && (bounds = inferenceContext.getSubstitutes(current, 2)) != null) {
                    TypeBinding mostSpecificSubstitute = scope.lowerUpperBound(bounds);
                    if (mostSpecificSubstitute == null) {
                        return false;
                    }
                    if (mostSpecificSubstitute != TypeBinding.VOID) {
                        substitutes[i] = mostSpecificSubstitute;
                    }
                }
                ++i;
            }
        }
        if (considerEXTENDSConstraints && inferenceContext.hasUnresolvedTypeArgument()) {
            i = 0;
            while (i < varLength) {
                current = typeVariables[i];
                substitute = substitutes[i];
                if (substitute == null && (bounds = inferenceContext.getSubstitutes(current, 1)) != null) {
                    TypeBinding[] glb = Scope.greaterLowerBound(bounds, scope, scope.environment());
                    TypeBinding mostSpecificSubstitute = null;
                    if (glb != null) {
                        if (glb.length == 1) {
                            mostSpecificSubstitute = glb[0];
                        } else {
                            TypeBinding[] otherBounds = new TypeBinding[glb.length - 1];
                            System.arraycopy(glb, 1, otherBounds, 0, glb.length - 1);
                            mostSpecificSubstitute = scope.environment().createWildcard(null, 0, glb[0], otherBounds, 1);
                        }
                    }
                    if (mostSpecificSubstitute != null) {
                        substitutes[i] = mostSpecificSubstitute;
                    }
                }
                ++i;
            }
        }
        return true;
    }

    public ParameterizedGenericMethodBinding(MethodBinding originalMethod, RawTypeBinding rawType, LookupEnvironment environment) {
        TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
        int length = originalVariables.length;
        TypeBinding[] rawArguments = new TypeBinding[length];
        int i = 0;
        while (i < length) {
            rawArguments[i] = environment.convertToRawType(originalVariables[i].erasure(), false);
            ++i;
        }
        this.isRaw = true;
        this.tagBits = originalMethod.tagBits;
        this.environment = environment;
        this.modifiers = originalMethod.modifiers;
        this.selector = originalMethod.selector;
        this.declaringClass = rawType == null ? originalMethod.declaringClass : rawType;
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeArguments = rawArguments;
        this.originalMethod = originalMethod;
        boolean ignoreRawTypeSubstitution = rawType == null || originalMethod.isStatic();
        this.parameters = Scope.substitute((Substitution)this, ignoreRawTypeSubstitution ? originalMethod.parameters : Scope.substitute((Substitution)rawType, originalMethod.parameters));
        this.thrownExceptions = Scope.substitute((Substitution)this, ignoreRawTypeSubstitution ? originalMethod.thrownExceptions : Scope.substitute((Substitution)rawType, originalMethod.thrownExceptions));
        if (this.thrownExceptions == null) {
            this.thrownExceptions = Binding.NO_EXCEPTIONS;
        }
        this.returnType = Scope.substitute((Substitution)this, ignoreRawTypeSubstitution ? originalMethod.returnType : Scope.substitute((Substitution)rawType, originalMethod.returnType));
        this.wasInferred = false;
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
    }

    public ParameterizedGenericMethodBinding(MethodBinding originalMethod, TypeBinding[] typeArguments, LookupEnvironment environment, boolean inferredWithUncheckConversion, boolean hasReturnProblem, TypeBinding targetType) {
        block15: {
            int i;
            this.environment = environment;
            this.inferredWithUncheckedConversion = inferredWithUncheckConversion;
            this.targetType = targetType;
            this.modifiers = originalMethod.modifiers;
            this.selector = originalMethod.selector;
            this.declaringClass = originalMethod.declaringClass;
            if (inferredWithUncheckConversion && originalMethod.isConstructor() && this.declaringClass.isParameterizedType()) {
                this.declaringClass = (ReferenceBinding)environment.convertToRawType(this.declaringClass.erasure(), false);
            }
            this.typeVariables = Binding.NO_TYPE_VARIABLES;
            this.typeArguments = typeArguments;
            this.isRaw = false;
            this.tagBits = originalMethod.tagBits;
            this.originalMethod = originalMethod;
            this.parameters = Scope.substitute((Substitution)this, originalMethod.parameters);
            if (inferredWithUncheckConversion) {
                this.returnType = this.getErasure18_5_2(originalMethod.returnType, environment, hasReturnProblem);
                this.thrownExceptions = new ReferenceBinding[originalMethod.thrownExceptions.length];
                i = 0;
                while (i < originalMethod.thrownExceptions.length) {
                    this.thrownExceptions[i] = (ReferenceBinding)this.getErasure18_5_2(originalMethod.thrownExceptions[i], environment, false);
                    ++i;
                }
            } else {
                this.returnType = Scope.substitute((Substitution)this, originalMethod.returnType);
                this.thrownExceptions = Scope.substitute((Substitution)this, originalMethod.thrownExceptions);
            }
            if (this.thrownExceptions == null) {
                this.thrownExceptions = Binding.NO_EXCEPTIONS;
            }
            if ((this.tagBits & 0x80L) == 0L) {
                if ((this.returnType.tagBits & 0x80L) != 0L) {
                    this.tagBits |= 0x80L;
                } else {
                    i = 0;
                    int max = this.parameters.length;
                    while (i < max) {
                        if ((this.parameters[i].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break block15;
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
        this.wasInferred = true;
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
        int len = this.parameters.length;
        int i = 0;
        while (i < len) {
            long nullBits;
            if (this.parameters[i] == TypeBinding.NULL && (nullBits = originalMethod.parameters[i].tagBits & 0x180000000000000L) == 0x100000000000000L) {
                if (this.parameterNonNullness == null) {
                    this.parameterNonNullness = new Boolean[len];
                }
                this.parameterNonNullness[i] = Boolean.TRUE;
            }
            ++i;
        }
    }

    TypeBinding getErasure18_5_2(TypeBinding type, LookupEnvironment env, boolean substitute) {
        if (substitute) {
            type = Scope.substitute((Substitution)this, type);
        }
        return env.convertToRawType(type.erasure(), true);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.originalMethod.computeUniqueKey(false));
        buffer.append('%');
        buffer.append('<');
        if (!this.isRaw) {
            int length = this.typeArguments.length;
            int i = 0;
            while (i < length) {
                TypeBinding typeArgument = this.typeArguments[i];
                buffer.append(typeArgument.computeUniqueKey(false));
                ++i;
            }
        }
        buffer.append('>');
        int resultLength = buffer.length();
        char[] result = new char[resultLength];
        buffer.getChars(0, resultLength, result, 0);
        return result;
    }

    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }

    @Override
    public boolean hasSubstitutedParameters() {
        if (this.wasInferred) {
            return this.originalMethod.hasSubstitutedParameters();
        }
        return super.hasSubstitutedParameters();
    }

    @Override
    public boolean hasSubstitutedReturnType() {
        if (this.inferredReturnType) {
            return this.originalMethod.hasSubstitutedReturnType();
        }
        return super.hasSubstitutedReturnType();
    }

    private ParameterizedGenericMethodBinding inferFromExpectedType(Scope scope, InferenceContext inferenceContext) {
        block21: {
            TypeVariableBinding[] originalVariables = this.originalMethod.typeVariables;
            int varLength = originalVariables.length;
            if (inferenceContext.expectedType != null) {
                this.returnType.collectSubstitutes(scope, inferenceContext.expectedType, inferenceContext, 2);
                if (inferenceContext.status == 1) {
                    return null;
                }
            }
            int i = 0;
            while (i < varLength) {
                TypeVariableBinding originalVariable = originalVariables[i];
                TypeBinding argument = this.typeArguments[i];
                boolean argAlreadyInferred = TypeBinding.notEquals(argument, originalVariable);
                if (TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass)) {
                    TypeBinding substitutedBound = Scope.substitute((Substitution)this, originalVariable.superclass);
                    argument.collectSubstitutes(scope, substitutedBound, inferenceContext, 2);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                    if (argAlreadyInferred) {
                        substitutedBound.collectSubstitutes(scope, argument, inferenceContext, 1);
                        if (inferenceContext.status == 1) {
                            return null;
                        }
                    }
                }
                int j = 0;
                int max = originalVariable.superInterfaces.length;
                while (j < max) {
                    TypeBinding substitutedBound = Scope.substitute((Substitution)this, originalVariable.superInterfaces[j]);
                    argument.collectSubstitutes(scope, substitutedBound, inferenceContext, 2);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                    if (argAlreadyInferred) {
                        substitutedBound.collectSubstitutes(scope, argument, inferenceContext, 1);
                        if (inferenceContext.status == 1) {
                            return null;
                        }
                    }
                    ++j;
                }
                ++i;
            }
            if (!ParameterizedGenericMethodBinding.resolveSubstituteConstraints(scope, originalVariables, inferenceContext, true)) {
                return null;
            }
            i = 0;
            while (i < varLength) {
                TypeBinding substitute = inferenceContext.substitutes[i];
                this.typeArguments[i] = substitute != null ? substitute : (inferenceContext.substitutes[i] = originalVariables[i].upperBound());
                ++i;
            }
            this.typeArguments = Scope.substitute((Substitution)this, this.typeArguments);
            TypeBinding oldReturnType = this.returnType;
            this.returnType = Scope.substitute((Substitution)this, this.returnType);
            this.inferredReturnType = inferenceContext.hasExplicitExpectedType && TypeBinding.notEquals(this.returnType, oldReturnType);
            this.parameters = Scope.substitute((Substitution)this, this.parameters);
            this.thrownExceptions = Scope.substitute((Substitution)this, this.thrownExceptions);
            if (this.thrownExceptions == null) {
                this.thrownExceptions = Binding.NO_EXCEPTIONS;
            }
            if ((this.tagBits & 0x80L) == 0L) {
                if ((this.returnType.tagBits & 0x80L) != 0L) {
                    this.tagBits |= 0x80L;
                } else {
                    int i2 = 0;
                    int max = this.parameters.length;
                    while (i2 < max) {
                        if ((this.parameters[i2].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break block21;
                        }
                        ++i2;
                    }
                    i2 = 0;
                    max = this.thrownExceptions.length;
                    while (i2 < max) {
                        if ((this.thrownExceptions[i2].tagBits & 0x80L) != 0L) {
                            this.tagBits |= 0x80L;
                            break;
                        }
                        ++i2;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public boolean isParameterizedGeneric() {
        return true;
    }

    @Override
    public boolean isRawSubstitution() {
        return this.isRaw;
    }

    @Override
    public TypeBinding substitute(TypeVariableBinding originalVariable) {
        TypeVariableBinding[] variables = this.originalMethod.typeVariables;
        int length = variables.length;
        if (originalVariable.rank < length && TypeBinding.equalsEquals(variables[originalVariable.rank], originalVariable)) {
            TypeBinding substitute = this.typeArguments[originalVariable.rank];
            return originalVariable.combineTypeAnnotations(substitute);
        }
        return originalVariable;
    }

    @Override
    public MethodBinding tiebreakMethod() {
        if (this.tiebreakMethod == null) {
            this.tiebreakMethod = this.originalMethod.asRawMethod(this.environment);
        }
        return this.tiebreakMethod;
    }

    @Override
    public MethodBinding genericMethod() {
        if (this.isRaw) {
            return this;
        }
        return this.originalMethod;
    }

    private static class LingeringTypeVariableEliminator
    implements Substitution {
        private final TypeVariableBinding[] variables;
        private final TypeBinding[] substitutes;
        private final Scope scope;

        public LingeringTypeVariableEliminator(TypeVariableBinding[] variables, TypeBinding[] substitutes, Scope scope) {
            this.variables = variables;
            this.substitutes = substitutes;
            this.scope = scope;
        }

        @Override
        public TypeBinding substitute(TypeVariableBinding typeVariable) {
            if (typeVariable.rank >= this.variables.length || TypeBinding.notEquals(this.variables[typeVariable.rank], typeVariable)) {
                return typeVariable;
            }
            if (this.substitutes != null) {
                return Scope.substitute((Substitution)new LingeringTypeVariableEliminator(this.variables, null, this.scope), this.substitutes[typeVariable.rank]);
            }
            ReferenceBinding genericType = (ReferenceBinding)(typeVariable.declaringElement instanceof ReferenceBinding ? typeVariable.declaringElement : null);
            return this.scope.environment().createWildcard(genericType, typeVariable.rank, null, null, 0, typeVariable.getTypeAnnotations());
        }

        @Override
        public LookupEnvironment environment() {
            return this.scope.environment();
        }

        @Override
        public boolean isRawSubstitution() {
            return false;
        }
    }
}

