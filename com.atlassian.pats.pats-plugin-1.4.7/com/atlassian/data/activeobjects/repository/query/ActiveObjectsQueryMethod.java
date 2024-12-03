/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.Query;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParameters;
import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.DefaultActiveObjectsEntityMetadata;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityMetadata;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ActiveObjectsQueryMethod<T>
extends QueryMethod {
    private static final Set<Class<?>> NATIVE_ARRAY_TYPES;
    private final Method method;
    private final Lazy<Boolean> isCollectionQuery;
    private final Lazy<ActiveObjectsEntityMetadata<T>> entityMetadata;

    protected ActiveObjectsQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
        this.isCollectionQuery = Lazy.of(() -> super.isCollectionQuery() && !NATIVE_ARRAY_TYPES.contains(method.getReturnType()));
        this.entityMetadata = Lazy.of(() -> new DefaultActiveObjectsEntityMetadata<T>(this.getDomainClass()));
        Assert.isTrue((!this.isModifyingQuery() || !this.getParameters().hasSpecialParameter() ? 1 : 0) != 0, (String)String.format("Modifying method must not contain %s!", Parameters.TYPES));
        this.assertParameterNamesInAnnotatedQuery();
    }

    private void assertParameterNamesInAnnotatedQuery() {
        String annotatedQuery = this.getAnnotatedQuery();
        if (!DeclaredQuery.of(annotatedQuery).hasNamedParameter()) {
            return;
        }
        for (Parameter parameter : this.getParameters()) {
            if (!parameter.isNamedParameter() || !StringUtils.isEmpty((Object)annotatedQuery) && (annotatedQuery.contains(String.format(":%s", parameter.getName().orElse("NOTFOUND"))) || annotatedQuery.contains(String.format("#%s", parameter.getName().orElse("NOTFOUND"))))) continue;
            throw new IllegalStateException(String.format("Using named parameters for method %s but parameter '%s' not found in annotated query '%s'!", this.method, parameter.getName(), annotatedQuery));
        }
    }

    public ActiveObjectsEntityMetadata<T> getEntityInformation() {
        return this.entityMetadata.get();
    }

    protected Class<T> getDomainClass() {
        return super.getDomainClass();
    }

    Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    @Nullable
    public String getAnnotatedQuery() {
        String query = this.getAnnotationValue("value", String.class);
        return StringUtils.hasText((String)query) ? query : null;
    }

    String getRequiredAnnotatedQuery() {
        String query = this.getAnnotatedQuery();
        if (query != null) {
            return query;
        }
        throw new IllegalStateException(String.format("No annotated query found for query method %s!", this.getName()));
    }

    @Nullable
    String getCountQuery() {
        String countQuery = this.getAnnotationValue("countQuery", String.class);
        return StringUtils.hasText((String)countQuery) ? countQuery : null;
    }

    @Nullable
    String getCountQueryProjection() {
        String countProjection = this.getAnnotationValue("countProjection", String.class);
        return StringUtils.hasText((String)countProjection) ? countProjection : null;
    }

    boolean isNativeQuery() {
        return false;
    }

    private <T> T getAnnotationValue(String attribute, Class<T> type) {
        return this.getMergedOrDefaultAnnotationValue(attribute, Query.class, type);
    }

    private <T> T getMergedOrDefaultAnnotationValue(String attribute, Class annotationType, Class<T> targetType) {
        Annotation annotation = AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)this.method, (Class)annotationType);
        if (annotation == null) {
            return targetType.cast(AnnotationUtils.getDefaultValue((Class)annotationType, (String)attribute));
        }
        return targetType.cast(AnnotationUtils.getValue((Annotation)annotation, (String)attribute));
    }

    protected ActiveObjectsParameters createParameters(Method method) {
        return new ActiveObjectsParameters(method);
    }

    public ActiveObjectsParameters getParameters() {
        return (ActiveObjectsParameters)super.getParameters();
    }

    @Override
    public boolean isCollectionQuery() {
        return this.isCollectionQuery.get();
    }

    static {
        HashSet<Class> types = new HashSet<Class>();
        types.add(byte[].class);
        types.add(Byte[].class);
        types.add(char[].class);
        types.add(Character[].class);
        NATIVE_ARRAY_TYPES = Collections.unmodifiableSet(types);
    }
}

