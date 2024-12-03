/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;

public interface CacheInvocationContext<A extends Annotation>
extends CacheMethodDetails<A> {
    public Object getTarget();

    public CacheInvocationParameter[] getAllParameters();

    public <T> T unwrap(Class<T> var1);
}

