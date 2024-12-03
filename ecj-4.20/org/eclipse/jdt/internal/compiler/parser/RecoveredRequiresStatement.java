/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleStatement;

public class RecoveredRequiresStatement
extends RecoveredModuleStatement {
    public RecoveredRequiresStatement(RequiresStatement requiresStatement, RecoveredElement parent, int bracketBalance) {
        super(requiresStatement, parent, bracketBalance);
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered requires: " + super.toString();
    }

    public RequiresStatement updatedRequiresStatement() {
        return (RequiresStatement)this.moduleStatement;
    }
}

