/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.exception;

public class ConfluenceEventPropagatingException
extends RuntimeException {
    private static final long serialVersionUID = -3629438415538257224L;

    public ConfluenceEventPropagatingException() {
    }

    public ConfluenceEventPropagatingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConfluenceEventPropagatingException(String s) {
        super(s);
    }

    public ConfluenceEventPropagatingException(Throwable throwable) {
        super(throwable);
    }
}

