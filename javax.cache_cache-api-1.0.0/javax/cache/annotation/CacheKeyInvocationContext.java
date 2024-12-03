/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;

public interface CacheKeyInvocationContext<A extends Annotation>
extends CacheInvocationContext<A> {
    public CacheInvocationParameter[] getKeyParameters();

    public CacheInvocationParameter getValueParameter();
}

