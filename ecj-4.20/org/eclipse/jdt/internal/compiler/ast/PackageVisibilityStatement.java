/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;

public abstract class PackageVisibilityStatement
extends ModuleStatement {
    public ImportReference pkgRef;
    public ModuleReference[] targets;
    public char[] pkgName;
    public PlainPackageBinding resolvedPackage;

    public PackageVisibilityStatement(ImportReference pkgRef, ModuleReference[] targets) {
        this.pkgRef = pkgRef;
        this.pkgName = CharOperation.concatWith(this.pkgRef.tokens, '.');
        this.targets = targets;
    }

    public boolean isQualified() {
        return this.targets != null && this.targets.length > 0;
    }

    public ModuleReference[] getTargetedModules() {
        return this.targets;
    }

    public boolean resolve(Scope scope) {
        boolean errorsExist;
        boolean bl = errorsExist = this.resolvePackageReference(scope) == null;
        if (this.isQualified()) {
            HashtableOfObject modules = new HashtableOfObject(this.targets.length);
            int i = 0;
            while (i < this.targets.length) {
                ModuleReference ref = this.targets[i];
                if (modules.containsKey(ref.moduleName)) {
                    scope.problemReporter().duplicateModuleReference(8389922, ref);
                    errorsExist = true;
                } else {
                    modules.put(ref.moduleName, ref);
                }
                ++i;
            }
        }
        return !errorsExist;
    }

    public int computeSeverity(int problemId) {
        return 1;
    }

    protected PlainPackageBinding resolvePackageReference(Scope scope) {
        if (this.resolvedPackage != null) {
            return this.resolvedPackage;
        }
        ModuleDeclaration exportingModule = scope.compilationUnitScope().referenceContext.moduleDeclaration;
        SourceModuleBinding src = exportingModule.binding;
        this.resolvedPackage = src != null ? src.getOrCreateDeclaredPackage(this.pkgRef.tokens) : null;
        return this.resolvedPackage;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        this.pkgRef.print(indent, output);
        if (this.isQualified()) {
            output.append(" to ");
            int i = 0;
            while (i < this.targets.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.targets[i].print(0, output);
                ++i;
            }
        }
        return output;
    }
}

