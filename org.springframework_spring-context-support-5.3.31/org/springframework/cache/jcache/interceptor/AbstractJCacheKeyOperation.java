/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheMethodDetails
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.cache.interceptor.KeyGenerator
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractJCacheOperation;

abstract class AbstractJCacheKeyOperation<A extends Annotation>
extends AbstractJCacheOperation<A> {
    private final KeyGenerator keyGenerator;
    private final List<AbstractJCacheOperation.CacheParameterDetail> keyParameterDetails;

    protected AbstractJCacheKeyOperation(CacheMethodDetails<A> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator) {
        super(methodDetails, cacheResolver);
        this.keyGenerator = keyGenerator;
        this.keyParameterDetails = AbstractJCacheKeyOperation.initializeKeyParameterDetails(this.allParameterDetails);
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    public CacheInvocationParameter[] getKeyParameters(Object ... values) {
        ArrayList<CacheInvocationParameter> result = new ArrayList<CacheInvocationParameter>();
        for (AbstractJCacheOperation.CacheParameterDetail keyParameterDetail : this.keyParameterDetails) {
            int parameterPosition = keyParameterDetail.getParameterPosition();
            if (parameterPosition >= values.length) {
                throw new IllegalStateException("Values mismatch, key parameter at position " + parameterPosition + " cannot be matched against " + values.length + " value(s)");
            }
            result.add(keyParameterDetail.toCacheInvocationParameter(values[parameterPosition]));
        }
        return result.toArray(new CacheInvocationParameter[0]);
    }

    private static List<AbstractJCacheOperation.CacheParameterDetail> initializeKeyParameterDetails(List<AbstractJCacheOperation.CacheParameterDetail> allParameters) {
        ArrayList<AbstractJCacheOperation.CacheParameterDetail> all = new ArrayList<AbstractJCacheOperation.CacheParameterDetail>();
        ArrayList<AbstractJCacheOperation.CacheParameterDetail> annotated = new ArrayList<AbstractJCacheOperation.CacheParameterDetail>();
        for (AbstractJCacheOperation.CacheParameterDetail allParameter : allParameters) {
            if (!allParameter.isValue()) {
                all.add(allParameter);
            }
            if (!allParameter.isKey()) continue;
            annotated.add(allParameter);
        }
        return annotated.isEmpty() ? all : annotated;
    }
}

