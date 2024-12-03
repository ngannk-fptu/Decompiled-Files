/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredPackageVisibilityStatement;

public class RecoveredExportsStatement
extends RecoveredPackageVisibilityStatement {
    public RecoveredExportsStatement(ExportsStatement exportsStatement, RecoveredElement parent, int bracketBalance) {
        super(exportsStatement, parent, bracketBalance);
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered exports stmt: " + super.toString();
    }
}

