/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface CacheInvocationParameter {
    public Class<?> getRawType();

    public Object getValue();

    public Set<Annotation> getAnnotations();

    public int getParameterPosition();
}

