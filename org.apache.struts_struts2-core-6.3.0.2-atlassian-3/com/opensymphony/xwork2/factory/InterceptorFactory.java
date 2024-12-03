/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Map;

public interface InterceptorFactory {
    public Interceptor buildInterceptor(InterceptorConfig var1, Map<String, String> var2) throws ConfigurationException;
}

