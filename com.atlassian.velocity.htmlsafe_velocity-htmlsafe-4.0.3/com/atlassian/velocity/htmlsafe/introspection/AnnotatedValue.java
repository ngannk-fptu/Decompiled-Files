/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.velocity.htmlsafe.annotations.CollectionInheritable;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class AnnotatedValue<E>
implements AnnotationBoxedElement<E> {
    private final E value;
    private final ImmutableSet<Annotation> annotations;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final LoadingCache<Annotation, Boolean> annotationCache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader)new CacheLoader<Annotation, Boolean>(){

        public Boolean load(Annotation annotation) {
            return annotation.annotationType().isAnnotationPresent(CollectionInheritable.class);
        }
    });

    public AnnotatedValue(E value, Collection<Annotation> annotations) {
        Preconditions.checkArgument((!(value instanceof BoxedValue) ? 1 : 0) != 0, (Object)"Attempting to box an already boxed value");
        this.value = value;
        this.annotations = ImmutableSet.copyOf(annotations);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> aClass) {
        for (Annotation annotation : this.annotations) {
            if (!annotation.annotationType().equals(aClass)) continue;
            return true;
        }
        return false;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> tClass) {
        for (Annotation annotation : this.annotations) {
            if (!annotation.annotationType().equals(tClass)) continue;
            return (T)((Annotation)tClass.cast(annotation));
        }
        return null;
    }

    @Override
    public <T extends Annotation> boolean hasAnnotation(Class<T> tClass) {
        return this.getAnnotation(tClass) != null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return (Annotation[])this.annotations.toArray((Object[])new Annotation[this.annotations.size()]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }

    @Override
    public Collection<Annotation> getAnnotationCollection() {
        return this.annotations;
    }

    @Override
    public E unbox() {
        return this.value;
    }

    @Override
    public Object box(Object value) {
        return new AnnotatedValue<Object>(value, (Collection<Annotation>)this.annotations);
    }

    public Collection<Annotation> getCollectionInheritableAnnotations() {
        HashSet<Annotation> inheritableAnnotations = new HashSet<Annotation>();
        for (Annotation annotation : this.annotations) {
            if (!((Boolean)annotationCache.getUnchecked((Object)annotation)).booleanValue()) continue;
            inheritableAnnotations.add(annotation);
        }
        return Collections.unmodifiableCollection(inheritableAnnotations);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AnnotatedValue that = (AnnotatedValue)o;
        return (this.value != null ? this.value.equals(that.value) : that.value == null) && this.annotations.equals(that.annotations);
    }

    public int hashCode() {
        int result = this.value != null ? this.value.hashCode() : 0;
        result = 31 * result + this.annotations.hashCode();
        return result;
    }

    public final String getDescription() {
        return "Annotated value: " + this.value.toString() + "; Annotations: " + this.annotations;
    }

    public String toString() {
        return this.value.toString();
    }
}

