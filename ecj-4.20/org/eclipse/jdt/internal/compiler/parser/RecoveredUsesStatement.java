/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleStatement;

public class RecoveredUsesStatement
extends RecoveredModuleStatement {
    public RecoveredUsesStatement(UsesStatement usesStatement, RecoveredElement parent, int bracketBalance) {
        super(usesStatement, parent, bracketBalance);
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered Uses: " + super.toString();
    }

    public UsesStatement updatedUsesStatement() {
        return (UsesStatement)this.moduleStatement;
    }

    @Override
    public void updateParseTree() {
        this.updatedUsesStatement();
    }
}

