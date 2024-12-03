/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy;

import org.codehaus.groovy.GroovyExceptionInterface;

public class GroovyException
extends Exception
implements GroovyExceptionInterface {
    private boolean fatal = true;

    public GroovyException() {
    }

    public GroovyException(String message) {
        super(message);
    }

    public GroovyException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroovyException(boolean fatal) {
        this.fatal = fatal;
    }

    public GroovyException(String message, boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    @Override
    public boolean isFatal() {
        return this.fatal;
    }

    @Override
    public void setFatal(boolean fatal) {
        this.fatal = fatal;
    }
}

