/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.config;

public class IdpNotFoundException
extends RuntimeException {
    private final Long id;

    public IdpNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}

