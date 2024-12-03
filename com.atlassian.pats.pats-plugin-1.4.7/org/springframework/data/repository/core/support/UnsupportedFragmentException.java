/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.core.RepositoryCreationException;

public class UnsupportedFragmentException
extends RepositoryCreationException {
    private final Class<?> fragmentInterface;

    public UnsupportedFragmentException(String msg, Class<?> repositoryInterface, Class<?> fragmentInterface) {
        super(msg, repositoryInterface);
        this.fragmentInterface = fragmentInterface;
    }

    public Class<?> getFragmentInterface() {
        return this.fragmentInterface;
    }
}

