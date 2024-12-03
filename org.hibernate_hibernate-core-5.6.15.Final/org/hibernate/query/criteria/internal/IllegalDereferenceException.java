/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal;

import org.hibernate.query.criteria.internal.PathSource;

public class IllegalDereferenceException
extends RuntimeException {
    private final PathSource pathSource;

    public IllegalDereferenceException(PathSource pathSource) {
        super("Illegal attempt to dereference path source [" + pathSource.getPathIdentifier() + "]");
        this.pathSource = pathSource;
    }

    public PathSource getPathSource() {
        return this.pathSource;
    }
}

