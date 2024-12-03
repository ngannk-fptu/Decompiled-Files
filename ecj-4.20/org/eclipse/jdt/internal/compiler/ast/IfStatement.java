/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class IfStatement
extends Statement {
    public Expression condition;
    public Statement thenStatement;
    public Statement elseStatement;
    int thenInitStateIndex = -1;
    int elseInitStateIndex = -1;
    int mergedInitStateIndex = -1;

    public IfStatement(Expression condition, Statement thenStatement, int sourceStart, int sourceEnd) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        if (thenStatement instanceof EmptyStatement) {
            thenStatement.bits |= 1;
        }
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    public IfStatement(Expression condition, Statement thenStatement, Statement elseStatement, int sourceStart, int sourceEnd) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        if (thenStatement instanceof EmptyStatement) {
            thenStatement.bits |= 1;
        }
        this.elseStatement = elseStatement;
        if (elseStatement instanceof IfStatement) {
            elseStatement.bits |= 0x20000000;
        }
        if (elseStatement instanceof EmptyStatement) {
            elseStatement.bits |= 1;
        }
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        boolean reportDeadCodeForKnownPattern;
        FlowInfo conditionFlowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo);
        int initialComplaintLevel = (flowInfo.reachMode() & 3) != 0 ? 1 : 0;
        Constant cst = this.condition.optimizedBooleanConstant();
        this.condition.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        ++flowContext.conditionalLevel;
        FlowInfo thenFlowInfo = conditionFlowInfo.safeInitsWhenTrue();
        if (isConditionOptimizedFalse) {
            thenFlowInfo.setReachMode(1);
        }
        FlowInfo elseFlowInfo = conditionFlowInfo.initsWhenFalse().copy();
        if (isConditionOptimizedTrue) {
            elseFlowInfo.setReachMode(1);
        }
        if ((flowInfo.tagBits & 3) == 0 && (thenFlowInfo.tagBits & 3) != 0) {
            this.bits |= 0x100;
        } else if ((flowInfo.tagBits & 3) == 0 && (elseFlowInfo.tagBits & 3) != 0) {
            this.bits |= 0x80;
        }
        boolean bl = reportDeadCodeForKnownPattern = !IfStatement.isKnowDeadCodePattern(this.condition) || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement;
        if (this.thenStatement != null) {
            this.thenInitStateIndex = currentScope.methodScope().recordInitializationStates(thenFlowInfo);
            if (isConditionOptimizedFalse || (this.bits & 0x100) != 0) {
                if (reportDeadCodeForKnownPattern) {
                    this.thenStatement.complainIfUnreachable(thenFlowInfo, currentScope, initialComplaintLevel, false);
                } else {
                    this.bits &= 0xFFFFFEFF;
                }
            }
            this.condition.updateFlowOnBooleanResult(thenFlowInfo, true);
            thenFlowInfo = this.thenStatement.analyseCode(currentScope, flowContext, thenFlowInfo);
            if (!(this.thenStatement instanceof Block)) {
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        flowContext.expireNullCheckedFieldInfo();
        if ((thenFlowInfo.tagBits & 1) != 0) {
            this.bits |= 0x40000000;
        }
        if (this.elseStatement != null) {
            if (thenFlowInfo == FlowInfo.DEAD_END && (this.bits & 0x20000000) == 0 && !(this.elseStatement instanceof IfStatement)) {
                currentScope.problemReporter().unnecessaryElse(this.elseStatement);
            }
            this.elseInitStateIndex = currentScope.methodScope().recordInitializationStates(elseFlowInfo);
            if (isConditionOptimizedTrue || (this.bits & 0x80) != 0) {
                if (reportDeadCodeForKnownPattern) {
                    this.elseStatement.complainIfUnreachable(elseFlowInfo, currentScope, initialComplaintLevel, false);
                } else {
                    this.bits &= 0xFFFFFF7F;
                }
            }
            this.condition.updateFlowOnBooleanResult(elseFlowInfo, false);
            elseFlowInfo = this.elseStatement.analyseCode(currentScope, flowContext, elseFlowInfo);
            if (!(this.elseStatement instanceof Block)) {
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        currentScope.correlateTrackingVarsIfElse(thenFlowInfo, elseFlowInfo);
        UnconditionalFlowInfo mergedInfo = FlowInfo.mergedOptimizedBranchesIfElse(thenFlowInfo, isConditionOptimizedTrue, elseFlowInfo, isConditionOptimizedFalse, true, flowInfo, this, reportDeadCodeForKnownPattern);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        --flowContext.conditionalLevel;
        return mergedInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        boolean hasElsePart;
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        BranchLabel endifLabel = new BranchLabel(codeStream);
        Constant cst = this.condition.optimizedBooleanConstant();
        boolean hasThenPart = (cst == Constant.NotAConstant || cst.booleanValue()) && this.thenStatement != null && !this.thenStatement.isEmptyBlock();
        boolean bl = hasElsePart = (cst == Constant.NotAConstant || !cst.booleanValue()) && this.elseStatement != null && !this.elseStatement.isEmptyBlock();
        if (hasThenPart) {
            BranchLabel falseLabel = null;
            if (cst != Constant.NotAConstant && cst.booleanValue()) {
                this.condition.generateCode(currentScope, codeStream, false);
            } else {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, null, hasElsePart ? (falseLabel = new BranchLabel(codeStream)) : endifLabel, true);
            }
            if (this.thenInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
            }
            this.thenStatement.generateCode(currentScope, codeStream);
            if (hasElsePart) {
                if ((this.bits & 0x40000000) == 0) {
                    this.thenStatement.branchChainTo(endifLabel);
                    int position = codeStream.position;
                    codeStream.goto_(endifLabel);
                    codeStream.recordPositionsFrom(position, this.thenStatement.sourceEnd);
                }
                if (this.elseInitStateIndex != -1) {
                    codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                }
                if (falseLabel != null) {
                    falseLabel.place();
                }
                this.elseStatement.generateCode(currentScope, codeStream);
            }
        } else if (hasElsePart) {
            if (cst != Constant.NotAConstant && !cst.booleanValue()) {
                this.condition.generateCode(currentScope, codeStream, false);
            } else {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, endifLabel, null, true);
            }
            if (this.elseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
            }
            this.elseStatement.generateCode(currentScope, codeStream);
        } else {
            if (this.condition.containsPatternVariable()) {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, endifLabel, null, cst == Constant.NotAConstant);
            } else {
                this.condition.generateCode(currentScope, codeStream, false);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        endifLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        IfStatement.printIndent(indent, output).append("if (");
        this.condition.printExpression(0, output).append(")\n");
        this.thenStatement.printStatement(indent + 2, output);
        if (this.elseStatement != null) {
            output.append('\n');
            IfStatement.printIndent(indent, output);
            output.append("else\n");
            this.elseStatement.printStatement(indent + 2, output);
        }
        return output;
    }

    private void resolveIfStatement(BlockScope scope) {
        TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
        this.condition.computeConversion(scope, type, type);
        if (this.thenStatement != null) {
            this.thenStatement.resolve(scope);
        }
        if (this.elseStatement != null) {
            this.elseStatement.resolve(scope);
        }
    }

    @Override
    public void resolve(BlockScope scope) {
        if (this.containsPatternVariable()) {
            this.condition.collectPatternVariablesToScope(null, scope);
            LocalVariableBinding[] patternVariablesInTrueScope = this.condition.getPatternVariablesWhenTrue();
            LocalVariableBinding[] patternVariablesInFalseScope = this.condition.getPatternVariablesWhenFalse();
            TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
            this.condition.computeConversion(scope, type, type);
            if (this.thenStatement != null) {
                this.thenStatement.resolveWithPatternVariablesInScope(patternVariablesInTrueScope, scope);
            }
            if (this.elseStatement != null) {
                this.elseStatement.resolveWithPatternVariablesInScope(patternVariablesInFalseScope, scope);
            }
            if (this.thenStatement != null) {
                this.thenStatement.promotePatternVariablesIfApplicable(patternVariablesInFalseScope, this.thenStatement::doesNotCompleteNormally);
            }
            if (this.elseStatement != null) {
                this.elseStatement.promotePatternVariablesIfApplicable(patternVariablesInTrueScope, this.elseStatement::doesNotCompleteNormally);
            }
        } else {
            this.resolveIfStatement(scope);
        }
    }

    @Override
    public boolean containsPatternVariable() {
        return this.condition.containsPatternVariable();
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.condition.traverse(visitor, blockScope);
            if (this.thenStatement != null) {
                this.thenStatement.traverse(visitor, blockScope);
            }
            if (this.elseStatement != null) {
                this.elseStatement.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public boolean doesNotCompleteNormally() {
        return this.thenStatement != null && this.thenStatement.doesNotCompleteNormally() && this.elseStatement != null && this.elseStatement.doesNotCompleteNormally();
    }

    @Override
    public boolean completesByContinue() {
        return this.thenStatement != null && this.thenStatement.completesByContinue() || this.elseStatement != null && this.elseStatement.completesByContinue();
    }

    @Override
    public boolean canCompleteNormally() {
        return this.thenStatement == null || this.thenStatement.canCompleteNormally() || this.elseStatement == null || this.elseStatement.canCompleteNormally();
    }

    @Override
    public boolean continueCompletes() {
        return this.thenStatement != null && this.thenStatement.continueCompletes() || this.elseStatement != null && this.elseStatement.continueCompletes();
    }
}

