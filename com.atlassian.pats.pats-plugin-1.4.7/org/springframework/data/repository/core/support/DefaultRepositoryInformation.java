/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

class DefaultRepositoryInformation
implements RepositoryInformation {
    private final Map<Method, Method> methodCache = new ConcurrentHashMap<Method, Method>();
    private final RepositoryMetadata metadata;
    private final Class<?> repositoryBaseClass;
    private final RepositoryComposition composition;
    private final RepositoryComposition baseComposition;

    public DefaultRepositoryInformation(RepositoryMetadata metadata, Class<?> repositoryBaseClass, RepositoryComposition composition) {
        Assert.notNull((Object)metadata, (String)"Repository metadata must not be null!");
        Assert.notNull(repositoryBaseClass, (String)"Repository base class must not be null!");
        Assert.notNull((Object)composition, (String)"Repository composition must not be null!");
        this.metadata = metadata;
        this.repositoryBaseClass = repositoryBaseClass;
        this.composition = composition;
        this.baseComposition = RepositoryComposition.of(RepositoryFragment.structural(repositoryBaseClass)).withArgumentConverter(composition.getArgumentConverter()).withMethodLookup(composition.getMethodLookup());
    }

    @Override
    public Class<?> getDomainType() {
        return this.metadata.getDomainType();
    }

    @Override
    public Class<?> getIdType() {
        return this.metadata.getIdType();
    }

    @Override
    public Class<?> getRepositoryBaseClass() {
        return this.repositoryBaseClass;
    }

    @Override
    public Method getTargetClassMethod(Method method) {
        if (this.methodCache.containsKey(method)) {
            return this.methodCache.get(method);
        }
        Method result = this.composition.findMethod(method).orElse(method);
        if (!result.equals(method)) {
            return this.cacheAndReturn(method, result);
        }
        return this.cacheAndReturn(method, this.baseComposition.findMethod(method).orElse(method));
    }

    private Method cacheAndReturn(Method key, Method value) {
        if (value != null) {
            ReflectionUtils.makeAccessible((Method)value);
        }
        this.methodCache.put(key, value);
        return value;
    }

    @Override
    public Streamable<Method> getQueryMethods() {
        HashSet<Method> result = new HashSet<Method>();
        for (Method method : this.getRepositoryInterface().getMethods()) {
            if (!this.isQueryMethodCandidate(method = ClassUtils.getMostSpecificMethod((Method)method, this.getRepositoryInterface()))) continue;
            result.add(method);
        }
        return Streamable.of(Collections.unmodifiableSet(result));
    }

    private boolean isQueryMethodCandidate(Method method) {
        return !method.isBridge() && !method.isDefault() && !Modifier.isStatic(method.getModifiers()) && (this.isQueryAnnotationPresentOn(method) || !this.isCustomMethod(method) && !this.isBaseClassMethod(method));
    }

    private boolean isQueryAnnotationPresentOn(Method method) {
        return AnnotationUtils.findAnnotation((Method)method, QueryAnnotation.class) != null;
    }

    @Override
    public boolean isCustomMethod(Method method) {
        return this.composition.getMethod(method) != null;
    }

    @Override
    public boolean isQueryMethod(Method method) {
        return this.getQueryMethods().stream().anyMatch(it -> it.equals(method));
    }

    @Override
    public boolean isBaseClassMethod(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        return this.baseComposition.getMethod(method) != null;
    }

    @Override
    public boolean hasCustomMethod() {
        Class<?> repositoryInterface = this.getRepositoryInterface();
        if (org.springframework.data.repository.util.ClassUtils.isGenericRepositoryInterface(repositoryInterface)) {
            return false;
        }
        for (Method method : repositoryInterface.getMethods()) {
            if (!this.isCustomMethod(method) || this.isBaseClassMethod(method)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getRepositoryInterface() {
        return this.metadata.getRepositoryInterface();
    }

    @Override
    public Class<?> getReturnedDomainClass(Method method) {
        return this.metadata.getReturnedDomainClass(method);
    }

    @Override
    public TypeInformation<?> getReturnType(Method method) {
        return this.metadata.getReturnType(method);
    }

    @Override
    public CrudMethods getCrudMethods() {
        return this.metadata.getCrudMethods();
    }

    @Override
    public boolean isPagingRepository() {
        return this.metadata.isPagingRepository();
    }

    @Override
    public Set<Class<?>> getAlternativeDomainTypes() {
        return this.metadata.getAlternativeDomainTypes();
    }

    @Override
    public boolean isReactiveRepository() {
        return this.metadata.isReactiveRepository();
    }
}

