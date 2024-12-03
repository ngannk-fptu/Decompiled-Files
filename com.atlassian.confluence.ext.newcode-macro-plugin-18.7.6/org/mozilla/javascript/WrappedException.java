/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;

public class WrappedException
extends EvaluatorException {
    private static final long serialVersionUID = -1551979216966520648L;
    private Throwable exception;

    public WrappedException(Throwable exception) {
        super("Wrapped " + exception);
        this.exception = exception;
        this.initCause(exception);
        int[] linep = new int[]{0};
        String sourceName = Context.getSourcePositionFromStack(linep);
        int lineNumber = linep[0];
        if (sourceName != null) {
            this.initSourceName(sourceName);
        }
        if (lineNumber != 0) {
            this.initLineNumber(lineNumber);
        }
    }

    public Throwable getWrappedException() {
        return this.exception;
    }

    @Deprecated
    public Object unwrap() {
        return this.getWrappedException();
    }
}

