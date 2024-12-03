/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BranchStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class YieldStatement
extends BranchStatement {
    public Expression expression;
    public SwitchExpression switchExpression;
    public TryStatement tryStatement;
    public boolean isImplicit;
    static final char[] SECRET_YIELD_RESULT_VALUE_NAME = " secretYieldValue".toCharArray();
    private LocalVariableBinding secretYieldResultValue = null;
    public BlockScope scope;

    public YieldStatement(Expression exp, int sourceStart, int sourceEnd) {
        super(null, sourceStart, sourceEnd);
        this.expression = exp;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FlowContext targetContext = this.isImplicit ? flowContext.getTargetContextForDefaultBreak() : flowContext.getTargetContextForDefaultYield();
        flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
        this.expression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        if (flowInfo.reachMode() == 0 && currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            this.checkAgainstNullAnnotation(currentScope, flowContext, flowInfo, this.expression);
        }
        targetContext.recordAbruptExit();
        targetContext.expireNullCheckedFieldInfo();
        this.initStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        this.targetLabel = targetContext.breakLabel();
        FlowContext traversedContext = flowContext;
        int subCount = 0;
        this.subroutines = new SubRoutineStatement[5];
        do {
            SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null) {
                if (subCount == this.subroutines.length) {
                    this.subroutines = new SubRoutineStatement[subCount * 2];
                    System.arraycopy(this.subroutines, 0, this.subroutines, 0, subCount);
                }
                this.subroutines[subCount++] = sub;
                if (sub.isSubRoutineEscaping()) break;
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            traversedContext.recordBreakTo(targetContext);
            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                ASTNode node = traversedContext.associatedNode;
                if (!(node instanceof TryStatement)) continue;
                flowInfo.addInitializationsFrom(((TryStatement)node).subRoutineInits);
                continue;
            }
            if (traversedContext != targetContext) continue;
            targetContext.recordBreakFrom(flowInfo);
            break;
        } while ((traversedContext = traversedContext.getLocalParent()) != null);
        if (subCount != this.subroutines.length) {
            this.subroutines = new SubRoutineStatement[subCount];
            System.arraycopy(this.subroutines, 0, this.subroutines, 0, subCount);
        }
        return FlowInfo.DEAD_END;
    }

    @Override
    protected void setSubroutineSwitchExpression(SubRoutineStatement sub) {
        sub.setSwitchExpression(this.switchExpression);
    }

    protected void addSecretYieldResultValue(BlockScope scope1) {
        SwitchExpression se = this.switchExpression;
        if (se == null || !se.containsTry) {
            return;
        }
        LocalVariableBinding local = new LocalVariableBinding(SECRET_YIELD_RESULT_VALUE_NAME, se.resolvedType, 0, false);
        local.setConstant(Constant.NotAConstant);
        local.useFlag = 1;
        local.declaration = new LocalDeclaration(SECRET_YIELD_RESULT_VALUE_NAME, 0, 0);
        assert (se.yieldResolvedPosition >= 0);
        local.resolvedPosition = se.yieldResolvedPosition;
        assert (local.resolvedPosition < this.scope.maxOffset);
        this.scope.addLocalVariable(local);
        this.secretYieldResultValue = local;
    }

    @Override
    protected void restartExceptionLabels(CodeStream codeStream) {
        SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
    }

    protected void generateExpressionResultCodeExpanded(BlockScope currentScope, CodeStream codeStream) {
        SwitchExpression se = this.switchExpression;
        this.addSecretYieldResultValue(this.scope);
        assert (this.secretYieldResultValue != null);
        codeStream.record(this.secretYieldResultValue);
        SingleNameReference lhs = new SingleNameReference(this.secretYieldResultValue.name, 0L);
        lhs.binding = this.secretYieldResultValue;
        lhs.bits &= 0xFFFFFFF8;
        lhs.bits |= 2;
        lhs.bits |= 0x10;
        ((LocalVariableBinding)lhs.binding).markReferenced();
        Assignment assignment = new Assignment(lhs, this.expression, 0);
        assignment.generateCode(this.scope, codeStream);
        int pc = codeStream.position;
        if (this.subroutines != null) {
            int i = 0;
            int max = this.subroutines.length;
            while (i < max) {
                SubRoutineStatement sub = this.subroutines[i];
                sub.exitAnyExceptionHandler();
                sub.exitDeclaredExceptionHandlers(codeStream);
                SwitchExpression se1 = sub.getSwitchExpression();
                this.setSubroutineSwitchExpression(sub);
                boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, this.targetLabel, this.initStateIndex, null);
                sub.setSwitchExpression(se1);
                if (didEscape) {
                    codeStream.removeVariable(this.secretYieldResultValue);
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
                    if (this.initStateIndex != -1) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                    }
                    this.restartExceptionLabels(codeStream);
                    return;
                }
                ++i;
            }
        }
        se.loadStoredTypesAndKeep(codeStream);
        codeStream.load(this.secretYieldResultValue);
        codeStream.removeVariable(this.secretYieldResultValue);
        codeStream.goto_(this.targetLabel);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
        if (this.initStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        if (this.switchExpression != null && this.switchExpression.containsTry && this.switchExpression.resolvedType != null) {
            this.generateExpressionResultCodeExpanded(currentScope, codeStream);
            return;
        }
        this.expression.generateCode(this.scope, codeStream, this.switchExpression != null);
        int pc = codeStream.position;
        if (this.subroutines != null) {
            int i = 0;
            int max = this.subroutines.length;
            while (i < max) {
                SubRoutineStatement sub = this.subroutines[i];
                SwitchExpression se = sub.getSwitchExpression();
                this.setSubroutineSwitchExpression(sub);
                boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, this.targetLabel, this.initStateIndex, null);
                sub.setSwitchExpression(se);
                if (didEscape) {
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
                    if (this.initStateIndex != -1) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                    }
                    this.restartExceptionLabels(codeStream);
                    return;
                }
                ++i;
            }
        }
        codeStream.goto_(this.targetLabel);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
        if (this.initStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
        }
    }

    private boolean isInsideTry() {
        return this.switchExpression != null && this.switchExpression.containsTry;
    }

    @Override
    public void resolve(BlockScope skope) {
        this.scope = this.isInsideTry() ? new BlockScope(skope) : skope;
        super.resolve(this.scope);
        if (this.expression == null) {
            return;
        }
        if (this.switchExpression != null || this.isImplicit) {
            if (this.switchExpression == null && this.isImplicit && !this.expression.statementExpression() && this.scope.compilerOptions().sourceLevel >= 0x3A0000L) {
                this.scope.problemReporter().invalidExpressionAsStatement(this.expression);
                return;
            }
        } else if (this.scope.compilerOptions().sourceLevel >= 0x3A0000L) {
            this.scope.problemReporter().switchExpressionsYieldOutsideSwitchExpression(this);
        }
        this.expression.resolveType(this.scope);
    }

    @Override
    public TypeBinding resolveExpressionType(BlockScope scope1) {
        return this.expression != null ? this.expression.resolveType(scope1) : null;
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        if (!this.isImplicit) {
            YieldStatement.printIndent(tab, output).append("yield");
        }
        if (this.expression != null) {
            output.append(' ');
            this.expression.printExpression(tab, output);
        }
        return output.append(';');
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockscope) {
        if (visitor.visit(this, blockscope) && this.expression != null) {
            this.expression.traverse(visitor, blockscope);
        }
        visitor.endVisit(this, blockscope);
    }

    @Override
    public boolean doesNotCompleteNormally() {
        return true;
    }

    @Override
    public boolean canCompleteNormally() {
        return false;
    }
}

