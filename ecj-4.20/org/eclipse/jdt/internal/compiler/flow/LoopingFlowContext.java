/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.LabelFlowContext;
import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class LoopingFlowContext
extends SwitchFlowContext {
    public BranchLabel continueLabel;
    public UnconditionalFlowInfo initsOnContinue = FlowInfo.DEAD_END;
    private UnconditionalFlowInfo upstreamNullFlowInfo;
    private LoopingFlowContext[] innerFlowContexts = null;
    private UnconditionalFlowInfo[] innerFlowInfos = null;
    private int innerFlowContextsCount = 0;
    private LabelFlowContext[] breakTargetContexts = null;
    private int breakTargetsCount = 0;
    Reference[] finalAssignments;
    VariableBinding[] finalVariables;
    int assignCount = 0;
    LocalVariableBinding[] nullLocals;
    ASTNode[] nullReferences;
    int[] nullCheckTypes;
    UnconditionalFlowInfo[] nullInfos;
    NullAnnotationMatching[] nullAnnotationStatuses;
    int nullCount;
    private ArrayList escapingExceptionCatchSites = null;
    Scope associatedScope;

    public LoopingFlowContext(FlowContext parent, FlowInfo upstreamNullFlowInfo, ASTNode associatedNode, BranchLabel breakLabel, BranchLabel continueLabel, Scope associatedScope, boolean isPreTest) {
        super(parent, associatedNode, breakLabel, isPreTest, false);
        this.tagBits |= 2;
        this.continueLabel = continueLabel;
        this.associatedScope = associatedScope;
        this.upstreamNullFlowInfo = upstreamNullFlowInfo.unconditionalCopy();
    }

    public void complainOnDeferredFinalChecks(BlockScope scope, FlowInfo flowInfo) {
        int i = 0;
        while (i < this.assignCount) {
            VariableBinding variable = this.finalVariables[i];
            if (variable != null) {
                boolean complained = false;
                if (variable instanceof FieldBinding) {
                    if (flowInfo.isPotentiallyAssigned((FieldBinding)variable)) {
                        complained = true;
                        scope.problemReporter().duplicateInitializationOfBlankFinalField((FieldBinding)variable, this.finalAssignments[i]);
                    }
                } else if (flowInfo.isPotentiallyAssigned((LocalVariableBinding)variable)) {
                    variable.tagBits &= 0xFFFFFFFFFFFFF7FFL;
                    if (variable.isFinal()) {
                        complained = true;
                        scope.problemReporter().duplicateInitializationOfFinalLocal((LocalVariableBinding)variable, this.finalAssignments[i]);
                    }
                }
                if (complained) {
                    FlowContext context = this.getLocalParent();
                    while (context != null) {
                        context.removeFinalAssignmentIfAny(this.finalAssignments[i]);
                        context = context.getLocalParent();
                    }
                }
            }
            ++i;
        }
    }

    public void complainOnDeferredNullChecks(BlockScope scope, FlowInfo callerFlowInfo) {
        this.complainOnDeferredNullChecks(scope, callerFlowInfo, true);
    }

    /*
     * Unable to fully structure code
     */
    public void complainOnDeferredNullChecks(BlockScope scope, FlowInfo callerFlowInfo, boolean updateInitsOnBreak) {
        block62: {
            block61: {
                i = 0;
                while (i < this.innerFlowContextsCount) {
                    this.upstreamNullFlowInfo.addPotentialNullInfoFrom(this.innerFlowContexts[i].upstreamNullFlowInfo).addPotentialNullInfoFrom(this.innerFlowInfos[i]);
                    ++i;
                }
                this.innerFlowContextsCount = 0;
                upstreamCopy = this.upstreamNullFlowInfo.copy();
                incomingInfo = this.upstreamNullFlowInfo.addPotentialNullInfoFrom(callerFlowInfo.unconditionalInitsWithoutSideEffect());
                if ((this.tagBits & 1) == 0) break block61;
                i = 0;
                while (i < this.nullCount) {
                    local = this.nullLocals[i];
                    location = this.nullReferences[i];
                    flowInfo = this.nullInfos[i] != null ? incomingInfo.copy().addNullInfoFrom(this.nullInfos[i]) : incomingInfo;
                    block0 : switch (this.nullCheckTypes[i] & -61441) {
                        case 258: 
                        case 514: {
                            if (flowInfo.isDefinitelyNonNull(local)) {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & -61441) == 514) {
                                    if ((this.nullCheckTypes[i] & 4096) != 0) break;
                                    scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                                    break;
                                }
                                scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                                break;
                            }
                            ** GOTO lbl107
                        }
                        case 256: 
                        case 512: {
                            if (flowInfo.isDefinitelyNonNull(local)) {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & -61441) == 512) {
                                    if ((this.nullCheckTypes[i] & 4096) != 0) break;
                                    scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                                    break;
                                }
                                scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                                break;
                            }
                            if (flowInfo.isDefinitelyNull(local)) {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & -61441) == 256) {
                                    if ((this.nullCheckTypes[i] & 4096) != 0) break;
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, location);
                                    break;
                                }
                                scope.problemReporter().localVariableNullComparedToNonNull(local, location);
                                break;
                            }
                            ** GOTO lbl107
                        }
                        case 257: 
                        case 513: 
                        case 769: 
                        case 1025: {
                            expression = (Expression)location;
                            if (!flowInfo.isDefinitelyNull(local)) ** GOTO lbl70
                            this.nullReferences[i] = null;
                            switch (this.nullCheckTypes[i] & -61696) {
                                case 256: {
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block0;
                                    }
                                    if ((this.nullCheckTypes[i] & 4096) != 0) break block0;
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
                                    break block0;
                                }
                                case 512: {
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block0;
                                    }
                                    scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
                                    break block0;
                                }
                                case 768: {
                                    scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
                                    break block0;
                                }
                                case 1024: {
                                    scope.problemReporter().localVariableNullInstanceof(local, expression);
                                    break block0;
                                }
                            }
                            ** GOTO lbl107
