/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.parser.RecoveredBlock;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;

public class RecoveredStatement
extends RecoveredElement {
    public Statement statement;
    RecoveredBlock nestedBlock;

    public RecoveredStatement(Statement statement, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.statement = statement;
    }

    @Override
    public ASTNode parseTree() {
        return this.statement;
    }

    @Override
    public int sourceEnd() {
        return this.statement.sourceEnd;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered statement:\n" + this.statement.print(tab + 1, new StringBuffer(10));
    }

    public Statement updatedStatement(int depth, Set<TypeDeclaration> knownTypes) {
        if (this.nestedBlock != null) {
            this.nestedBlock.updatedStatement(depth, knownTypes);
        }
        return this.statement;
    }

    @Override
    public void updateParseTree() {
        this.updatedStatement(0, new HashSet<TypeDeclaration>());
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.statement.sourceEnd == 0) {
            this.statement.sourceEnd = bodyEnd;
        }
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (--this.bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }

    @Override
    public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
        if (this.statement instanceof ForeachStatement) {
            ForeachStatement foreach = (ForeachStatement)this.statement;
            this.resetPendingModifiers();
            if (foreach.sourceEnd != 0 && foreach.action != null && nestedBlockDeclaration.sourceStart > foreach.sourceEnd) {
                return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
            }
            foreach.action = nestedBlockDeclaration;
            RecoveredBlock element = new RecoveredBlock(nestedBlockDeclaration, (RecoveredElement)this, bracketBalanceValue);
            if (this.parser().statementRecoveryActivated) {
                this.addBlockStatement(element);
            }
            this.nestedBlock = element;
            if (nestedBlockDeclaration.sourceEnd == 0) {
                return element;
            }
            return this;
        }
        return super.add(nestedBlockDeclaration, bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(Statement stmt, int bracketBalanceValue) {
        if (this.statement instanceof ForeachStatement) {
            ForeachStatement foreach = (ForeachStatement)this.statement;
            if (foreach.action == null) {
                foreach.action = stmt;
                return this;
            }
        }
        return super.add(stmt, bracketBalanceValue);
    }
}

