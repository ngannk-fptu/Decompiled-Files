/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;

public class AbortType
extends AbortCompilationUnit {
    private static final long serialVersionUID = -5882417089349134385L;

    public AbortType(CompilationResult compilationResult, CategorizedProblem problem) {
        super(compilationResult, problem);
    }
}

