/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.FailSuppressedMessageLogger;
import com.mchange.util.MessageLogger;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FSMessageLoggerAdapter
implements FailSuppressedMessageLogger {
    MessageLogger inner;
    List failures = null;

    public FSMessageLoggerAdapter(MessageLogger messageLogger) {
        this.inner = messageLogger;
    }

    @Override
    public void log(String string) {
        try {
            this.inner.log(string);
        }
        catch (IOException iOException) {
            this.addFailure(iOException);
        }
    }

    @Override
    public void log(Throwable throwable, String string) {
        try {
            this.inner.log(throwable, string);
        }
        catch (IOException iOException) {
            this.addFailure(iOException);
        }
    }

    @Override
    public synchronized Iterator getFailures() {
        if (this.inner instanceof FailSuppressedMessageLogger) {
            return ((FailSuppressedMessageLogger)this.inner).getFailures();
        }
        return this.failures != null ? this.failures.iterator() : null;
    }

    @Override
    public synchronized void clearFailures() {
        if (this.inner instanceof FailSuppressedMessageLogger) {
            ((FailSuppressedMessageLogger)this.inner).clearFailures();
        } else {
            this.failures = null;
        }
    }

    private synchronized void addFailure(IOException iOException) {
        if (this.failures == null) {
            this.failures = new LinkedList();
        }
        this.failures.add(iOException);
    }
}

