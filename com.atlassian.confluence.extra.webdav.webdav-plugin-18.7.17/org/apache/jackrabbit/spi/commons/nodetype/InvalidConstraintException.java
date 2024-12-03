/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.RepositoryException;

public class InvalidConstraintException
extends RepositoryException {
    public InvalidConstraintException(String message) {
        super(message);
    }

    public InvalidConstraintException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

