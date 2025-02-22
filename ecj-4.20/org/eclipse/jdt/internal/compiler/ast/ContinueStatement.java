/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.BranchStatement;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ContinueStatement
extends BranchStatement {
    public ContinueStatement(char[] label, int sourceStart, int sourceEnd) {
        super(label, sourceStart, sourceEnd);
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FlowContext targetContext;
        FlowContext flowContext2 = targetContext = this.label == null ? flowContext.getTargetContextForDefaultContinue() : flowContext.getTargetContextForContinueLabel(this.label);
        if (targetContext == null) {
            if (this.label == null) {
                currentScope.problemReporter().invalidContinue(this);
            } else {
                currentScope.problemReporter().undefinedLabel(this);
            }
            return flowInfo;
        }
        targetContext.recordAbruptExit();
        targetContext.expireNullCheckedFieldInfo();
        if (targetContext == FlowContext.NotContinuableContext) {
            currentScope.problemReporter().invalidContinue(this);
            return flowInfo;
        }
        this.initStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        this.targetLabel = targetContext.continueLabel();
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
            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                ASTNode node = traversedContext.associatedNode;
                if (!(node instanceof TryStatement)) continue;
                TryStatement tryStatement = (TryStatement)node;
                flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
                continue;
            }
            if (traversedContext != targetContext) continue;
            targetContext.recordContinueFrom(flowContext, flowInfo);
            break;
        } while ((traversedContext = traversedContext.getLocalParent()) != null);
        if (subCount != this.subroutines.length) {
            this.subroutines = new SubRoutineStatement[subCount];
            System.arraycopy(this.subroutines, 0, this.subroutines, 0, subCount);
        }
        return FlowInfo.DEAD_END;
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        ContinueStatement.printIndent(tab, output).append("continue ");
        if (this.label != null) {
            output.append(this.label);
        }
        return output.append(';');
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }

    @Override
    public boolean doesNotCompleteNormally() {
        return true;
    }

    @Override
    public boolean completesByContinue() {
        return true;
    }

    @Override
    public boolean canCompleteNormally() {
        return false;
    }

    @Override
    public boolean continueCompletes() {
        return true;
    }
}

