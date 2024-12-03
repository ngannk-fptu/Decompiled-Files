/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.core.RepositoryCreationException;

public class IncompleteRepositoryCompositionException
extends RepositoryCreationException {
    public IncompleteRepositoryCompositionException(String msg, Class<?> repositoryInterface) {
        super(msg, repositoryInterface);
    }
}

