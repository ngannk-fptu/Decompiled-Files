/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class RequiresStatement
extends ModuleStatement {
    public ModuleReference module;
    public ModuleBinding resolvedBinding;
    public int modifiers = 0;
    public int modifiersSourceStart;

    public RequiresStatement(ModuleReference module) {
        this.module = module;
    }

    public boolean isTransitive() {
        return (this.modifiers & 0x20) != 0;
    }

    public boolean isStatic() {
        return (this.modifiers & 0x40) != 0;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        output.append("requires ");
        if (this.isTransitive()) {
            output.append("transitive ");
        }
        if (this.isStatic()) {
            output.append("static ");
        }
        this.module.print(indent, output);
        output.append(";");
        return output;
    }

    public ModuleBinding resolve(Scope scope) {
        if (this.resolvedBinding != null) {
            return this.resolvedBinding;
        }
        this.resolvedBinding = this.module.resolve(scope);
        if (scope != null) {
            if (this.resolvedBinding == null) {
                scope.problemReporter().invalidModule(this.module);
            } else if (this.resolvedBinding.hasUnstableAutoName()) {
                scope.problemReporter().autoModuleWithUnstableName(this.module);
            }
        }
        return this.resolvedBinding;
    }
}

