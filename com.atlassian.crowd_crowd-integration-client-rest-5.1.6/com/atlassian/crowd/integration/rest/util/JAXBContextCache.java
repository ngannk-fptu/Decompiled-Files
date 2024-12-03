/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.xml.bind.DataBindingException
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 */
package com.atlassian.crowd.integration.rest.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextCache {
    private final LoadingCache<Class<?>, JAXBContext> cache = CacheBuilder.newBuilder().softValues().build(new CacheLoader<Class<?>, JAXBContext>(){

        public JAXBContext load(Class<?> clazz) throws JAXBException {
            return JAXBContext.newInstance((Class[])new Class[]{clazz});
        }
    });

    public JAXBContext getJAXBContext(Class<?> clazz) {
        try {
            return (JAXBContext)this.cache.get(clazz);
        }
        catch (ExecutionException e) {
            throw new DataBindingException("Cannot instantiate JAXBContext for class " + clazz, (Throwable)e);
        }
    }
}

