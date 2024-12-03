/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.batch.Main;

public class BatchCompilerRequestor
implements ICompilerRequestor {
    private Main compiler;
    private int lineDelta = 0;

    public BatchCompilerRequestor(Main compiler) {
        this.compiler = compiler;
    }

    @Override
    public void acceptResult(CompilationResult compilationResult) {
        if (compilationResult.lineSeparatorPositions != null) {
            int unitLineCount = compilationResult.lineSeparatorPositions.length;
            this.lineDelta += unitLineCount;
            if (this.compiler.showProgress && this.lineDelta > 2000) {
                this.compiler.logger.logProgress();
                this.lineDelta = 0;
            }
        }
        this.compiler.logger.startLoggingSource(compilationResult);
        if (compilationResult.hasProblems() || compilationResult.hasTasks()) {
            this.compiler.logger.logProblems(compilationResult.getAllProblems(), compilationResult.compilationUnit.getContents(), this.compiler);
            this.reportProblems(compilationResult);
        }
        this.compiler.outputClassFiles(compilationResult);
        this.compiler.logger.endLoggingSource();
    }

    protected void reportProblems(CompilationResult result) {
    }
}

