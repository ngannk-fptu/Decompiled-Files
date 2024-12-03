/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;

public class ImportReference
extends ASTNode {
    public char[][] tokens;
    public long[] sourcePositions;
    public int declarationEnd;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int modifiers;
    public Annotation[] annotations;
    public int trailingStarPosition;

    public ImportReference(char[][] tokens, long[] sourcePositions, boolean onDemand, int modifiers) {
        this.tokens = tokens;
        this.sourcePositions = sourcePositions;
        if (onDemand) {
            this.bits |= 0x20000;
        }
        this.sourceEnd = (int)(sourcePositions[sourcePositions.length - 1] & 0xFFFFFFFFFFFFFFFFL);
        this.sourceStart = (int)(sourcePositions[0] >>> 32);
        this.modifiers = modifiers;
    }

    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public char[][] getImportName() {
        return this.tokens;
    }

    public char[] getSimpleName() {
        return this.tokens[this.tokens.length - 1];
    }

    public void checkPackageConflict(CompilationUnitScope scope) {
        ModuleBinding module = scope.module();
        PackageBinding visiblePackage = module.getVisiblePackage(this.tokens);
        if (visiblePackage instanceof SplitPackageBinding) {
            HashSet<ModuleBinding> declaringMods = new HashSet<ModuleBinding>();
            for (PackageBinding packageBinding : ((SplitPackageBinding)visiblePackage).incarnations) {
                if (packageBinding.enclosingModule == module || !module.canAccess(packageBinding)) continue;
                declaringMods.add(packageBinding.enclosingModule);
            }
            if (!declaringMods.isEmpty()) {
                CompilerOptions compilerOptions = scope.compilerOptions();
                boolean inJdtDebugCompileMode = compilerOptions.enableJdtDebugCompileMode;
                if (!inJdtDebugCompileMode) {
                    scope.problemReporter().conflictingPackagesFromOtherModules(this, declaringMods);
                }
            }
        }
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        return this.print(indent, output, true);
    }

    public StringBuffer print(int tab, StringBuffer output, boolean withOnDemand) {
        int i = 0;
        while (i < this.tokens.length) {
            if (i > 0) {
                output.append('.');
            }
            output.append(this.tokens[i]);
            ++i;
        }
        if (withOnDemand && (this.bits & 0x20000) != 0) {
            output.append(".*");
        }
        return output;
    }

    public void traverse(ASTVisitor visitor, CompilationUnitScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}

