/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashMap;
import java.util.function.BiConsumer;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnlikelyArgumentCheck;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class MessageSend
extends Expression
implements IPolyExpression,
Invocation {
    public Expression receiver;
    public char[] selector;
    public Expression[] arguments;
    public MethodBinding binding;
    public MethodBinding syntheticAccessor;
    public TypeBinding expectedType;
    public long nameSourcePosition;
    public TypeBinding actualReceiverType;
    public TypeBinding valueCast;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public ExpressionContext expressionContext = ExpressionContext.VANILLA_CONTEXT;
    private SimpleLookupTable inferenceContexts;
    private HashMap<TypeBinding, MethodBinding> solutionsPerTargetType;
    private InferenceContext18 outerInferenceContext;
    private boolean receiverIsType;
    protected boolean argsContainCast;
    public TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
    public boolean argumentsHaveErrors = false;
    public FakedTrackingVariable closeTracker;
    BiConsumer<FlowInfo, Boolean> flowUpdateOnBooleanResult;

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        boolean nonStatic = !this.binding.isStatic();
        boolean wasInsideAssert = (flowContext.tagBits & 0x1000) != 0;
        flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo, nonStatic).unconditionalInits();
        this.yieldQualifiedCheck(currentScope);
        CompilerOptions compilerOptions = currentScope.compilerOptions();
        boolean analyseResources = compilerOptions.analyseResourceLeaks;
        if (analyseResources) {
            if (nonStatic) {
                if (CharOperation.equals(TypeConstants.CLOSE, this.selector)) {
                    this.recordCallingClose(currentScope, flowContext, flowInfo, this.receiver);
                }
            } else if (this.arguments != null && this.arguments.length > 0 && FakedTrackingVariable.isAnyCloseable(this.arguments[0].resolvedType)) {
                int i = 0;
                while (i < TypeConstants.closeMethods.length) {
                    TypeConstants.CloseMethodRecord record = TypeConstants.closeMethods[i];
                    if (CharOperation.equals(record.selector, this.selector) && CharOperation.equals(record.typeName, this.binding.declaringClass.compoundName)) {
                        int len = Math.min(record.numCloseableArgs, this.arguments.length);
                        int j = 0;
                        while (j < len) {
                            this.recordCallingClose(currentScope, flowContext, flowInfo, this.arguments[j]);
                            ++j;
                        }
                        break;
                    }
                    ++i;
                }
            }
        }
        if (compilerOptions.isAnyEnabled(IrritantSet.UNLIKELY_ARGUMENT_TYPE) && this.binding.isValidBinding() && this.arguments != null) {
            UnlikelyArgumentCheck argumentChecks;
            if (this.arguments.length == 1 && !this.binding.isStatic()) {
                UnlikelyArgumentCheck argumentChecks2 = UnlikelyArgumentCheck.determineCheckForNonStaticSingleArgumentMethod(this.argumentTypes[0], currentScope, this.selector, this.actualReceiverType, this.binding.parameters);
                if (argumentChecks2 != null && argumentChecks2.isDangerous(currentScope)) {
                    currentScope.problemReporter().unlikelyArgumentType(this.arguments[0], this.binding, this.argumentTypes[0], argumentChecks2.typeToReport, argumentChecks2.dangerousMethod);
                }
            } else if (this.arguments.length == 2 && this.binding.isStatic() && (argumentChecks = UnlikelyArgumentCheck.determineCheckForStaticTwoArgumentMethod(this.argumentTypes[1], currentScope, this.selector, this.argumentTypes[0], this.binding.parameters, this.actualReceiverType)) != null && argumentChecks.isDangerous(currentScope)) {
                currentScope.problemReporter().unlikelyArgumentType(this.arguments[1], this.binding, this.argumentTypes[1], argumentChecks.typeToReport, argumentChecks.dangerousMethod);
            }
        }
        if (nonStatic) {
            int timeToLive = (this.bits & 0x100000) != 0 ? 3 : 2;
            this.receiver.checkNPE(currentScope, flowContext, flowInfo, timeToLive);
        }
        if (this.arguments != null) {
            int length = this.arguments.length;
            int i = 0;
            while (i < length) {
                Expression argument = this.arguments[i];
                argument.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                switch (this.detectAssertionUtility(i)) {
                    case TRUE_ASSERTION: {
                        flowInfo = this.analyseBooleanAssertion(currentScope, argument, flowContext, flowInfo, wasInsideAssert, true);
                        break;
                    }
                    case FALSE_ASSERTION: {
                        flowInfo = this.analyseBooleanAssertion(currentScope, argument, flowContext, flowInfo, wasInsideAssert, false);
                        break;
                    }
                    case NONNULL_ASSERTION: {
                        flowInfo = this.analyseNullAssertion(currentScope, argument, flowContext, flowInfo, false);
                        break;
                    }
                    case NULL_ASSERTION: {
                        flowInfo = this.analyseNullAssertion(currentScope, argument, flowContext, flowInfo, true);
                        break;
                    }
                    case ARG_NONNULL_IF_TRUE: {
                        this.recordFlowUpdateOnResult(((SingleNameReference)argument).localVariableBinding(), true, false);
                        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                        break;
                    }
                    case ARG_NONNULL_IF_TRUE_NEGATABLE: {
                        this.recordFlowUpdateOnResult(((SingleNameReference)argument).localVariableBinding(), true, true);
                        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                        break;
                    }
                    case ARG_NULL_IF_TRUE: {
                        this.recordFlowUpdateOnResult(((SingleNameReference)argument).localVariableBinding(), false, true);
                        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                        break;
                    }
                    default: {
                        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                    }
                }
                if (analyseResources) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, argument, flowInfo, flowContext, false);
                }
                ++i;
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
        }
        TypeBinding[] thrownExceptions = this.binding.thrownExceptions;
        if (this.binding.thrownExceptions != Binding.NO_EXCEPTIONS) {
            if ((this.bits & 0x10000) != 0 && this.genericTypeArguments == null) {
                thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
            }
            flowContext.checkExceptionHandlers(thrownExceptions, (ASTNode)this, flowInfo.copy(), currentScope);
        }
        if (analyseResources && FakedTrackingVariable.isAnyCloseable(this.resolvedType)) {
            flowInfo = FakedTrackingVariable.analyseCloseableAcquisition(currentScope, flowInfo, this);
        }
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        flowContext.expireNullCheckedFieldInfo();
        return flowInfo;
    }

    public void recordFlowUpdateOnResult(LocalVariableBinding local, boolean nonNullIfTrue, boolean negatable) {
        this.flowUpdateOnBooleanResult = (f, result) -> {
            if (result.booleanValue() || negatable) {
                if (result == nonNullIfTrue) {
                    f.markAsDefinitelyNonNull(local);
                } else {
                    f.markAsDefinitelyNull(local);
                }
            }
        };
    }

    @Override
    protected void updateFlowOnBooleanResult(FlowInfo flowInfo, boolean result) {
        if (this.flowUpdateOnBooleanResult != null) {
            this.flowUpdateOnBooleanResult.accept(flowInfo, result);
        }
    }

    private void yieldQualifiedCheck(BlockScope currentScope) {
        long sourceLevel = currentScope.compilerOptions().sourceLevel;
        if (sourceLevel < 0x3A0000L || !this.receiverIsImplicitThis()) {
            return;
        }
        if (this.selector == null || !"yield".equals(new String(this.selector))) {
            return;
        }
        if (sourceLevel >= 0x3A0000L) {
            currentScope.problemReporter().switchExpressionsYieldUnqualifiedMethodError(this);
        } else {
            currentScope.problemReporter().switchExpressionsYieldUnqualifiedMethodWarning(this);
        }
    }

    private void recordCallingClose(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Expression closeTarget) {
        FakedTrackingVariable trackingVariable = FakedTrackingVariable.getCloseTrackingVariable(closeTarget, flowInfo, flowContext);
        if (trackingVariable != null) {
            if (trackingVariable.methodScope == currentScope.methodScope()) {
                trackingVariable.markClose(flowInfo, flowContext);
            } else {
                trackingVariable.markClosedInNestedMethod();
            }
        }
    }

    private AssertUtil detectAssertionUtility(int argumentIdx) {
        TypeBinding[] parameters = this.binding.original().parameters;
        if (argumentIdx < parameters.length) {
            TypeBinding parameterType = parameters[argumentIdx];
            ReferenceBinding declaringClass = this.binding.declaringClass;
            if (declaringClass != null && parameterType != null) {
                switch (declaringClass.original().id) {
                    case 68: {
                        if (parameterType.id == 5) {
                            return AssertUtil.TRUE_ASSERTION;
                        }
                        if (parameterType.id != 1 || !CharOperation.equals(TypeConstants.IS_NOTNULL, this.selector)) break;
                        return AssertUtil.NONNULL_ASSERTION;
                    }
                    case 69: 
                    case 70: 
                    case 75: {
                        if (parameterType.id == 5) {
                            if (CharOperation.equals(TypeConstants.ASSERT_TRUE, this.selector)) {
                                return AssertUtil.TRUE_ASSERTION;
                            }
                            if (!CharOperation.equals(TypeConstants.ASSERT_FALSE, this.selector)) break;
                            return AssertUtil.FALSE_ASSERTION;
                        }
                        if (parameterType.id != 1) break;
                        if (CharOperation.equals(TypeConstants.ASSERT_NOTNULL, this.selector)) {
                            return AssertUtil.NONNULL_ASSERTION;
                        }
                        if (!CharOperation.equals(TypeConstants.ASSERT_NULL, this.selector)) break;
                        return AssertUtil.NULL_ASSERTION;
                    }
                    case 71: {
                        if (parameterType.id == 5) {
                            if (!CharOperation.equals(TypeConstants.IS_TRUE, this.selector)) break;
                            return AssertUtil.TRUE_ASSERTION;
                        }
                        if (parameterType.id != 1 || !CharOperation.equals(TypeConstants.NOT_NULL, this.selector)) break;
                        return AssertUtil.NONNULL_ASSERTION;
                    }
                    case 72: {
                        if (parameterType.id == 5) {
                            if (!CharOperation.equals(TypeConstants.IS_TRUE, this.selector)) break;
                            return AssertUtil.TRUE_ASSERTION;
                        }
                        if (!parameterType.isTypeVariable() || !CharOperation.equals(TypeConstants.NOT_NULL, this.selector)) break;
                        return AssertUtil.NONNULL_ASSERTION;
                    }
                    case 73: {
                        if (parameterType.id == 5) {
                            if (!CharOperation.equals(TypeConstants.CHECK_ARGUMENT, this.selector) && !CharOperation.equals(TypeConstants.CHECK_STATE, this.selector)) break;
                            return AssertUtil.TRUE_ASSERTION;
                        }
                        if (!parameterType.isTypeVariable() || !CharOperation.equals(TypeConstants.CHECK_NOT_NULL, this.selector)) break;
                        return AssertUtil.NONNULL_ASSERTION;
                    }
                    case 74: {
                        if (parameterType.isTypeVariable() && CharOperation.equals(TypeConstants.REQUIRE_NON_NULL, this.selector)) {
                            return AssertUtil.NONNULL_ASSERTION;
                        }
                        if (!(this.arguments[argumentIdx] instanceof SingleNameReference)) break;
                        SingleNameReference nameRef = (SingleNameReference)this.arguments[argumentIdx];
                        if (!(nameRef.binding instanceof LocalVariableBinding)) break;
                        if (CharOperation.equals(TypeConstants.NON_NULL, this.selector)) {
                            return AssertUtil.ARG_NONNULL_IF_TRUE_NEGATABLE;
                        }
                        if (!CharOperation.equals(TypeConstants.IS_NULL, this.selector)) break;
                        return AssertUtil.ARG_NULL_IF_TRUE;
                    }
                    case 16: {
                        if (!CharOperation.equals(TypeConstants.IS_INSTANCE, this.selector) || !(this.arguments[argumentIdx] instanceof SingleNameReference)) break;
                        SingleNameReference nameRef = (SingleNameReference)this.arguments[argumentIdx];
                        if (!(nameRef.binding instanceof LocalVariableBinding)) break;
                        return AssertUtil.ARG_NONNULL_IF_TRUE;
                    }
                }
            }
        }
        return AssertUtil.NONE;
    }

    private FlowInfo analyseBooleanAssertion(BlockScope currentScope, Expression argument, FlowContext flowContext, FlowInfo flowInfo, boolean wasInsideAssert, boolean passOnTrue) {
        boolean isOptimizedFailing;
        boolean isOptimizedPassing;
        FlowInfo assertWhenFailInfo;
        UnconditionalFlowInfo assertWhenPassInfo;
        Constant cst = argument.optimizedBooleanConstant();
        boolean isOptimizedTrueAssertion = cst != Constant.NotAConstant && cst.booleanValue();
        boolean isOptimizedFalseAssertion = cst != Constant.NotAConstant && !cst.booleanValue();
        int tagBitsSave = flowContext.tagBits;
        flowContext.tagBits |= 0x1000;
        if (!passOnTrue) {
            flowContext.tagBits |= 4;
        }
        FlowInfo conditionFlowInfo = argument.analyseCode(currentScope, flowContext, flowInfo.copy());
        flowContext.extendTimeToLiveForNullCheckedField(2);
        flowContext.tagBits = tagBitsSave;
        if (passOnTrue) {
            assertWhenPassInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
            assertWhenFailInfo = conditionFlowInfo.initsWhenFalse();
            isOptimizedPassing = isOptimizedTrueAssertion;
            isOptimizedFailing = isOptimizedFalseAssertion;
        } else {
            assertWhenPassInfo = conditionFlowInfo.initsWhenFalse().unconditionalInits();
            assertWhenFailInfo = conditionFlowInfo.initsWhenTrue();
            isOptimizedPassing = isOptimizedFalseAssertion;
            isOptimizedFailing = isOptimizedTrueAssertion;
        }
        if (isOptimizedPassing) {
            assertWhenFailInfo.setReachMode(1);
        }
        if (!isOptimizedFailing) {
            flowInfo = flowInfo.mergedWith(assertWhenFailInfo.nullInfoLessUnconditionalCopy()).addInitializationsFrom(assertWhenPassInfo.discardInitializationInfo());
        }
        return flowInfo;
    }

    private FlowInfo analyseNullAssertion(BlockScope currentScope, Expression argument, FlowContext flowContext, FlowInfo flowInfo, boolean expectingNull) {
        FieldBinding field;
        int nullStatus = argument.nullStatus(flowInfo, flowContext);
        boolean willFail = nullStatus == (expectingNull ? 4 : 2);
        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        LocalVariableBinding local = argument.localVariableBinding();
        if (local != null) {
            if (expectingNull) {
                flowInfo.markAsDefinitelyNull(local);
            } else {
                flowInfo.markAsDefinitelyNonNull(local);
            }
        } else if (!expectingNull && argument instanceof Reference && currentScope.compilerOptions().enableSyntacticNullAnalysisForFields && (field = ((Reference)argument).lastFieldBinding()) != null && (field.type.tagBits & 2L) == 0L) {
            flowContext.recordNullCheckedFieldReference((Reference)argument, 3);
        }
        if (willFail) {
            flowInfo.setReachMode(2);
        }
        return flowInfo;
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        NullAnnotationMatching nonNullStatus;
        int nullStatus = this.nullStatus(flowInfo, flowContext);
        if ((nullStatus & 0x10) != 0) {
            if (this.binding.returnType.isTypeVariable() && nullStatus == 48 && scope.environment().globalOptions.pessimisticNullAnalysisForFreeTypeVariablesEnabled) {
                scope.problemReporter().methodReturnTypeFreeTypeVariableReference(this.binding, this);
            } else {
                scope.problemReporter().messageSendPotentialNullReference(this.binding, this);
            }
        } else if ((this.resolvedType.tagBits & 0x100000000000000L) != 0L && (nonNullStatus = NullAnnotationMatching.okNonNullStatus(this)).wantToReport()) {
            nonNullStatus.report(scope);
        }
        return true;
    }

    @Override
    public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        if (this.binding != null && this.binding.isValidBinding()) {
            ReferenceBinding referenceCast;
            MethodBinding originalBinding = this.binding.original();
            TypeBinding originalType = originalBinding.returnType;
            if (ArrayBinding.isArrayClone(this.actualReceiverType, this.binding) && runtimeTimeType.id != 1 && scope.compilerOptions().sourceLevel >= 0x310000L) {
                this.valueCast = runtimeTimeType;
            } else if (originalType.leafComponentType().isTypeVariable()) {
                TypeBinding targetType = !compileTimeType.isBaseType() && runtimeTimeType.isBaseType() ? compileTimeType : runtimeTimeType;
                this.valueCast = originalType.genericCast(targetType);
            }
            if (this.valueCast instanceof ReferenceBinding && !(referenceCast = (ReferenceBinding)this.valueCast).canBeSeenBy(scope)) {
                scope.problemReporter().invalidType(this, new ProblemReferenceBinding(CharOperation.splitOn('.', referenceCast.shortReadableName()), referenceCast, 2));
            }
        }
        super.computeConversion(scope, runtimeTimeType, compileTimeType);
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        this.cleanUpInferenceContexts();
        int pc = codeStream.position;
        MethodBinding codegenBinding = this.binding instanceof PolymorphicMethodBinding ? this.binding : this.binding.original();
        boolean isStatic = codegenBinding.isStatic();
        if (isStatic) {
            this.receiver.generateCode(currentScope, codeStream, false);
        } else if ((this.bits & 0x1FE0) != 0 && this.receiver.isImplicitThis()) {
            ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
            Object[] path = currentScope.getEmulationPath(targetType, true, false);
            codeStream.generateOuterAccess(path, this, targetType, currentScope);
        } else {
            this.receiver.generateCode(currentScope, codeStream, true);
            if ((this.bits & 0x40000) != 0) {
                codeStream.checkcast(this.actualReceiverType);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
        pc = codeStream.position;
        if (this.syntheticAccessor == null) {
            TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
            if (isStatic) {
                codeStream.invoke((byte)-72, codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            } else if (this.receiver.isSuper() || !currentScope.enclosingSourceType().isNestmateOf(this.binding.declaringClass) && codegenBinding.isPrivate()) {
                codeStream.invoke((byte)-73, codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            } else if (constantPoolDeclaringClass.isInterface()) {
                codeStream.invoke((byte)-71, codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            } else {
                codeStream.invoke((byte)-74, codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            }
        } else {
            codeStream.invoke((byte)-72, this.syntheticAccessor, null, this.typeArguments);
        }
        if (this.valueCast != null) {
            codeStream.checkcast(this.valueCast);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else {
            boolean isUnboxing;
            boolean bl = isUnboxing = (this.implicitConversion & 0x400) != 0;
            if (isUnboxing) {
                codeStream.generateImplicitConversion(this.implicitConversion);
            }
            switch (isUnboxing ? this.postConversionType((Scope)currentScope).id : codegenBinding.returnType.id) {
                case 7: 
                case 8: {
                    codeStream.pop2();
                    break;
                }
                case 6: {
                    break;
                }
                default: {
                    codeStream.pop();
                }
            }
        }
        codeStream.recordPositionsFrom(pc, (int)(this.nameSourcePosition >>> 32));
    }

    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }

    @Override
    public boolean isSuperAccess() {
        return this.receiver.isSuper();
    }

    @Override
    public boolean isTypeAccess() {
        return this.receiver != null && this.receiver.isTypeReference();
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0) {
            return;
        }
        MethodBinding codegenBinding = this.binding.original();
        if (this.binding.isPrivate()) {
            boolean useNesting;
            boolean bl = useNesting = currentScope.enclosingSourceType().isNestmateOf(codegenBinding.declaringClass) && !(this.receiver instanceof QualifiedSuperReference);
            if (!useNesting && TypeBinding.notEquals(currentScope.enclosingSourceType(), codegenBinding.declaringClass)) {
                this.syntheticAccessor = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, false);
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                return;
            }
        } else {
            if (this.receiver instanceof QualifiedSuperReference) {
                if (this.actualReceiverType.isInterface()) {
                    return;
                }
                SourceTypeBinding destinationType = (SourceTypeBinding)((QualifiedSuperReference)this.receiver).currentCompatibleType;
                this.syntheticAccessor = destinationType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                return;
            }
            if (this.binding.isProtected() && (this.bits & 0x1FE0) != 0) {
                SourceTypeBinding enclosingSourceType = currentScope.enclosingSourceType();
                if (codegenBinding.declaringClass.getPackage() != enclosingSourceType.getPackage()) {
                    SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                    this.syntheticAccessor = currentCompatibleType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
                    currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                    return;
                }
            }
        }
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        if (this.binding.isValidBinding()) {
            long tagBits = this.binding.tagBits;
            if ((tagBits & 0x180000000000000L) == 0L) {
                tagBits = this.binding.returnType.tagBits & 0x180000000000000L;
            }
            if (tagBits == 0L && this.binding.returnType.isFreeTypeVariable()) {
                return 48;
            }
            return FlowInfo.tagBitsToNullStatus(tagBits);
        }
        return 1;
    }

    @Override
    public TypeBinding postConversionType(Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        if (this.valueCast != null) {
            convertedType = this.valueCast;
        }
        int runtimeType = (this.implicitConversion & 0xFF) >> 4;
        switch (runtimeType) {
            case 5: {
                convertedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                convertedType = TypeBinding.BYTE;
                break;
            }
            case 4: {
                convertedType = TypeBinding.SHORT;
                break;
            }
            case 2: {
                convertedType = TypeBinding.CHAR;
                break;
            }
            case 10: {
                convertedType = TypeBinding.INT;
                break;
            }
            case 9: {
                convertedType = TypeBinding.FLOAT;
                break;
            }
            case 7: {
                convertedType = TypeBinding.LONG;
                break;
            }
            case 8: {
                convertedType = TypeBinding.DOUBLE;
            }
        }
        if ((this.implicitConversion & 0x200) != 0) {
            convertedType = scope.environment().computeBoxingType(convertedType);
        }
        return convertedType;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (!this.receiver.isImplicitThis()) {
            this.receiver.printExpression(0, output).append('.');
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
        output.append(this.selector).append('(');
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
        ReferenceContext referenceContext;
        TypeBinding returnType;
        TypeBinding methodType;
        if (this.constant != Constant.NotAConstant) {
            int i;
            TypeBinding resolvedType2;
            this.constant = Constant.NotAConstant;
            long sourceLevel = scope.compilerOptions().sourceLevel;
            boolean receiverCast = false;
            if (this.receiver instanceof CastExpression) {
                this.receiver.bits |= 0x20;
                receiverCast = true;
            }
            this.actualReceiverType = this.receiver.resolveType(scope);
            if (this.actualReceiverType instanceof InferenceVariable) {
                return null;
            }
            boolean bl = this.receiverIsType = this.receiver instanceof NameReference && (((NameReference)this.receiver).bits & 4) != 0;
            if (receiverCast && this.actualReceiverType != null && TypeBinding.equalsEquals(resolvedType2 = ((CastExpression)this.receiver).expression.resolvedType, this.actualReceiverType) && (!scope.environment().usesNullTypeAnnotations() || !NullAnnotationMatching.analyse(this.actualReceiverType, resolvedType2, -1).isAnyMismatch())) {
                scope.problemReporter().unnecessaryCast((CastExpression)this.receiver);
            }
            if (this.typeArguments != null) {
                int length = this.typeArguments.length;
                this.argumentsHaveErrors = sourceLevel < 0x310000L;
                this.genericTypeArguments = new TypeBinding[length];
                i = 0;
                while (i < length) {
                    TypeReference typeReference = this.typeArguments[i];
                    this.genericTypeArguments[i] = typeReference.resolveType(scope, true, 64);
                    if (this.genericTypeArguments[i] == null) {
                        this.argumentsHaveErrors = true;
                    }
                    if (this.argumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                    ++i;
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
                i = 0;
                while (i < length) {
                    Expression argument = this.arguments[i];
                    if (this.arguments[i].resolvedType != null) {
                        scope.problemReporter().genericInferenceError("Argument was unexpectedly found resolved", this);
                    }
                    if (argument instanceof CastExpression) {
                        argument.bits |= 0x20;
                        this.argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    this.argumentTypes[i] = argument.resolveType(scope);
                    if (this.argumentTypes[i] == null) {
                        this.argumentsHaveErrors = true;
                    }
                    ++i;
                }
                if (this.argumentsHaveErrors) {
                    if (this.actualReceiverType instanceof ReferenceBinding) {
                        MethodBinding closestMatch;
                        TypeBinding[] pseudoArgs = new TypeBinding[length];
                        int i2 = length;
                        while (--i2 >= 0) {
                            TypeBinding typeBinding = pseudoArgs[i2] = this.argumentTypes[i2] == null ? TypeBinding.NULL : this.argumentTypes[i2];
                        }
                        MethodBinding methodBinding = this.binding = this.receiver.isImplicitThis() ? scope.getImplicitMethod(this.selector, pseudoArgs, this) : scope.findMethod((ReferenceBinding)this.actualReceiverType, this.selector, pseudoArgs, this, false);
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
                    return null;
                }
            }
            if (this.actualReceiverType == null) {
                return null;
            }
            if (this.actualReceiverType.isBaseType()) {
                scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, this.argumentTypes);
                return null;
            }
        }
        if ((methodType = this.findMethodBinding(scope)) != null && methodType.isPolyType()) {
            this.resolvedType = this.binding.returnType.capture(scope, this.sourceStart, this.sourceEnd);
            return methodType;
        }
        if (!this.binding.isValidBinding()) {
            ReferenceBinding declaringClass;
            boolean avoidSecondary;
            if (this.binding.declaringClass == null) {
                if (this.actualReceiverType instanceof ReferenceBinding) {
                    this.binding.declaringClass = (ReferenceBinding)this.actualReceiverType;
                } else {
                    scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, this.argumentTypes);
                    return null;
                }
            }
            boolean bl = avoidSecondary = (declaringClass = this.binding.declaringClass) != null && declaringClass.isAnonymousType() && declaringClass.superclass() instanceof MissingTypeBinding;
            if (!avoidSecondary) {
                scope.problemReporter().invalidMethod(this, this.binding, scope);
            }
            MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
            switch (this.binding.problemId()) {
                case 3: {
                    break;
                }
                case 23: 
                case 27: {
                    if (this.expressionContext != ExpressionContext.INVOCATION_CONTEXT) break;
                }
                case 2: 
                case 6: 
                case 7: 
                case 8: 
                case 10: {
                    if (closestMatch == null) break;
                    this.resolvedType = closestMatch.returnType;
                    break;
                }
                case 25: {
                    if (closestMatch == null || closestMatch.returnType == null) break;
                    this.resolvedType = closestMatch.returnType.withoutToplevelNullAnnotation();
                }
            }
            if (closestMatch != null) {
                this.binding = closestMatch;
                MethodBinding closestMatchOriginal = closestMatch.original();
                if (closestMatchOriginal.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal)) {
                    closestMatchOriginal.modifiers |= 0x8000000;
                }
            }
            return this.resolvedType != null && (this.resolvedType.tagBits & 0x80L) == 0L ? this.resolvedType : null;
        }
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.complianceLevel <= 0x320000L && this.binding.isPolymorphic()) {
            scope.problemReporter().polymorphicMethodNotBelow17(this);
            return null;
        }
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
            ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(this.binding, scope);
            if (compilerOptions.sourceLevel >= 0x340000L && this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
                TypeBinding[] typeVariables = this.binding.original().typeVariables();
                int i = 0;
                while (i < this.typeArguments.length) {
                    this.typeArguments[i].checkNullConstraints(scope, (ParameterizedGenericMethodBinding)this.binding, typeVariables, i);
                    ++i;
                }
            }
        }
        if ((this.bits & 0x100000) != 0 && this.binding.isPolymorphic()) {
            this.binding = scope.environment().updatePolymorphicMethodReturnType((PolymorphicMethodBinding)this.binding, TypeBinding.VOID);
        }
        if ((this.binding.tagBits & 0x80L) != 0L) {
            scope.problemReporter().missingTypeInMethod(this, this.binding);
        }
        if (!this.binding.isStatic()) {
            if (this.receiverIsType) {
                scope.problemReporter().mustUseAStaticMethod(this, this.binding);
                if (this.actualReceiverType.isRawType() && (this.receiver.bits & 0x40000000) == 0 && compilerOptions.getSeverity(0x20010000) != 256) {
                    scope.problemReporter().rawTypeReference(this.receiver, this.actualReceiverType);
                }
            } else {
                TypeBinding oldReceiverType = this.actualReceiverType;
                this.actualReceiverType = this.actualReceiverType.getErasureCompatibleType(this.binding.declaringClass);
                this.receiver.computeConversion(scope, this.actualReceiverType, this.actualReceiverType);
                if (TypeBinding.notEquals(this.actualReceiverType, oldReceiverType) && TypeBinding.notEquals(this.receiver.postConversionType(scope), this.actualReceiverType)) {
                    this.bits |= 0x40000;
                }
            }
        } else {
            if (this.binding.declaringClass.isInterface() && (!this.isTypeAccess() && !this.receiver.isImplicitThis() || !TypeBinding.equalsEquals(this.binding.declaringClass, this.actualReceiverType))) {
                scope.problemReporter().nonStaticOrAlienTypeReceiver(this, this.binding);
            } else if (!(this.receiver.isImplicitThis() || this.receiver.isSuper() || this.receiverIsType)) {
                scope.problemReporter().nonStaticAccessToStaticMethod(this, this.binding);
            }
            if (!this.receiver.isImplicitThis() && TypeBinding.notEquals(this.binding.declaringClass, this.actualReceiverType)) {
                scope.problemReporter().indirectAccessToStaticMethod(this, this.binding);
            }
        }
        if (MessageSend.checkInvocationArguments(scope, this.receiver, this.actualReceiverType, this.binding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
            this.bits |= 0x10000;
        }
        if (this.binding.isAbstract() && this.receiver.isSuper()) {
            scope.problemReporter().cannotDireclyInvokeAbstractMethod(this, this.binding);
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true, this)) {
            scope.problemReporter().deprecatedMethod(this.binding, this);
        }
        if ((this.bits & 0x10000) != 0 && this.genericTypeArguments == null) {
            returnType = this.binding.returnType;
            if (returnType != null) {
                returnType = scope.environment().convertToRawType(returnType.erasure(), true);
            }
        } else {
            returnType = this.binding.returnType;
            if (returnType != null) {
                returnType = returnType.capture(scope, this.sourceStart, this.sourceEnd);
            }
        }
        this.resolvedType = returnType;
        if (this.receiver.isSuper() && compilerOptions.getSeverity(0x20100000) != 256 && (referenceContext = scope.methodScope().referenceContext) instanceof AbstractMethodDeclaration) {
            AbstractMethodDeclaration abstractMethodDeclaration = (AbstractMethodDeclaration)referenceContext;
            MethodBinding enclosingMethodBinding = abstractMethodDeclaration.binding;
            if (enclosingMethodBinding.isOverriding() && CharOperation.equals(this.binding.selector, enclosingMethodBinding.selector) && this.binding.areParametersEqual(enclosingMethodBinding)) {
                abstractMethodDeclaration.bits |= 0x10;
            }
        }
        if (this.receiver.isSuper() && this.actualReceiverType.isInterface()) {
            scope.checkAppropriateMethodAgainstSupers(this.selector, this.binding, this.argumentTypes, this);
        }
        if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
            scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
        }
        return (this.resolvedType.tagBits & 0x80L) == 0L ? this.resolvedType : null;
    }

    protected TypeBinding findMethodBinding(BlockScope scope) {
        ReferenceContext referenceContext = scope.methodScope().referenceContext;
        if (referenceContext instanceof LambdaExpression) {
            this.outerInferenceContext = ((LambdaExpression)referenceContext).inferenceContext;
        }
        if (this.expectedType != null && this.binding instanceof PolyParameterizedGenericMethodBinding) {
            this.binding = this.solutionsPerTargetType.get(this.expectedType);
        }
        if (this.binding == null) {
            MethodBinding methodBinding = this.binding = this.receiver.isImplicitThis() ? scope.getImplicitMethod(this.selector, this.argumentTypes, this) : scope.getMethod(this.actualReceiverType, this.selector, this.argumentTypes, this);
            if (this.binding instanceof PolyParameterizedGenericMethodBinding) {
                this.solutionsPerTargetType = new HashMap();
                return new PolyTypeBinding(this);
            }
        }
        this.binding = MessageSend.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        return this.binding.returnType;
    }

    @Override
    public void setActualReceiverType(ReferenceBinding receiverType) {
        if (receiverType == null) {
            return;
        }
        this.actualReceiverType = receiverType;
    }

    @Override
    public void setDepth(int depth) {
        this.bits &= 0xFFFFE01F;
        if (depth > 0) {
            this.bits |= (depth & 0xFF) << 5;
        }
    }

    @Override
    public void setExpectedType(TypeBinding expectedType) {
        this.expectedType = expectedType;
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
    public boolean isBoxingCompatibleWith(TypeBinding targetType, Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        if (this.isPolyExpression() && !targetType.isPrimitiveOrBoxedPrimitiveType()) {
            return false;
        }
        TypeBinding originalExpectedType = this.expectedType;
        try {
            MethodBinding method;
            MethodBinding methodBinding = method = this.solutionsPerTargetType != null ? this.solutionsPerTargetType.get(targetType) : null;
            if (method == null) {
                this.expectedType = targetType;
                method = this.isPolyExpression() ? ParameterizedGenericMethodBinding.computeCompatibleMethod18(this.binding.shallowOriginal(), this.argumentTypes, scope, this) : this.binding;
                this.registerResult(targetType, method);
            }
            if (method == null || !method.isValidBinding() || method.returnType == null || !method.returnType.isValidBinding()) {
                return false;
            }
            boolean bl = super.isBoxingCompatible(method.returnType.capture(scope, this.sourceStart, this.sourceEnd), targetType, this, scope);
            return bl;
        }
        finally {
            this.expectedType = originalExpectedType;
        }
    }

    @Override
    public boolean isCompatibleWith(TypeBinding targetType, Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        TypeBinding originalExpectedType = this.expectedType;
        try {
            TypeBinding returnType;
            MethodBinding method;
            MethodBinding methodBinding = method = this.solutionsPerTargetType != null ? this.solutionsPerTargetType.get(targetType) : null;
            if (method == null) {
                this.expectedType = targetType;
                method = this.isPolyExpression() ? ParameterizedGenericMethodBinding.computeCompatibleMethod18(this.binding.shallowOriginal(), this.argumentTypes, scope, this) : this.binding;
                this.registerResult(targetType, method);
            }
            if (method == null || !method.isValidBinding() || (returnType = method.returnType) == null || !returnType.isValidBinding()) {
                return false;
            }
            if ((this.bits & 0x10000) != 0 && this.genericTypeArguments == null) {
                returnType = scope.environment().convertToRawType(returnType.erasure(), true);
            }
            boolean bl = returnType.capture(scope, this.sourceStart, this.sourceEnd).isCompatibleWith(targetType, scope);
            return bl;
        }
        finally {
            this.expectedType = originalExpectedType;
        }
    }

    @Override
    public boolean isPolyExpression(MethodBinding resolutionCandidate) {
        if (this.expressionContext != ExpressionContext.ASSIGNMENT_CONTEXT && this.expressionContext != ExpressionContext.INVOCATION_CONTEXT) {
            return false;
        }
        if (this.typeArguments != null && this.typeArguments.length > 0) {
            return false;
        }
        if (this.constant != Constant.NotAConstant) {
            throw new UnsupportedOperationException("Unresolved MessageSend can't be queried if it is a polyexpression");
        }
        if (resolutionCandidate != null) {
            if (resolutionCandidate instanceof ParameterizedGenericMethodBinding) {
                ParameterizedGenericMethodBinding pgmb = (ParameterizedGenericMethodBinding)resolutionCandidate;
                if (pgmb.inferredReturnType) {
                    return true;
                }
            }
            if (resolutionCandidate.returnType != null) {
                MethodBinding candidateOriginal = resolutionCandidate.original();
                return candidateOriginal.returnType.mentionsAny(candidateOriginal.typeVariables(), -1);
            }
        }
        return false;
    }

    @Override
    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope scope) {
        if (super.sIsMoreSpecific(s, t, scope)) {
            return true;
        }
        return this.isPolyExpression() ? !s.isBaseType() && t.isBaseType() : false;
    }

    @Override
    public void setFieldIndex(int depth) {
    }

    @Override
    public TypeBinding invocationTargetType() {
        return this.expectedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.receiver.traverse(visitor, blockScope);
            if (this.typeArguments != null) {
                int i = 0;
                int typeArgumentsLength = this.typeArguments.length;
                while (i < typeArgumentsLength) {
                    this.typeArguments[i].traverse(visitor, blockScope);
                    ++i;
                }
            }
            if (this.arguments != null) {
                int argumentsLength = this.arguments.length;
                int i = 0;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, blockScope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public boolean statementExpression() {
        return (this.bits & 0x1FE00000) == 0;
    }

    @Override
    public boolean receiverIsImplicitThis() {
        return this.receiver.isImplicitThis();
    }

    @Override
    public MethodBinding binding() {
        return this.binding;
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
        if (this.solutionsPerTargetType == null) {
            this.solutionsPerTargetType = new HashMap();
        }
        this.solutionsPerTargetType.put(targetType, method);
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
    public Expression[] arguments() {
        return this.arguments;
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
    public boolean isQualifiedSuper() {
        return this.receiver.isQualifiedSuper();
    }

    @Override
    public int nameSourceStart() {
        return (int)(this.nameSourcePosition >>> 32);
    }

    @Override
    public int nameSourceEnd() {
        return (int)this.nameSourcePosition;
    }

    private static enum AssertUtil {
        NONE,
        TRUE_ASSERTION,
        FALSE_ASSERTION,
        NULL_ASSERTION,
        NONNULL_ASSERTION,
        ARG_NONNULL_IF_TRUE,
        ARG_NONNULL_IF_TRUE_NEGATABLE,
        ARG_NULL_IF_TRUE;

    }
}

