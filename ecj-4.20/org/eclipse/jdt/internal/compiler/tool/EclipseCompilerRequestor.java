/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.tool;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.batch.BatchCompilerRequestor;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseDiagnostic;

public class EclipseCompilerRequestor
extends BatchCompilerRequestor {
    private final DiagnosticListener<? super JavaFileObject> diagnosticListener;
    private final DefaultProblemFactory problemFactory;

    public EclipseCompilerRequestor(Main compiler, DiagnosticListener<? super JavaFileObject> diagnosticListener, DefaultProblemFactory problemFactory) {
        super(compiler);
        this.diagnosticListener = diagnosticListener;
        this.problemFactory = problemFactory;
    }

    @Override
    protected void reportProblems(CompilationResult result) {
        if (this.diagnosticListener != null) {
            CategorizedProblem[] categorizedProblemArray = result.getAllProblems();
            int n = categorizedProblemArray.length;
            int n2 = 0;
            while (n2 < n) {
                CategorizedProblem problem = categorizedProblemArray[n2];
                EclipseDiagnostic diagnostic = EclipseDiagnostic.newInstance(problem, this.problemFactory);
                this.diagnosticListener.report(diagnostic);
                ++n2;
            }
        }
    }
}

