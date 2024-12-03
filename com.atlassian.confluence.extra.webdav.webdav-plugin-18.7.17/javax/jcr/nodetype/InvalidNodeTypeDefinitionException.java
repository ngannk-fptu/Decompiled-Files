/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RepositoryException;

public class InvalidNodeTypeDefinitionException
extends RepositoryException {
    public InvalidNodeTypeDefinitionException() {
    }

    public InvalidNodeTypeDefinitionException(String message) {
        super(message);
    }

    public InvalidNodeTypeDefinitionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public InvalidNodeTypeDefinitionException(Throwable rootCause) {
        super(rootCause);
    }
}

