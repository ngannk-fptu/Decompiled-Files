/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.KotlinDetector
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.KotlinDetector;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AnnotationRepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultCrudMethods;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryMetadata
implements RepositoryMetadata {
    private final TypeInformation<?> typeInformation;
    private final Class<?> repositoryInterface;
    private final Lazy<CrudMethods> crudMethods;

    public AbstractRepositoryMetadata(Class<?> repositoryInterface) {
        Assert.notNull(repositoryInterface, (String)"Given type must not be null!");
        Assert.isTrue((boolean)repositoryInterface.isInterface(), (String)"Given type must be an interface!");
        this.repositoryInterface = repositoryInterface;
        this.typeInformation = ClassTypeInformation.from(repositoryInterface);
        this.crudMethods = Lazy.of(() -> new DefaultCrudMethods(this));
    }

    public static RepositoryMetadata getMetadata(Class<?> repositoryInterface) {
        Assert.notNull(repositoryInterface, (String)"Repository interface must not be null!");
        return Repository.class.isAssignableFrom(repositoryInterface) ? new DefaultRepositoryMetadata(repositoryInterface) : new AnnotationRepositoryMetadata(repositoryInterface);
    }

    @Override
    public TypeInformation<?> getReturnType(Method method) {
        TypeInformation<?> returnType = null;
        if (KotlinDetector.isKotlinType(method.getDeclaringClass()) && KotlinReflectionUtils.isSuspend(method)) {
            List<TypeInformation<?>> types = this.typeInformation.getParameterTypes(method);
            returnType = types.get(types.size() - 1).getComponentType();
        }
        if (returnType == null) {
            returnType = this.typeInformation.getReturnType(method);
        }
        return returnType;
    }

    @Override
    public Class<?> getReturnedDomainClass(Method method) {
        TypeInformation<?> returnType = this.getReturnType(method);
        return QueryExecutionConverters.unwrapWrapperTypes(ReactiveWrapperConverters.unwrapWrapperTypes(returnType)).getType();
    }

    @Override
    public Class<?> getRepositoryInterface() {
        return this.repositoryInterface;
    }

    @Override
    public CrudMethods getCrudMethods() {
        return this.crudMethods.get();
    }

    @Override
    public boolean isPagingRepository() {
        return this.getCrudMethods().getFindAllMethod().map(it -> Arrays.asList(it.getParameterTypes()).contains(Pageable.class)).orElse(false);
    }

    @Override
    public Set<Class<?>> getAlternativeDomainTypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isReactiveRepository() {
        return ReactiveWrappers.usesReactiveType(this.repositoryInterface);
    }
}

