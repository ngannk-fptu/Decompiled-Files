/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.LabelFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class LabeledStatement
extends Statement {
    public Statement statement;
    public char[] label;
    public BranchLabel targetLabel;
    public int labelEnd;
    int mergedInitStateIndex = -1;

    public LabeledStatement(char[] label, Statement statement, long labelPosition, int sourceEnd) {
        this.statement = statement;
        if (statement instanceof EmptyStatement) {
            statement.bits |= 1;
        }
        this.label = label;
        this.sourceStart = (int)(labelPosition >>> 32);
        this.labelEnd = (int)labelPosition;
        this.sourceEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.statement == null) {
            return flowInfo;
        }
        this.targetLabel = new BranchLabel();
        LabelFlowContext labelContext = new LabelFlowContext(flowContext, (ASTNode)this, this.label, this.targetLabel, currentScope);
        FlowInfo statementInfo = this.statement.analyseCode(currentScope, labelContext, flowInfo);
        boolean reinjectNullInfo = (statementInfo.tagBits & 3) != 0 && (labelContext.initsOnBreak.tagBits & 3) == 0;
        UnconditionalFlowInfo mergedInfo = statementInfo.mergedWith(labelContext.initsOnBreak);
        if (reinjectNullInfo) {
            mergedInfo.addNullInfoFrom(flowInfo.unconditionalFieldLessCopy()).addNullInfoFrom(labelContext.initsOnBreak.unconditionalFieldLessCopy());
        }
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        if ((this.bits & 0x40) == 0) {
            currentScope.problemReporter().unusedLabel(this);
        }
        return mergedInfo;
    }

    @Override
    public ASTNode concreteStatement() {
        return this.statement;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.targetLabel != null) {
            this.targetLabel.initialize(codeStream);
            if (this.statement != null) {
                this.statement.generateCode(currentScope, codeStream);
            }
            this.targetLabel.place();
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        LabeledStatement.printIndent(tab, output).append(this.label).append(": ");
        if (this.statement == null) {
            output.append(';');
        } else {
            this.statement.printStatement(0, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope scope) {
        if (this.statement != null) {
            this.statement.resolve(scope);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope) && this.statement != null) {
            this.statement.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public boolean doesNotCompleteNormally() {
        if (this.statement.breaksOut(this.label)) {
            return false;
        }
        return this.statement.doesNotCompleteNormally();
    }

    @Override
    public boolean completesByContinue() {
        return this.statement instanceof ContinueStatement;
    }

    @Override
    public boolean canCompleteNormally() {
        if (this.statement.canCompleteNormally()) {
            return true;
        }
        return this.statement.breaksOut(this.label);
    }

    @Override
    public boolean continueCompletes() {
        return this.statement instanceof ContinueStatement;
    }
}

