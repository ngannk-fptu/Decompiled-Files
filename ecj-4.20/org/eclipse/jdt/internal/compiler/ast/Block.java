/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class Block
extends Statement {
    public Statement[] statements;
    public int explicitDeclarations;
    public BlockScope scope;

    public Block(int explicitDeclarations) {
        this.explicitDeclarations = explicitDeclarations;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        LocalVariableBinding[] locals;
        if (this.statements == null) {
            return flowInfo;
        }
        int complaintLevel = (flowInfo.reachMode() & 3) != 0 ? 1 : 0;
        CompilerOptions compilerOptions = currentScope.compilerOptions();
        boolean enableSyntacticNullAnalysisForFields = compilerOptions.enableSyntacticNullAnalysisForFields;
        int i = 0;
        int max = this.statements.length;
        while (i < max) {
            Statement stat = this.statements[i];
            if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                flowInfo = stat.analyseCode(this.scope, flowContext, flowInfo);
            }
            flowContext.mergeFinallyNullInfo(flowInfo);
            if (enableSyntacticNullAnalysisForFields) {
                flowContext.expireNullCheckedFieldInfo();
            }
            if (compilerOptions.analyseResourceLeaks) {
                FakedTrackingVariable.cleanUpUnassigned(this.scope, stat, flowInfo);
            }
            ++i;
        }
        if (this.scope != currentScope) {
            this.scope.checkUnclosedCloseables(flowInfo, flowContext, null, null);
        }
        if (this.explicitDeclarations > 0 && (locals = this.scope.locals) != null) {
            int numLocals = this.scope.localIndex;
            int i2 = 0;
            while (i2 < numLocals) {
                flowInfo.resetAssignmentInfo(locals[i2]);
                ++i2;
            }
        }
        return flowInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.statements != null) {
            Statement[] statementArray = this.statements;
            int n = this.statements.length;
            int n2 = 0;
            while (n2 < n) {
                Statement stmt = statementArray[n2];
                stmt.generateCode(this.scope, codeStream);
                ++n2;
            }
        }
        if (this.scope != currentScope) {
            codeStream.exitUserScope(this.scope);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public boolean isEmptyBlock() {
        return this.statements == null;
    }

    public StringBuffer printBody(int indent, StringBuffer output) {
        if (this.statements == null) {
            return output;
        }
        int i = 0;
        while (i < this.statements.length) {
            this.statements[i].printStatement(indent + 1, output);
            output.append('\n');
            ++i;
        }
        return output;
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        Block.printIndent(indent, output);
        output.append("{\n");
        this.printBody(indent, output);
        return Block.printIndent(indent, output).append('}');
    }

    @Override
    public void resolve(BlockScope upperScope) {
        if ((this.bits & 8) != 0) {
            upperScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
        }
        if (this.statements != null) {
            this.scope = this.explicitDeclarations == 0 ? upperScope : new BlockScope(upperScope, this.explicitDeclarations);
            int i = 0;
            int length = this.statements.length;
            while (i < length) {
                Statement stmt = this.statements[i];
                stmt.resolve(this.scope);
                ++i;
            }
        }
    }

    public void resolveUsing(BlockScope givenScope) {
        if ((this.bits & 8) != 0) {
            givenScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
        }
        this.scope = givenScope;
        if (this.statements != null) {
            int i = 0;
            int length = this.statements.length;
            while (i < length) {
                this.statements[i].resolve(this.scope);
                ++i;
            }
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope) && this.statements != null) {
            int i = 0;
            int length = this.statements.length;
            while (i < length) {
                this.statements[i].traverse(visitor, this.scope);
                ++i;
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void branchChainTo(BranchLabel label) {
        if (this.statements != null) {
            this.statements[this.statements.length - 1].branchChainTo(label);
        }
    }

    @Override
    public boolean doesNotCompleteNormally() {
        int length;
        int n = length = this.statements == null ? 0 : this.statements.length;
        return length > 0 && this.statements[length - 1].doesNotCompleteNormally();
    }

    @Override
    public boolean completesByContinue() {
        int length;
        int n = length = this.statements == null ? 0 : this.statements.length;
        return length > 0 && this.statements[length - 1].completesByContinue();
    }

    @Override
    public boolean canCompleteNormally() {
        int length;
        int n = length = this.statements == null ? 0 : this.statements.length;
        return length == 0 || this.statements[length - 1].canCompleteNormally();
    }

    @Override
    public boolean continueCompletes() {
        int length;
        int n = length = this.statements == null ? 0 : this.statements.length;
        return length > 0 && this.statements[length - 1].continueCompletes();
    }
}

