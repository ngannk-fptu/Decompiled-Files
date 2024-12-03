/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public interface ReferenceContext {
    public void abort(int var1, CategorizedProblem var2);

    public CompilationResult compilationResult();

    public CompilationUnitDeclaration getCompilationUnitDeclaration();

    public boolean hasErrors();

    public void tagAsHavingErrors();

    public void tagAsHavingIgnoredMandatoryErrors(int var1);
}

