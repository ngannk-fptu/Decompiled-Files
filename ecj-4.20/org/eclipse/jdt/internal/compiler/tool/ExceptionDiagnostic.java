/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.tool;

import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

final class ExceptionDiagnostic
implements Diagnostic<JavaFileObject> {
    private final Exception exception;

    ExceptionDiagnostic(Exception e) {
        this.exception = e;
    }

    @Override
    public String getCode() {
        return "exception";
    }

    @Override
    public long getColumnNumber() {
        return 0L;
    }

    @Override
    public long getEndPosition() {
        return 0L;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return Diagnostic.Kind.ERROR;
    }

    @Override
    public long getLineNumber() {
        return 0L;
    }

    @Override
    public String getMessage(Locale arg0) {
        return this.exception.toString();
    }

    @Override
    public long getPosition() {
        return 0L;
    }

    @Override
    public JavaFileObject getSource() {
        return null;
    }

    @Override
    public long getStartPosition() {
        return 0L;
    }
}

