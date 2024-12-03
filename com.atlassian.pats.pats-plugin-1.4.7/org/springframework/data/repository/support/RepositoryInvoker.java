/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.repository.support;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvocationInformation;
import org.springframework.util.MultiValueMap;

public interface RepositoryInvoker
extends RepositoryInvocationInformation {
    public <T> T invokeSave(T var1);

    public <T> Optional<T> invokeFindById(Object var1);

    public Iterable<Object> invokeFindAll(Pageable var1);

    public Iterable<Object> invokeFindAll(Sort var1);

    public void invokeDeleteById(Object var1);

    public Optional<Object> invokeQueryMethod(Method var1, MultiValueMap<String, ? extends Object> var2, Pageable var3, Sort var4);
}

