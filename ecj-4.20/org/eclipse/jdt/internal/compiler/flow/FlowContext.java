/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionInferenceFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FieldInitsFakingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class FlowContext
implements TypeConstants {
    public static final FlowContext NotContinuableContext = new FlowContext(null, null, true);
    public ASTNode associatedNode;
    public FlowContext parent;
    public FlowInfo initsOnFinally;
    public int conditionalLevel = -1;
    public int tagBits;
    public TypeBinding[][] providedExpectedTypes = null;
    private Reference[] nullCheckedFieldReferences = null;
    private int[] timesToLiveForNullCheckInfo = null;
    public static final int DEFER_NULL_DIAGNOSTIC = 1;
    public static final int PREEMPT_NULL_DIAGNOSTIC = 2;
    public static final int INSIDE_NEGATION = 4;
    public static final int HIDE_NULL_COMPARISON_WARNING = 4096;
    public static final int HIDE_NULL_COMPARISON_WARNING_MASK = 61440;
    public static final int CAN_ONLY_NULL_NON_NULL = 0;
    public static final int CAN_ONLY_NULL = 1;
    public static final int CAN_ONLY_NON_NULL = 2;
    public static final int MAY_NULL = 3;
    public static final int ASSIGN_TO_NONNULL = 128;
    public static final int IN_UNBOXING = 16;
    public static final int EXIT_RESOURCE = 2048;
    public static final int CHECK_MASK = 255;
    public static final int IN_COMPARISON_NULL = 256;
    public static final int IN_COMPARISON_NON_NULL = 512;
    public static final int IN_ASSIGNMENT = 768;
    public static final int IN_INSTANCEOF = 1024;
    public static final int CONTEXT_MASK = -61696;

    public FlowContext(FlowContext parent, ASTNode associatedNode, boolean inheritNullFieldChecks) {
        this.parent = parent;
        this.associatedNode = associatedNode;
        if (parent != null) {
            if ((parent.tagBits & 3) != 0) {
                this.tagBits |= 1;
            }
            this.initsOnFinally = parent.initsOnFinally;
            this.conditionalLevel = parent.conditionalLevel;
            if (inheritNullFieldChecks) {
                this.copyNullCheckedFieldsFrom(parent);
            }
        }
    }

    public void copyNullCheckedFieldsFrom(FlowContext other) {
        Reference[] fieldReferences = other.nullCheckedFieldReferences;
        if (fieldReferences != null && fieldReferences.length > 0 && fieldReferences[0] != null) {
            this.nullCheckedFieldReferences = other.nullCheckedFieldReferences;
            this.timesToLiveForNullCheckInfo = other.timesToLiveForNullCheckInfo;
        }
    }

    public void recordNullCheckedFieldReference(Reference reference, int timeToLive) {
        if (this.nullCheckedFieldReferences == null) {
            Reference[] referenceArray = new Reference[2];
            referenceArray[0] = reference;
            this.nullCheckedFieldReferences = referenceArray;
            this.timesToLiveForNullCheckInfo = new int[]{timeToLive, -1};
        } else {
            int len = this.nullCheckedFieldReferences.length;
            int i = 0;
            while (i < len) {
                if (this.nullCheckedFieldReferences[i] == null) {
                    this.nullCheckedFieldReferences[i] = reference;
                    this.timesToLiveForNullCheckInfo[i] = timeToLive;
                    return;
                }
                ++i;
            }
            this.nullCheckedFieldReferences = new Reference[len + 2];
            System.arraycopy(this.nullCheckedFieldReferences, 0, this.nullCheckedFieldReferences, 0, len);
            this.timesToLiveForNullCheckInfo = new int[len + 2];
            System.arraycopy(this.timesToLiveForNullCheckInfo, 0, this.timesToLiveForNullCheckInfo, 0, len);
            this.nullCheckedFieldReferences[len] = reference;
            this.timesToLiveForNullCheckInfo[len] = timeToLive;
        }
    }

    public void extendTimeToLiveForNullCheckedField(int t) {
        if (this.timesToLiveForNullCheckInfo != null) {
            int i = 0;
            while (i < this.timesToLiveForNullCheckInfo.length) {
                if (this.timesToLiveForNullCheckInfo[i] > 0) {
                    int n = i;
                    this.timesToLiveForNullCheckInfo[n] = this.timesToLiveForNullCheckInfo[n] + t;
                }
                ++i;
            }
        }
    }

    public void expireNullCheckedFieldInfo() {
        if (this.nullCheckedFieldReferences != null) {
            int i = 0;
            while (i < this.nullCheckedFieldReferences.length) {
                int n = i;
                this.timesToLiveForNullCheckInfo[n] = this.timesToLiveForNullCheckInfo[n] - 1;
                if (this.timesToLiveForNullCheckInfo[n] == 0) {
                    this.nullCheckedFieldReferences[i] = null;
                }
                ++i;
            }
        }
    }

    public boolean isNullcheckedFieldAccess(Reference reference) {
        if (this.nullCheckedFieldReferences == null) {
            return false;
        }
        int len = this.nullCheckedFieldReferences.length;
        int i = 0;
        while (i < len) {
            Reference checked = this.nullCheckedFieldReferences[i];
            if (checked != null && checked.isEquivalent(reference)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public BranchLabel breakLabel() {
        return null;
    }

    public void checkExceptionHandlers(TypeBinding raisedException, ASTNode location, FlowInfo flowInfo, BlockScope scope) {
        this.checkExceptionHandlers(raisedException, location, flowInfo, scope, false);
    }

    /*
     * Unable to fully structure code
     */
    public void checkExceptionHandlers(TypeBinding raisedException, ASTNode location, FlowInfo flowInfo, BlockScope scope, boolean isExceptionOnAutoClose) {
        traversedContext = this;
        abruptlyExitedLoops = null;
        if (scope.compilerOptions().sourceLevel < 0x330000L || !(location instanceof ThrowStatement)) ** GOTO lbl65
        throwExpression = ((ThrowStatement)location).exception;
        throwArgBinding = throwExpression.localVariableBinding();
        if (!(throwExpression instanceof SingleNameReference) || !(throwArgBinding instanceof CatchParameterBinding) || !throwArgBinding.isEffectivelyFinal()) ** GOTO lbl65
        parameter = (CatchParameterBinding)throwArgBinding;
        this.checkExceptionHandlers(parameter.getPreciseTypes(), location, flowInfo, scope);
        return;
lbl-1000:
        // 1 sources

        {
            sub = traversedContext.subroutine();
            if (sub != null && sub.isSubRoutineEscaping()) {
                return;
            }
            if (traversedContext instanceof ExceptionHandlingFlowContext) {
                exceptionContext = (ExceptionHandlingFlowContext)traversedContext;
                caughtExceptions = exceptionContext.handledExceptions;
                if (exceptionContext.handledExceptions != Binding.NO_EXCEPTIONS) {
                    definitelyCaught = false;
                    caughtIndex = 0;
                    caughtCount = caughtExceptions.length;
                    while (caughtIndex < caughtCount) {
                        caughtException = caughtExceptions[caughtIndex];
                        exceptionFlow = flowInfo;
                        v0 = state = caughtException == null ? -1 : Scope.compareTypes(raisedException, caughtException);
                        if (abruptlyExitedLoops != null && caughtException != null && state != 0) {
                            i = 0;
                            abruptlyExitedLoopsCount = abruptlyExitedLoops.size();
                            while (i < abruptlyExitedLoopsCount) {
                                loop = (LoopingFlowContext)abruptlyExitedLoops.get(i);
                                loop.recordCatchContextOfEscapingException(exceptionContext, caughtException, flowInfo);
                                ++i;
                            }
                            exceptionFlow = FlowInfo.DEAD_END;
                        }
                        switch (state) {
                            case -1: {
                                exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, raisedException, location, definitelyCaught);
                                definitelyCaught = true;
                                break;
                            }
                            case 1: {
                                exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, caughtException, location, false);
                            }
                        }
                        ++caughtIndex;
                    }
                    if (definitelyCaught) {
                        return;
                    }
                }
                if (exceptionContext.isMethodContext) {
                    if (raisedException.isUncheckedException(false)) {
                        return;
                    }
                    shouldMergeUnhandledExceptions = exceptionContext instanceof ExceptionInferenceFlowContext;
                    if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration && (method = (AbstractMethodDeclaration)exceptionContext.associatedNode).isConstructor() && method.binding.declaringClass.isAnonymousType()) {
                        shouldMergeUnhandledExceptions = true;
                    }
                    if (!shouldMergeUnhandledExceptions) break;
                    exceptionContext.mergeUnhandledException(raisedException);
                    return;
                }
            } else if (traversedContext instanceof LoopingFlowContext) {
                if (abruptlyExitedLoops == null) {
                    abruptlyExitedLoops = new ArrayList<FlowContext>(5);
                }
                abruptlyExitedLoops.add(traversedContext);
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (!isExceptionOnAutoClose && traversedContext instanceof InsideSubRoutineFlowContext && (node = traversedContext.associatedNode) instanceof TryStatement) {
                tryStatement = (TryStatement)node;
                flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
            }
            traversedContext = traversedContext.getLocalParent();
lbl65:
            // 3 sources

            ** while (traversedContext != null)
        }
lbl66:
        // 2 sources

        if (isExceptionOnAutoClose) {
            scope.problemReporter().unhandledExceptionFromAutoClose(raisedException, location);
        } else {
            scope.problemReporter().unhandledException(raisedException, location);
        }
    }

    public void checkExceptionHandlers(TypeBinding[] raisedExceptions, ASTNode location, FlowInfo flowInfo, BlockScope scope) {
        int raisedCount;
        if (raisedExceptions == null || (raisedCount = raisedExceptions.length) == 0) {
            return;
        }
        int remainingCount = raisedCount;
        TypeBinding[] typeBindingArray = raisedExceptions;
        raisedExceptions = new TypeBinding[raisedCount];
        System.arraycopy(typeBindingArray, 0, raisedExceptions, 0, raisedCount);
        FlowContext traversedContext = this;
        ArrayList<FlowContext> abruptlyExitedLoops = null;
        while (traversedContext != null) {
            ASTNode node;
            SubRoutineStatement sub = traversedContext.subroutine();
            if (sub != null && sub.isSubRoutineEscaping()) {
                return;
            }
            if (traversedContext instanceof ExceptionHandlingFlowContext) {
                ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)traversedContext;
                ReferenceBinding[] caughtExceptions = exceptionContext.handledExceptions;
                if (exceptionContext.handledExceptions != Binding.NO_EXCEPTIONS) {
                    int caughtCount = caughtExceptions.length;
                    boolean[] locallyCaught = new boolean[raisedCount];
                    int caughtIndex = 0;
                    while (caughtIndex < caughtCount) {
                        ReferenceBinding caughtException = caughtExceptions[caughtIndex];
                        int raisedIndex = 0;
                        while (raisedIndex < raisedCount) {
                            TypeBinding raisedException = raisedExceptions[raisedIndex];
                            if (raisedException != null) {
                                int state;
                                FlowInfo exceptionFlow = flowInfo;
                                int n = state = caughtException == null ? -1 : Scope.compareTypes(raisedException, caughtException);
                                if (abruptlyExitedLoops != null && caughtException != null && state != 0) {
                                    int i = 0;
                                    int abruptlyExitedLoopsCount = abruptlyExitedLoops.size();
                                    while (i < abruptlyExitedLoopsCount) {
                                        LoopingFlowContext loop = (LoopingFlowContext)abruptlyExitedLoops.get(i);
                                        loop.recordCatchContextOfEscapingException(exceptionContext, caughtException, flowInfo);
                                        ++i;
                                    }
                                    exceptionFlow = FlowInfo.DEAD_END;
                                }
                                switch (state) {
                                    case -1: {
                                        exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, raisedException, location, locallyCaught[raisedIndex]);
                                        if (locallyCaught[raisedIndex]) break;
                                        locallyCaught[raisedIndex] = true;
                                        --remainingCount;
                                        break;
                                    }
                                    case 1: {
                                        exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, caughtException, location, false);
                                    }
                                }
                            }
                            ++raisedIndex;
                        }
                        ++caughtIndex;
                    }
                    int i = 0;
                    while (i < raisedCount) {
                        if (locallyCaught[i]) {
                            raisedExceptions[i] = null;
                        }
                        ++i;
                    }
                }
                if (exceptionContext.isMethodContext) {
                    AbstractMethodDeclaration method;
                    int i = 0;
                    while (i < raisedCount) {
                        TypeBinding raisedException = raisedExceptions[i];
                        if (raisedException != null && raisedException.isUncheckedException(false)) {
                            --remainingCount;
                            raisedExceptions[i] = null;
                        }
                        ++i;
                    }
                    boolean shouldMergeUnhandledException = exceptionContext instanceof ExceptionInferenceFlowContext;
                    if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration && (method = (AbstractMethodDeclaration)exceptionContext.associatedNode).isConstructor() && method.binding.declaringClass.isAnonymousType()) {
                        shouldMergeUnhandledException = true;
                    }
                    if (!shouldMergeUnhandledException) break;
                    int i2 = 0;
                    while (i2 < raisedCount) {
                        TypeBinding raisedException = raisedExceptions[i2];
                        if (raisedException != null) {
                            exceptionContext.mergeUnhandledException(raisedException);
                        }
                        ++i2;
                    }
                    return;
                }
            } else if (traversedContext instanceof LoopingFlowContext) {
                if (abruptlyExitedLoops == null) {
                    abruptlyExitedLoops = new ArrayList<FlowContext>(5);
                }
                abruptlyExitedLoops.add(traversedContext);
            }
            if (remainingCount == 0) {
                return;
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (traversedContext instanceof InsideSubRoutineFlowContext && (node = traversedContext.associatedNode) instanceof TryStatement) {
                TryStatement tryStatement = (TryStatement)node;
                flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
            }
            traversedContext = traversedContext.getLocalParent();
        }
        int i = 0;
        while (i < raisedCount) {
            block31: {
                TypeBinding exception = raisedExceptions[i];
                if (exception != null) {
                    int j = 0;
                    while (j < i) {
                        if (!TypeBinding.equalsEquals(raisedExceptions[j], exception)) {
                            ++j;
                            continue;
                        }
                        break block31;
                    }
                    scope.problemReporter().unhandledException(exception, location);
                }
            }
            ++i;
        }
    }

    public BranchLabel continueLabel() {
        return null;
    }

    public FlowInfo getInitsForFinalBlankInitializationCheck(TypeBinding declaringType, FlowInfo flowInfo) {
        FlowContext current = this;
        FlowInfo inits = flowInfo;
        do {
            if (current instanceof InitializationFlowContext) {
                InitializationFlowContext initializationContext = (InitializationFlowContext)current;
                if (TypeBinding.equalsEquals(((TypeDeclaration)initializationContext.associatedNode).binding, declaringType)) {
                    return inits;
                }
                inits = initializationContext.initsBeforeContext;
                current = initializationContext.initializationParent;
                continue;
            }
            if (current instanceof ExceptionHandlingFlowContext) {
                if (current instanceof FieldInitsFakingFlowContext) {
                    return FlowInfo.DEAD_END;
                }
                ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)current;
                current = exceptionContext.initializationParent == null ? exceptionContext.parent : exceptionContext.initializationParent;
                continue;
            }
            current = current.getLocalParent();
        } while (current != null);
        throw new IllegalStateException(declaringType.debugName());
    }

    public FlowContext getTargetContextForBreakLabel(char[] labelName) {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            char[] currentLabelName;
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if ((currentLabelName = current.labelName()) != null && CharOperation.equals(currentLabelName, labelName)) {
                ((LabeledStatement)current.associatedNode).bits |= 0x40;
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            current = current.getLocalParent();
        }
        return null;
    }

    public FlowContext getTargetContextForContinueLabel(char[] labelName) {
        FlowContext current = this;
        FlowContext lastContinuable = null;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            } else if (current.isContinuable()) {
                lastContinuable = current;
            }
            char[] currentLabelName = current.labelName();
            if (currentLabelName != null && CharOperation.equals(currentLabelName, labelName)) {
                ((LabeledStatement)current.associatedNode).bits |= 0x40;
                if (lastContinuable != null && current.associatedNode.concreteStatement() == lastContinuable.associatedNode) {
                    if (lastNonReturningSubRoutine == null) {
                        return lastContinuable;
                    }
                    return lastNonReturningSubRoutine;
                }
                return NotContinuableContext;
            }
            current = current.getLocalParent();
        }
        return null;
    }

    public FlowContext getTargetContextForDefaultBreak() {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if (current.isBreakable() && current.labelName() == null) {
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            current = current.getLocalParent();
        }
        return null;
    }

    public FlowContext getTargetContextForDefaultYield() {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if (current.isBreakable() && current.labelName() == null && ((SwitchFlowContext)current).isExpression) {
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            current = current.getLocalParent();
        }
        return null;
    }

    public FlowContext getTargetContextForDefaultContinue() {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if (current.isContinuable()) {
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            current = current.getLocalParent();
        }
        return null;
    }

    public FlowContext getInitializationContext() {
        return null;
    }

    public FlowContext getLocalParent() {
        if (this.associatedNode instanceof AbstractMethodDeclaration || this.associatedNode instanceof TypeDeclaration || this.associatedNode instanceof LambdaExpression) {
            return null;
        }
        return this.parent;
    }

    public String individualToString() {
        return "Flow context";
    }

    public FlowInfo initsOnBreak() {
        return FlowInfo.DEAD_END;
    }

    public UnconditionalFlowInfo initsOnReturn() {
        return FlowInfo.DEAD_END;
    }

    public boolean isBreakable() {
        return false;
    }

    public boolean isContinuable() {
        return false;
    }

    public boolean isNonReturningContext() {
        return false;
    }

    public boolean isSubRoutine() {
        return false;
    }

    public char[] labelName() {
        return null;
    }

    public void markFinallyNullStatus(LocalVariableBinding local, int nullStatus) {
        if (this.initsOnFinally == null) {
            return;
        }
        if (this.conditionalLevel == -1) {
            return;
        }
        if (this.conditionalLevel == 0) {
            this.initsOnFinally.markNullStatus(local, nullStatus);
            return;
        }
        UnconditionalFlowInfo newInfo = this.initsOnFinally.unconditionalCopy();
        newInfo.markNullStatus(local, nullStatus);
        this.initsOnFinally = this.initsOnFinally.mergedWith(newInfo);
    }

    public void mergeFinallyNullInfo(FlowInfo flowInfo) {
        if (this.initsOnFinally == null) {
            return;
        }
        if (this.conditionalLevel == -1) {
            return;
        }
        if (this.conditionalLevel == 0) {
            this.initsOnFinally.addNullInfoFrom(flowInfo);
            return;
        }
        this.initsOnFinally = this.initsOnFinally.mergedWith(flowInfo.unconditionalCopy());
    }

    public void recordAbruptExit() {
        if (this.conditionalLevel > -1) {
            ++this.conditionalLevel;
            if (!(this instanceof ExceptionHandlingFlowContext) && this.parent != null) {
                this.parent.recordAbruptExit();
            }
        }
    }

    public void recordBreakFrom(FlowInfo flowInfo) {
    }

    public void recordBreakTo(FlowContext targetContext) {
    }

    public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo) {
    }

    public boolean recordExitAgainstResource(BlockScope scope, FlowInfo flowInfo, FakedTrackingVariable trackingVar, ASTNode reference) {
        return false;
    }

    protected void recordProvidedExpectedTypes(TypeBinding providedType, TypeBinding expectedType, int nullCount) {
        if (nullCount == 0) {
            this.providedExpectedTypes = new TypeBinding[5][];
        } else if (this.providedExpectedTypes == null) {
            int size = 5;
            while (size <= nullCount) {
                size *= 2;
            }
            this.providedExpectedTypes = new TypeBinding[size][];
        } else if (nullCount >= this.providedExpectedTypes.length) {
            int oldLen = this.providedExpectedTypes.length;
            this.providedExpectedTypes = new TypeBinding[nullCount * 2][];
            System.arraycopy(this.providedExpectedTypes, 0, this.providedExpectedTypes, 0, oldLen);
        }
        this.providedExpectedTypes[nullCount] = new TypeBinding[]{providedType, expectedType};
    }

    protected boolean recordFinalAssignment(VariableBinding variable, Reference finalReference) {
        return true;
    }

    protected final void recordNullReference(LocalVariableBinding local, ASTNode location, int checkType, FlowInfo nullInfo) {
        this.recordNullReferenceWithAnnotationStatus(local, location, checkType, nullInfo, null);
    }

    protected void recordNullReferenceWithAnnotationStatus(LocalVariableBinding local, ASTNode location, int checkType, FlowInfo nullInfo, NullAnnotationMatching nullAnnotationStatus) {
    }

    public void recordUnboxing(Scope scope, Expression expression, int nullStatus, FlowInfo flowInfo) {
        this.checkUnboxing(scope, expression, flowInfo);
    }

    protected void checkUnboxing(Scope scope, Expression expression, FlowInfo flowInfo) {
        int status = expression.nullStatus(flowInfo, this);
        if ((status & 2) != 0) {
            scope.problemReporter().nullUnboxing(expression, expression.resolvedType);
            return;
        }
        if ((status & 0x10) != 0) {
            scope.problemReporter().potentialNullUnboxing(expression, expression.resolvedType);
            return;
        }
        if ((status & 4) != 0) {
            return;
        }
        if (this.parent != null) {
            this.parent.recordUnboxing(scope, expression, 1, flowInfo);
        }
    }

    public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
    }

    public void recordSettingFinal(VariableBinding variable, Reference finalReference, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            FlowContext context = this;
            while (context != null) {
                if (!context.recordFinalAssignment(variable, finalReference)) break;
                context = context.getLocalParent();
            }
        }
    }

    public void recordUsingNullReference(Scope scope, LocalVariableBinding local, ASTNode location, int checkType, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 3) != 0 || flowInfo.isDefinitelyUnknown(local)) {
            return;
        }
        int checkTypeWithoutHideNullWarning = (checkType |= this.tagBits & 0x1000) & 0xFFFF0FFF;
        block0 : switch (checkTypeWithoutHideNullWarning) {
            case 256: 
            case 512: {
                if (flowInfo.isDefinitelyNonNull(local)) {
                    if (checkTypeWithoutHideNullWarning == 512) {
                        if ((checkType & 0x1000) == 0) {
                            scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                        }
                        flowInfo.initsWhenFalse().setReachMode(2);
                    } else {
                        scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                        flowInfo.initsWhenTrue().setReachMode(2);
                    }
                    return;
                }
                if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
                    return;
                }
            }
            case 257: 
            case 513: 
            case 769: 
            case 1025: {
                Expression reference = (Expression)location;
                if (flowInfo.isDefinitelyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 1 && (reference.implicitConversion & 0x400) != 0) {
                                scope.problemReporter().localVariableNullReference(local, reference);
                                return;
                            }
                            if ((checkType & 0x1000) == 0) {
                                scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
                            }
                            flowInfo.initsWhenFalse().setReachMode(2);
                            return;
                        }
                        case 512: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 1 && (reference.implicitConversion & 0x400) != 0) {
                                scope.problemReporter().localVariableNullReference(local, reference);
                                return;
                            }
                            scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
                            flowInfo.initsWhenTrue().setReachMode(2);
                            return;
                        }
                        case 768: {
                            scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
                            return;
                        }
                        case 1024: {
                            scope.problemReporter().localVariableNullInstanceof(local, reference);
                            return;
                        }
                    }
                    break;
                }
                if (flowInfo.isPotentiallyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break block0;
                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                            return;
                        }
                        case 512: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break block0;
                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                            return;
                        }
                    }
                    break;
                }
                if (!flowInfo.cannotBeDefinitelyNullOrNonNull(local)) break;
                return;
            }
            case 3: {
                if (flowInfo.isDefinitelyNull(local)) {
                    scope.problemReporter().localVariableNullReference(local, location);
                    return;
                }
                if (!flowInfo.isPotentiallyNull(local)) break;
                if (local.type.isFreeTypeVariable()) {
                    scope.problemReporter().localVariableFreeTypeVariableReference(local, location);
                    return;
                }
                scope.problemReporter().localVariablePotentialNullReference(local, location);
                return;
            }
        }
        if (this.parent != null) {
            this.parent.recordUsingNullReference(scope, local, location, checkType, flowInfo);
        }
    }

    void removeFinalAssignmentIfAny(Reference reference) {
    }

    public SubRoutineStatement subroutine() {
        return null;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        FlowContext current = this;
        int parentsCount = 0;
        while ((current = current.parent) != null) {
            ++parentsCount;
        }
        FlowContext[] parents = new FlowContext[parentsCount + 1];
        current = this;
        int index = parentsCount;
        while (index >= 0) {
            parents[index--] = current;
            current = current.parent;
        }
        int i = 0;
        while (i < parentsCount) {
            int j = 0;
            while (j < i) {
                buffer.append('\t');
                ++j;
            }
            buffer.append(parents[i].individualToString()).append('\n');
            ++i;
        }
        buffer.append('*');
        int j = 0;
        while (j < parentsCount + 1) {
            buffer.append('\t');
            ++j;
        }
        buffer.append(this.individualToString()).append('\n');
        return buffer.toString();
    }

    public void recordNullityMismatch(BlockScope currentScope, Expression expression, TypeBinding providedType, TypeBinding expectedType, FlowInfo flowInfo, int nullStatus, NullAnnotationMatching annotationStatus) {
        if (providedType == null) {
            return;
        }
        if (expression.localVariableBinding() != null) {
            FlowContext currentContext = this;
            while (currentContext != null) {
                int isInsideAssert = 0;
                if ((this.tagBits & 0x1000) != 0) {
                    isInsideAssert = 4096;
                }
                if (currentContext.internalRecordNullityMismatch(expression, providedType, flowInfo, nullStatus, annotationStatus, expectedType, 0x80 | isInsideAssert)) {
                    return;
                }
                currentContext = currentContext.parent;
            }
        }
        if (annotationStatus != null) {
            currentScope.problemReporter().nullityMismatchingTypeAnnotation(expression, providedType, expectedType, annotationStatus);
        } else {
            currentScope.problemReporter().nullityMismatch(expression, providedType, expectedType, nullStatus, currentScope.environment().getNonNullAnnotationName());
        }
    }

    protected boolean internalRecordNullityMismatch(Expression expression, TypeBinding providedType, FlowInfo flowInfo, int nullStatus, NullAnnotationMatching nullAnnotationStatus, TypeBinding expectedType, int checkType) {
        return false;
    }
}

