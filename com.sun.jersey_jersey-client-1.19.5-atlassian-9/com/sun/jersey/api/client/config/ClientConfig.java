/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.util.FeaturesAndProperties
 */
package com.sun.jersey.api.client.config;

import com.sun.jersey.core.util.FeaturesAndProperties;
import java.util.Set;

public interface ClientConfig
extends FeaturesAndProperties {
    public static final String PROPERTY_FOLLOW_REDIRECTS = "com.sun.jersey.client.property.followRedirects";
    public static final String PROPERTY_READ_TIMEOUT = "com.sun.jersey.client.property.readTimeout";
    public static final String PROPERTY_CONNECT_TIMEOUT = "com.sun.jersey.client.property.connectTimeout";
    public static final String PROPERTY_CHUNKED_ENCODING_SIZE = "com.sun.jersey.client.property.chunkedEncodingSize";
    public static final String PROPERTY_BUFFER_RESPONSE_ENTITY_ON_EXCEPTION = "com.sun.jersey.client.property.bufferResponseEntityOnException";
    public static final String PROPERTY_THREADPOOL_SIZE = "com.sun.jersey.client.property.threadpoolSize";

    public Set<Class<?>> getClasses();

    public Set<Object> getSingletons();

    public boolean getPropertyAsFeature(String var1);
}

