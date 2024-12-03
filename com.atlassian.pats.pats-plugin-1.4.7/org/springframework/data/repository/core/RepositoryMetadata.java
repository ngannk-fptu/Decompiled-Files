/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core;

import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.util.TypeInformation;

public interface RepositoryMetadata {
    public Class<?> getIdType();

    public Class<?> getDomainType();

    public Class<?> getRepositoryInterface();

    public TypeInformation<?> getReturnType(Method var1);

    public Class<?> getReturnedDomainClass(Method var1);

    public CrudMethods getCrudMethods();

    public boolean isPagingRepository();

    public Set<Class<?>> getAlternativeDomainTypes();

    public boolean isReactiveRepository();
}

