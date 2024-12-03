/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import javax.annotation.Nonnull;

public interface RestRepresentation<T>
extends ReadOnlyRestRepresentation<T> {
    @Nonnull
    public T asDomain() throws IllegalRestRepresentationStateException;
}

