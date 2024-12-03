/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticFactoryMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class AllocationExpression
extends Expression
implements IPolyExpression,
Invocation {
    public TypeReference type;
    public Expression[] arguments;
    public MethodBinding binding;
    MethodBinding syntheticAccessor;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public FieldDeclaration enumConstant;
    protected TypeBinding typeExpected;
    public boolean inferredReturnType;
    public FakedTrackingVariable closeTracker;
    public ExpressionContext expressionContext = ExpressionContext.VANILLA_CONTEXT;
    private SimpleLookupTable inferenceContexts;
    public HashMap<TypeBinding, MethodBinding> solutionsPerTargetType;
    private InferenceContext18 outerInferenceContext;
    public boolean argsContainCast;
    public TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
    public boolean argumentsHaveErrors = false;

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        this.checkCapturedLocalInitializationIfNecessary((ReferenceBinding)this.binding.declaringClass.erasure(), currentScope, flowInfo);
        if (this.arguments != null) {
            boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
            boolean hasResourceWrapperType = analyseResources && this.resolvedType instanceof ReferenceBinding && ((ReferenceBinding)this.resolvedType).hasTypeBit(4);
            int i = 0;
            int count = this.arguments.length;
            while (i < count) {
                flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                if (analyseResources && !hasResourceWrapperType) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                }
                this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                ++i;
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
        }
        TypeBinding[] thrownExceptions = this.binding.thrownExceptions;
        if (this.binding.thrownExceptions.length != 0) {
            if ((this.bits & 0x10000) != 0 && this.genericTypeArguments == null) {
                thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
            }
            flowContext.checkExceptionHandlers(thrownExceptions, (ASTNode)this, (FlowInfo)flowInfo.unconditionalCopy(), currentScope);
        }
        if (currentScope.compilerOptions().analyseResourceLeaks && FakedTrackingVariable.isAnyCloseable(this.resolvedType)) {
            FakedTrackingVariable.analyseCloseableAllocation(currentScope, flowInfo, this);
        }
        ReferenceBinding declaringClass = this.binding.declaringClass;
        MethodScope methodScope = currentScope.methodScope();
        if (declaringClass.isMemberType() && !declaringClass.isStatic() || declaringClass.isLocalType() && !methodScope.isStatic && methodScope.isLambdaScope()) {
            currentScope.tagAsAccessingEnclosingInstanceStateOf(this.binding.declaringClass.enclosingType(), false);
        }
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        return flowInfo;
    }

    public void checkCapturedLocalInitializationIfNecessary(ReferenceBinding checkedType, BlockScope currentScope, FlowInfo flowInfo) {
        NestedTypeBinding nestedType;
        SyntheticArgumentBinding[] syntheticArguments;
        if ((checkedType.tagBits & 0x834L) == 2068L && !currentScope.isDefinedInType(checkedType) && (syntheticArguments = (nestedType = (NestedTypeBinding)checkedType).syntheticOuterLocalVariables()) != null) {
            int i = 0;
            int count = syntheticArguments.length;
            while (i < count) {
                SyntheticArgumentBinding syntheticArgument = syntheticArguments[i];
                LocalVariableBinding targetLocal = syntheticArgument.actualOuterLocalVariable;
                if (targetLocal != null && targetLocal.declaration != null && !flowInfo.isDefinitelyAssigned(targetLocal)) {
                    currentScope.problemReporter().uninitializedLocalVariable(targetLocal, this, currentScope);
                }
                ++i;
            }
        }
    }

    public Expression enclosingInstance() {
        return null;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        boolean isUnboxing;
        this.cleanUpInferenceContexts();
        if (!valueRequired) {
            currentScope.problemReporter().unusedObjectAllocation(this);
        }
        int pc = codeStream.position;
        MethodBinding codegenBinding = this.binding.original();
        ReferenceBinding allocatedType = codegenBinding.declaringClass;
        codeStream.new_(this.type, allocatedType);
        boolean bl = isUnboxing = (this.implicitConversion & 0x400) != 0;
        if (valueRequired || isUnboxing) {
            codeStream.dup();
        }
        if (this.type != null) {
            codeStream.recordPositionsFrom(pc, this.type.sourceStart);
        } else {
            codeStream.ldc(String.valueOf(this.enumConstant.name));
            codeStream.generateInlinedValue(this.enumConstant.binding.id);
        }
        if (allocatedType.isNestedType()) {
            codeStream.generateSyntheticEnclosingInstanceValues(currentScope, allocatedType, this.enclosingInstance(), this);
        }
        this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
        if (allocatedType.isNestedType()) {
            codeStream.generateSyntheticOuterArgumentValues(currentScope, allocatedType, this);
        }
        if (this.syntheticAccessor == null) {
            codeStream.invoke((byte)-73, codegenBinding, null, this.typeArguments);
        } else {
            int i = 0;
            int max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
            while (i < max) {
                codeStream.aconst_null();
                ++i;
            }
            codeStream.invoke((byte)-73, this.syntheticAccessor, null, this.typeArguments);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else if (isUnboxing) {
            codeStream.generateImplicitConversion(this.implicitConversion);
            switch (this.postConversionType((Scope)currentScope).id) {
                case 7: 
                case 8: {
                    codeStream.pop2();
                    break;
                }
                default: {
                    codeStream.pop();
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }

    @Override
    public boolean isSuperAccess() {
        return false;
    }

    @Override
    public boolean isTypeAccess() {
        return true;
    }

    public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0) {
            return;
        }
        ReferenceBinding allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
        if (allocatedTypeErasure.isNestedType() && (currentScope.enclosingSourceType().isLocalType() || currentScope.isLambdaSubscope())) {
            if (allocatedTypeErasure.isLocalType()) {
                ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, false);
            } else {
                currentScope.propagateInnerEmulation(allocatedTypeErasure, false);
            }
        }
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0) {
            return;
        }
        MethodBinding codegenBinding = this.binding.original();
        if (codegenBinding.isPrivate() && !currentScope.enclosingSourceType().isNestmateOf(this.binding.declaringClass)) {
            ReferenceBinding declaringClass = codegenBinding.declaringClass;
            if (TypeBinding.notEquals(currentScope.enclosingSourceType(), declaringClass)) {
                if ((declaringClass.tagBits & 0x10L) != 0L && currentScope.compilerOptions().complianceLevel >= 0x300000L) {
                    codegenBinding.tagBits |= 0x200L;
                } else {
                    this.syntheticAccessor = ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenBinding, this.isSuperAccess());
                    currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                }
            }
        }
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.type != null) {
            output.append("new ");
        }
        if (this.typeArguments != null) {
            output.append('<');
            int max = this.typeArguments.length - 1;
            int j = 0;
            while (j < max) {
                this.typeArguments[j].print(0, output);
                output.append(", ");
                ++j;
            }
            this.typeArguments[max].print(0, output);
            output.append('>');
        }
        if (this.type != null) {
            this.type.printExpression(0, output);
        }
        output.append('(');
        if (this.arguments != null) {
            int i = 0;
            while (i < this.arguments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
                ++i;
            }
        }
        return output.append(')');
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        boolean isDiamond = this.type != null && (this.type.bits & 0x80000) != 0;
        CompilerOptions compilerOptions = scope.compilerOptions();
        long sourceLevel = compilerOptions.sourceLevel;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            this.resolvedType = this.type == null ? scope.enclosingReceiverType() : this.type.resolveType(scope, true);
            if (this.type != null) {
                this.checkIllegalNullAnnotation(scope, this.resolvedType);
                if (this.type instanceof ParameterizedQualifiedTypeReference) {
                    ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
                    if (currentType == null) {
                        return currentType;
                    }
                    block0: while ((currentType.modifiers & 8) == 0 && !currentType.isRawType()) {
                        if ((currentType = currentType.enclosingType()) != null) continue;
                        ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
                        int i = qRef.typeArguments.length - 2;
                        while (i >= 0) {
                            if (qRef.typeArguments[i] != null) {
                                scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, this.resolvedType);
                                break block0;
                            }
                            --i;
                        }
                        break block0;
                    }
                }
            }
            if (this.typeArguments != null) {
                int length = this.typeArguments.length;
                this.argumentsHaveErrors = sourceLevel < 0x310000L;
                this.genericTypeArguments = new TypeBinding[length];
                int i = 0;
                while (i < length) {
                    TypeReference typeReference = this.typeArguments[i];
                    this.genericTypeArguments[i] = typeReference.resolveType(scope, true);
                    if (this.genericTypeArguments[i] == null) {
                        this.argumentsHaveErrors = true;
                    }
                    if (this.argumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                    ++i;
                }
                if (isDiamond) {
                    scope.problemReporter().diamondNotWithExplicitTypeArguments(this.typeArguments);
                    return null;
                }
                if (this.argumentsHaveErrors) {
                    if (this.arguments != null) {
                        i = 0;
                        int max = this.arguments.length;
                        while (i < max) {
                            this.arguments[i].resolveType(scope);
                            ++i;
                        }
                    }
                    return null;
                }
            }
            if (this.arguments != null) {
                this.argumentsHaveErrors = false;
                int length = this.arguments.length;
                this.argumentTypes = new TypeBinding[length];
                int i = 0;
                while (i < length) {
                    Expression argument = this.arguments[i];
                    if (argument instanceof CastExpression) {
                        argument.bits |= 0x20;
                        this.argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    if (this.arguments[i].resolvedType != null) {
                        scope.problemReporter().genericInferenceError("Argument was unexpectedly found resolved", this);
                    }
                    if ((this.argumentTypes[i] = argument.resolveType(scope)) == null) {
                        this.argumentsHaveErrors = true;
                    }
                    ++i;
                }
                if (this.argumentsHaveErrors) {
                    if (isDiamond) {
                        return null;
                    }
                    if (this.resolvedType instanceof ReferenceBinding) {
                        MethodBinding closestMatch;
                        TypeBinding[] pseudoArgs = new TypeBinding[length];
                        int i2 = length;
                        while (--i2 >= 0) {
                            TypeBinding typeBinding = pseudoArgs[i2] = this.argumentTypes[i2] == null ? TypeBinding.NULL : this.argumentTypes[i2];
                        }
                        this.binding = scope.findMethod((ReferenceBinding)this.resolvedType, TypeConstants.INIT, pseudoArgs, this, false);
                        if (this.binding != null && !this.binding.isValidBinding() && (closestMatch = ((ProblemMethodBinding)this.binding).closestMatch) != null) {
                            if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES) {
                                closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), (RawTypeBinding)null);
                            }
                            this.binding = closestMatch;
                            MethodBinding closestMatchOriginal = closestMatch.original();
                            if (closestMatchOriginal.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal)) {
                                closestMatchOriginal.modifiers |= 0x8000000;
                            }
                        }
                    }
                    return this.resolvedType;
                }
            }
            if (this.resolvedType == null || !this.resolvedType.isValidBinding()) {
                return null;
            }
            if (this.type != null && !this.resolvedType.canBeInstantiated()) {
                scope.problemReporter().cannotInstantiate(this.type, this.resolvedType);
                return this.resolvedType;
            }
        }
        if (isDiamond) {
            TypeBinding lastArg;
            this.binding = this.inferConstructorOfElidedParameterizedType(scope);
            if (this.binding == null || !this.binding.isValidBinding()) {
                scope.problemReporter().cannotInferElidedTypes(this);
                this.resolvedType = null;
                return null;
            }
            if (this.typeExpected == null && compilerOptions.sourceLevel >= 0x340000L && this.expressionContext.definesTargetType()) {
                return new PolyTypeBinding(this);
            }
            this.resolvedType = this.type.resolvedType = this.binding.declaringClass;
            if (this.binding.isVarargs() && !(lastArg = this.binding.parameters[this.binding.parameters.length - 1].leafComponentType()).erasure().canBeSeenBy(scope)) {
                scope.problemReporter().invalidType(this, new ProblemReferenceBinding(new char[][]{lastArg.readableName()}, (ReferenceBinding)lastArg, 2));
                this.resolvedType = null;
                return null;
            }
            this.binding = AllocationExpression.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        } else {
            this.binding = this.findConstructorBinding(scope, this, (ReferenceBinding)this.resolvedType, this.argumentTypes);
        }
        if (!this.binding.isValidBinding()) {
            if (this.binding.declaringClass == null) {
                this.binding.declaringClass = (ReferenceBinding)this.resolvedType;
            }
            if (this.type != null && !this.type.resolvedType.isValidBinding()) {
                return null;
            }
            scope.problemReporter().invalidConstructor(this, this.binding);
            return this.resolvedType;
        }
        if ((this.binding.tagBits & 0x80L) != 0L) {
            scope.problemReporter().missingTypeInConstructor(this, this.binding);
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true, this)) {
            scope.problemReporter().deprecatedMethod(this.binding, this);
        }
        if (AllocationExpression.checkInvocationArguments(scope, null, this.resolvedType, this.binding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
            this.bits |= 0x10000;
        }
        if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
            scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
        }
        if (!isDiamond && this.resolvedType.isParameterizedTypeWithActualArguments()) {
            this.checkTypeArgumentRedundancy((ParameterizedTypeBinding)this.resolvedType, scope);
        }
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
            ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(this.binding, scope);
            if (scope.environment().usesNullTypeAnnotations()) {
                if (this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
                    TypeBinding[] typeVariables = this.binding.original().typeVariables();
                    int i = 0;
                    while (i < this.typeArguments.length) {
                        this.typeArguments[i].checkNullConstraints(scope, (ParameterizedGenericMethodBinding)this.binding, typeVariables, i);
                        ++i;
                    }
                }
                this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, new AnnotationBinding[]{scope.environment().getNonNullAnnotation()});
            }
        }
        if (compilerOptions.sourceLevel >= 0x340000L && this.binding.getTypeAnnotations() != Binding.NO_ANNOTATIONS) {
            this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, this.binding.getTypeAnnotations());
        }
        return this.resolvedType;
    }

    void checkIllegalNullAnnotation(BlockScope scope, TypeBinding allocationType) {
        Annotation annotation;
        long nullTagBits;
        if (allocationType != null && (nullTagBits = allocationType.tagBits & 0x180000000000000L) != 0L && (annotation = this.type.findAnnotation(nullTagBits)) != null) {
            scope.problemReporter().nullAnnotationUnsupportedLocation(annotation);
        }
    }

    @Override
    public boolean isBoxingCompatibleWith(TypeBinding targetType, Scope scope) {
        if (this.isPolyExpression()) {
            return false;
        }
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        return this.isBoxingCompatible(this.resolvedType, targetType, this, scope);
    }

    @Override
    public boolean isCompatibleWith(TypeBinding targetType, Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        TypeBinding allocationType = this.resolvedType;
        if (this.isPolyExpression()) {
            TypeBinding originalExpectedType = this.typeExpected;
            try {
                MethodBinding method;
                MethodBinding methodBinding = method = this.solutionsPerTargetType != null ? this.solutionsPerTargetType.get(targetType) : null;
                if (method == null) {
                    this.typeExpected = targetType;
                    method = this.inferConstructorOfElidedParameterizedType(scope);
                    if (method == null || !method.isValidBinding()) {
                        return false;
                    }
                }
                allocationType = method.declaringClass;
            }
            finally {
                this.typeExpected = originalExpectedType;
            }
        }
        return allocationType != null && allocationType.isCompatibleWith(targetType, scope);
    }

    public MethodBinding inferConstructorOfElidedParameterizedType(Scope scope) {
        boolean[] inferredReturnTypeOut;
        MethodBinding constructor;
        if (this.typeExpected != null && this.binding != null) {
            MethodBinding cached;
            MethodBinding methodBinding = cached = this.solutionsPerTargetType != null ? this.solutionsPerTargetType.get(this.typeExpected) : null;
            if (cached != null) {
                return cached;
            }
        }
        if ((constructor = AllocationExpression.inferDiamondConstructor(scope, this, this.resolvedType, this.argumentTypes, inferredReturnTypeOut = new boolean[1])) != null) {
            this.inferredReturnType = inferredReturnTypeOut[0];
            if (constructor instanceof ParameterizedGenericMethodBinding && scope.compilerOptions().sourceLevel >= 0x340000L && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT && this.typeExpected == null) {
                constructor = ParameterizedGenericMethodBinding.computeCompatibleMethod18(constructor.shallowOriginal(), this.argumentTypes, scope, this);
            }
            if (this.typeExpected != null && this.typeExpected.isProperType(true)) {
                this.registerResult(this.typeExpected, constructor);
            }
        }
        return constructor;
    }

    public static MethodBinding inferDiamondConstructor(Scope scope, InvocationSite site, TypeBinding type, TypeBinding[] argumentTypes, boolean[] inferredReturnTypeOut) {
        ReferenceBinding genericType = ((ParameterizedTypeBinding)type).genericType();
        ReferenceBinding enclosingType = type.enclosingType();
        ParameterizedTypeBinding allocationType = scope.environment().createParameterizedType(genericType, genericType.typeVariables(), enclosingType);
        MethodBinding factory = scope.getStaticFactory(allocationType, enclosingType, argumentTypes, site);
        if (factory instanceof ParameterizedGenericMethodBinding && factory.isValidBinding()) {
            TypeBinding[] constructorTypeArguments;
            ParameterizedGenericMethodBinding genericFactory = (ParameterizedGenericMethodBinding)factory;
            inferredReturnTypeOut[0] = genericFactory.inferredReturnType;
            SyntheticFactoryMethodBinding sfmb = (SyntheticFactoryMethodBinding)factory.original();
            TypeVariableBinding[] constructorTypeVariables = sfmb.getConstructor().typeVariables();
            TypeBinding[] typeBindingArray = constructorTypeArguments = constructorTypeVariables != null ? new TypeBinding[constructorTypeVariables.length] : Binding.NO_TYPES;
            if (constructorTypeArguments.length > 0) {
                System.arraycopy(((ParameterizedGenericMethodBinding)factory).typeArguments, sfmb.typeVariables().length - constructorTypeArguments.length, constructorTypeArguments, 0, constructorTypeArguments.length);
            }
            if (allocationType.isInterface()) {
                ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)factory.returnType;
                return new ParameterizedMethodBinding(parameterizedType, sfmb.getConstructor());
            }
            return sfmb.applyTypeArgumentsOnConstructor(((ParameterizedTypeBinding)factory.returnType).arguments, constructorTypeArguments, genericFactory.inferredWithUncheckedConversion, site.invocationTargetType());
        }
        return null;
    }

    public TypeBinding[] inferElidedTypes(Scope scope) {
        return this.inferElidedTypes((ParameterizedTypeBinding)this.resolvedType, scope);
    }

    public TypeBinding[] inferElidedTypes(ParameterizedTypeBinding parameterizedType, Scope scope) {
        ReferenceBinding genericType = parameterizedType.genericType();
        ReferenceBinding enclosingType = parameterizedType.enclosingType();
        ParameterizedTypeBinding allocationType = scope.environment().createParameterizedType(genericType, genericType.typeVariables(), enclosingType);
        MethodBinding factory = scope.getStaticFactory(allocationType, enclosingType, this.argumentTypes, this);
        if (factory instanceof ParameterizedGenericMethodBinding && factory.isValidBinding()) {
            ParameterizedGenericMethodBinding genericFactory = (ParameterizedGenericMethodBinding)factory;
            this.inferredReturnType = genericFactory.inferredReturnType;
            return ((ParameterizedTypeBinding)factory.returnType).arguments;
        }
        return null;
    }

    public void checkTypeArgumentRedundancy(ParameterizedTypeBinding allocationType, BlockScope scope) {
        TypeBinding[] inferredTypes;
        if (scope.problemReporter().computeSeverity(16778100) == 256 || scope.compilerOptions().sourceLevel < 0x330000L) {
            return;
        }
        if (allocationType.arguments == null) {
            return;
        }
        if (this.genericTypeArguments != null) {
            return;
        }
        if (this.type == null) {
            return;
        }
        if (this.argumentTypes == Binding.NO_PARAMETERS && this.typeExpected instanceof ParameterizedTypeBinding) {
            ParameterizedTypeBinding expected = (ParameterizedTypeBinding)this.typeExpected;
            if (expected.arguments != null && allocationType.arguments.length == expected.arguments.length) {
                int i = 0;
                while (i < allocationType.arguments.length) {
                    if (TypeBinding.notEquals(allocationType.arguments[i], expected.arguments[i])) break;
                    ++i;
                }
                if (i == allocationType.arguments.length) {
                    scope.problemReporter().redundantSpecificationOfTypeArguments(this.type, allocationType.arguments);
                    return;
                }
            }
        }
        int previousBits = this.type.bits;
        try {
            this.type.bits |= 0x80000;
            inferredTypes = this.inferElidedTypes(allocationType, scope);
        }
        finally {
            this.type.bits = previousBits;
        }
        if (inferredTypes == null) {
            return;
        }
        int i = 0;
        while (i < inferredTypes.length) {
            if (TypeBinding.notEquals(inferredTypes[i], allocationType.arguments[i])) {
                return;
            }
            ++i;
        }
        this.reportTypeArgumentRedundancyProblem(allocationType, scope);
    }

    protected void reportTypeArgumentRedundancyProblem(ParameterizedTypeBinding allocationType, BlockScope scope) {
        scope.problemReporter().redundantSpecificationOfTypeArguments(this.type, allocationType.arguments);
    }

    @Override
    public void setActualReceiverType(ReferenceBinding receiverType) {
    }

    @Override
    public void setDepth(int i) {
    }

    @Override
    public void setFieldIndex(int i) {
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int i;
            if (this.typeArguments != null) {
                i = 0;
                int typeArgumentsLength = this.typeArguments.length;
                while (i < typeArgumentsLength) {
                    this.typeArguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                i = 0;
                int argumentsLength = this.arguments.length;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void setExpectedType(TypeBinding expectedType) {
        this.typeExpected = expectedType;
    }

    @Override
    public void setExpressionContext(ExpressionContext context) {
        this.expressionContext = context;
    }

    @Override
    public boolean isPolyExpression() {
        return this.isPolyExpression(this.binding);
    }

    @Override
    public boolean isPolyExpression(MethodBinding method) {
        return (this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) && this.type != null && (this.type.bits & 0x80000) != 0;
    }

    @Override
    public TypeBinding invocationTargetType() {
        return this.typeExpected;
    }

    @Override
    public boolean statementExpression() {
        return (this.bits & 0x1FE00000) == 0;
    }

    @Override
    public MethodBinding binding() {
        return this.binding;
    }

    @Override
    public Expression[] arguments() {
        return this.arguments;
    }

    @Override
    public void registerInferenceContext(ParameterizedGenericMethodBinding method, InferenceContext18 infCtx18) {
        if (this.inferenceContexts == null) {
            this.inferenceContexts = new SimpleLookupTable();
        }
        this.inferenceContexts.put(method, infCtx18);
    }

    @Override
    public void registerResult(TypeBinding targetType, MethodBinding method) {
        if (method != null && method.isConstructor()) {
            if (this.solutionsPerTargetType == null) {
                this.solutionsPerTargetType = new HashMap();
            }
            this.solutionsPerTargetType.put(targetType, method);
        }
    }

    @Override
    public InferenceContext18 getInferenceContext(ParameterizedMethodBinding method) {
        if (this.inferenceContexts == null) {
            return null;
        }
        return (InferenceContext18)this.inferenceContexts.get(method);
    }

    @Override
    public void cleanUpInferenceContexts() {
        if (this.inferenceContexts == null) {
            return;
        }
        Object[] objectArray = this.inferenceContexts.valueTable;
        int n = this.inferenceContexts.valueTable.length;
        int n2 = 0;
        while (n2 < n) {
            Object value = objectArray[n2];
            if (value != null) {
                ((InferenceContext18)value).cleanUp();
            }
            ++n2;
        }
        this.inferenceContexts = null;
        this.outerInferenceContext = null;
        this.solutionsPerTargetType = null;
    }

    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }

    @Override
    public InferenceContext18 freshInferenceContext(Scope scope) {
        return new InferenceContext18(scope, this.arguments, this, this.outerInferenceContext);
    }

    @Override
    public int nameSourceStart() {
        if (this.enumConstant != null) {
            return this.enumConstant.sourceStart;
        }
        return this.type.sourceStart;
    }

    @Override
    public int nameSourceEnd() {
        if (this.enumConstant != null) {
            return this.enumConstant.sourceEnd;
        }
        return this.type.sourceEnd;
    }
}

