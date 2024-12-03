/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public interface CacheMethodDetails<A extends Annotation> {
    public Method getMethod();

    public Set<Annotation> getAnnotations();

    public A getCacheAnnotation();

    public String getCacheName();
}

