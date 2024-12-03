/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.rest.api.model.RestObject;

@ExperimentalApi
public class RestEntity<T>
extends RestObject {
    private final T delegate;

    public RestEntity(T delegate) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return this.delegate;
    }

    public String toString() {
        return "RestEntity{delegate=" + this.delegate + ", jsonProperties=" + this.jsonProperties + '}';
    }
}

