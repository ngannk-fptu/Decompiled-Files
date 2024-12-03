/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class DefaultProblem
extends CategorizedProblem {
    private char[] fileName;
    private int id;
    private int startPosition;
    private int endPosition;
    private int line;
    public int column;
    public int severity;
    private String[] arguments;
    private String message;
    private static final String MARKER_TYPE_PROBLEM = "org.eclipse.jdt.core.problem";
    private static final String MARKER_TYPE_TASK = "org.eclipse.jdt.core.task";
    public static final Object[] EMPTY_VALUES = new Object[0];

    public DefaultProblem(char[] originatingFileName, String message, int id, String[] stringArguments, int severity, int startPosition, int endPosition, int line, int column) {
        this.fileName = originatingFileName;
        this.message = message;
        this.id = id;
        this.arguments = stringArguments;
        this.severity = severity;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.line = line;
        this.column = column;
    }

    public void reportError() {
    }

    public String errorReportSource(char[] unitSource) {
        char c;
        if (this.startPosition > this.endPosition || this.startPosition < 0 && this.endPosition < 0 || unitSource.length == 0) {
            return Messages.problem_noSourceInformation;
        }
        StringBuffer errorBuffer = new StringBuffer();
        errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(this.line)));
        errorBuffer.append(Util.LINE_SEPARATOR);
        errorBuffer.append('\t');
        int length = unitSource.length;
        int begin = this.startPosition >= length ? length - 1 : this.startPosition;
        while (begin > 0) {
            c = unitSource[begin - 1];
            if (c == '\n' || c == '\r') break;
            --begin;
        }
        int end = this.endPosition >= length ? length - 1 : this.endPosition;
        while (end + 1 < length) {
            c = unitSource[end + 1];
            if (c == '\r' || c == '\n') break;
            ++end;
        }
        while ((c = unitSource[begin]) == ' ' || c == '\t') {
            ++begin;
        }
        errorBuffer.append(unitSource, begin, end - begin + 1);
        errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
        int i = begin;
        while (i < this.startPosition) {
            errorBuffer.append(unitSource[i] == '\t' ? (char)'\t' : ' ');
            ++i;
        }
        i = this.startPosition;
        while (i <= (this.endPosition >= length ? length - 1 : this.endPosition)) {
            errorBuffer.append('^');
            ++i;
        }
        return errorBuffer.toString();
    }

    @Override
    public String[] getArguments() {
        return this.arguments;
    }

    @Override
    public int getCategoryID() {
        return ProblemReporter.getProblemCategory(this.severity, this.id);
    }

    @Override
    public int getID() {
        return this.id;
    }

    public String getInternalCategoryMessage() {
        switch (this.getCategoryID()) {
            case 0: {
                return "unspecified";
            }
            case 10: {
                return "buildpath";
            }
            case 20: {
                return "syntax";
            }
            case 30: {
                return "import";
            }
            case 40: {
                return "type";
            }
            case 50: {
                return "member";
            }
            case 60: {
                return "internal";
            }
            case 70: {
                return "javadoc";
            }
            case 80: {
                return "code style";
            }
            case 90: {
                return "potential programming problem";
            }
            case 100: {
                return "name shadowing conflict";
            }
            case 110: {
                return "deprecation";
            }
            case 120: {
                return "unnecessary code";
            }
            case 130: {
                return "unchecked/raw";
            }
            case 140: {
                return "nls";
            }
            case 150: {
                return "restriction";
            }
            case 160: {
                return "module";
            }
            case 180: {
                return "preview related";
            }
        }
        return null;
    }

    @Override
    public String getMarkerType() {
        return this.id == 536871362 ? MARKER_TYPE_TASK : MARKER_TYPE_PROBLEM;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public char[] getOriginatingFileName() {
        return this.fileName;
    }

    @Override
    public int getSourceEnd() {
        return this.endPosition;
    }

    public int getSourceColumnNumber() {
        return this.column;
    }

    @Override
    public int getSourceLineNumber() {
        return this.line;
    }

    @Override
    public int getSourceStart() {
        return this.startPosition;
    }

    @Override
    public boolean isError() {
        return (this.severity & 1) != 0;
    }

    @Override
    public boolean isWarning() {
        return (this.severity & 1) == 0 && (this.severity & 0x400) == 0;
    }

    @Override
    public boolean isInfo() {
        return (this.severity & 0x400) != 0;
    }

    public void setOriginatingFileName(char[] fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setSourceEnd(int sourceEnd) {
        this.endPosition = sourceEnd;
    }

    @Override
    public void setSourceLineNumber(int lineNumber) {
        this.line = lineNumber;
    }

    @Override
    public void setSourceStart(int sourceStart) {
        this.startPosition = sourceStart;
    }

    public String toString() {
        String s = "Pb(" + (this.id & 0x1FFFFF) + ") ";
        if (this.message != null) {
            s = String.valueOf(s) + this.message;
        } else if (this.arguments != null) {
            int i = 0;
            while (i < this.arguments.length) {
                s = String.valueOf(s) + " " + this.arguments[i];
                ++i;
            }
        }
        return s;
    }
}

