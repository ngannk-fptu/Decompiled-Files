/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.interceptors.RestrictHttpMethodInterceptor
 *  com.atlassian.xwork.interceptors.RestrictHttpMethodInterceptor$SecurityLevel
 *  com.opensymphony.xwork2.ActionInvocation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.xwork.http.HttpMethodRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.interceptors.RestrictHttpMethodInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpMethodValidationInterceptor
extends RestrictHttpMethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HttpMethodValidationInterceptor.class);
    private static RestrictHttpMethodInterceptor.SecurityLevel configuredSecurityLevel = HttpMethodValidationInterceptor.getConfiguredSecurityLevel();

    protected HttpMethod[] getPermittedHttpMethods(ActionInvocation invocation, Method invocationMethod) {
        HttpMethod[] permittedHttpMethods = super.getPermittedHttpMethods(invocation, invocationMethod);
        if (permittedHttpMethods != null && permittedHttpMethods.length != 0) {
            return permittedHttpMethods;
        }
        if (invocationMethod.isAnnotationPresent(HttpMethodRequired.class)) {
            return invocationMethod.getAnnotation(HttpMethodRequired.class).value();
        }
        return new HttpMethod[0];
    }

    protected RestrictHttpMethodInterceptor.SecurityLevel getSecurityLevel() {
        return configuredSecurityLevel;
    }

    private static RestrictHttpMethodInterceptor.SecurityLevel getConfiguredSecurityLevel() {
        String securityLevelProp = System.getProperty("confluence.xwork.httpmethods.securitylevel");
        if (securityLevelProp != null) {
            try {
                return RestrictHttpMethodInterceptor.SecurityLevel.valueOf((String)securityLevelProp);
            }
            catch (IllegalArgumentException e) {
                log.error("Invalid security level. Using DEFAULT", (Throwable)e);
            }
        }
        return RestrictHttpMethodInterceptor.SecurityLevel.OPT_IN;
    }
}

