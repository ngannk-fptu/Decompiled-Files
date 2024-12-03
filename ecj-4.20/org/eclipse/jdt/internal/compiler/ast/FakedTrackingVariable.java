/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class FakedTrackingVariable
extends LocalDeclaration {
    private static final char[] UNASSIGNED_CLOSEABLE_NAME = "<unassigned Closeable value>".toCharArray();
    private static final char[] UNASSIGNED_CLOSEABLE_NAME_TEMPLATE = "<unassigned Closeable value from line {0}>".toCharArray();
    private static final char[] TEMPLATE_ARGUMENT = "{0}".toCharArray();
    private static final int CLOSE_SEEN = 1;
    private static final int SHARED_WITH_OUTSIDE = 2;
    private static final int OWNED_BY_OUTSIDE = 4;
    private static final int CLOSED_IN_NESTED_METHOD = 8;
    private static final int REPORTED_EXPLICIT_CLOSE = 16;
    private static final int REPORTED_POTENTIAL_LEAK = 32;
    private static final int REPORTED_DEFINITIVE_LEAK = 64;
    private static final int FOREACH_ELEMENT_VAR = 128;
    private static final int TWR_EFFECTIVELY_FINAL = 256;
    public MessageSend acquisition;
    public static boolean TEST_372319 = false;
    private int globalClosingState = 0;
    public LocalVariableBinding originalBinding;
    public FakedTrackingVariable innerTracker;
    public FakedTrackingVariable outerTracker;
    MethodScope methodScope;
    private HashMap recordedLocations;
    private ASTNode currentAssignment;
    private FlowContext tryContext;

    public FakedTrackingVariable(LocalVariableBinding original, ASTNode location, FlowInfo flowInfo, FlowContext flowContext, int nullStatus) {
        super(original.name, location.sourceStart, location.sourceEnd);
        this.type = new SingleTypeReference(TypeConstants.OBJECT, ((long)this.sourceStart << 32) + (long)this.sourceEnd);
        this.methodScope = original.declaringScope.methodScope();
        this.originalBinding = original;
        while (flowContext != null) {
            if (flowContext instanceof FinallyFlowContext) {
                this.tryContext = ((FinallyFlowContext)flowContext).tryContext;
                break;
            }
            flowContext = flowContext.parent;
        }
        this.resolve(original.declaringScope);
        if (nullStatus != 0) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
    }

    private FakedTrackingVariable(BlockScope scope, ASTNode location, FlowInfo flowInfo, int nullStatus) {
        super(UNASSIGNED_CLOSEABLE_NAME, location.sourceStart, location.sourceEnd);
        this.type = new SingleTypeReference(TypeConstants.OBJECT, ((long)this.sourceStart << 32) + (long)this.sourceEnd);
        this.methodScope = scope.methodScope();
        this.originalBinding = null;
        this.resolve(scope);
        if (nullStatus != 0) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
    }

    private void attachTo(LocalVariableBinding local) {
        local.closeTracker = this;
        this.originalBinding = local;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
    }

    @Override
    public void resolve(BlockScope scope) {
        this.binding = new LocalVariableBinding(this.name, (TypeBinding)scope.getJavaLangObject(), 0, false);
        this.binding.closeTracker = this;
        this.binding.declaringScope = scope;
        this.binding.setConstant(Constant.NotAConstant);
        this.binding.useFlag = 1;
        this.binding.id = scope.registerTrackingVariable(this);
    }

    public static FakedTrackingVariable getCloseTrackingVariable(Expression expression, FlowInfo flowInfo, FlowContext flowContext) {
        while (true) {
            if (expression instanceof CastExpression) {
                expression = ((CastExpression)expression).expression;
                continue;
            }
            if (!(expression instanceof Assignment)) break;
            expression = ((Assignment)expression).expression;
        }
        if (expression instanceof ConditionalExpression) {
            FakedTrackingVariable falseTrackingVariable = FakedTrackingVariable.getCloseTrackingVariable(((ConditionalExpression)expression).valueIfFalse, flowInfo, flowContext);
            if (falseTrackingVariable != null) {
                return falseTrackingVariable;
            }
            return FakedTrackingVariable.getCloseTrackingVariable(((ConditionalExpression)expression).valueIfTrue, flowInfo, flowContext);
        }
        if (expression instanceof SwitchExpression) {
            for (Expression re : ((SwitchExpression)expression).resultExpressions) {
                FakedTrackingVariable fakedTrackingVariable = FakedTrackingVariable.getCloseTrackingVariable(re, flowInfo, flowContext);
                if (fakedTrackingVariable == null) continue;
                return fakedTrackingVariable;
            }
            return null;
        }
        if (expression instanceof SingleNameReference) {
            SingleNameReference name = (SingleNameReference)expression;
            if (name.binding instanceof LocalVariableBinding) {
                LocalVariableBinding local = (LocalVariableBinding)name.binding;
                if (local.closeTracker != null) {
                    return local.closeTracker;
                }
                if (!FakedTrackingVariable.isAnyCloseable(expression.resolvedType)) {
                    return null;
                }
                if ((local.tagBits & 0x2000L) != 0L) {
                    return null;
                }
                LocalDeclaration location = local.declaration;
                local.closeTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
                if (local.isParameter()) {
                    local.closeTracker.globalClosingState |= 4;
                }
                return local.closeTracker;
            }
        } else {
            if (expression instanceof AllocationExpression) {
                return ((AllocationExpression)expression).closeTracker;
            }
            if (expression instanceof MessageSend) {
                return ((MessageSend)expression).closeTracker;
            }
        }
        return null;
    }

    public static FakedTrackingVariable preConnectTrackerAcrossAssignment(ASTNode location, LocalVariableBinding local, Expression rhs, FlowInfo flowInfo) {
        FakedTrackingVariable closeTracker = null;
        if (FakedTrackingVariable.containsAllocation(rhs)) {
            closeTracker = local.closeTracker;
            if (closeTracker == null && rhs.resolvedType != TypeBinding.NULL) {
                closeTracker = new FakedTrackingVariable(local, location, flowInfo, null, 1);
                if (local.isParameter()) {
                    closeTracker.globalClosingState |= 4;
                }
            }
            if (closeTracker != null) {
                closeTracker.currentAssignment = location;
                FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, rhs);
            }
        } else if (rhs instanceof MessageSend) {
            closeTracker = local.closeTracker;
            if (closeTracker != null) {
                FakedTrackingVariable.handleReassignment(flowInfo, closeTracker, location);
            }
            if (rhs.resolvedType != TypeBinding.NULL) {
                closeTracker = new FakedTrackingVariable(local, location, flowInfo, null, 1);
                closeTracker.currentAssignment = location;
                ((MessageSend)rhs).closeTracker = closeTracker;
            }
        }
        return closeTracker;
    }

    private static boolean containsAllocation(SwitchExpression location) {
        for (Expression re : location.resultExpressions) {
            if (!FakedTrackingVariable.containsAllocation(re)) continue;
            return true;
        }
        return false;
    }

    private static boolean containsAllocation(ASTNode location) {
        if (location instanceof AllocationExpression) {
            return true;
        }
        if (location instanceof ConditionalExpression) {
            ConditionalExpression conditional = (ConditionalExpression)location;
            return FakedTrackingVariable.containsAllocation(conditional.valueIfTrue) || FakedTrackingVariable.containsAllocation(conditional.valueIfFalse);
        }
        if (location instanceof SwitchExpression) {
            return FakedTrackingVariable.containsAllocation((SwitchExpression)location);
        }
        if (location instanceof CastExpression) {
            return FakedTrackingVariable.containsAllocation(((CastExpression)location).expression);
        }
        if (location instanceof MessageSend && FakedTrackingVariable.isFluentMethod(((MessageSend)location).binding)) {
            return FakedTrackingVariable.containsAllocation(((MessageSend)location).receiver);
        }
        return false;
    }

    private static void preConnectTrackerAcrossAssignment(ASTNode location, LocalVariableBinding local, FlowInfo flowInfo, FakedTrackingVariable closeTracker, Expression expression) {
        if (expression instanceof AllocationExpression) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, (AllocationExpression)expression, closeTracker);
        } else if (expression instanceof ConditionalExpression) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, (ConditionalExpression)expression, closeTracker);
        } else if (expression instanceof SwitchExpression) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, (SwitchExpression)expression, closeTracker);
        } else if (expression instanceof CastExpression) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, ((CastExpression)expression).expression, flowInfo);
        } else if (expression instanceof MessageSend && FakedTrackingVariable.isFluentMethod(((MessageSend)expression).binding)) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, ((MessageSend)expression).receiver, flowInfo);
        }
    }

    private static void preConnectTrackerAcrossAssignment(ASTNode location, LocalVariableBinding local, FlowInfo flowInfo, ConditionalExpression conditional, FakedTrackingVariable closeTracker) {
        FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, conditional.valueIfFalse);
        FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, conditional.valueIfTrue);
    }

    private static void preConnectTrackerAcrossAssignment(ASTNode location, LocalVariableBinding local, FlowInfo flowInfo, SwitchExpression se, FakedTrackingVariable closeTracker) {
        for (Expression re : se.resultExpressions) {
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, re);
        }
    }

    private static void preConnectTrackerAcrossAssignment(ASTNode location, LocalVariableBinding local, FlowInfo flowInfo, AllocationExpression allocationExpression, FakedTrackingVariable closeTracker) {
        FakedTrackingVariable inner;
        allocationExpression.closeTracker = closeTracker;
        if (allocationExpression.arguments != null && allocationExpression.arguments.length > 0 && (inner = FakedTrackingVariable.preConnectTrackerAcrossAssignment(location, local, allocationExpression.arguments[0], flowInfo)) != closeTracker && closeTracker.innerTracker == null) {
            closeTracker.innerTracker = inner;
        }
    }

    public static void analyseCloseableAllocation(BlockScope scope, FlowInfo flowInfo, AllocationExpression allocation) {
        if (((ReferenceBinding)allocation.resolvedType).hasTypeBit(8)) {
            if (allocation.closeTracker != null) {
                allocation.closeTracker.withdraw();
                allocation.closeTracker = null;
            }
        } else if (((ReferenceBinding)allocation.resolvedType).hasTypeBit(4)) {
            boolean isWrapper = true;
            if (allocation.arguments != null && allocation.arguments.length > 0) {
                FakedTrackingVariable innerTracker = FakedTrackingVariable.findCloseTracker(scope, flowInfo, allocation.arguments[0]);
                if (innerTracker != null) {
                    int finallyStatus;
                    FakedTrackingVariable currentInner = innerTracker;
                    do {
                        if (currentInner != allocation.closeTracker) continue;
                        return;
                    } while ((currentInner = currentInner.innerTracker) != null);
                    int newStatus = 2;
                    if (allocation.closeTracker == null) {
                        allocation.closeTracker = new FakedTrackingVariable(scope, allocation, flowInfo, 1);
                    } else if (scope.finallyInfo != null && (finallyStatus = scope.finallyInfo.nullStatus(allocation.closeTracker.binding)) != 1) {
                        newStatus = finallyStatus;
                    }
                    if (allocation.closeTracker.innerTracker != null && allocation.closeTracker.innerTracker != innerTracker) {
                        innerTracker = FakedTrackingVariable.pickMoreUnsafe(allocation.closeTracker.innerTracker, innerTracker, scope, flowInfo);
                    }
                    allocation.closeTracker.innerTracker = innerTracker;
                    innerTracker.outerTracker = allocation.closeTracker;
                    flowInfo.markNullStatus(allocation.closeTracker.binding, newStatus);
                    if (newStatus != 2) {
                        FakedTrackingVariable currentTracker = innerTracker;
                        while (currentTracker != null) {
                            flowInfo.markNullStatus(currentTracker.binding, newStatus);
                            currentTracker.globalClosingState |= allocation.closeTracker.globalClosingState;
                            currentTracker = currentTracker.innerTracker;
                        }
                    }
                    return;
                }
                if (!FakedTrackingVariable.isAnyCloseable(allocation.arguments[0].resolvedType)) {
                    isWrapper = false;
                }
            } else {
                isWrapper = false;
            }
            if (isWrapper) {
                if (allocation.closeTracker != null) {
                    allocation.closeTracker.withdraw();
                    allocation.closeTracker = null;
                }
            } else {
                FakedTrackingVariable.handleRegularResource(scope, flowInfo, allocation);
            }
        } else {
            FakedTrackingVariable.handleRegularResource(scope, flowInfo, allocation);
        }
    }

    public static FlowInfo analyseCloseableAcquisition(BlockScope scope, FlowInfo flowInfo, MessageSend acquisition) {
        if (FakedTrackingVariable.isFluentMethod(acquisition.binding)) {
            acquisition.closeTracker = FakedTrackingVariable.findCloseTracker(scope, flowInfo, acquisition.receiver);
            return flowInfo;
        }
        if (((ReferenceBinding)acquisition.resolvedType).hasTypeBit(8)) {
            if (acquisition.closeTracker != null) {
                acquisition.closeTracker.withdraw();
                acquisition.closeTracker = null;
            }
            return flowInfo;
        }
        FakedTrackingVariable tracker = acquisition.closeTracker;
        if (tracker != null) {
            tracker.withdraw();
            acquisition.closeTracker = null;
            return flowInfo;
        }
        acquisition.closeTracker = tracker = new FakedTrackingVariable(scope, acquisition, flowInfo, 1);
        tracker.acquisition = acquisition;
        FlowInfo outsideInfo = flowInfo.copy();
        outsideInfo.markAsDefinitelyNonNull(tracker.binding);
        flowInfo.markAsDefinitelyNull(tracker.binding);
        return FlowInfo.conditional(outsideInfo, flowInfo);
    }

    private static boolean isFluentMethod(MethodBinding binding) {
        if (binding.isStatic()) {
            return false;
        }
        ReferenceBinding declaringClass = binding.declaringClass;
        if (declaringClass.equals(binding.returnType)) {
            char[][][] cArray = TypeConstants.FLUENT_RESOURCE_CLASSES;
            int n = TypeConstants.FLUENT_RESOURCE_CLASSES.length;
            int n2 = 0;
            while (n2 < n) {
                char[][] compoundName = cArray[n2];
                if (CharOperation.equals(compoundName, declaringClass.compoundName)) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    private static FakedTrackingVariable pickMoreUnsafe(FakedTrackingVariable tracker1, FakedTrackingVariable tracker2, BlockScope scope, FlowInfo info) {
        int status1 = info.nullStatus(tracker1.binding);
        int status2 = info.nullStatus(tracker2.binding);
        if (status1 == 2 || status2 == 4) {
            return FakedTrackingVariable.pick(tracker1, tracker2, scope);
        }
        if (status1 == 4 || status2 == 2) {
            return FakedTrackingVariable.pick(tracker2, tracker1, scope);
        }
        if ((status1 & 0x10) != 0) {
            return FakedTrackingVariable.pick(tracker1, tracker2, scope);
        }
        if ((status2 & 0x10) != 0) {
            return FakedTrackingVariable.pick(tracker2, tracker1, scope);
        }
        return FakedTrackingVariable.pick(tracker1, tracker2, scope);
    }

    private static FakedTrackingVariable pick(FakedTrackingVariable tracker1, FakedTrackingVariable tracker2, BlockScope scope) {
        tracker2.withdraw();
        return tracker1;
    }

    private static void handleRegularResource(BlockScope scope, FlowInfo flowInfo, AllocationExpression allocation) {
        FakedTrackingVariable presetTracker = allocation.closeTracker;
        if (presetTracker != null && presetTracker.originalBinding != null) {
            FakedTrackingVariable.handleReassignment(flowInfo, presetTracker, presetTracker.currentAssignment);
        } else {
            allocation.closeTracker = new FakedTrackingVariable(scope, allocation, flowInfo, 1);
        }
        flowInfo.markAsDefinitelyNull(allocation.closeTracker.binding);
    }

    private static void handleReassignment(FlowInfo flowInfo, FakedTrackingVariable existingTracker, ASTNode location) {
        int closeStatus = flowInfo.nullStatus(existingTracker.binding);
        if (closeStatus != 4 && closeStatus != 1 && !flowInfo.isDefinitelyNull(existingTracker.originalBinding) && !(location instanceof LocalDeclaration)) {
            existingTracker.recordErrorLocation(location, closeStatus);
        }
    }

    private static FakedTrackingVariable findCloseTracker(BlockScope scope, FlowInfo flowInfo, Expression arg) {
        while (arg instanceof Assignment) {
            Assignment assign = (Assignment)arg;
            LocalVariableBinding innerLocal = assign.localVariableBinding();
            if (innerLocal != null) {
                return innerLocal.closeTracker;
            }
            arg = assign.expression;
        }
        if (arg instanceof SingleNameReference) {
            LocalVariableBinding local = arg.localVariableBinding();
            if (local != null) {
                return local.closeTracker;
            }
        } else {
            if (arg instanceof AllocationExpression) {
                return ((AllocationExpression)arg).closeTracker;
            }
            if (arg instanceof MessageSend) {
                return ((MessageSend)arg).closeTracker;
            }
        }
        return null;
    }

    /*
     * Unable to fully structure code
     */
    public static void handleResourceAssignment(BlockScope scope, FlowInfo upstreamInfo, FlowInfo flowInfo, FlowContext flowContext, ASTNode location, Expression rhs, LocalVariableBinding local) {
        block20: {
            block21: {
                block22: {
                    previousTracker = null;
                    disconnectedTracker = null;
                    if (local.closeTracker != null) {
                        previousTracker = local.closeTracker;
                        nullStatus = upstreamInfo.nullStatus(local);
                        if (nullStatus != 2 && nullStatus != 1) {
                            disconnectedTracker = previousTracker;
                        }
                    }
                    if (rhs.resolvedType == TypeBinding.NULL) break block20;
                    rhsTrackVar = FakedTrackingVariable.getCloseTrackingVariable(rhs, flowInfo, flowContext);
                    if (rhsTrackVar == null) break block21;
                    if (local.closeTracker != null) break block22;
                    if (rhsTrackVar.originalBinding != null) {
                        local.closeTracker = rhsTrackVar;
                    }
                    if (rhsTrackVar.currentAssignment == location) {
                        rhsTrackVar.globalClosingState &= -135;
                    }
                    break block20;
                }
                if (!(rhs instanceof AllocationExpression) && !(rhs instanceof ConditionalExpression) && !(rhs instanceof SwitchExpression) && !(rhs instanceof MessageSend)) ** GOTO lbl-1000
                if (rhsTrackVar == disconnectedTracker) {
                    return;
                }
                if (local.closeTracker == rhsTrackVar && (rhsTrackVar.globalClosingState & 4) != 0) {
                    local.closeTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 2);
                } else lbl-1000:
                // 2 sources

                {
                    rhsTrackVar.attachTo(local);
                }
                break block20;
            }
            if (previousTracker != null) {
                currentFlowContext = flowContext;
                if (previousTracker.tryContext != null) {
                    while (currentFlowContext != null) {
                        if (previousTracker.tryContext != currentFlowContext) {
                            currentFlowContext = currentFlowContext.parent;
                            continue;
                        }
                        break;
                    }
                } else {
                    if ((previousTracker.globalClosingState & 134) == 0 && flowInfo.hasNullInfoFor(previousTracker.binding)) {
                        flowInfo.markAsDefinitelyNull(previousTracker.binding);
                    }
                    local.closeTracker = FakedTrackingVariable.analyseCloseableExpression(flowInfo, flowContext, local, location, rhs, previousTracker);
                }
            } else {
                rhsTrackVar = FakedTrackingVariable.analyseCloseableExpression(flowInfo, flowContext, local, location, rhs, null);
                if (rhsTrackVar != null) {
                    rhsTrackVar.attachTo(local);
                    if ((rhsTrackVar.globalClosingState & 134) == 0) {
                        flowInfo.markAsDefinitelyNull(rhsTrackVar.binding);
                    }
                }
            }
        }
        if (disconnectedTracker != null) {
            if (disconnectedTracker.innerTracker != null && disconnectedTracker.innerTracker.binding.declaringScope == scope) {
                disconnectedTracker.innerTracker.outerTracker = null;
                scope.pruneWrapperTrackingVar(disconnectedTracker);
            } else {
                upstreamStatus = upstreamInfo.nullStatus(disconnectedTracker.binding);
                if (upstreamStatus != 4) {
                    disconnectedTracker.recordErrorLocation(location, upstreamStatus);
                }
            }
        }
    }

    private static FakedTrackingVariable analyseCloseableExpression(FlowInfo flowInfo, FlowContext flowContext, LocalVariableBinding local, ASTNode location, Expression expression, FakedTrackingVariable previousTracker) {
        FakedTrackingVariable tracker;
        ReferenceBinding resourceType;
        while (true) {
            if (expression instanceof Assignment) {
                expression = ((Assignment)expression).expression;
                continue;
            }
            if (!(expression instanceof CastExpression)) break;
            expression = ((CastExpression)expression).expression;
        }
        boolean isResourceProducer = false;
        if (expression.resolvedType instanceof ReferenceBinding && (resourceType = (ReferenceBinding)expression.resolvedType).hasTypeBit(8)) {
            if (FakedTrackingVariable.isBlacklistedMethod(expression)) {
                isResourceProducer = true;
            } else {
                return null;
            }
        }
        if (expression instanceof AllocationExpression) {
            tracker = ((AllocationExpression)expression).closeTracker;
            if (tracker != null && tracker.originalBinding == null) {
                return null;
            }
            return tracker;
        }
        if (expression instanceof MessageSend || expression instanceof ArrayReference) {
            tracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 16);
            if (!isResourceProducer) {
                tracker.globalClosingState |= 2;
            }
            return tracker;
        }
        if ((expression.bits & 7) == 1 || expression instanceof QualifiedNameReference && ((QualifiedNameReference)expression).isFieldAccess()) {
            tracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
            tracker.globalClosingState |= 4;
            return tracker;
        }
        if (local.closeTracker != null) {
            return local.closeTracker;
        }
        FakedTrackingVariable newTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
        LocalVariableBinding rhsLocal = expression.localVariableBinding();
        if (rhsLocal != null && rhsLocal.isParameter()) {
            newTracker.globalClosingState |= 4;
        }
        return newTracker;
    }

    private static boolean isBlacklistedMethod(Expression expression) {
        MethodBinding method;
        if (expression instanceof MessageSend && (method = ((MessageSend)expression).binding) != null && method.isValidBinding()) {
            return CharOperation.equals(method.declaringClass.compoundName, TypeConstants.JAVA_NIO_FILE_FILES);
        }
        return false;
    }

    public static void cleanUpAfterAssignment(BlockScope currentScope, int lhsBits, Expression expression) {
        while (true) {
            if (expression instanceof Assignment) {
                expression = ((Assignment)expression).expression;
                continue;
            }
            if (!(expression instanceof CastExpression)) break;
            expression = ((CastExpression)expression).expression;
        }
        if (expression instanceof AllocationExpression) {
            FakedTrackingVariable tracker = ((AllocationExpression)expression).closeTracker;
            if (tracker != null && tracker.originalBinding == null) {
                tracker.withdraw();
                ((AllocationExpression)expression).closeTracker = null;
            }
        } else if (expression instanceof MessageSend) {
            FakedTrackingVariable tracker = ((MessageSend)expression).closeTracker;
            if (tracker != null && tracker.originalBinding == null) {
                tracker.withdraw();
                ((MessageSend)expression).closeTracker = null;
            }
        } else {
            LocalVariableBinding local = expression.localVariableBinding();
            if (local != null && local.closeTracker != null && (lhsBits & 1) != 0) {
                local.closeTracker.withdraw();
            }
        }
    }

    public static void cleanUpUnassigned(BlockScope scope, ASTNode location, final FlowInfo flowInfo) {
        if (!scope.hasResourceTrackers()) {
            return;
        }
        location.traverse(new ASTVisitor(){

            @Override
            public boolean visit(MessageSend messageSend, BlockScope skope) {
                FakedTrackingVariable closeTracker = messageSend.closeTracker;
                if (closeTracker != null && closeTracker.originalBinding == null) {
                    int nullStatus = flowInfo.nullStatus(closeTracker.binding);
                    if ((nullStatus & 0x12) != 0) {
                        closeTracker.reportError(skope.problemReporter(), messageSend, nullStatus);
                    }
                    closeTracker.withdraw();
                }
                return true;
            }
        }, scope);
    }

    public static boolean isAnyCloseable(TypeBinding typeBinding) {
        return typeBinding instanceof ReferenceBinding && ((ReferenceBinding)typeBinding).hasTypeBit(3);
    }

    public int findMostSpecificStatus(FlowInfo flowInfo, BlockScope currentScope, BlockScope locationScope) {
        int status = 1;
        FakedTrackingVariable currentTracker = this;
        while (currentTracker != null) {
            LocalVariableBinding currentVar = currentTracker.binding;
            int currentStatus = this.getNullStatusAggressively(currentVar, flowInfo);
            if (locationScope != null) {
                currentStatus = this.mergeCloseStatus(locationScope, currentStatus, currentVar, currentScope);
            }
            if (currentStatus == 4) {
                status = currentStatus;
                break;
            }
            if (status == 2 || status == 1) {
                status = currentStatus;
            }
            currentTracker = currentTracker.innerTracker;
        }
        return status;
    }

    private int getNullStatusAggressively(LocalVariableBinding local, FlowInfo flowInfo) {
        if (flowInfo == FlowInfo.DEAD_END) {
            return 1;
        }
        int reachMode = flowInfo.reachMode();
        int status = 0;
        try {
            if (reachMode != 0) {
                flowInfo.tagBits &= 0xFFFFFFFC;
            }
            status = flowInfo.nullStatus(local);
            if (TEST_372319) {
                try {
                    Thread.sleep(5L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        finally {
            flowInfo.tagBits |= reachMode;
        }
        if ((status & 2) != 0) {
            if ((status & 0x24) != 0) {
                return 16;
            }
            return 2;
        }
        if ((status & 4) != 0) {
            if ((status & 0x10) != 0) {
                return 16;
            }
            return 4;
        }
        if ((status & 0x10) != 0) {
            return 16;
        }
        if (status == 1 && this.originalBinding == null) {
            return 2;
        }
        return status;
    }

    public int mergeCloseStatus(BlockScope currentScope, int status, LocalVariableBinding local, BlockScope outerScope) {
        if (status != 4) {
            if (currentScope.finallyInfo != null) {
                int finallyStatus = currentScope.finallyInfo.nullStatus(local);
                if (finallyStatus == 4) {
                    return finallyStatus;
                }
                if (finallyStatus != 2 && currentScope.finallyInfo.hasNullInfoFor(local)) {
                    status = 16;
                }
            }
            if (currentScope != outerScope && currentScope.parent instanceof BlockScope) {
                return this.mergeCloseStatus((BlockScope)currentScope.parent, status, local, outerScope);
            }
        }
        return status;
    }

    public void markClose(FlowInfo flowInfo, FlowContext flowContext) {
        FakedTrackingVariable current = this;
        do {
            flowInfo.markAsDefinitelyNonNull(current.binding);
            current.globalClosingState |= 1;
            flowContext.markFinallyNullStatus(current.binding, 4);
        } while ((current = current.innerTracker) != null);
    }

    public void markClosedInNestedMethod() {
        this.globalClosingState |= 8;
    }

    public void markClosedEffectivelyFinal() {
        this.globalClosingState |= 0x100;
    }

    public static FlowInfo markPassedToOutside(BlockScope scope, Expression expression, FlowInfo flowInfo, FlowContext flowContext, boolean owned) {
        FakedTrackingVariable trackVar = FakedTrackingVariable.getCloseTrackingVariable(expression, flowInfo, flowContext);
        if (trackVar != null) {
            FlowInfo infoResourceIsClosed = owned ? flowInfo : flowInfo.copy();
            int flag = owned ? 4 : 2;
            do {
                trackVar.globalClosingState |= flag;
                if (scope.methodScope() != trackVar.methodScope) {
                    trackVar.globalClosingState |= 8;
                }
                infoResourceIsClosed.markAsDefinitelyNonNull(trackVar.binding);
            } while ((trackVar = trackVar.innerTracker) != null);
            if (owned) {
                return infoResourceIsClosed;
            }
            return FlowInfo.conditional(flowInfo, infoResourceIsClosed).unconditionalCopy();
        }
        return flowInfo;
    }

    public static void markForeachElementVar(LocalDeclaration local) {
        if (local.binding != null && local.binding.closeTracker != null) {
            local.binding.closeTracker.globalClosingState |= 0x80;
        }
    }

    public boolean hasDefinitelyNoResource(FlowInfo flowInfo) {
        if (this.originalBinding == null) {
            return false;
        }
        if (flowInfo.isDefinitelyNull(this.originalBinding)) {
            return true;
        }
        return !flowInfo.isDefinitelyAssigned(this.originalBinding) && !flowInfo.isPotentiallyAssigned(this.originalBinding);
    }

    public boolean isClosedInFinallyOfEnclosing(BlockScope scope) {
        BlockScope currentScope = scope;
        while (currentScope.finallyInfo == null || !currentScope.finallyInfo.isDefinitelyNonNull(this.binding)) {
            if (!(currentScope.parent instanceof BlockScope)) {
                return false;
            }
            currentScope = (BlockScope)currentScope.parent;
        }
        return true;
    }

    public boolean isResourceBeingReturned(FakedTrackingVariable returnedResource) {
        FakedTrackingVariable current = this;
        do {
            if (current != returnedResource) continue;
            this.globalClosingState |= 0x40;
            return true;
        } while ((current = current.innerTracker) != null);
        return false;
    }

    public void withdraw() {
        this.binding.declaringScope.removeTrackingVar(this);
    }

    public void recordErrorLocation(ASTNode location, int nullStatus) {
        if ((this.globalClosingState & 4) != 0) {
            return;
        }
        if (this.recordedLocations == null) {
            this.recordedLocations = new HashMap();
        }
        this.recordedLocations.put(location, nullStatus);
    }

    public boolean reportRecordedErrors(Scope scope, int mergedStatus, boolean atDeadEnd) {
        FakedTrackingVariable current = this;
        while (current.globalClosingState == 0) {
            current = current.innerTracker;
            if (current != null) continue;
            if (atDeadEnd && this.neverClosedAtLocations()) {
                mergedStatus = 2;
            }
            if ((mergedStatus & 0x32) == 0) break;
            this.reportError(scope.problemReporter(), null, mergedStatus);
            return true;
        }
        boolean hasReported = false;
        if (this.recordedLocations != null) {
            Iterator locations = this.recordedLocations.entrySet().iterator();
            int reportFlags = 0;
            while (locations.hasNext()) {
                Map.Entry entry = locations.next();
                reportFlags |= this.reportError(scope.problemReporter(), (ASTNode)entry.getKey(), (Integer)entry.getValue());
                hasReported = true;
            }
            if (reportFlags != 0) {
                current = this;
                do {
                    current.globalClosingState |= reportFlags;
                } while ((current = current.innerTracker) != null);
            }
        }
        return hasReported;
    }

    private boolean neverClosedAtLocations() {
        if (this.recordedLocations != null) {
            for (Object value : this.recordedLocations.values()) {
                if (value.equals(2)) continue;
                return false;
            }
        }
        return true;
    }

    public int reportError(ProblemReporter problemReporter, ASTNode location, int nullStatus) {
        int reportFlag;
        if ((this.globalClosingState & 4) != 0) {
            return 0;
        }
        boolean isPotentialProblem = false;
        if (nullStatus == 2) {
            if ((this.globalClosingState & 8) != 0) {
                isPotentialProblem = true;
            }
        } else if ((nullStatus & 0x30) != 0) {
            isPotentialProblem = true;
        }
        if (isPotentialProblem) {
            if ((this.globalClosingState & 0x60) != 0) {
                return 0;
            }
            problemReporter.potentiallyUnclosedCloseable(this, location);
        } else {
            if ((this.globalClosingState & 0x40) != 0) {
                return 0;
            }
            problemReporter.unclosedCloseable(this, location);
        }
        int n = reportFlag = isPotentialProblem ? 32 : 64;
        if (location == null) {
            FakedTrackingVariable current = this;
            do {
                current.globalClosingState |= reportFlag;
            } while ((current = current.innerTracker) != null);
        }
        return reportFlag;
    }

    public void reportExplicitClosing(ProblemReporter problemReporter) {
        if ((this.globalClosingState & 0x194) == 0) {
            this.globalClosingState |= 0x10;
            problemReporter.explicitlyClosedAutoCloseable(this);
        }
    }

    public String nameForReporting(ASTNode location, ReferenceContext referenceContext) {
        int reportLine;
        int[] lineEnds;
        int resourceLine;
        CompilationResult compResult;
        if (this.name == UNASSIGNED_CLOSEABLE_NAME && location != null && referenceContext != null && (compResult = referenceContext.compilationResult()) != null && (resourceLine = Util.getLineNumber(this.sourceStart, lineEnds = compResult.getLineSeparatorPositions(), 0, lineEnds.length - 1)) != (reportLine = Util.getLineNumber(location.sourceStart, lineEnds, 0, lineEnds.length - 1))) {
            char[] replacement = Integer.toString(resourceLine).toCharArray();
            return String.valueOf(CharOperation.replace(UNASSIGNED_CLOSEABLE_NAME_TEMPLATE, TEMPLATE_ARGUMENT, replacement));
        }
        return String.valueOf(this.name);
    }

    public static class IteratorForReporting
    implements Iterator<FakedTrackingVariable> {
        private final Set<FakedTrackingVariable> varSet;
        private final Scope scope;
        private final boolean atExit;
        private Stage stage;
        private Iterator<FakedTrackingVariable> iterator;
        private FakedTrackingVariable next;

        public IteratorForReporting(List<FakedTrackingVariable> variables, Scope scope, boolean atExit) {
            this.varSet = new HashSet<FakedTrackingVariable>(variables);
            this.scope = scope;
            this.atExit = atExit;
            this.setUpForStage(Stage.OuterLess);
        }

        @Override
        public boolean hasNext() {
            switch (this.stage) {
                case OuterLess: {
                    FakedTrackingVariable trackingVar;
                    while (this.iterator.hasNext()) {
                        trackingVar = this.iterator.next();
                        if (trackingVar.outerTracker != null) continue;
                        return this.found(trackingVar);
                    }
                    this.setUpForStage(Stage.InnerOfProcessed);
                }
                case InnerOfProcessed: {
                    FakedTrackingVariable outer;
                    FakedTrackingVariable trackingVar;
                    while (this.iterator.hasNext()) {
                        trackingVar = this.iterator.next();
                        outer = trackingVar.outerTracker;
                        if (outer.binding.declaringScope != this.scope || this.varSet.contains(outer)) continue;
                        return this.found(trackingVar);
                    }
                    this.setUpForStage(Stage.InnerOfNotEnclosing);
                }
                case InnerOfNotEnclosing: {
                    FakedTrackingVariable outer;
                    FakedTrackingVariable trackingVar;
                    block8: while (this.iterator.hasNext()) {
                        trackingVar = this.iterator.next();
                        outer = trackingVar.outerTracker;
                        if (this.varSet.contains(outer)) continue;
                        BlockScope outerTrackerScope = outer.binding.declaringScope;
                        Scope currentScope = this.scope;
                        while ((currentScope = currentScope.parent) instanceof BlockScope) {
                            if (outerTrackerScope == currentScope) break block8;
                        }
                        return this.found(trackingVar);
                    }
                    this.setUpForStage(Stage.AtExit);
                }
                case AtExit: {
                    if (this.atExit && this.iterator.hasNext()) {
                        return this.found(this.iterator.next());
                    }
                    return false;
                }
            }
            throw new IllegalStateException("Unexpected Stage " + (Object)((Object)this.stage));
        }

        private boolean found(FakedTrackingVariable trackingVar) {
            this.iterator.remove();
            this.next = trackingVar;
            return true;
        }

        private void setUpForStage(Stage nextStage) {
            this.iterator = this.varSet.iterator();
            this.stage = nextStage;
        }

        @Override
        public FakedTrackingVariable next() {
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        static enum Stage {
            OuterLess,
            InnerOfProcessed,
            InnerOfNotEnclosing,
            AtExit;

        }
    }
}

