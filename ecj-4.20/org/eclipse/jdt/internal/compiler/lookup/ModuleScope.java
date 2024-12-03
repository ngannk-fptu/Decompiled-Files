/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class ModuleScope
extends BlockScope {
    public ModuleDeclaration referenceContext;

    public ModuleScope(Scope parent, ModuleDeclaration referenceContext) {
        super(5, parent);
        this.referenceContext = referenceContext;
    }

    @Override
    public ProblemReporter problemReporter() {
        ProblemReporter problemReporter = this.referenceCompilationUnit().problemReporter;
        problemReporter.referenceContext = this.referenceContext;
        return problemReporter;
    }
}

