/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core;

import java.lang.reflect.Method;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.util.Streamable;

public interface RepositoryInformation
extends RepositoryMetadata {
    public Class<?> getRepositoryBaseClass();

    public boolean hasCustomMethod();

    public boolean isCustomMethod(Method var1);

    public boolean isQueryMethod(Method var1);

    public boolean isBaseClassMethod(Method var1);

    public Streamable<Method> getQueryMethods();

    public Method getTargetClassMethod(Method var1);

    default public boolean hasQueryMethods() {
        return this.getQueryMethods().iterator().hasNext();
    }
}

