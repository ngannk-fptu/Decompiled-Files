/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;

public class ExportsStatement
extends PackageVisibilityStatement {
    public ExportsStatement(ImportReference pkgRef) {
        this(pkgRef, null);
    }

    public ExportsStatement(ImportReference pkgRef, ModuleReference[] targets) {
        super(pkgRef, targets);
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        ExportsStatement.printIndent(indent, output);
        output.append("exports ");
        super.print(0, output);
        output.append(";");
        return output;
    }
}

