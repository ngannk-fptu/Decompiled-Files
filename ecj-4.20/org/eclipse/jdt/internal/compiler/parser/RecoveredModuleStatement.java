/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;

public abstract class RecoveredModuleStatement
extends RecoveredElement {
    public ModuleStatement moduleStatement;

    public RecoveredModuleStatement(ModuleStatement moduleStmt, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.moduleStatement = moduleStmt;
    }

    @Override
    public ASTNode parseTree() {
        return this.moduleStatement;
    }

    @Override
    public int sourceEnd() {
        return this.moduleStatement.declarationSourceEnd;
    }

    @Override
    public String toString(int tab) {
        return this.moduleStatement.toString();
    }

    protected ModuleStatement updatedModuleStatement() {
        return this.moduleStatement;
    }

    @Override
    public void updateParseTree() {
        this.updatedModuleStatement();
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.moduleStatement.declarationSourceEnd == 0) {
            this.moduleStatement.declarationSourceEnd = bodyEnd;
            this.moduleStatement.declarationEnd = bodyEnd;
        }
    }
}

