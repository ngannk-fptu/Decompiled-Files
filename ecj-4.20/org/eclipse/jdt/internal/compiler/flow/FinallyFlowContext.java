/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.TryFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class FinallyFlowContext
extends TryFlowContext {
    Reference[] finalAssignments;
    VariableBinding[] finalVariables;
    int assignCount;
    LocalVariableBinding[] nullLocals;
    ASTNode[] nullReferences;
    int[] nullCheckTypes;
    NullAnnotationMatching[] nullAnnotationStatuses;
    int nullCount;
    public FlowContext tryContext;

    public FinallyFlowContext(FlowContext parent, ASTNode associatedNode, ExceptionHandlingFlowContext tryContext) {
        super(parent, associatedNode);
        this.tryContext = tryContext;
    }

    public void complainOnDeferredChecks(FlowInfo flowInfo, BlockScope scope) {
        ASTNode location;
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
                    FlowContext currentContext = this.getLocalParent();
                    while (currentContext != null) {
                        currentContext.removeFinalAssignmentIfAny(this.finalAssignments[i]);
                        currentContext = currentContext.getLocalParent();
                    }
                }
            }
            ++i;
        }
        if ((this.tagBits & 1) != 0) {
            i = 0;
            while (i < this.nullCount) {
                location = this.nullReferences[i];
                switch (this.nullCheckTypes[i] & 0xFFFF0FFF) {
                    case 128: {
                        int nullStatus = flowInfo.nullStatus(this.nullLocals[i]);
                        if (nullStatus == 4) break;
                        this.parent.recordNullityMismatch(scope, (Expression)location, this.providedExpectedTypes[i][0], this.providedExpectedTypes[i][1], flowInfo, nullStatus, null);
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location, flowInfo);
                        break;
                    }
                    default: {
                        this.parent.recordUsingNullReference(scope, this.nullLocals[i], this.nullReferences[i], this.nullCheckTypes[i], flowInfo);
                    }
                }
                ++i;
            }
        } else {
            i = 0;
            while (i < this.nullCount) {
                location = this.nullReferences[i];
                LocalVariableBinding local = this.nullLocals[i];
                block4 : switch (this.nullCheckTypes[i] & 0xFFFF0FFF) {
                    case 256: 
                    case 512: {
                        if (flowInfo.isDefinitelyNonNull(local)) {
                            if ((this.nullCheckTypes[i] & 0xFFFF0FFF) == 512) {
                                if ((this.nullCheckTypes[i] & 0x1000) != 0) break;
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
                        Expression expression = (Expression)location;
                        if (flowInfo.isDefinitelyNull(local)) {
                            switch (this.nullCheckTypes[i] & 0xFFFF0F00) {
                                case 256: {
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 1 && (expression.implicitConversion & 0x400) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block4;
                                    }
                                    if ((this.nullCheckTypes[i] & 0x1000) != 0) break block4;
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
                                    break block4;
                                }
                                case 512: {
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 1 && (expression.implicitConversion & 0x400) != 0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        break block4;
                                    }
                                    scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
                                    break block4;
                                }
                                case 768: {
                                    scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
                                    break block4;
                                }
                                case 1024: {
                                    scope.problemReporter().localVariableNullInstanceof(local, expression);
                                    break block4;
                                }
                            }
                            break;
                        }
                        if (!flowInfo.isPotentiallyNull(local)) break;
                        switch (this.nullCheckTypes[i] & 0xFFFF0F00) {
                            case 256: {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) != 1 || (expression.implicitConversion & 0x400) == 0) break block4;
                                scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                break block4;
                            }
                            case 512: {
                                this.nullReferences[i] = null;
                                if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) != 1 || (expression.implicitConversion & 0x400) == 0) break block4;
                                scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                break block4;
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (flowInfo.isDefinitelyNull(local)) {
                            scope.problemReporter().localVariableNullReference(local, location);
                            break;
                        }
                        if (!flowInfo.isPotentiallyNull(local)) break;
                        scope.problemReporter().localVariablePotentialNullReference(local, location);
                        break;
                    }
                    case 128: {
                        int nullStatus = flowInfo.nullStatus(local);
                        if (nullStatus == 4) break;
                        char[][] annotationName = scope.environment().getNonNullAnnotationName();
                        TypeBinding providedType = this.providedExpectedTypes[i][0];
                        TypeBinding expectedType = this.providedExpectedTypes[i][1];
                        Expression expression2 = (Expression)location;
                        if (this.nullAnnotationStatuses[i] != null) {
                            this.nullAnnotationStatuses[i] = this.nullAnnotationStatuses[i].withNullStatus(nullStatus);
                            scope.problemReporter().nullityMismatchingTypeAnnotation(expression2, providedType, expectedType, this.nullAnnotationStatuses[i]);
                            break;
                        }
                        scope.problemReporter().nullityMismatch(expression2, providedType, expectedType, nullStatus, annotationName);
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location, flowInfo);
                    }
                }
                ++i;
            }
        }
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Finally flow context");
        buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
        buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
        return buffer.toString();
    }

    @Override
    public boolean isSubRoutine() {
        return true;
    }

    @Override
    protected boolean recordFinalAssignment(VariableBinding binding, Reference finalAssignment) {
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
    public void recordUsingNullReference(Scope scope, LocalVariableBinding local, ASTNode location, int checkType, FlowInfo flowInfo) {
        block52: {
            block54: {
                int checkTypeWithoutHideNullWarning;
                block53: {
                    if ((flowInfo.tagBits & 3) != 0 || flowInfo.isDefinitelyUnknown(local)) break block52;
                    checkTypeWithoutHideNullWarning = (checkType |= this.tagBits & 0x1000) & 0xFFFF0FFF;
                    if ((this.tagBits & 1) == 0) break block53;
                    switch (checkTypeWithoutHideNullWarning) {
                        case 256: 
                        case 257: 
                        case 512: 
                        case 513: 
                        case 769: 
                        case 1025: {
                            Expression reference = (Expression)location;
                            if (flowInfo.cannotBeNull(local)) {
                                if (checkTypeWithoutHideNullWarning == 512) {
                                    if ((checkType & 0x1000) == 0) {
                                        scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
                                    }
                                    flowInfo.initsWhenFalse().setReachMode(2);
                                } else if (checkTypeWithoutHideNullWarning == 256) {
                                    scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
                                    flowInfo.initsWhenTrue().setReachMode(2);
                                }
                                return;
                            }
                            if (flowInfo.canOnlyBeNull(local)) {
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
                            if (!flowInfo.isPotentiallyNull(local)) break block54;
                            switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                                case 256: {
                                    if ((checkTypeWithoutHideNullWarning & 0xFF) == 1 && (reference.implicitConversion & 0x400) != 0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                        return;
                                    }
                                    break block54;
                                }
                                case 512: {
                                    if ((checkTypeWithoutHideNullWarning & 0xFF) == 1 && (reference.implicitConversion & 0x400) != 0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                        return;
                                    }
                                    break block54;
                                }
                            }
                            break block54;
                        }
                        case 3: {
                            if (flowInfo.cannotBeNull(local)) {
                                return;
                            }
                            if (flowInfo.canOnlyBeNull(local)) {
                                scope.problemReporter().localVariableNullReference(local, location);
                                return;
                            }
                            break block54;
                        }
                    }
                    break block54;
                }
                block14 : switch (checkTypeWithoutHideNullWarning) {
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
                        if (!flowInfo.isPotentiallyNull(local)) break;
                        switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                            case 256: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break block14;
                                scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                return;
                            }
                            case 512: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) != 1 || (reference.implicitConversion & 0x400) == 0) break block14;
                                scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                return;
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (flowInfo.isDefinitelyNull(local)) {
                            scope.problemReporter().localVariableNullReference(local, location);
                            return;
                        }
                        if (flowInfo.isPotentiallyNull(local)) {
                            scope.problemReporter().localVariablePotentialNullReference(local, location);
                            return;
                        }
                        if (!flowInfo.isDefinitelyNonNull(local)) break;
                        return;
                    }
                }
            }
            this.recordNullReference(local, location, checkType, flowInfo);
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

    @Override
    protected void recordNullReferenceWithAnnotationStatus(LocalVariableBinding local, ASTNode expression, int checkType, FlowInfo nullInfo, NullAnnotationMatching nullAnnotationStatus) {
        if (this.nullCount == 0) {
            this.nullLocals = new LocalVariableBinding[5];
            this.nullReferences = new ASTNode[5];
            this.nullCheckTypes = new int[5];
            this.nullAnnotationStatuses = new NullAnnotationMatching[5];
        } else if (this.nullCount == this.nullLocals.length) {
            int newLength = this.nullCount * 2;
            this.nullLocals = new LocalVariableBinding[newLength];
            System.arraycopy(this.nullLocals, 0, this.nullLocals, 0, this.nullCount);
            this.nullReferences = new ASTNode[newLength];
            System.arraycopy(this.nullReferences, 0, this.nullReferences, 0, this.nullCount);
            this.nullCheckTypes = new int[newLength];
            System.arraycopy(this.nullCheckTypes, 0, this.nullCheckTypes, 0, this.nullCount);
            this.nullAnnotationStatuses = new NullAnnotationMatching[this.nullCount * 2];
            System.arraycopy(this.nullAnnotationStatuses, 0, this.nullAnnotationStatuses, 0, this.nullCount);
        }
        this.nullLocals[this.nullCount] = local;
        this.nullReferences[this.nullCount] = expression;
        this.nullAnnotationStatuses[this.nullCount] = nullAnnotationStatus;
        this.nullCheckTypes[this.nullCount++] = checkType;
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
    protected boolean internalRecordNullityMismatch(Expression expression, TypeBinding providedType, FlowInfo flowInfo, int nullStatus, NullAnnotationMatching nullAnnotationStatus, TypeBinding expectedType, int checkType) {
        if (nullStatus == 1 || (this.tagBits & 1) != 0 && nullStatus != 2) {
            this.recordProvidedExpectedTypes(providedType, expectedType, this.nullCount);
            this.recordNullReferenceWithAnnotationStatus(expression.localVariableBinding(), expression, checkType, flowInfo, nullAnnotationStatus);
            return true;
        }
        return false;
    }
}

