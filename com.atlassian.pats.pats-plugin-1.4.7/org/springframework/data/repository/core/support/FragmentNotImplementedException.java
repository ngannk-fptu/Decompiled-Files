/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.core.RepositoryCreationException;
import org.springframework.data.repository.core.support.RepositoryFragment;

public class FragmentNotImplementedException
extends RepositoryCreationException {
    private final RepositoryFragment<?> fragment;

    public FragmentNotImplementedException(String msg, Class<?> repositoryInterface, RepositoryFragment<?> fragment) {
        super(msg, repositoryInterface);
        this.fragment = fragment;
    }

    public RepositoryFragment<?> getFragment() {
        return this.fragment;
    }
}

