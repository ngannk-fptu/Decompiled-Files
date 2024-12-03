/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.codegen.MultiCatchExceptionLabel;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class TryStatement
extends SubRoutineStatement {
    static final char[] SECRET_RETURN_ADDRESS_NAME = " returnAddress".toCharArray();
    static final char[] SECRET_ANY_HANDLER_NAME = " anyExceptionHandler".toCharArray();
    static final char[] SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME = " primaryException".toCharArray();
    static final char[] SECRET_CAUGHT_THROWABLE_VARIABLE_NAME = " caughtThrowable".toCharArray();
    static final char[] SECRET_RETURN_VALUE_NAME = " returnValue".toCharArray();
    public Statement[] resources = new Statement[0];
    public Block tryBlock;
    public Block[] catchBlocks;
    public Argument[] catchArguments;
    public Block finallyBlock;
    BlockScope scope;
    public UnconditionalFlowInfo subRoutineInits;
    ReferenceBinding[] caughtExceptionTypes;
    boolean[] catchExits;
    BranchLabel subRoutineStartLabel;
    public LocalVariableBinding anyExceptionVariable;
    public LocalVariableBinding returnAddressVariable;
    public LocalVariableBinding secretReturnValue;
    ExceptionLabel[] declaredExceptionLabels;
    private Object[] reusableJSRTargets;
    private BranchLabel[] reusableJSRSequenceStartLabels;
    private int[] reusableJSRStateIndexes;
    private int reusableJSRTargetsCount = 0;
    private static final int NO_FINALLY = 0;
    private static final int FINALLY_SUBROUTINE = 1;
    private static final int FINALLY_DOES_NOT_COMPLETE = 2;
    private static final int FINALLY_INLINE = 3;
    int mergedInitStateIndex = -1;
    int preTryInitStateIndex = -1;
    int postTryInitStateIndex = -1;
    int[] postResourcesInitStateIndexes;
    int naturalExitMergeInitStateIndex = -1;
    int[] catchExitInitStateIndexes;
    private LocalVariableBinding primaryExceptionVariable;
    private LocalVariableBinding caughtThrowableVariable;
    private ExceptionLabel[] resourceExceptionLabels;
    private int[] caughtExceptionsCatchBlocks;
    public SwitchExpression enclosingSwitchExpression = null;

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        int i;
        int resourcesLength;
        this.preTryInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        if (this.anyExceptionVariable != null) {
            this.anyExceptionVariable.useFlag = 1;
        }
        if (this.primaryExceptionVariable != null) {
            this.primaryExceptionVariable.useFlag = 1;
        }
        if (this.caughtThrowableVariable != null) {
            this.caughtThrowableVariable.useFlag = 1;
        }
        if (this.returnAddressVariable != null) {
            this.returnAddressVariable.useFlag = 1;
        }
        if ((resourcesLength = this.resources.length) > 0) {
            this.postResourcesInitStateIndexes = new int[resourcesLength];
        }
        if (this.subRoutineStartLabel == null) {
            if (flowContext instanceof FinallyFlowContext) {
                FinallyFlowContext finallyContext = (FinallyFlowContext)flowContext;
                finallyContext.outerTryContext = finallyContext.tryContext;
            }
            ExceptionHandlingFlowContext handlingContext = new ExceptionHandlingFlowContext(flowContext, this, this.caughtExceptionTypes, this.caughtExceptionsCatchBlocks, null, this.scope, flowInfo);
            handlingContext.conditionalLevel = 0;
            FlowInfo tryInfo = flowInfo.copy();
            int i2 = 0;
            while (i2 < resourcesLength) {
                MethodBinding closeMethod;
                Statement resource = this.resources[i2];
                tryInfo = resource.analyseCode(currentScope, handlingContext, tryInfo);
                this.postResourcesInitStateIndexes[i2] = currentScope.methodScope().recordInitializationStates(tryInfo);
                TypeBinding resolvedType = null;
                LocalVariableBinding localVariableBinding = null;
                if (resource instanceof LocalDeclaration) {
                    localVariableBinding = ((LocalDeclaration)resource).binding;
                    resolvedType = localVariableBinding.type;
                    if (localVariableBinding.closeTracker != null) {
                        localVariableBinding.closeTracker.withdraw();
                        localVariableBinding.closeTracker = null;
                    }
                } else {
                    if (resource instanceof NameReference && ((NameReference)resource).binding instanceof LocalVariableBinding) {
                        localVariableBinding = (LocalVariableBinding)((NameReference)resource).binding;
                    }
                    resolvedType = ((Expression)resource).resolvedType;
                    this.recordCallingClose(currentScope, flowContext, tryInfo, (Expression)resource);
                }
                if (localVariableBinding != null) {
                    localVariableBinding.useFlag = 1;
                }
                if ((closeMethod = this.findCloseMethod(resource, resolvedType)) != null && closeMethod.isValidBinding() && closeMethod.returnType.id == 6) {
                    ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
                    int j = 0;
                    int length = thrownExceptions.length;
                    while (j < length) {
                        handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[i2], tryInfo, currentScope, true);
                        ++j;
                    }
                }
                ++i2;
            }
            if (!this.tryBlock.isEmptyBlock()) {
                tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, tryInfo);
                if ((tryInfo.tagBits & 1) != 0) {
                    this.bits |= 0x20000000;
                }
            }
            if (resourcesLength > 0) {
                this.postTryInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
                i2 = 0;
                while (i2 < resourcesLength) {
                    if (this.resources[i2] instanceof LocalDeclaration) {
                        tryInfo.resetAssignmentInfo(((LocalDeclaration)this.resources[i2]).binding);
                    }
                    ++i2;
                }
            }
            handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);
            if (this.catchArguments != null) {
                int catchCount = this.catchBlocks.length;
                this.catchExits = new boolean[catchCount];
                this.catchExitInitStateIndexes = new int[catchCount];
                int i3 = 0;
                while (i3 < catchCount) {
                    FlowInfo catchInfo = this.prepareCatchInfo(flowInfo, handlingContext, tryInfo, i3);
                    ++flowContext.conditionalLevel;
                    catchInfo = this.catchBlocks[i3].analyseCode(currentScope, flowContext, catchInfo);
                    --flowContext.conditionalLevel;
                    this.catchExitInitStateIndexes[i3] = currentScope.methodScope().recordInitializationStates(catchInfo);
                    this.catchExits[i3] = (catchInfo.tagBits & 1) != 0;
                    tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
                    ++i3;
                }
            }
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
            flowContext.mergeFinallyNullInfo(handlingContext.initsOnFinally);
            return tryInfo;
        }
        InsideSubRoutineFlowContext insideSubContext = new InsideSubRoutineFlowContext(flowContext, this);
        if (flowContext instanceof FinallyFlowContext) {
            insideSubContext.outerTryContext = ((FinallyFlowContext)flowContext).tryContext;
        }
        ExceptionHandlingFlowContext handlingContext = new ExceptionHandlingFlowContext(insideSubContext, this, this.caughtExceptionTypes, this.caughtExceptionsCatchBlocks, null, this.scope, flowInfo);
        insideSubContext.initsOnFinally = handlingContext.initsOnFinally;
        FinallyFlowContext finallyContext = new FinallyFlowContext(flowContext, (ASTNode)this.finallyBlock, handlingContext);
        UnconditionalFlowInfo subInfo = this.finallyBlock.analyseCode(currentScope, finallyContext, flowInfo.nullInfoLessUnconditionalCopy()).unconditionalInits();
        handlingContext.conditionalLevel = 0;
        if (subInfo == FlowInfo.DEAD_END) {
            this.bits |= 0x4000;
            this.scope.problemReporter().finallyMustCompleteNormally(this.finallyBlock);
        } else {
            FlowInfo finallyInfo;
            this.tryBlock.scope.finallyInfo = finallyInfo = subInfo.copy();
            if (this.catchBlocks != null) {
                i = 0;
                while (i < this.catchBlocks.length) {
                    this.catchBlocks[i].scope.finallyInfo = finallyInfo;
                    ++i;
                }
            }
        }
        this.subRoutineInits = subInfo;
        FlowInfo tryInfo = flowInfo.copy();
        i = 0;
        while (i < resourcesLength) {
            MethodBinding closeMethod;
            Statement resource = this.resources[i];
            tryInfo = resource.analyseCode(currentScope, handlingContext, tryInfo);
            this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(tryInfo);
            TypeBinding resolvedType = null;
            LocalVariableBinding localVariableBinding = null;
            if (resource instanceof LocalDeclaration) {
                localVariableBinding = ((LocalDeclaration)this.resources[i]).binding;
                resolvedType = localVariableBinding.type;
                if (localVariableBinding.closeTracker != null) {
                    localVariableBinding.closeTracker.withdraw();
                }
            } else {
                if (resource instanceof NameReference && ((NameReference)resource).binding instanceof LocalVariableBinding) {
                    localVariableBinding = (LocalVariableBinding)((NameReference)resource).binding;
                }
                this.recordCallingClose(currentScope, flowContext, tryInfo, (Expression)resource);
                resolvedType = ((Expression)resource).resolvedType;
            }
            if (localVariableBinding != null) {
                localVariableBinding.useFlag = 1;
            }
            if ((closeMethod = this.findCloseMethod(resource, resolvedType)) != null && closeMethod.isValidBinding() && closeMethod.returnType.id == 6) {
                ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
                int j = 0;
                int length = thrownExceptions.length;
                while (j < length) {
                    handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[i], tryInfo, currentScope, true);
                    ++j;
                }
            }
            ++i;
        }
        if (!this.tryBlock.isEmptyBlock()) {
            tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, tryInfo);
            if ((tryInfo.tagBits & 1) != 0) {
                this.bits |= 0x20000000;
            }
        }
        if (resourcesLength > 0) {
            this.postTryInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
            i = 0;
            while (i < resourcesLength) {
                if (this.resources[i] instanceof LocalDeclaration) {
                    tryInfo.resetAssignmentInfo(((LocalDeclaration)this.resources[i]).binding);
                }
                ++i;
            }
        }
        handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);
        if (this.catchArguments != null) {
            int catchCount = this.catchBlocks.length;
            this.catchExits = new boolean[catchCount];
            this.catchExitInitStateIndexes = new int[catchCount];
            int i4 = 0;
            while (i4 < catchCount) {
                FlowInfo catchInfo = this.prepareCatchInfo(flowInfo, handlingContext, tryInfo, i4);
                insideSubContext.conditionalLevel = 1;
                catchInfo = this.catchBlocks[i4].analyseCode(currentScope, insideSubContext, catchInfo);
                this.catchExitInitStateIndexes[i4] = currentScope.methodScope().recordInitializationStates(catchInfo);
                this.catchExits[i4] = (catchInfo.tagBits & 1) != 0;
                tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
                ++i4;
            }
        }
        finallyContext.complainOnDeferredChecks(((tryInfo.tagBits & 3) == 0 ? flowInfo.unconditionalCopy().addPotentialInitializationsFrom(tryInfo).addPotentialInitializationsFrom(insideSubContext.initsOnReturn) : insideSubContext.initsOnReturn).addNullInfoFrom(handlingContext.initsOnFinally), currentScope);
        flowContext.mergeFinallyNullInfo(handlingContext.initsOnFinally);
        this.naturalExitMergeInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
        if (subInfo == FlowInfo.DEAD_END) {
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(subInfo);
            return subInfo;
        }
        FlowInfo mergedInfo = tryInfo.addInitializationsFrom(subInfo);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }

    private void recordCallingClose(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Expression closeTarget) {
        FakedTrackingVariable trackingVariable = FakedTrackingVariable.getCloseTrackingVariable(closeTarget, flowInfo, flowContext);
        if (trackingVariable != null) {
            if (trackingVariable.methodScope == currentScope.methodScope()) {
                trackingVariable.markClose(flowInfo, flowContext);
            } else {
                trackingVariable.markClosedInNestedMethod();
            }
            trackingVariable.markClosedEffectivelyFinal();
        }
    }

    private MethodBinding findCloseMethod(ASTNode resource, TypeBinding type) {
        ReferenceBinding binding;
        MethodBinding closeMethod = null;
        if (type != null && type.isValidBinding() && type instanceof ReferenceBinding && (closeMethod = (binding = (ReferenceBinding)type).getExactMethod(ConstantPool.Close, new TypeBinding[0], this.scope.compilationUnitScope())) == null) {
            InvocationSite.EmptyWithAstNode site = new InvocationSite.EmptyWithAstNode(resource);
            closeMethod = this.scope.compilationUnitScope().findMethod(binding, ConstantPool.Close, new TypeBinding[0], site, false);
        }
        return closeMethod;
    }

    private FlowInfo prepareCatchInfo(FlowInfo flowInfo, ExceptionHandlingFlowContext handlingContext, FlowInfo tryInfo, int i) {
        FlowInfo catchInfo;
        if (this.isUncheckedCatchBlock(i)) {
            catchInfo = flowInfo.unconditionalCopy().addPotentialInitializationsFrom(handlingContext.initsOnException(i)).addPotentialInitializationsFrom(tryInfo).addPotentialInitializationsFrom(handlingContext.initsOnReturn).addNullInfoFrom(handlingContext.initsOnFinally);
        } else {
            UnconditionalFlowInfo initsOnException = handlingContext.initsOnException(i);
            catchInfo = flowInfo.nullInfoLessUnconditionalCopy().addPotentialInitializationsFrom(initsOnException).addNullInfoFrom(initsOnException).addPotentialInitializationsFrom(tryInfo.nullInfoLessUnconditionalCopy()).addPotentialInitializationsFrom(handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
        }
        LocalVariableBinding catchArg = this.catchArguments[i].binding;
        catchInfo.markAsDefinitelyAssigned(catchArg);
        catchInfo.markAsDefinitelyNonNull(catchArg);
        if (this.tryBlock.statements == null && this.resources == null) {
            catchInfo.setReachMode(1);
        }
        return catchInfo;
    }

    private boolean isUncheckedCatchBlock(int catchBlock) {
        if (this.caughtExceptionsCatchBlocks == null) {
            return this.caughtExceptionTypes[catchBlock].isUncheckedException(true);
        }
        int i = 0;
        int length = this.caughtExceptionsCatchBlocks.length;
        while (i < length) {
            if (this.caughtExceptionsCatchBlocks[i] == catchBlock && this.caughtExceptionTypes[i].isUncheckedException(true)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream) {
        if (this.subRoutineStartLabel == null) {
            return null;
        }
        return super.enterAnyExceptionHandler(codeStream);
    }

    @Override
    public void enterDeclaredExceptionHandlers(CodeStream codeStream) {
        int i = 0;
        int length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length;
        while (i < length) {
            this.declaredExceptionLabels[i].placeStart();
            ++i;
        }
        int resourceCount = this.resources.length;
        if (resourceCount > 0 && this.resourceExceptionLabels != null) {
            int i2 = resourceCount;
            while (i2 >= 0) {
                this.resourceExceptionLabels[i2].placeStart();
                --i2;
            }
        }
    }

    @Override
    public void exitAnyExceptionHandler() {
        if (this.subRoutineStartLabel == null) {
            return;
        }
        super.exitAnyExceptionHandler();
    }

    @Override
    public void exitDeclaredExceptionHandlers(CodeStream codeStream) {
        int i = 0;
        int length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length;
        while (i < length) {
            this.declaredExceptionLabels[i].placeEnd();
            ++i;
        }
    }

    private int finallyMode() {
        if (this.subRoutineStartLabel == null) {
            return 0;
        }
        if (this.isSubRoutineEscaping()) {
            return 2;
        }
        if (this.scope.compilerOptions().inlineJsrBytecode) {
            return 3;
        }
        return 1;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        boolean tryBlockHasSomeCode;
        ExceptionLabel[] exceptionLabels;
        int maxCatches;
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
        this.anyExceptionLabel = null;
        this.reusableJSRTargets = null;
        this.reusableJSRSequenceStartLabels = null;
        this.reusableJSRTargetsCount = 0;
        int pc = codeStream.position;
        int finallyMode = this.finallyMode();
        boolean requiresNaturalExit = false;
        int n = maxCatches = this.catchArguments == null ? 0 : this.catchArguments.length;
        if (maxCatches > 0) {
            exceptionLabels = new ExceptionLabel[maxCatches];
            int i = 0;
            while (i < maxCatches) {
                Argument argument = this.catchArguments[i];
                ExceptionLabel exceptionLabel = null;
                if ((argument.binding.tagBits & 0x1000L) != 0L) {
                    MultiCatchExceptionLabel multiCatchExceptionLabel = new MultiCatchExceptionLabel(codeStream, argument.binding.type);
                    multiCatchExceptionLabel.initialize((UnionTypeReference)argument.type, argument.annotations);
                    exceptionLabel = multiCatchExceptionLabel;
                } else {
                    exceptionLabel = new ExceptionLabel(codeStream, argument.binding.type, argument.type, argument.annotations);
                }
                exceptionLabel.placeStart();
                exceptionLabels[i] = exceptionLabel;
                ++i;
            }
        } else {
            exceptionLabels = null;
        }
        if (this.subRoutineStartLabel != null) {
            this.subRoutineStartLabel.initialize(codeStream);
            this.enterAnyExceptionHandler(codeStream);
        }
        try {
            this.declaredExceptionLabels = exceptionLabels;
            int resourceCount = this.resources.length;
            if (resourceCount > 0) {
                this.resourceExceptionLabels = new ExceptionLabel[resourceCount + 1];
                codeStream.aconst_null();
                codeStream.store(this.primaryExceptionVariable, false);
                codeStream.addVariable(this.primaryExceptionVariable);
                codeStream.aconst_null();
                codeStream.store(this.caughtThrowableVariable, false);
                codeStream.addVariable(this.caughtThrowableVariable);
                int i = 0;
                while (i <= resourceCount) {
                    this.resourceExceptionLabels[i] = new ExceptionLabel(codeStream, null);
                    this.resourceExceptionLabels[i].placeStart();
                    if (i < resourceCount) {
                        Statement stmt = this.resources[i];
                        if (stmt instanceof NameReference) {
                            NameReference ref = (NameReference)stmt;
                            ref.bits |= 0x80000;
                            VariableBinding binding = (VariableBinding)ref.binding;
                            ref.checkEffectiveFinality(binding, this.scope);
                        } else if (stmt instanceof FieldReference) {
                            FieldReference fieldReference = (FieldReference)stmt;
                            if (!fieldReference.binding.isFinal()) {
                                this.scope.problemReporter().cannotReferToNonFinalField(fieldReference.binding, fieldReference);
                            }
                        }
                        stmt.generateCode(this.scope, codeStream);
                    }
                    ++i;
                }
            }
            this.tryBlock.generateCode(this.scope, codeStream);
            if (resourceCount > 0) {
                int i = resourceCount;
                while (i >= 0) {
                    Statement stmt;
                    BranchLabel exitLabel = new BranchLabel(codeStream);
                    this.resourceExceptionLabels[i].placeEnd();
                    Statement statement = stmt = i > 0 ? this.resources[i - 1] : null;
                    if ((this.bits & 0x20000000) == 0) {
                        if (i > 0) {
                            int invokeCloseStartPc = codeStream.position;
                            if (this.postTryInitStateIndex != -1) {
                                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postTryInitStateIndex);
                                codeStream.addDefinitelyAssignedVariables(currentScope, this.postTryInitStateIndex);
                            }
                            this.generateCodeSnippet(stmt, codeStream, exitLabel, false, new int[0]);
                            codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
                        }
                        codeStream.goto_(exitLabel);
                    }
                    if (i > 0) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[i - 1]);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[i - 1]);
                    } else {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                    }
                    codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                    this.resourceExceptionLabels[i].place();
                    if (i == resourceCount) {
                        codeStream.store(this.primaryExceptionVariable, false);
                    } else {
                        BranchLabel elseLabel = new BranchLabel(codeStream);
                        BranchLabel postElseLabel = new BranchLabel(codeStream);
                        codeStream.store(this.caughtThrowableVariable, false);
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.ifnonnull(elseLabel);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.store(this.primaryExceptionVariable, false);
                        codeStream.goto_(postElseLabel);
                        elseLabel.place();
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.if_acmpeq(postElseLabel);
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.invokeThrowableAddSuppressed();
                        postElseLabel.place();
                    }
                    if (i > 0) {
                        BranchLabel postCloseLabel = new BranchLabel(codeStream);
                        this.generateCodeSnippet(stmt, codeStream, postCloseLabel, true, i, codeStream.position);
                        postCloseLabel.place();
                    }
                    codeStream.load(this.primaryExceptionVariable);
                    codeStream.athrow();
                    exitLabel.place();
                    --i;
                }
                codeStream.removeVariable(this.primaryExceptionVariable);
                codeStream.removeVariable(this.caughtThrowableVariable);
            }
        }
        finally {
            this.declaredExceptionLabels = null;
            this.resourceExceptionLabels = null;
        }
        boolean bl = tryBlockHasSomeCode = codeStream.position != pc;
        if (tryBlockHasSomeCode) {
            BranchLabel naturalExitLabel = new BranchLabel(codeStream);
            BranchLabel postCatchesFinallyLabel = null;
            int i = 0;
            while (i < maxCatches) {
                exceptionLabels[i].placeEnd();
                ++i;
            }
            if ((this.bits & 0x20000000) == 0) {
                int position = codeStream.position;
                switch (finallyMode) {
                    case 1: 
                    case 3: {
                        requiresNaturalExit = true;
                        if (this.naturalExitMergeInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                        }
                        codeStream.goto_(naturalExitLabel);
                        break;
                    }
                    case 0: {
                        if (this.naturalExitMergeInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                        }
                        codeStream.goto_(naturalExitLabel);
                        break;
                    }
                    case 2: {
                        codeStream.goto_(this.subRoutineStartLabel);
                    }
                }
                codeStream.recordPositionsFrom(position, this.tryBlock.sourceEnd);
            }
            this.exitAnyExceptionHandler();
            if (this.catchArguments != null) {
                postCatchesFinallyLabel = new BranchLabel(codeStream);
                i = 0;
                while (i < maxCatches) {
                    if (exceptionLabels[i].getCount() != 0) {
                        this.enterAnyExceptionHandler(codeStream);
                        if (this.preTryInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        }
                        codeStream.pushExceptionOnStack(exceptionLabels[i].exceptionType);
                        exceptionLabels[i].place();
                        int varPC = codeStream.position;
                        LocalVariableBinding catchVar = this.catchArguments[i].binding;
                        if (catchVar.resolvedPosition != -1) {
                            codeStream.store(catchVar, false);
                            catchVar.recordInitializationStartPC(codeStream.position);
                            codeStream.addVisibleLocalVariable(catchVar);
                        } else {
                            codeStream.pop();
                        }
                        codeStream.recordPositionsFrom(varPC, this.catchArguments[i].sourceStart);
                        this.catchBlocks[i].generateCode(this.scope, codeStream);
                        this.exitAnyExceptionHandler();
                        if (!this.catchExits[i]) {
                            switch (finallyMode) {
                                case 3: {
                                    if (isStackMapFrameCodeStream) {
                                        ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
                                    }
                                    if (this.catchExitInitStateIndexes[i] != -1) {
                                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
                                        codeStream.addDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
                                    }
                                    this.finallyBlock.generateCode(this.scope, codeStream);
                                    codeStream.goto_(postCatchesFinallyLabel);
                                    if (!isStackMapFrameCodeStream) break;
                                    ((StackMapFrameCodeStream)codeStream).popStateIndex();
                                    break;
                                }
                                case 1: {
                                    requiresNaturalExit = true;
                                }
                                case 0: {
                                    if (this.naturalExitMergeInitStateIndex != -1) {
                                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                        codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                    }
                                    codeStream.goto_(naturalExitLabel);
                                    break;
                                }
                                case 2: {
                                    codeStream.goto_(this.subRoutineStartLabel);
                                }
                            }
                        }
                    }
                    ++i;
                }
            }
            ExceptionLabel naturalExitExceptionHandler = requiresNaturalExit && finallyMode == 1 ? new ExceptionLabel(codeStream, null) : null;
            int finallySequenceStartPC = codeStream.position;
            if (this.subRoutineStartLabel != null && this.anyExceptionLabel.getCount() != 0) {
                codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                if (this.preTryInitStateIndex != -1) {
                    codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                }
                this.placeAllAnyExceptionHandler();
                if (naturalExitExceptionHandler != null) {
                    naturalExitExceptionHandler.place();
                }
                switch (finallyMode) {
                    case 1: {
                        codeStream.store(this.anyExceptionVariable, false);
                        codeStream.jsr(this.subRoutineStartLabel);
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        int position = codeStream.position;
                        codeStream.throwAnyException(this.anyExceptionVariable);
                        codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
                        this.subRoutineStartLabel.place();
                        codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                        position = codeStream.position;
                        codeStream.store(this.returnAddressVariable, false);
                        codeStream.recordPositionsFrom(position, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(this.scope, codeStream);
                        position = codeStream.position;
                        codeStream.ret(this.returnAddressVariable.resolvedPosition);
                        codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
                        break;
                    }
                    case 3: {
                        codeStream.store(this.anyExceptionVariable, false);
                        codeStream.addVariable(this.anyExceptionVariable);
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(currentScope, codeStream);
                        int position = codeStream.position;
                        codeStream.throwAnyException(this.anyExceptionVariable);
                        codeStream.removeVariable(this.anyExceptionVariable);
                        if (this.preTryInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        }
                        this.subRoutineStartLabel.place();
                        codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
                        break;
                    }
                    case 2: {
                        codeStream.pop();
                        this.subRoutineStartLabel.place();
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(this.scope, codeStream);
                    }
                }
                if (requiresNaturalExit) {
                    switch (finallyMode) {
                        case 1: {
                            naturalExitLabel.place();
                            int position = codeStream.position;
                            naturalExitExceptionHandler.placeStart();
                            codeStream.jsr(this.subRoutineStartLabel);
                            naturalExitExceptionHandler.placeEnd();
                            codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
                            break;
                        }
                        case 3: {
                            if (isStackMapFrameCodeStream) {
                                ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
                            }
                            if (this.naturalExitMergeInitStateIndex != -1) {
                                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            }
                            naturalExitLabel.place();
                            this.finallyBlock.generateCode(this.scope, codeStream);
                            if (postCatchesFinallyLabel != null) {
                                int position = codeStream.position;
                                codeStream.goto_(postCatchesFinallyLabel);
                                codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
                            }
                            if (!isStackMapFrameCodeStream) break;
                            ((StackMapFrameCodeStream)codeStream).popStateIndex();
                            break;
                        }
                        case 2: {
                            break;
                        }
                        default: {
                            naturalExitLabel.place();
                        }
                    }
                }
                if (postCatchesFinallyLabel != null) {
                    postCatchesFinallyLabel.place();
                }
            } else {
                naturalExitLabel.place();
            }
        } else if (this.subRoutineStartLabel != null) {
            this.finallyBlock.generateCode(this.scope, codeStream);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    private void generateCodeSnippet(Statement statement, CodeStream codeStream, BranchLabel postCloseLabel, boolean record, int ... values) {
        int i = -1;
        int invokeCloseStartPc = -1;
        if (record) {
            i = values[0];
            invokeCloseStartPc = values[1];
        }
        if (statement instanceof LocalDeclaration) {
            this.generateCodeSnippet((LocalDeclaration)statement, codeStream, postCloseLabel, record, i, invokeCloseStartPc);
        } else if (statement instanceof Reference) {
            this.generateCodeSnippet((Reference)statement, codeStream, postCloseLabel, record, i, invokeCloseStartPc);
        }
    }

    private void generateCodeSnippet(Reference reference, CodeStream codeStream, BranchLabel postCloseLabel, boolean record, int i, int invokeCloseStartPc) {
        reference.generateCode(this.scope, codeStream, true);
        codeStream.ifnull(postCloseLabel);
        reference.generateCode(this.scope, codeStream, true);
        codeStream.invokeAutoCloseableClose(reference.resolvedType);
        if (!record) {
            return;
        }
        codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
        this.isDuplicateResourceReference(i);
    }

    private void generateCodeSnippet(LocalDeclaration localDeclaration, CodeStream codeStream, BranchLabel postCloseLabel, boolean record, int i, int invokeCloseStartPc) {
        LocalVariableBinding variableBinding = localDeclaration.binding;
        codeStream.load(variableBinding);
        codeStream.ifnull(postCloseLabel);
        codeStream.load(variableBinding);
        codeStream.invokeAutoCloseableClose(variableBinding.type);
        if (!record) {
            return;
        }
        codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
        if (!this.isDuplicateResourceReference(i)) {
            codeStream.removeVariable(variableBinding);
        }
    }

    private boolean isDuplicateResourceReference(int index) {
        int len = this.resources.length;
        if (index < len && this.resources[index] instanceof Reference) {
            Binding refBinding;
            Reference ref = (Reference)this.resources[index];
            Binding binding = ref instanceof NameReference ? ((NameReference)ref).binding : (refBinding = ref instanceof FieldReference ? ((FieldReference)ref).binding : null);
            if (refBinding == null) {
                return false;
            }
            int i = 0;
            while (i < index) {
                LocalVariableBinding b;
                Statement stmt = this.resources[i];
                Binding binding2 = stmt instanceof LocalDeclaration ? ((LocalDeclaration)stmt).binding : (stmt instanceof NameReference ? ((NameReference)stmt).binding : (b = stmt instanceof FieldReference ? ((FieldReference)stmt).binding : null));
                if (b == refBinding) {
                    this.scope.problemReporter().duplicateResourceReference(ref);
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    @Override
    public boolean generateSubRoutineInvocation(BlockScope currentScope, CodeStream codeStream, Object targetLocation, int stateIndex, LocalVariableBinding secretLocal) {
        int resourceCount = this.resources.length;
        if (resourceCount > 0 && this.resourceExceptionLabels != null) {
            int i = resourceCount;
            while (i > 0) {
                this.resourceExceptionLabels[i].placeEnd();
                BranchLabel exitLabel = new BranchLabel(codeStream);
                int invokeCloseStartPc = codeStream.position;
                this.generateCodeSnippet(this.resources[i - 1], codeStream, exitLabel, false, new int[0]);
                codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
                exitLabel.place();
                --i;
            }
            this.resourceExceptionLabels[0].placeEnd();
        }
        boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
        int finallyMode = this.finallyMode();
        switch (finallyMode) {
            case 2: {
                if (this.switchExpression != null) {
                    this.finallyBlock.generateCode(currentScope, codeStream);
                    return true;
                }
                codeStream.goto_(this.subRoutineStartLabel);
                return true;
            }
            case 0: {
                if (this.switchExpression == null) {
                    this.exitDeclaredExceptionHandlers(codeStream);
                }
                return false;
            }
        }
        CompilerOptions options = this.scope.compilerOptions();
        if (options.shareCommonFinallyBlocks && targetLocation != null) {
            boolean reuseTargetLocation = true;
            if (this.reusableJSRTargetsCount > 0) {
                int i = 0;
                int count = this.reusableJSRTargetsCount;
                while (i < count) {
                    Object reusableJSRTarget = this.reusableJSRTargets[i];
                    if (targetLocation == reusableJSRTarget || targetLocation instanceof Constant && reusableJSRTarget instanceof Constant && ((Constant)targetLocation).hasSameValue((Constant)reusableJSRTarget)) {
                        if (this.reusableJSRStateIndexes[i] != stateIndex && finallyMode == 3) {
                            reuseTargetLocation = false;
                            break;
                        }
                        codeStream.goto_(this.reusableJSRSequenceStartLabels[i]);
                        return true;
                    }
                    ++i;
                }
            } else {
                this.reusableJSRTargets = new Object[3];
                this.reusableJSRSequenceStartLabels = new BranchLabel[3];
                this.reusableJSRStateIndexes = new int[3];
            }
            if (reuseTargetLocation) {
                if (this.reusableJSRTargetsCount == this.reusableJSRTargets.length) {
                    this.reusableJSRTargets = new Object[2 * this.reusableJSRTargetsCount];
                    System.arraycopy(this.reusableJSRTargets, 0, this.reusableJSRTargets, 0, this.reusableJSRTargetsCount);
                    this.reusableJSRSequenceStartLabels = new BranchLabel[2 * this.reusableJSRTargetsCount];
                    System.arraycopy(this.reusableJSRSequenceStartLabels, 0, this.reusableJSRSequenceStartLabels, 0, this.reusableJSRTargetsCount);
                    this.reusableJSRStateIndexes = new int[2 * this.reusableJSRTargetsCount];
                    System.arraycopy(this.reusableJSRStateIndexes, 0, this.reusableJSRStateIndexes, 0, this.reusableJSRTargetsCount);
                }
                this.reusableJSRTargets[this.reusableJSRTargetsCount] = targetLocation;
                BranchLabel reusableJSRSequenceStartLabel = new BranchLabel(codeStream);
                reusableJSRSequenceStartLabel.place();
                this.reusableJSRStateIndexes[this.reusableJSRTargetsCount] = stateIndex;
                this.reusableJSRSequenceStartLabels[this.reusableJSRTargetsCount++] = reusableJSRSequenceStartLabel;
            }
        }
        if (finallyMode == 3) {
            if (isStackMapFrameCodeStream) {
                ((StackMapFrameCodeStream)codeStream).pushStateIndex(stateIndex);
            }
            this.exitAnyExceptionHandler();
            this.exitDeclaredExceptionHandlers(codeStream);
            this.finallyBlock.generateCode(currentScope, codeStream);
            if (isStackMapFrameCodeStream) {
                ((StackMapFrameCodeStream)codeStream).popStateIndex();
            }
        } else {
            codeStream.jsr(this.subRoutineStartLabel);
            this.exitAnyExceptionHandler();
            this.exitDeclaredExceptionHandlers(codeStream);
        }
        return false;
    }

    @Override
    public boolean isSubRoutineEscaping() {
        return (this.bits & 0x4000) != 0;
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        int length = this.resources.length;
        TryStatement.printIndent(indent, output).append("try" + (length == 0 ? "\n" : " ("));
        int i = 0;
        while (i < length) {
            block11: {
                block10: {
                    Statement stmt;
                    block9: {
                        stmt = this.resources[i];
                        if (!(stmt instanceof LocalDeclaration)) break block9;
                        ((LocalDeclaration)stmt).printAsExpression(0, output);
                        break block10;
                    }
                    if (!(stmt instanceof Reference)) break block11;
                    ((Reference)stmt).printExpression(0, output);
                }
                if (i != length - 1) {
                    output.append(";\n");
                    TryStatement.printIndent(indent + 2, output);
                }
            }
            ++i;
        }
        if (length > 0) {
            output.append(")\n");
        }
        this.tryBlock.printStatement(indent + 1, output);
        if (this.catchBlocks != null) {
            i = 0;
            while (i < this.catchBlocks.length) {
                output.append('\n');
                TryStatement.printIndent(indent, output).append("catch (");
                this.catchArguments[i].print(0, output).append(")\n");
                this.catchBlocks[i].printStatement(indent + 1, output);
                ++i;
            }
        }
        if (this.finallyBlock != null) {
            output.append('\n');
            TryStatement.printIndent(indent, output).append("finally\n");
            this.finallyBlock.printStatement(indent + 1, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope upperScope) {
        this.scope = new BlockScope(upperScope);
        BlockScope finallyScope = null;
        BlockScope resourceManagementScope = null;
        int resourceCount = this.resources.length;
        if (resourceCount > 0) {
            resourceManagementScope = new BlockScope(this.scope);
            this.primaryExceptionVariable = new LocalVariableBinding(SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME, (TypeBinding)this.scope.getJavaLangThrowable(), 0, false);
            resourceManagementScope.addLocalVariable(this.primaryExceptionVariable);
            this.primaryExceptionVariable.setConstant(Constant.NotAConstant);
            this.caughtThrowableVariable = new LocalVariableBinding(SECRET_CAUGHT_THROWABLE_VARIABLE_NAME, (TypeBinding)this.scope.getJavaLangThrowable(), 0, false);
            resourceManagementScope.addLocalVariable(this.caughtThrowableVariable);
            this.caughtThrowableVariable.setConstant(Constant.NotAConstant);
        }
        int i = 0;
        while (i < resourceCount) {
            Statement node;
            this.resources[i].resolve(resourceManagementScope);
            if (this.resources[i] instanceof LocalDeclaration) {
                node = (LocalDeclaration)this.resources[i];
                LocalVariableBinding localVariableBinding = node.binding;
                if (localVariableBinding != null && localVariableBinding.isValidBinding()) {
                    localVariableBinding.modifiers |= 0x10;
                    localVariableBinding.tagBits |= 0x2000L;
                    TypeBinding resourceType = localVariableBinding.type;
                    if (resourceType instanceof ReferenceBinding) {
                        if (resourceType.findSuperTypeOriginatingFrom(62, false) == null && resourceType.isValidBinding()) {
                            upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, node.type);
                            localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                        }
                    } else if (resourceType != null) {
                        upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, node.type);
                        localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                    }
                }
            } else {
                node = (Expression)this.resources[i];
                TypeBinding resourceType = ((Expression)node).resolvedType;
                if (resourceType instanceof ReferenceBinding) {
                    if (resourceType.findSuperTypeOriginatingFrom(62, false) == null && resourceType.isValidBinding()) {
                        upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, node);
                        ((Expression)this.resources[i]).resolvedType = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                    }
                } else if (resourceType != null) {
                    upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, node);
                    ((Expression)this.resources[i]).resolvedType = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                }
            }
            ++i;
        }
        BlockScope tryScope = new BlockScope(resourceManagementScope != null ? resourceManagementScope : this.scope);
        if (this.finallyBlock != null) {
            if (this.finallyBlock.isEmptyBlock()) {
                if ((this.finallyBlock.bits & 8) != 0) {
                    this.scope.problemReporter().undocumentedEmptyBlock(this.finallyBlock.sourceStart, this.finallyBlock.sourceEnd);
                }
            } else {
                finallyScope = new BlockScope(this.scope, false);
                MethodScope methodScope = this.scope.methodScope();
                if (!upperScope.compilerOptions().inlineJsrBytecode) {
                    this.returnAddressVariable = new LocalVariableBinding(SECRET_RETURN_ADDRESS_NAME, (TypeBinding)upperScope.getJavaLangObject(), 0, false);
                    finallyScope.addLocalVariable(this.returnAddressVariable);
                    this.returnAddressVariable.setConstant(Constant.NotAConstant);
                }
                this.subRoutineStartLabel = new BranchLabel();
                this.anyExceptionVariable = new LocalVariableBinding(SECRET_ANY_HANDLER_NAME, (TypeBinding)this.scope.getJavaLangThrowable(), 0, false);
                finallyScope.addLocalVariable(this.anyExceptionVariable);
                this.anyExceptionVariable.setConstant(Constant.NotAConstant);
                if (!methodScope.isInsideInitializer()) {
                    MethodBinding methodBinding;
                    MethodBinding methodBinding2 = methodScope.referenceContext instanceof AbstractMethodDeclaration ? ((AbstractMethodDeclaration)methodScope.referenceContext).binding : (methodBinding = methodScope.referenceContext instanceof LambdaExpression ? ((LambdaExpression)methodScope.referenceContext).binding : null);
                    if (methodBinding != null) {
                        TypeBinding methodReturnType = methodBinding.returnType;
                        if (methodReturnType.id != 6) {
                            this.secretReturnValue = new LocalVariableBinding(SECRET_RETURN_VALUE_NAME, methodReturnType, 0, false);
                            finallyScope.addLocalVariable(this.secretReturnValue);
                            this.secretReturnValue.setConstant(Constant.NotAConstant);
                        }
                    }
                }
                this.finallyBlock.resolveUsing(finallyScope);
                int shiftScopesLength = this.catchArguments == null ? 1 : this.catchArguments.length + 1;
                finallyScope.shiftScopes = new BlockScope[shiftScopesLength];
                finallyScope.shiftScopes[0] = tryScope;
            }
        }
        this.tryBlock.resolveUsing(tryScope);
        if (this.catchBlocks != null) {
            int length = this.catchArguments.length;
            TypeBinding[] argumentTypes = new TypeBinding[length];
            boolean containsUnionTypes = false;
            boolean catchHasError = false;
            int i2 = 0;
            while (i2 < length) {
                BlockScope catchScope = new BlockScope(this.scope);
                if (finallyScope != null) {
                    finallyScope.shiftScopes[i2 + 1] = catchScope;
                }
                Argument catchArgument = this.catchArguments[i2];
                containsUnionTypes |= (catchArgument.type.bits & 0x20000000) != 0;
                argumentTypes[i2] = catchArgument.resolveForCatch(catchScope);
                if (argumentTypes[i2] == null) {
                    catchHasError = true;
                }
                this.catchBlocks[i2].resolveUsing(catchScope);
                ++i2;
            }
            if (catchHasError) {
                return;
            }
            this.verifyDuplicationAndOrder(length, argumentTypes, containsUnionTypes);
        } else {
            this.caughtExceptionTypes = new ReferenceBinding[0];
        }
        if (finallyScope != null) {
            this.scope.addSubscope(finallyScope);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            Statement[] statements = this.resources;
            int i = 0;
            int max = statements.length;
            while (i < max) {
                statements[i].traverse(visitor, this.scope);
                ++i;
            }
            this.tryBlock.traverse(visitor, this.scope);
            if (this.catchArguments != null) {
                i = 0;
                max = this.catchBlocks.length;
                while (i < max) {
                    this.catchArguments[i].traverse(visitor, this.scope);
                    this.catchBlocks[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.finallyBlock != null) {
                this.finallyBlock.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }

    protected void verifyDuplicationAndOrder(int length, TypeBinding[] argumentTypes, boolean containsUnionTypes) {
        if (containsUnionTypes) {
            int totalCount = 0;
            ReferenceBinding[][] allExceptionTypes = new ReferenceBinding[length][];
            int i = 0;
            while (i < length) {
                if (!(argumentTypes[i] instanceof ArrayBinding)) {
                    ReferenceBinding currentExceptionType = (ReferenceBinding)argumentTypes[i];
                    TypeReference catchArgumentType = this.catchArguments[i].type;
                    if ((catchArgumentType.bits & 0x20000000) != 0) {
                        TypeReference[] typeReferences = ((UnionTypeReference)catchArgumentType).typeReferences;
                        int typeReferencesLength = typeReferences.length;
                        ReferenceBinding[] unionExceptionTypes = new ReferenceBinding[typeReferencesLength];
                        int j = 0;
                        while (j < typeReferencesLength) {
                            unionExceptionTypes[j] = (ReferenceBinding)typeReferences[j].resolvedType;
                            ++j;
                        }
                        totalCount += typeReferencesLength;
                        allExceptionTypes[i] = unionExceptionTypes;
                    } else {
                        allExceptionTypes[i] = new ReferenceBinding[]{currentExceptionType};
                        ++totalCount;
                    }
                }
                ++i;
            }
            this.caughtExceptionTypes = new ReferenceBinding[totalCount];
            this.caughtExceptionsCatchBlocks = new int[totalCount];
            i = 0;
            int l = 0;
            while (i < length) {
                ReferenceBinding[] currentExceptions = allExceptionTypes[i];
                if (currentExceptions != null) {
                    int j = 0;
                    int max = currentExceptions.length;
                    block3: while (j < max) {
                        ReferenceBinding exception;
                        this.caughtExceptionTypes[l] = exception = currentExceptions[j];
                        this.caughtExceptionsCatchBlocks[l++] = i;
                        int k = 0;
                        while (k < i) {
                            ReferenceBinding[] exceptions = allExceptionTypes[k];
                            if (exceptions != null) {
                                int n = 0;
                                int max2 = exceptions.length;
                                while (n < max2) {
                                    ReferenceBinding currentException = exceptions[n];
                                    if (exception.isCompatibleWith(currentException)) {
                                        TypeReference catchArgumentType = this.catchArguments[i].type;
                                        if ((catchArgumentType.bits & 0x20000000) != 0) {
                                            catchArgumentType = ((UnionTypeReference)catchArgumentType).typeReferences[j];
                                        }
                                        this.scope.problemReporter().wrongSequenceOfExceptionTypesError(catchArgumentType, exception, currentException);
                                        break block3;
                                    }
                                    ++n;
                                }
                            }
                            ++k;
                        }
                        ++j;
                    }
                }
                ++i;
            }
        } else {
            this.caughtExceptionTypes = new ReferenceBinding[length];
            int i = 0;
            while (i < length) {
                if (!(argumentTypes[i] instanceof ArrayBinding)) {
                    this.caughtExceptionTypes[i] = (ReferenceBinding)argumentTypes[i];
                    int j = 0;
                    while (j < i) {
                        if (this.caughtExceptionTypes[i].isCompatibleWith(argumentTypes[j])) {
                            this.scope.problemReporter().wrongSequenceOfExceptionTypesError(this.catchArguments[i].type, this.caughtExceptionTypes[i], argumentTypes[j]);
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
    }

    @Override
    public boolean doesNotCompleteNormally() {
        if (!this.tryBlock.doesNotCompleteNormally()) {
            return this.finallyBlock != null ? this.finallyBlock.doesNotCompleteNormally() : false;
        }
        if (this.catchBlocks != null) {
            int i = 0;
            while (i < this.catchBlocks.length) {
                if (!this.catchBlocks[i].doesNotCompleteNormally()) {
                    return this.finallyBlock != null ? this.finallyBlock.doesNotCompleteNormally() : false;
                }
                ++i;
            }
        }
        return true;
    }

    @Override
    public boolean completesByContinue() {
        if (this.tryBlock.completesByContinue()) {
            return this.finallyBlock == null ? true : !this.finallyBlock.doesNotCompleteNormally() || this.finallyBlock.completesByContinue();
        }
        if (this.catchBlocks != null) {
            int i = 0;
            while (i < this.catchBlocks.length) {
                if (this.catchBlocks[i].completesByContinue()) {
                    return this.finallyBlock == null ? true : !this.finallyBlock.doesNotCompleteNormally() || this.finallyBlock.completesByContinue();
                }
                ++i;
            }
        }
        return this.finallyBlock != null && this.finallyBlock.completesByContinue();
    }

    @Override
    public boolean canCompleteNormally() {
        if (this.tryBlock.canCompleteNormally()) {
            return this.finallyBlock != null ? this.finallyBlock.canCompleteNormally() : true;
        }
        if (this.catchBlocks != null) {
            int i = 0;
            while (i < this.catchBlocks.length) {
                if (this.catchBlocks[i].canCompleteNormally()) {
                    return this.finallyBlock != null ? this.finallyBlock.canCompleteNormally() : true;
                }
                ++i;
            }
        }
        return false;
    }

    @Override
    public boolean continueCompletes() {
        if (this.tryBlock.continueCompletes()) {
            return this.finallyBlock == null ? true : this.finallyBlock.canCompleteNormally() || this.finallyBlock.continueCompletes();
        }
        if (this.catchBlocks != null) {
            int i = 0;
            while (i < this.catchBlocks.length) {
                if (this.catchBlocks[i].continueCompletes()) {
                    return this.finallyBlock == null ? true : this.finallyBlock.canCompleteNormally() || this.finallyBlock.continueCompletes();
                }
                ++i;
            }
        }
        return this.finallyBlock != null && this.finallyBlock.continueCompletes();
    }
}

