/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class UsesStatement
extends ModuleStatement {
    public TypeReference serviceInterface;

    public UsesStatement(TypeReference serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        UsesStatement.printIndent(indent, output);
        output.append("uses ");
        this.serviceInterface.print(0, output);
        output.append(";");
        return output;
    }
}

