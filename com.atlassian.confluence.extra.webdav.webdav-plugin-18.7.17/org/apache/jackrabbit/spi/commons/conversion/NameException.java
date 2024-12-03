/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.RepositoryException;

public class NameException
extends RepositoryException {
    public NameException(String message) {
        super(message);
    }

    public NameException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

