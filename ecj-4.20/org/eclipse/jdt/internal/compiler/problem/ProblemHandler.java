/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ProblemHandler {
    public static final String[] NoArgument = CharOperation.NO_STRINGS;
    public IErrorHandlingPolicy policy;
    public final IProblemFactory problemFactory;
    public final CompilerOptions options;
    private IErrorHandlingPolicy rootPolicy;
    protected boolean suppressTagging = false;

    public ProblemHandler(IErrorHandlingPolicy policy, CompilerOptions options, IProblemFactory problemFactory) {
        this.policy = policy;
        this.problemFactory = problemFactory;
        this.options = options;
    }

    public int computeSeverity(int problemId) {
        return 1;
    }

    public CategorizedProblem createProblem(char[] fileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, int lineNumber, int columnNumber) {
        return this.problemFactory.createProblem(fileName, problemId, problemArguments, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
    }

    public CategorizedProblem createProblem(char[] fileName, int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, int lineNumber, int columnNumber) {
        return this.problemFactory.createProblem(fileName, problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
    }

    public void handle(int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, ReferenceContext referenceContext, CompilationResult unitResult) {
        int n;
        boolean mandatory;
        if (severity == 256) {
            return;
        }
        boolean bl = mandatory = (severity & 0x21) == 1;
        if ((severity & 0x200) == 0 && this.policy.ignoreAllErrors()) {
            if (referenceContext == null) {
                if ((severity & 1) != 0) {
                    CategorizedProblem problem = this.createProblem(null, problemId, problemArguments, elaborationId, messageArguments, severity, 0, 0, 0, 0);
                    throw new AbortCompilation(null, problem);
                }
                return;
            }
            if (mandatory) {
                referenceContext.tagAsHavingIgnoredMandatoryErrors(problemId);
            }
            return;
        }
        if ((severity & 0x20) != 0 && problemId != 536871362 && !this.options.ignoreSourceFolderWarningOption) {
            ICompilationUnit cu = unitResult.getCompilationUnit();
            try {
                if (cu != null && cu.ignoreOptionalProblems()) {
                    return;
                }
            }
            catch (AbstractMethodError abstractMethodError) {}
        }
        if (referenceContext == null) {
            if ((severity & 1) != 0) {
                CategorizedProblem problem = this.createProblem(null, problemId, problemArguments, elaborationId, messageArguments, severity, 0, 0, 0, 0);
                throw new AbortCompilation(null, problem);
            }
            return;
        }
        if (problemStartPosition >= 0) {
            int[] lineEnds = unitResult.getLineSeparatorPositions();
            n = Util.getLineNumber(problemStartPosition, lineEnds, 0, lineEnds.length - 1);
        } else {
            n = 0;
        }
        int lineNumber = n;
        int columnNumber = problemStartPosition >= 0 ? Util.searchColumnNumber(unitResult.getLineSeparatorPositions(), lineNumber, problemStartPosition) : 0;
        CategorizedProblem problem = this.createProblem(unitResult.getFileName(), problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, lineNumber, columnNumber);
        if (problem == null) {
            return;
        }
        switch (severity & 1) {
            case 1: {
                int abortLevel;
                CompilationUnitDeclaration unitDecl;
                this.record(problem, unitResult, referenceContext, mandatory);
                if ((severity & 0x80) == 0) break;
                if (!referenceContext.hasErrors() && !mandatory && this.options.suppressOptionalErrors && (unitDecl = referenceContext.getCompilationUnitDeclaration()) != null && unitDecl.isSuppressed(problem)) {
                    return;
                }
                if (!this.suppressTagging || this.options.treatOptionalErrorAsFatal) {
                    referenceContext.tagAsHavingErrors();
                }
                if ((abortLevel = this.policy.stopOnFirstError() ? 2 : severity & 0x1E) == 0) break;
                referenceContext.abort(abortLevel, problem);
                break;
            }
            case 0: {
                this.record(problem, unitResult, referenceContext, false);
            }
        }
    }

    public void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition, ReferenceContext referenceContext, CompilationResult unitResult) {
        this.handle(problemId, problemArguments, 0, messageArguments, this.computeSeverity(problemId), problemStartPosition, problemEndPosition, referenceContext, unitResult);
    }

    public void record(CategorizedProblem problem, CompilationResult unitResult, ReferenceContext referenceContext, boolean mandatoryError) {
        unitResult.record(problem, referenceContext, mandatoryError);
    }

    public IErrorHandlingPolicy switchErrorHandlingPolicy(IErrorHandlingPolicy newPolicy) {
        if (this.rootPolicy == null) {
            this.rootPolicy = this.policy;
        }
        IErrorHandlingPolicy presentPolicy = this.policy;
        this.policy = newPolicy;
        return presentPolicy;
    }

    public IErrorHandlingPolicy suspendTempErrorHandlingPolicy() {
        IErrorHandlingPolicy presentPolicy = this.policy;
        if (this.rootPolicy != null) {
            this.policy = this.rootPolicy;
        }
        return presentPolicy;
    }

    public void resumeTempErrorHandlingPolicy(IErrorHandlingPolicy previousPolicy) {
        this.policy = previousPolicy;
    }
}