lbl70:
                            // 1 sources

                            if (!flowInfo.isPotentiallyNull(local)) ** GOTO lbl107
                            switch (this.nullCheckTypes[i] & -61696) {
                                case 256: {
                                    this.nullReferences[i] = null;
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                        break block0;
                                    }
                                    ** GOTO lbl107
                                }
                                case 512: {
                                    this.nullReferences[i] = null;
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                        break block0;
                                    }
                                    ** GOTO lbl107
                                }
                            }
                            ** GOTO lbl107
                        }
                        case 3: {
                            if (flowInfo.isDefinitelyNull(local)) {
                                this.nullReferences[i] = null;
                                scope.problemReporter().localVariableNullReference(local, location);
                                break;
                            }
                            ** GOTO lbl107
                        }
                        case 128: {
                            nullStatus = flowInfo.nullStatus(local);
                            if (nullStatus == 4) break;
                            this.parent.recordNullityMismatch(scope, (Expression)location, this.providedExpectedTypes[i][0], this.providedExpectedTypes[i][1], flowInfo, nullStatus, null);
                            break;
                        }
                        case 2048: {
                            trackingVar = local.closeTracker;
                            if (trackingVar == null) ** GOTO lbl107
                            if (trackingVar.hasDefinitelyNoResource(flowInfo) || trackingVar.isClosedInFinallyOfEnclosing(scope)) break;
                            if (this.parent.recordExitAgainstResource(scope, flowInfo, trackingVar, location)) {
                                this.nullReferences[i] = null;
                                break;
                            }
                            ** GOTO lbl107
                        }
                        case 16: {
                            this.checkUnboxing(scope, (Expression)location, flowInfo);
                            break;
                        }
