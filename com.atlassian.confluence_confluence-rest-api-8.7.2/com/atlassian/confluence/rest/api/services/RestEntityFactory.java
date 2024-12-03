/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.rest.api.services;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.rest.api.model.RestEntity;

@ExperimentalApi
public interface RestEntityFactory {
    default public <T> RestEntity<T> create(T entity) {
        return this.create(entity, false);
    }

    public <T> RestEntity<T> create(T var1, boolean var2);
}

