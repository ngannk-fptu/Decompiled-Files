/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.ws.rs.ext.ContextResolver
 *  javax.ws.rs.ext.Provider
 *  javax.xml.bind.JAXBContext
 */
package com.atlassian.crowd.plugin.rest.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

@Provider
public class JAXBContextResolver
implements ContextResolver<JAXBContext> {
    private static final Cache<Class, JAXBContext> CACHE = CacheBuilder.newBuilder().weakKeys().softValues().build();

    public JAXBContext getContext(Class<?> clz) {
        try {
            return (JAXBContext)CACHE.get(clz, () -> JAXBContext.newInstance((Class[])new Class[]{clz}));
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}

