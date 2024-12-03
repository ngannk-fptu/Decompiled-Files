/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class ModuleReference
extends ASTNode {
    public char[][] tokens;
    public long[] sourcePositions;
    public char[] moduleName;
    public ModuleBinding binding = null;

    public ModuleReference(char[][] tokens, long[] sourcePositions) {
        this.tokens = tokens;
        this.sourcePositions = sourcePositions;
        this.sourceEnd = (int)(sourcePositions[sourcePositions.length - 1] & 0xFFFFFFFFFFFFFFFFL);
        this.sourceStart = (int)(sourcePositions[0] >>> 32);
        this.moduleName = CharOperation.concatWith(tokens, '.');
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        int i = 0;
        while (i < this.tokens.length) {
            if (i > 0) {
                output.append('.');
            }
            output.append(this.tokens[i]);
            ++i;
        }
        return output;
    }

    public ModuleBinding resolve(Scope scope) {
        if (scope == null || this.binding != null) {
            return this.binding;
        }
        this.binding = scope.environment().getModule(this.moduleName);
        return this.binding;
    }
}

