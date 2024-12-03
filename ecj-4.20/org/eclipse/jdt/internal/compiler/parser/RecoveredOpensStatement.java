/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredPackageVisibilityStatement;

public class RecoveredOpensStatement
extends RecoveredPackageVisibilityStatement {
    public RecoveredOpensStatement(OpensStatement opensStatement, RecoveredElement parent, int bracketBalance) {
        super(opensStatement, parent, bracketBalance);
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered opens stmt: " + super.toString();
    }
}

