/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.tool;

import java.io.File;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileObject;

public class EclipseDiagnostic
implements Diagnostic<EclipseFileObject> {
    private Diagnostic.Kind kind;
    private final int problemId;
    private final String[] problemArguments;
    private final char[] originatingFileName;
    private final int lineNumber;
    private final int columnNumber;
    private final int startPosition;
    private final int endPosition;
    private final DefaultProblemFactory problemFactory;

    private EclipseDiagnostic(Diagnostic.Kind kind, int problemId, String[] problemArguments, char[] originatingFileName, DefaultProblemFactory problemFactory, int lineNumber, int columnNumber, int startPosition, int endPosition) {
        this.kind = kind;
        this.problemId = problemId;
        this.problemArguments = problemArguments;
        this.originatingFileName = originatingFileName;
        this.problemFactory = problemFactory;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    private EclipseDiagnostic(Diagnostic.Kind kind, int problemId, String[] problemArguments, char[] originatingFileName, DefaultProblemFactory problemFactory) {
        this(kind, problemId, problemArguments, originatingFileName, problemFactory, -1, -1, -1, -1);
    }

    public static EclipseDiagnostic newInstance(CategorizedProblem problem, DefaultProblemFactory factory) {
        if (problem instanceof DefaultProblem) {
            return EclipseDiagnostic.newInstanceFromDefaultProblem((DefaultProblem)problem, factory);
        }
        return new EclipseDiagnostic(EclipseDiagnostic.getKind(problem), problem.getID(), problem.getArguments(), problem.getOriginatingFileName(), factory);
    }

    private static EclipseDiagnostic newInstanceFromDefaultProblem(DefaultProblem problem, DefaultProblemFactory factory) {
        return new EclipseDiagnostic(EclipseDiagnostic.getKind(problem), problem.getID(), problem.getArguments(), problem.getOriginatingFileName(), factory, problem.getSourceLineNumber(), problem.getSourceColumnNumber(), problem.getSourceStart(), problem.getSourceEnd());
    }

    private static Diagnostic.Kind getKind(CategorizedProblem problem) {
        Diagnostic.Kind kind = Diagnostic.Kind.OTHER;
        if (problem.isError()) {
            kind = Diagnostic.Kind.ERROR;
        } else if (problem.isWarning()) {
            kind = Diagnostic.Kind.WARNING;
        } else if (problem instanceof DefaultProblem && ((DefaultProblem)problem).isInfo()) {
            kind = Diagnostic.Kind.NOTE;
        }
        return kind;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return this.kind;
    }

    @Override
    public EclipseFileObject getSource() {
        File f = new File(new String(this.originatingFileName));
        if (f.exists()) {
            return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
        }
        return null;
    }

    @Override
    public long getPosition() {
        return this.startPosition;
    }

    @Override
    public long getStartPosition() {
        return this.startPosition;
    }

    @Override
    public long getEndPosition() {
        return this.endPosition;
    }

    @Override
    public long getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public long getColumnNumber() {
        return this.columnNumber;
    }

    @Override
    public String getCode() {
        return Integer.toString(this.problemId);
    }

    @Override
    public String getMessage(Locale locale) {
        if (locale != null) {
            this.problemFactory.setLocale(locale);
        }
        return this.problemFactory.getLocalizedMessage(this.problemId, this.problemArguments);
    }
}

