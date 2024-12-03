/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class AbstractLogger {
    protected final LogWriter writer;

    public AbstractLogger(LogWriter writer) {
        this.writer = writer;
    }

    protected Object execute(Callable thunk, String methodName, Object[] args) throws RepositoryException {
        this.writer.enter(methodName, args);
        Object result = null;
        try {
            result = thunk.call();
            this.writer.leave(methodName, args, result);
            return result;
        }
        catch (RepositoryException e) {
            this.writer.error(methodName, args, e);
            throw e;
        }
        catch (RuntimeException e) {
            this.writer.error(methodName, args, e);
            throw e;
        }
    }

    protected Object execute(SafeCallable thunk, String methodName, Object[] args) {
        this.writer.enter(methodName, args);
        try {
            Object result = thunk.call();
            this.writer.leave(methodName, args, result);
            return result;
        }
        catch (RuntimeException e) {
            this.writer.error(methodName, args, e);
            throw e;
        }
    }

    protected static interface SafeCallable {
        public Object call();
    }

    protected static interface Callable {
        public Object call() throws RepositoryException;
    }
}

