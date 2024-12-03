/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.support;

import org.springframework.data.repository.core.EntityMetadata;

public interface ActiveObjectsEntityMetadata<T>
extends EntityMetadata<T> {
    public String getEntityName();
}