lbl107:
                        // 11 sources

                        default: {
                            if (this.nullCheckTypes[i] == 3 && upstreamCopy.isDefinitelyNonNull(local)) break;
                            this.parent.recordUsingNullReference(scope, local, location, this.nullCheckTypes[i], flowInfo);
                        }
                    }
                    ++i;
                }
                break block62;
            }
            i = 0;
            while (i < this.nullCount) {
                location = this.nullReferences[i];
                local = this.nullLocals[i];
                flowInfo = this.nullInfos[i] != null ? incomingInfo.copy().addNullInfoFrom(this.nullInfos[i]) : incomingInfo;
                block19 : switch (this.nullCheckTypes[i] & -61441) {
                    case 256: 
                    case 512: {
                        if (flowInfo.isDefinitelyNonNull(local)) {
                            this.nullReferences[i] = null;
                            if ((this.nullCheckTypes[i] & -61441) == 512) {
                                if ((this.nullCheckTypes[i] & 4096) != 0) break;
                                scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                                break;
                            }
                            scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                            break;
                        }
                    }
                    case 257: 
                    case 513: 
                    case 769: 
                    case 1025: {
                        expression = (Expression)location;
                        if (flowInfo.isDefinitelyNull(local)) {
                            this.nullReferences[i] = null;
                            switch (this.nullCheckTypes[i] & -61696) {
                                case 256: {
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block19;
                                    }
                                    if ((this.nullCheckTypes[i] & 4096) != 0) break block19;
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
                                    break block19;
                                }
                                case 512: {
                                    if ((this.nullCheckTypes[i] & 255 & -61441) == 1 && (expression.implicitConversion & 1024) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block19;
                                    }
                                    scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
                                    break block19;
                                }
                                case 768: {
                                    scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
                                    break block19;
                                }
                                case 1024: {
                                    scope.problemReporter().localVariableNullInstanceof(local, expression);
                                    break block19;
                                }
                            }
                            break;
                        }
                        if (!flowInfo.isPotentiallyNull(local)) break;
                        switch (this.nullCheckTypes[i] & -61696) {
                            case 256: {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & 255 & -61441) != 1 || (expression.implicitConversion & 1024) == 0) break block19;
                                scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                break block19;
                            }
                            case 512: {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & 255 & -61441) != 1 || (expression.implicitConversion & 1024) == 0) break block19;
                                scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                break block19;
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (flowInfo.isDefinitelyNull(local)) {
                            this.nullReferences[i] = null;
                            scope.problemReporter().localVariableNullReference(local, location);
                            break;
                        }
                        if (!flowInfo.isPotentiallyNull(local)) break;
                        this.nullReferences[i] = null;
                        scope.problemReporter().localVariablePotentialNullReference(local, location);
                        break;
                    }
                    case 128: {
                        nullStatus = flowInfo.nullStatus(local);
                        if (nullStatus == 4) break;
                        annotationName = scope.environment().getNonNullAnnotationName();
                        providedType = this.providedExpectedTypes[i][0];
                        expectedType = this.providedExpectedTypes[i][1];
                        expression2 = (Expression)location;
                        if (this.nullAnnotationStatuses[i] != null) {
                            this.nullAnnotationStatuses[i] = this.nullAnnotationStatuses[i].withNullStatus(nullStatus);
                            scope.problemReporter().nullityMismatchingTypeAnnotation(expression2, providedType, expectedType, this.nullAnnotationStatuses[i]);
                            break;
                        }
                        scope.problemReporter().nullityMismatch(expression2, providedType, expectedType, nullStatus, annotationName);
                        break;
                    }
                    case 2048: {
                        nullStatus = flowInfo.nullStatus(local);
                        if (nullStatus == 4 || (closeTracker = local.closeTracker) == null || closeTracker.hasDefinitelyNoResource(flowInfo) || closeTracker.isClosedInFinallyOfEnclosing(scope)) break;
                        nullStatus = closeTracker.findMostSpecificStatus(flowInfo, scope, null);
                        closeTracker.recordErrorLocation(this.nullReferences[i], nullStatus);
                        closeTracker.reportRecordedErrors(scope, nullStatus, flowInfo.reachMode() != 0);
                        this.nullReferences[i] = null;
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location, flowInfo);
                    }
                }
                ++i;
            }
        }
        if (updateInitsOnBreak) {
            this.initsOnBreak.addPotentialNullInfoFrom(incomingInfo);
            i = 0;
            while (i < this.breakTargetsCount) {
                this.breakTargetContexts[i].initsOnBreak.addPotentialNullInfoFrom(incomingInfo);
                ++i;
            }
        }
    }

    @Override
    public BranchLabel continueLabel() {
        return this.continueLabel;
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Looping flow context");
        buffer.append("[initsOnBreak - ").append(this.initsOnBreak.toString()).append(']');
        buffer.append("[initsOnContinue - ").append(this.initsOnContinue.toString()).append(']');
        buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
        buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
        return buffer.toString();
    }

    @Override
    public boolean isContinuable() {
        return true;
    }

    public boolean isContinuedTo() {
        return this.initsOnContinue != FlowInfo.DEAD_END;
    }

    @Override
    public void recordBreakTo(FlowContext targetContext) {
        if (targetContext instanceof LabelFlowContext) {
            int current;
            if ((current = this.breakTargetsCount++) == 0) {
                this.breakTargetContexts = new LabelFlowContext[2];
            } else if (current == this.breakTargetContexts.length) {
                this.breakTargetContexts = new LabelFlowContext[current + 2];
                System.arraycopy(this.breakTargetContexts, 0, this.breakTargetContexts, 0, current);
            }
            this.breakTargetContexts[current] = (LabelFlowContext)targetContext;
        }
    }

    @Override
    public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            this.initsOnContinue = (this.initsOnContinue.tagBits & 1) == 0 ? this.initsOnContinue.mergedWith(flowInfo.unconditionalInitsWithoutSideEffect()) : flowInfo.unconditionalCopy();
            FlowContext inner = innerFlowContext;
            while (inner != this && !(inner instanceof LoopingFlowContext)) {
                inner = inner.parent;
            }
            if (inner == this) {
                this.upstreamNullFlowInfo.addPotentialNullInfoFrom(flowInfo.unconditionalInitsWithoutSideEffect());
            } else {
                int length = 0;
                if (this.innerFlowContexts == null) {
                    this.innerFlowContexts = new LoopingFlowContext[5];
                    this.innerFlowInfos = new UnconditionalFlowInfo[5];
                } else {
                    length = this.innerFlowContexts.length;
                    if (this.innerFlowContextsCount == length - 1) {
                        this.innerFlowContexts = new LoopingFlowContext[length + 5];
                        System.arraycopy(this.innerFlowContexts, 0, this.innerFlowContexts, 0, length);
                        this.innerFlowInfos = new UnconditionalFlowInfo[length + 5];
                        System.arraycopy(this.innerFlowInfos, 0, this.innerFlowInfos, 0, length);
                    }
                }
                this.innerFlowContexts[this.innerFlowContextsCount] = (LoopingFlowContext)inner;
                this.innerFlowInfos[this.innerFlowContextsCount++] = flowInfo.unconditionalInitsWithoutSideEffect();
            }
        }
    }

    @Override
    protected boolean recordFinalAssignment(VariableBinding binding, Reference finalAssignment) {
        if (binding instanceof LocalVariableBinding) {
            Scope scope = ((LocalVariableBinding)binding).declaringScope;
            while ((scope = scope.parent) != null) {
                if (scope != this.associatedScope) continue;
                return false;
            }
        }
        if (this.assignCount == 0) {
            this.finalAssignments = new Reference[5];
            this.finalVariables = new VariableBinding[5];
        } else {
            if (this.assignCount == this.finalAssignments.length) {
                this.finalAssignments = new Reference[this.assignCount * 2];
                System.arraycopy(this.finalAssignments, 0, this.finalAssignments, 0, this.assignCount);
            }
            this.finalVariables = new VariableBinding[this.assignCount * 2];
            System.arraycopy(this.finalVariables, 0, this.finalVariables, 0, this.assignCount);
        }
        this.finalAssignments[this.assignCount] = finalAssignment;
        this.finalVariables[this.assignCount++] = binding;
        return true;
    }

    @Override
    protected void recordNullReferenceWithAnnotationStatus(LocalVariableBinding local, ASTNode expression, int checkType, FlowInfo nullInfo, NullAnnotationMatching nullAnnotationStatus) {
        if (this.nullCount == 0) {
            this.nullLocals = new LocalVariableBinding[5];
            this.nullReferences = new ASTNode[5];
            this.nullCheckTypes = new int[5];
            this.nullInfos = new UnconditionalFlowInfo[5];
            this.nullAnnotationStatuses = new NullAnnotationMatching[5];
        } else if (this.nullCount == this.nullLocals.length) {
            this.nullLocals = new LocalVariableBinding[this.nullCount * 2];
            System.arraycopy(this.nullLocals, 0, this.nullLocals, 0, this.nullCount);
            this.nullReferences = new ASTNode[this.nullCount * 2];
            System.arraycopy(this.nullReferences, 0, this.nullReferences, 0, this.nullCount);
            this.nullCheckTypes = new int[this.nullCount * 2];
            System.arraycopy(this.nullCheckTypes, 0, this.nullCheckTypes, 0, this.nullCount);
            this.nullInfos = new UnconditionalFlowInfo[this.nullCount * 2];
            System.arraycopy(this.nullInfos, 0, this.nullInfos, 0, this.nullCount);
            this.nullAnnotationStatuses = new NullAnnotationMatching[this.nullCount * 2];
            System.arraycopy(this.nullAnnotationStatuses, 0, this.nullAnnotationStatuses, 0, this.nullCount);
        }
        this.nullLocals[this.nullCount] = local;
        this.nullReferences[this.nullCount] = expression;
        this.nullCheckTypes[this.nullCount] = checkType;
        this.nullAnnotationStatuses[this.nullCount] = nullAnnotationStatus;
        this.nullInfos[this.nullCount++] = nullInfo != null ? nullInfo.unconditionalCopy() : null;
    }

    @Override
    public void recordUnboxing(Scope scope, Expression expression, int nullStatus, FlowInfo flowInfo) {
        if (nullStatus == 2) {
            super.recordUnboxing(scope, expression, nullStatus, flowInfo);
        } else {
            this.recordNullReference(null, expression, 16, flowInfo);
        }
    }

    @Override
    public boolean recordExitAgainstResource(BlockScope scope, FlowInfo flowInfo, FakedTrackingVariable trackingVar, ASTNode reference) {
        LocalVariableBinding local = trackingVar.binding;
        if (flowInfo.isDefinitelyNonNull(local)) {
            return false;
        }
        if (flowInfo.isDefinitelyNull(local)) {
            scope.problemReporter().unclosedCloseable(trackingVar, reference);
            return true;
        }
        if (flowInfo.isPotentiallyNull(local)) {
            scope.problemReporter().potentiallyUnclosedCloseable(trackingVar, reference);
            return true;
        }
        this.recordNullReference(trackingVar.binding, reference, 2048, flowInfo);
        return true;
    }

    @Override
    public void recordUsingNullReference(Scope scope, LocalVariableBinding local, ASTNode location, int checkType, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 3) != 0 || flowInfo.isDefinitelyUnknown(local)) {
            return;
        }
        int checkTypeWithoutHideNullWarning = (checkType |= this.tagBits & 0x1000) & 0xFFFF0FFF;
        switch (checkTypeWithoutHideNullWarning) {
            case 256: 
            case 512: {
                Expression reference = (Expression)location;
                if (flowInfo.isDefinitelyNonNull(local)) {
                    if (checkTypeWithoutHideNullWarning == 512) {
                        if ((this.tagBits & 0x1000) == 0) {
                            scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
                        }
                        flowInfo.initsWhenFalse().setReachMode(2);
                    } else {
                        scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
                        flowInfo.initsWhenTrue().setReachMode(2);
                    }
                } else if (flowInfo.isDefinitelyNull(local)) {
                    if (checkTypeWithoutHideNullWarning == 256) {
                        if ((this.tagBits & 0x1000) == 0) {
                            scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
                        }
                        flowInfo.initsWhenFalse().setReachMode(2);
                    } else {
                        scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
                        flowInfo.initsWhenTrue().setReachMode(2);
                    }
                } else if (this.upstreamNullFlowInfo.isDefinitelyNonNull(local) && !flowInfo.isPotentiallyNull(local) && !flowInfo.isPotentiallyUnknown(local)) {
                    this.recordNullReference(local, reference, checkType, flowInfo);
                    flowInfo.markAsDefinitelyNonNull(local);
                } else {
                    if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
                        return;
                    }
                    if (flowInfo.isPotentiallyNonNull(local)) {
                        this.recordNullReference(local, reference, 2 | checkType & 0xFFFFFF00, flowInfo);
                    } else if (flowInfo.isPotentiallyNull(local)) {
                        this.recordNullReference(local, reference, 1 | checkType & 0xFFFFFF00, flowInfo);
                    } else {
                        this.recordNullReference(local, reference, checkType, flowInfo);
                    }
                }
                return;
            }
            case 257: 
            case 513: 
            case 769: 
            case 1025: {
                Expression reference = (Expression)location;
                if (flowInfo.isPotentiallyNonNull(local) || flowInfo.isPotentiallyUnknown(local) || flowInfo.isProtectedNonNull(local)) {
                    return;
                }
                if (flowInfo.isDefinitelyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 1 && (reference.implicitConversion & 0x400) != 0) {
                                scope.problemReporter().localVariableNullReference(local, reference);
                                return;
                            }
                            if ((this.tagBits & 0x1000) == 0) {
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
                } else if (flowInfo.isPotentiallyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break;
                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                            return;
                        }
                        case 512: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break;
                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                            return;
                        }
                    }
                }
                this.recordNullReference(local, reference, checkType, flowInfo);
                return;
            }
            case 3: {
                if (flowInfo.isDefinitelyNonNull(local)) {
                    return;
                }
                if (flowInfo.isDefinitelyNull(local)) {
                    scope.problemReporter().localVariableNullReference(local, location);
                    return;
                }
                if (flowInfo.isPotentiallyNull(local)) {
                    scope.problemReporter().localVariablePotentialNullReference(local, location);
                    return;
                }
                this.recordNullReference(local, location, checkType, flowInfo);
                return;
            }
        }
    }

    @Override
    void removeFinalAssignmentIfAny(Reference reference) {
        int i = 0;
        while (i < this.assignCount) {
            if (this.finalAssignments[i] == reference) {
                this.finalAssignments[i] = null;
                this.finalVariables[i] = null;
                return;
            }
            ++i;
        }
    }

    public void simulateThrowAfterLoopBack(FlowInfo flowInfo) {
        if (this.escapingExceptionCatchSites != null) {
            int i = 0;
            int exceptionCount = this.escapingExceptionCatchSites.size();
            while (i < exceptionCount) {
                ((EscapingExceptionCatchSite)this.escapingExceptionCatchSites.get(i)).simulateThrowAfterLoopBack(flowInfo);
                ++i;
            }
            this.escapingExceptionCatchSites = null;
        }
    }

    public void recordCatchContextOfEscapingException(ExceptionHandlingFlowContext catchingContext, ReferenceBinding caughtException, FlowInfo exceptionInfo) {
        if (this.escapingExceptionCatchSites == null) {
            this.escapingExceptionCatchSites = new ArrayList(5);
        }
        this.escapingExceptionCatchSites.add(new EscapingExceptionCatchSite(catchingContext, caughtException, exceptionInfo));
    }

    public boolean hasEscapingExceptions() {
        return this.escapingExceptionCatchSites != null;
    }

    @Override
    protected boolean internalRecordNullityMismatch(Expression expression, TypeBinding providedType, FlowInfo flowInfo, int nullStatus, NullAnnotationMatching nullAnnotationStatus, TypeBinding expectedType, int checkType) {
        this.recordProvidedExpectedTypes(providedType, expectedType, this.nullCount);
        this.recordNullReferenceWithAnnotationStatus(expression.localVariableBinding(), expression, checkType, flowInfo, nullAnnotationStatus);
        return true;
    }

    private static class EscapingExceptionCatchSite {
        final ReferenceBinding caughtException;
        final ExceptionHandlingFlowContext catchingContext;
        final FlowInfo exceptionInfo;

        public EscapingExceptionCatchSite(ExceptionHandlingFlowContext catchingContext, ReferenceBinding caughtException, FlowInfo exceptionInfo) {
            this.catchingContext = catchingContext;
            this.caughtException = caughtException;
            this.exceptionInfo = exceptionInfo;
        }

        void simulateThrowAfterLoopBack(FlowInfo flowInfo) {
            this.catchingContext.recordHandlingException(this.caughtException, flowInfo.unconditionalCopy().addNullInfoFrom(this.exceptionInfo).unconditionalInits(), null, null, null, true);
        }
    }
}

