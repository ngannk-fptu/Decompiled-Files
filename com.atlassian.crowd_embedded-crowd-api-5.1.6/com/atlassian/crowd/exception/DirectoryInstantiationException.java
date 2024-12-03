/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.OperationFailedException;

public class DirectoryInstantiationException
extends OperationFailedException {
    public DirectoryInstantiationException() {
    }

    public DirectoryInstantiationException(String s) {
        super(s);
    }

    public DirectoryInstantiationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DirectoryInstantiationException(Throwable throwable) {
        super(throwable);
    }
}

