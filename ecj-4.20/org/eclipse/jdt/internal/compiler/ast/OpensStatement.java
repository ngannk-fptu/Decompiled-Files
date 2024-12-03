/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;

public class OpensStatement
extends PackageVisibilityStatement {
    public OpensStatement(ImportReference pkgRef) {
        this(pkgRef, null);
    }

    public OpensStatement(ImportReference pkgRef, ModuleReference[] targets) {
        super(pkgRef, targets);
    }

    @Override
    public int computeSeverity(int problemId) {
        switch (problemId) {
            case 8389919: {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        OpensStatement.printIndent(indent, output);
        output.append("opens ");
        super.print(0, output);
        output.append(";");
        return output;
    }
}

