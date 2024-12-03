/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ForStatement
extends Statement {
    public Statement[] initializations;
    public Expression condition;
    public Statement[] increments;
    public Statement action;
    public BlockScope scope;
    private BranchLabel breakLabel;
    private BranchLabel continueLabel;
    int preCondInitStateIndex = -1;
    int preIncrementsInitStateIndex = -1;
    int condIfTrueInitStateIndex = -1;
    int mergedInitStateIndex = -1;

    public ForStatement(Statement[] initializations, Expression condition, Statement[] increments, Statement action, boolean neededScope, int s, int e) {
        this.sourceStart = s;
        this.sourceEnd = e;
        this.initializations = initializations;
        this.condition = condition;
        this.increments = increments;
        this.action = action;
        if (action instanceof EmptyStatement) {
            action.bits |= 1;
        }
        if (neededScope) {
            this.bits |= 0x20000000;
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        int i;
        LoopingFlowContext loopingContext;
        UnconditionalFlowInfo actionInfo;
        int initialComplaintLevel;
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        int n = initialComplaintLevel = (flowInfo.reachMode() & 3) != 0 ? 1 : 0;
        if (this.initializations != null) {
            int i2 = 0;
            int count = this.initializations.length;
            while (i2 < count) {
                flowInfo = this.initializations[i2].analyseCode(this.scope, flowContext, flowInfo);
                ++i2;
            }
        }
        this.preCondInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        Constant cst = this.condition == null ? null : this.condition.constant;
        boolean isConditionTrue = cst == null || cst != Constant.NotAConstant && cst.booleanValue();
        boolean isConditionFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
        boolean isConditionOptimizedTrue = cst == null || cst != Constant.NotAConstant && cst.booleanValue();
        boolean isConditionOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        LoopingFlowContext condLoopContext = null;
        FlowInfo condInfo = flowInfo.nullInfoLessUnconditionalCopy();
        if (this.condition != null && !isConditionTrue) {
            condLoopContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, this.scope, true);
            condInfo = this.condition.analyseCode(this.scope, condLoopContext, condInfo);
            this.condition.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        }
        if (this.action == null || this.action.isEmptyBlock() && currentScope.compilerOptions().complianceLevel <= 0x2F0000L) {
            if (condLoopContext != null) {
                condLoopContext.complainOnDeferredFinalChecks(this.scope, condInfo);
            }
            if (isConditionTrue) {
                if (condLoopContext != null) {
                    condLoopContext.complainOnDeferredNullChecks(currentScope, condInfo);
                }
                return FlowInfo.DEAD_END;
            }
            if (isConditionFalse) {
                this.continueLabel = null;
            }
            actionInfo = condInfo.initsWhenTrue().unconditionalCopy();
            loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope, false);
        } else {
            loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope, true);
            FlowInfo initsWhenTrue = condInfo.initsWhenTrue();
            this.condIfTrueInitStateIndex = currentScope.methodScope().recordInitializationStates(initsWhenTrue);
            if (isConditionFalse) {
                actionInfo = FlowInfo.DEAD_END;
            } else {
                actionInfo = initsWhenTrue.unconditionalCopy();
                if (isConditionOptimizedFalse) {
                    actionInfo.setReachMode(1);
                }
            }
            if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel, true) < 2) {
                if (this.condition != null) {
                    this.condition.updateFlowOnBooleanResult(actionInfo, true);
                }
                actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalInits();
            }
            if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & 1) != 0) {
                this.continueLabel = null;
            } else {
                if (condLoopContext != null) {
                    condLoopContext.complainOnDeferredFinalChecks(this.scope, condInfo);
                }
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
                loopingContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
            }
        }
        FlowInfo exitBranch = flowInfo.copy();
        LoopingFlowContext incrementContext = null;
        if (this.continueLabel != null) {
            if (this.increments != null) {
                incrementContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, this.scope, true);
                FlowInfo incrementInfo = actionInfo;
                this.preIncrementsInitStateIndex = currentScope.methodScope().recordInitializationStates(incrementInfo);
                i = 0;
                int count = this.increments.length;
                while (i < count) {
                    incrementInfo = this.increments[i].analyseCode(this.scope, incrementContext, incrementInfo);
                    ++i;
                }
                actionInfo = ((FlowInfo)incrementInfo).unconditionalInits();
                incrementContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
            }
            exitBranch.addPotentialInitializationsFrom(actionInfo).addInitializationsFrom(condInfo.initsWhenFalse());
        } else {
            exitBranch.addInitializationsFrom(condInfo.initsWhenFalse());
            if (this.increments != null && initialComplaintLevel == 0) {
                currentScope.problemReporter().fakeReachable(this.increments[0]);
            }
        }
        if (condLoopContext != null) {
            condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        }
        loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        if (incrementContext != null) {
            incrementContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        }
        if (loopingContext.hasEscapingExceptions()) {
            FlowInfo loopbackFlowInfo = flowInfo.copy();
            if (this.continueLabel != null) {
                loopbackFlowInfo = loopbackFlowInfo.mergedWith(loopbackFlowInfo.unconditionalCopy().addNullInfoFrom(actionInfo).unconditionalInits());
            }
            loopingContext.simulateThrowAfterLoopBack(loopbackFlowInfo);
        }
        UnconditionalFlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches((loopingContext.initsOnBreak.tagBits & 3) != 0 ? loopingContext.initsOnBreak : flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), isConditionOptimizedTrue, exitBranch, isConditionOptimizedFalse, !isConditionTrue);
        if (this.initializations != null) {
            i = 0;
            while (i < this.initializations.length) {
                Statement init = this.initializations[i];
                if (init instanceof LocalDeclaration) {
                    LocalVariableBinding binding = ((LocalDeclaration)init).binding;
                    ((FlowInfo)mergedInfo).resetAssignmentInfo(binding);
                }
                ++i;
            }
        }
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        this.scope.checkUnclosedCloseables(mergedInfo, loopingContext, null, null);
        if (this.condition != null) {
            this.condition.updateFlowOnBooleanResult(mergedInfo, false);
        }
        return mergedInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        boolean isConditionOptimizedFalse;
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.initializations != null) {
            int i = 0;
            int max = this.initializations.length;
            while (i < max) {
                this.initializations[i].generateCode(this.scope, codeStream);
                ++i;
            }
        }
        if (this.containsPatternVariable()) {
            this.condition.addPatternVariables(currentScope, codeStream);
        }
        Constant cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
        boolean bl = isConditionOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        if (isConditionOptimizedFalse) {
            this.condition.generateCode(this.scope, codeStream, false);
            if ((this.bits & 0x20000000) != 0) {
                codeStream.exitUserScope(this.scope);
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        BranchLabel actionLabel = new BranchLabel(codeStream);
        actionLabel.tagBits |= 2;
        BranchLabel conditionLabel = new BranchLabel(codeStream);
        this.breakLabel.initialize(codeStream);
        if (this.continueLabel == null) {
            conditionLabel.place();
            if (this.condition != null && this.condition.constant == Constant.NotAConstant) {
                this.condition.generateOptimizedBoolean(this.scope, codeStream, null, this.breakLabel, true);
            }
        } else {
            this.continueLabel.initialize(codeStream);
            if (this.condition != null && this.condition.constant == Constant.NotAConstant && (this.action != null && !this.action.isEmptyBlock() || this.increments != null)) {
                conditionLabel.tagBits |= 2;
                int jumpPC = codeStream.position;
                codeStream.goto_(conditionLabel);
                codeStream.recordPositionsFrom(jumpPC, this.condition.sourceStart);
            }
        }
        if (this.action != null) {
            if (this.condIfTrueInitStateIndex != -1) {
                codeStream.addDefinitelyAssignedVariables(currentScope, this.condIfTrueInitStateIndex);
            }
            actionLabel.place();
            this.action.generateCode(this.scope, codeStream);
        } else {
            actionLabel.place();
        }
        if (this.preIncrementsInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
        }
        if (this.continueLabel != null) {
            this.continueLabel.place();
            if (this.increments != null) {
                int i = 0;
                int max = this.increments.length;
                while (i < max) {
                    this.increments[i].generateCode(this.scope, codeStream);
                    ++i;
                }
            }
            if (this.preCondInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
            }
            conditionLabel.place();
            if (this.condition != null && this.condition.constant == Constant.NotAConstant) {
                this.condition.generateOptimizedBoolean(this.scope, codeStream, actionLabel, null, true);
            } else {
                codeStream.goto_(actionLabel);
            }
        } else if (this.preCondInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
        }
        if ((this.bits & 0x20000000) != 0) {
            codeStream.exitUserScope(this.scope);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        this.breakLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        int i;
        ForStatement.printIndent(tab, output).append("for (");
        if (this.initializations != null) {
            i = 0;
            while (i < this.initializations.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.initializations[i].print(0, output);
                ++i;
            }
        }
        output.append("; ");
        if (this.condition != null) {
            this.condition.printExpression(0, output);
        }
        output.append("; ");
        if (this.increments != null) {
            i = 0;
            while (i < this.increments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.increments[i].print(0, output);
                ++i;
            }
        }
        output.append(") ");
        if (this.action == null) {
            output.append(';');
        } else {
            output.append('\n');
            this.action.printStatement(tab + 1, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope upperScope) {
        int length;
        LocalVariableBinding[] patternVariablesInTrueScope = null;
        LocalVariableBinding[] patternVariablesInFalseScope = null;
        if (this.containsPatternVariable()) {
            this.condition.collectPatternVariablesToScope(null, upperScope);
            patternVariablesInTrueScope = this.condition.getPatternVariablesWhenTrue();
            patternVariablesInFalseScope = this.condition.getPatternVariablesWhenFalse();
        }
        BlockScope blockScope = this.scope = (this.bits & 0x20000000) != 0 ? new BlockScope(upperScope) : upperScope;
        if (this.initializations != null) {
            int i = 0;
            length = this.initializations.length;
            while (i < length) {
                this.initializations[i].resolve(this.scope);
                ++i;
            }
        }
        if (this.condition != null) {
            TypeBinding type = this.condition.resolveTypeExpecting(this.scope, TypeBinding.BOOLEAN);
            this.condition.computeConversion(this.scope, type, type);
        }
        if (this.increments != null) {
            int i = 0;
            length = this.increments.length;
            while (i < length) {
                this.increments[i].resolveWithPatternVariablesInScope(patternVariablesInTrueScope, this.scope);
                ++i;
            }
        }
        if (this.action != null) {
            this.action.resolveWithPatternVariablesInScope(patternVariablesInTrueScope, this.scope);
            this.action.promotePatternVariablesIfApplicable(patternVariablesInFalseScope, () -> !this.action.breaksOut(null));
        }
    }

    @Override
    public boolean containsPatternVariable() {
        return this.condition != null && this.condition.containsPatternVariable();
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            int i;
            if (this.initializations != null) {
                int initializationsLength = this.initializations.length;
                i = 0;
                while (i < initializationsLength) {
                    this.initializations[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.condition != null) {
                this.condition.traverse(visitor, this.scope);
            }
            if (this.increments != null) {
                int incrementsLength = this.increments.length;
                i = 0;
                while (i < incrementsLength) {
                    this.increments[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.action != null) {
                this.action.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public boolean doesNotCompleteNormally() {
        boolean isConditionOptimizedTrue;
        Constant cst = this.condition == null ? null : this.condition.constant;
        boolean isConditionTrue = cst == null || cst != Constant.NotAConstant && cst.booleanValue();
        Constant constant = cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
        boolean bl = cst == null ? true : (isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue());
        return !(!isConditionTrue && !isConditionOptimizedTrue || this.action != null && this.action.breaksOut(null));
    }

    @Override
    public boolean completesByContinue() {
        return this.action.continuesAtOuterLabel();
    }

    @Override
    public boolean canCompleteNormally() {
        boolean isConditionOptimizedTrue;
        Constant cst = this.condition == null ? null : this.condition.constant;
        boolean isConditionTrue = cst == null || cst != Constant.NotAConstant && cst.booleanValue();
        Constant constant = cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
        boolean bl = cst == null ? true : (isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue());
        if (!isConditionTrue && !isConditionOptimizedTrue) {
            return true;
        }
        return this.action != null && this.action.breaksOut(null);
    }

    @Override
    public boolean continueCompletes() {
        return this.action.continuesAtOuterLabel();
    }
}

