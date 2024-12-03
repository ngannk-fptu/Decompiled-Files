/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.RepositoryException;

public class NodeTypeConflictException
extends RepositoryException {
    public NodeTypeConflictException(String message) {
        super(message);
    }

    public NodeTypeConflictException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

