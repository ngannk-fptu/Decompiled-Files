/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityMetadata;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class QueryMethod {
    private final RepositoryMetadata metadata;
    private final Method method;
    private final Class<?> unwrappedReturnType;
    private final Parameters<?, ?> parameters;
    private final ResultProcessor resultProcessor;
    private final Lazy<Class<?>> domainClass;
    private final Lazy<Boolean> isCollectionQuery;

    public QueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Assert.notNull((Object)metadata, (String)"Repository metadata must not be null!");
        Assert.notNull((Object)factory, (String)"ProjectionFactory must not be null!");
        Parameters.TYPES.stream().filter(type -> org.springframework.data.repository.util.ClassUtils.getNumberOfOccurences(method, type) > 1).findFirst().ifPresent(type -> {
            throw new IllegalStateException(String.format("Method must only one argument of type %s! Offending method: %s", type.getSimpleName(), method.toString()));
        });
        this.method = method;
        this.unwrappedReturnType = QueryMethod.potentiallyUnwrapReturnTypeFor(metadata, method);
        this.parameters = this.createParameters(method);
        this.metadata = metadata;
        if (org.springframework.data.repository.util.ClassUtils.hasParameterOfType(method, Pageable.class)) {
            if (!this.isStreamQuery()) {
                QueryMethod.assertReturnTypeAssignable(method, QueryExecutionConverters.getAllowedPageableTypes());
            }
            if (org.springframework.data.repository.util.ClassUtils.hasParameterOfType(method, Sort.class)) {
                throw new IllegalStateException(String.format("Method must not have Pageable *and* Sort parameter. Use sorting capabilities on Pageable instead! Offending method: %s", method.toString()));
            }
        }
        Assert.notNull(this.parameters, () -> String.format("Parameters extracted from method '%s' must not be null!", method.getName()));
        if (this.isPageQuery()) {
            Assert.isTrue((boolean)this.parameters.hasPageableParameter(), (String)String.format("Paging query needs to have a Pageable parameter! Offending method %s", method.toString()));
        }
        this.domainClass = Lazy.of(() -> {
            Class<?> repositoryDomainClass = metadata.getDomainType();
            Class<?> methodDomainClass = metadata.getReturnedDomainClass(method);
            return repositoryDomainClass == null || repositoryDomainClass.isAssignableFrom(methodDomainClass) ? methodDomainClass : repositoryDomainClass;
        });
        this.resultProcessor = new ResultProcessor(this, factory);
        this.isCollectionQuery = Lazy.of(this::calculateIsCollectionQuery);
    }

    protected Parameters<?, ?> createParameters(Method method) {
        return new DefaultParameters(method);
    }

    public String getName() {
        return this.method.getName();
    }

    public EntityMetadata<?> getEntityInformation() {
        return () -> this.getDomainClass();
    }

    public String getNamedQueryName() {
        return String.format("%s.%s", this.getDomainClass().getSimpleName(), this.method.getName());
    }

    protected Class<?> getDomainClass() {
        return this.domainClass.get();
    }

    public Class<?> getReturnedObjectType() {
        return this.metadata.getReturnedDomainClass(this.method);
    }

    public boolean isCollectionQuery() {
        return this.isCollectionQuery.get();
    }

    public boolean isSliceQuery() {
        return !this.isPageQuery() && ClassUtils.isAssignable(Slice.class, this.unwrappedReturnType);
    }

    public final boolean isPageQuery() {
        return ClassUtils.isAssignable(Page.class, this.unwrappedReturnType);
    }

    public boolean isModifyingQuery() {
        return false;
    }

    public boolean isQueryForEntity() {
        return this.getDomainClass().isAssignableFrom(this.getReturnedObjectType());
    }

    public boolean isStreamQuery() {
        return Stream.class.isAssignableFrom(this.unwrappedReturnType);
    }

    public Parameters<?, ?> getParameters() {
        return this.parameters;
    }

    public ResultProcessor getResultProcessor() {
        return this.resultProcessor;
    }

    RepositoryMetadata getMetadata() {
        return this.metadata;
    }

    Method getMethod() {
        return this.method;
    }

    public String toString() {
        return this.method.toString();
    }

    private boolean calculateIsCollectionQuery() {
        if (this.isPageQuery() || this.isSliceQuery()) {
            return false;
        }
        Class<?> returnType = this.metadata.getReturnType(this.method).getType();
        if (QueryExecutionConverters.supports(returnType) && !QueryExecutionConverters.isSingleValue(returnType)) {
            return true;
        }
        if (QueryExecutionConverters.supports(this.unwrappedReturnType)) {
            return !QueryExecutionConverters.isSingleValue(this.unwrappedReturnType);
        }
        return ClassTypeInformation.from(this.unwrappedReturnType).isCollectionLike();
    }

    private static Class<? extends Object> potentiallyUnwrapReturnTypeFor(RepositoryMetadata metadata, Method method) {
        TypeInformation<?> returnType = metadata.getReturnType(method);
        if (QueryExecutionConverters.supports(returnType.getType()) || ReactiveWrapperConverters.supports(returnType.getType())) {
            TypeInformation<?> componentType = returnType.getComponentType();
            if (componentType == null) {
                throw new IllegalStateException(String.format("Couldn't find component type for return value of method %s!", method));
            }
            return componentType.getType();
        }
        return returnType.getType();
    }

    private static void assertReturnTypeAssignable(Method method, Set<Class<?>> types) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Assert.notEmpty(types, (String)"Types must not be null or empty!");
        TypeInformation returnType = ClassTypeInformation.fromReturnTypeOf(method);
        returnType = QueryExecutionConverters.isSingleValue(returnType.getType()) ? returnType.getRequiredComponentType() : returnType;
        for (Class clazz : types) {
            if (!clazz.isAssignableFrom(returnType.getType())) continue;
            return;
        }
        throw new IllegalStateException("Method has to have one of the following return types! " + types.toString());
    }
}

