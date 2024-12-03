/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
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
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class QualifiedAllocationExpression
extends AllocationExpression {
    public Expression enclosingInstance;
    public TypeDeclaration anonymousType;

    public QualifiedAllocationExpression() {
    }

    public QualifiedAllocationExpression(TypeDeclaration anonymousType) {
        this.anonymousType = anonymousType;
        anonymousType.allocation = this;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        ReferenceBinding superclass;
        if (this.enclosingInstance != null) {
            flowInfo = this.enclosingInstance.analyseCode(currentScope, flowContext, flowInfo);
        } else if (this.binding != null && this.binding.declaringClass != null && (superclass = this.binding.declaringClass.superclass()) != null && superclass.isMemberType() && !superclass.isStatic()) {
            currentScope.tagAsAccessingEnclosingInstanceStateOf(superclass.enclosingType(), false);
        }
        this.checkCapturedLocalInitializationIfNecessary((ReferenceBinding)(this.anonymousType == null ? this.binding.declaringClass.erasure() : this.binding.declaringClass.superclass().erasure()), currentScope, flowInfo);
        if (this.arguments != null) {
            boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
            boolean hasResourceWrapperType = analyseResources && this.resolvedType instanceof ReferenceBinding && ((ReferenceBinding)this.resolvedType).hasTypeBit(4);
            int i = 0;
            int count = this.arguments.length;
            while (i < count) {
                flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo);
                if (analyseResources && !hasResourceWrapperType) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                }
                this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                ++i;
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
        }
        if (this.anonymousType != null) {
            flowInfo = this.anonymousType.analyseCode(currentScope, flowContext, flowInfo);
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
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        return flowInfo;
    }

    @Override
    public Expression enclosingInstance() {
        return this.enclosingInstance;
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
        if (this.anonymousType != null) {
            this.anonymousType.generateCode(currentScope, codeStream);
        }
    }

    @Override
    public boolean isSuperAccess() {
        return this.anonymousType != null;
    }

    @Override
    public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        ReferenceBinding allocatedTypeErasure;
        if ((flowInfo.tagBits & 1) == 0 && (allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure()).isNestedType() && (currentScope.enclosingSourceType().isLocalType() || currentScope.isLambdaSubscope())) {
            if (allocatedTypeErasure.isLocalType()) {
                ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, this.enclosingInstance != null);
            } else {
                currentScope.propagateInnerEmulation(allocatedTypeErasure, this.enclosingInstance != null);
            }
        }
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.enclosingInstance != null) {
            this.enclosingInstance.printExpression(0, output).append('.');
        }
        super.printExpression(0, output);
        if (this.anonymousType != null) {
            this.anonymousType.print(indent, output);
        }
        return output;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        if (this.anonymousType == null && this.enclosingInstance == null) {
            return super.resolveType(scope);
        }
        TypeBinding result = this.resolveTypeForQualifiedAllocationExpression(scope);
        if (result != null && !result.isPolyType() && this.binding != null) {
            CompilerOptions compilerOptions = scope.compilerOptions();
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
                    if (this.resolvedType.isValidBinding()) {
                        this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, new AnnotationBinding[]{scope.environment().getNonNullAnnotation()});
                    }
                }
            }
            if (compilerOptions.sourceLevel >= 0x340000L && this.binding.getTypeAnnotations() != Binding.NO_ANNOTATIONS) {
                this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, this.binding.getTypeAnnotations());
            }
        }
        return result;
    }

    private TypeBinding resolveTypeForQualifiedAllocationExpression(BlockScope scope) {
        ReferenceBinding superType;
        long sourceLevel;
        TypeBinding receiverType;
        TypeBinding enclosingInstanceType;
        boolean isDiamond;
        block69: {
            block62: {
                boolean hasError;
                block65: {
                    block63: {
                        boolean enclosingInstanceContainsCast;
                        block68: {
                            ReferenceBinding enclosingInstanceReference;
                            block67: {
                                block66: {
                                    block64: {
                                        isDiamond = this.type != null && (this.type.bits & 0x80000) != 0;
                                        enclosingInstanceType = null;
                                        receiverType = null;
                                        sourceLevel = scope.compilerOptions().sourceLevel;
                                        if (this.constant == Constant.NotAConstant) break block62;
                                        this.constant = Constant.NotAConstant;
                                        enclosingInstanceReference = null;
                                        hasError = false;
                                        enclosingInstanceContainsCast = false;
                                        if (this.enclosingInstance == null) break block63;
                                        if (this.enclosingInstance instanceof CastExpression) {
                                            this.enclosingInstance.bits |= 0x20;
                                            enclosingInstanceContainsCast = true;
                                        }
                                        if ((enclosingInstanceType = this.enclosingInstance.resolveType(scope)) != null) break block64;
                                        hasError = true;
                                        break block65;
                                    }
                                    if (!enclosingInstanceType.isBaseType() && !enclosingInstanceType.isArrayType()) break block66;
                                    scope.problemReporter().illegalPrimitiveOrArrayTypeForEnclosingInstance(enclosingInstanceType, this.enclosingInstance);
                                    hasError = true;
                                    break block65;
                                }
                                if (!(this.type instanceof QualifiedTypeReference)) break block67;
                                scope.problemReporter().illegalUsageOfQualifiedTypeReference((QualifiedTypeReference)this.type);
                                hasError = true;
                                break block65;
                            }
                            enclosingInstanceReference = (ReferenceBinding)enclosingInstanceType;
                            if (enclosingInstanceReference.canBeSeenBy(scope)) break block68;
                            enclosingInstanceType = new ProblemReferenceBinding(enclosingInstanceReference.compoundName, enclosingInstanceReference, 2);
                            scope.problemReporter().invalidType(this.enclosingInstance, enclosingInstanceType);
                            hasError = true;
                            break block65;
                        }
                        this.resolvedType = receiverType = ((SingleTypeReference)this.type).resolveTypeEnclosing(scope, (ReferenceBinding)enclosingInstanceType);
                        this.checkIllegalNullAnnotation(scope, receiverType);
                        if (receiverType == null || !enclosingInstanceContainsCast) break block65;
                        CastExpression.checkNeedForEnclosingInstanceCast(scope, this.enclosingInstance, enclosingInstanceType, receiverType);
                        break block65;
                    }
                    if (this.type == null) {
                        receiverType = scope.enclosingSourceType();
                    } else {
                        receiverType = this.type.resolveType(scope, true);
                        this.checkIllegalNullAnnotation(scope, receiverType);
                        if (receiverType != null && receiverType.isValidBinding() && this.type instanceof ParameterizedQualifiedTypeReference) {
                            ReferenceBinding currentType = (ReferenceBinding)receiverType;
                            block0: while ((currentType.modifiers & 8) == 0 && !currentType.isRawType()) {
                                if ((currentType = currentType.enclosingType()) != null) continue;
                                ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
                                int i = qRef.typeArguments.length - 2;
                                while (i >= 0) {
                                    if (qRef.typeArguments[i] != null) {
                                        scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, receiverType);
                                        break block0;
                                    }
                                    --i;
                                }
                                break block0;
                            }
                        }
                    }
                }
                if (receiverType == null || !receiverType.isValidBinding()) {
                    hasError = true;
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
                this.argumentTypes = Binding.NO_PARAMETERS;
                if (this.arguments != null) {
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
                        this.argumentTypes[i] = argument.resolveType(scope);
                        if (this.argumentTypes[i] == null) {
                            hasError = true;
                            this.argumentsHaveErrors = true;
                        }
                        ++i;
                    }
                }
                if (hasError) {
                    if (isDiamond) {
                        return null;
                    }
                    if (receiverType instanceof ReferenceBinding) {
                        ReferenceBinding referenceReceiver = (ReferenceBinding)receiverType;
                        if (receiverType.isValidBinding()) {
                            MethodBinding closestMatch;
                            int length = this.arguments == null ? 0 : this.arguments.length;
                            TypeBinding[] pseudoArgs = new TypeBinding[length];
                            int i = length;
                            while (--i >= 0) {
                                TypeBinding typeBinding = pseudoArgs[i] = this.argumentTypes[i] == null ? TypeBinding.NULL : this.argumentTypes[i];
                            }
                            this.binding = scope.findMethod(referenceReceiver, TypeConstants.INIT, pseudoArgs, this, false);
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
                        if (this.anonymousType != null) {
                            scope.addAnonymousType(this.anonymousType, referenceReceiver);
                            this.anonymousType.resolve(scope);
                            this.resolvedType = this.anonymousType.binding;
                            return this.resolvedType;
                        }
                    }
                    this.resolvedType = receiverType;
                    return this.resolvedType;
                }
                if (this.anonymousType == null) {
                    if (!receiverType.canBeInstantiated()) {
                        scope.problemReporter().cannotInstantiate(this.type, receiverType);
                        this.resolvedType = receiverType;
                        return this.resolvedType;
                    }
                } else {
                    if (isDiamond && sourceLevel < 0x350000L) {
                        scope.problemReporter().diamondNotWithAnoymousClasses(this.type);
                        return null;
                    }
                    ReferenceBinding superType2 = (ReferenceBinding)receiverType;
                    if (superType2.isTypeVariable()) {
                        superType2 = new ProblemReferenceBinding(new char[][]{superType2.sourceName()}, superType2, 9);
                        scope.problemReporter().invalidType(this, superType2);
                        return null;
                    }
                    if (this.type != null && superType2.isEnum()) {
                        scope.problemReporter().cannotInstantiate(this.type, superType2);
                        this.resolvedType = superType2;
                        return this.resolvedType;
                    }
                    this.resolvedType = receiverType;
                }
                break block69;
            }
            if (this.enclosingInstance != null) {
                enclosingInstanceType = this.enclosingInstance.resolvedType;
                this.resolvedType = receiverType = this.type.resolvedType;
            }
        }
        MethodBinding constructorBinding = null;
        if (isDiamond) {
            TypeBinding lastArg;
            this.binding = constructorBinding = this.inferConstructorOfElidedParameterizedType(scope);
            if (this.binding == null || !this.binding.isValidBinding()) {
                scope.problemReporter().cannotInferElidedTypes(this);
                this.resolvedType = null;
                return null;
            }
            if (this.typeExpected == null && sourceLevel >= 0x340000L && this.expressionContext.definesTargetType()) {
                return new PolyTypeBinding(this);
            }
            this.type.resolvedType = receiverType = this.binding.declaringClass;
            this.resolvedType = receiverType;
            if (this.anonymousType != null) {
                constructorBinding = this.getAnonymousConstructorBinding((ReferenceBinding)receiverType, scope);
                if (constructorBinding == null) {
                    return null;
                }
                this.resolvedType = this.anonymousType.binding;
                if (!this.validate((ParameterizedTypeBinding)receiverType, scope)) {
                    return this.resolvedType;
                }
            } else if (this.binding.isVarargs() && !(lastArg = this.binding.parameters[this.binding.parameters.length - 1].leafComponentType()).erasure().canBeSeenBy(scope)) {
                scope.problemReporter().invalidType(this, new ProblemReferenceBinding(new char[][]{lastArg.readableName()}, (ReferenceBinding)lastArg, 2));
                this.resolvedType = null;
                return null;
            }
            this.binding = QualifiedAllocationExpression.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        } else if (this.anonymousType != null) {
            constructorBinding = this.getAnonymousConstructorBinding((ReferenceBinding)receiverType, scope);
            if (constructorBinding == null) {
                return null;
            }
            this.resolvedType = this.anonymousType.binding;
        } else {
            this.binding = constructorBinding = this.findConstructorBinding(scope, this, (ReferenceBinding)receiverType, this.argumentTypes);
        }
        ReferenceBinding receiver = (ReferenceBinding)receiverType;
        ReferenceBinding referenceBinding = superType = receiver.isInterface() ? scope.getJavaLangObject() : receiver;
        if (constructorBinding.isValidBinding()) {
            if (this.isMethodUseDeprecated(constructorBinding, scope, true, this)) {
                scope.problemReporter().deprecatedMethod(constructorBinding, this);
            }
            if (QualifiedAllocationExpression.checkInvocationArguments(scope, null, superType, constructorBinding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
                this.bits |= 0x10000;
            }
            if (this.typeArguments != null && constructorBinding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(constructorBinding, this.genericTypeArguments, this.typeArguments);
            }
        } else {
            if (constructorBinding.declaringClass == null) {
                constructorBinding.declaringClass = superType;
            }
            if (this.type != null && !this.type.resolvedType.isValidBinding()) {
                return null;
            }
            scope.problemReporter().invalidConstructor(this, constructorBinding);
            return this.resolvedType;
        }
        if ((constructorBinding.tagBits & 0x80L) != 0L) {
            scope.problemReporter().missingTypeInConstructor(this, constructorBinding);
        }
        if (this.enclosingInstance != null) {
            ReferenceBinding targetEnclosing = constructorBinding.declaringClass.enclosingType();
            if (targetEnclosing == null) {
                scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.enclosingInstance, receiver);
                return this.resolvedType;
            }
            if (!enclosingInstanceType.isCompatibleWith(targetEnclosing) && !scope.isBoxingCompatibleWith(enclosingInstanceType, targetEnclosing)) {
                scope.problemReporter().typeMismatchError(enclosingInstanceType, targetEnclosing, this.enclosingInstance, null);
                return this.resolvedType;
            }
            this.enclosingInstance.computeConversion(scope, targetEnclosing, enclosingInstanceType);
        }
        if (!isDiamond && receiverType.isParameterizedTypeWithActualArguments() && (this.anonymousType == null || sourceLevel >= 0x350000L)) {
            this.checkTypeArgumentRedundancy((ParameterizedTypeBinding)receiverType, scope);
        }
        if (this.anonymousType != null) {
            LookupEnvironment environment = scope.environment();
            if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(constructorBinding, scope);
            }
            this.binding = this.anonymousType.createDefaultConstructorWithBinding(constructorBinding, (this.bits & 0x10000) != 0 && this.genericTypeArguments == null);
            return this.resolvedType;
        }
        this.resolvedType = receiverType;
        return this.resolvedType;
    }

    private boolean validate(ParameterizedTypeBinding allocationType, Scope scope) {
        class ValidityInspector
        extends TypeBindingVisitor {
            private boolean noErrors = true;
            private final /* synthetic */ Scope val$scope;
            private final /* synthetic */ ParameterizedTypeBinding val$allocationType;

            public ValidityInspector(Scope scope, ParameterizedTypeBinding parameterizedTypeBinding) {
                this.val$scope = scope;
                this.val$allocationType = parameterizedTypeBinding;
            }

            @Override
            public boolean visit(IntersectionTypeBinding18 intersectionTypeBinding18) {
                Arrays.sort(intersectionTypeBinding18.intersectingTypes, (t1, t2) -> t1.id - t2.id);
                this.val$scope.problemReporter().anonymousDiamondWithNonDenotableTypeArguments(QualifiedAllocationExpression.this.type, this.val$allocationType);
                this.noErrors = false;
                return false;
            }

            @Override
            public boolean visit(TypeVariableBinding typeVariable) {
                if (typeVariable.isCapture()) {
                    this.val$scope.problemReporter().anonymousDiamondWithNonDenotableTypeArguments(QualifiedAllocationExpression.this.type, this.val$allocationType);
                    this.noErrors = false;
                    return false;
                }
                return true;
            }

            @Override
            public boolean visit(ReferenceBinding ref) {
                if (!ref.canBeSeenBy(this.val$scope)) {
                    this.val$scope.problemReporter().invalidType(QualifiedAllocationExpression.this.anonymousType, new ProblemReferenceBinding(ref.compoundName, ref, 2));
                    this.noErrors = false;
                    return false;
                }
                return true;
            }

            public boolean isValid() {
                TypeBindingVisitor.visit((TypeBindingVisitor)this, this.val$allocationType);
                return this.noErrors;
            }
        }
        return new ValidityInspector(scope, allocationType).isValid();
    }

    private MethodBinding getAnonymousConstructorBinding(ReferenceBinding receiverType, BlockScope scope) {
        ReferenceBinding superType = receiverType;
        ReferenceBinding anonymousSuperclass = superType.isInterface() ? scope.getJavaLangObject() : superType;
        scope.addAnonymousType(this.anonymousType, superType);
        this.anonymousType.resolve(scope);
        this.resolvedType = this.anonymousType.binding;
        if ((this.resolvedType.tagBits & 0x20000L) != 0L) {
            return null;
        }
        return this.findConstructorBinding(scope, this, anonymousSuperclass, this.argumentTypes);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.enclosingInstance != null) {
                this.enclosingInstance.traverse(visitor, scope);
            }
            if (this.typeArguments != null) {
                int i = 0;
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
                int argumentsLength = this.arguments.length;
                int i = 0;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.anonymousType != null) {
                this.anonymousType.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    protected void reportTypeArgumentRedundancyProblem(ParameterizedTypeBinding allocationType, BlockScope scope) {
        if (this.checkDiamondOperatorCanbeRemoved(scope)) {
            scope.problemReporter().redundantSpecificationOfTypeArguments(this.type, allocationType.arguments);
        }
    }

    private boolean checkDiamondOperatorCanbeRemoved(BlockScope scope) {
        if (this.anonymousType != null && this.anonymousType.methods != null && this.anonymousType.methods.length > 0) {
            if (scope.compilerOptions().complianceLevel < 0x350000L) {
                return false;
            }
            AbstractMethodDeclaration[] abstractMethodDeclarationArray = this.anonymousType.methods;
            int n = this.anonymousType.methods.length;
            int n2 = 0;
            while (n2 < n) {
                AbstractMethodDeclaration method = abstractMethodDeclarationArray[n2];
                if (method.binding != null && (method.binding.modifiers & 0x10000000) == 0) {
                    return false;
                }
                ++n2;
            }
        }
        return true;
    }
}

