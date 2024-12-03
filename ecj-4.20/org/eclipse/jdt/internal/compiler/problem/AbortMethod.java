/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortType;

public class AbortMethod
extends AbortType {
    private static final long serialVersionUID = -1480267398969840003L;

    public AbortMethod(CompilationResult compilationResult, CategorizedProblem problem) {
        super(compilationResult, problem);
    }
}

