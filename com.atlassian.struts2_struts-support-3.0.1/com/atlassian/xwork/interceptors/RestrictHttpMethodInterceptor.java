/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestrictHttpMethodInterceptor
implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(RestrictHttpMethodInterceptor.class);
    public static final String INVALID_METHOD_RESULT = "invalidmethod";
    public static final String PERMITTED_METHODS_PARAM_NAME = "permittedMethods";

    public final String intercept(ActionInvocation invocation) throws Exception {
        Class<?> actionClass = invocation.getAction().getClass();
        String methodName = invocation.getProxy().getMethod();
        Method invocationMethod = actionClass.getMethod(methodName, new Class[0]);
        HttpMethod[] permittedMethods = this.getPermittedHttpMethods(invocation, invocationMethod);
        String httpMethod = this.getHttpMethod();
        if (log.isDebugEnabled()) {
            log.debug("Checking HTTP method: " + this.getHttpMethod() + " permitted against " + this.fullMethodName(invocationMethod));
        }
        if (this.getSecurityLevel().isPermitted(invocationMethod.getName(), permittedMethods, httpMethod)) {
            log.debug("Invocation proceeding");
            return invocation.invoke();
        }
        log.info("Refusing HTTP method: " + httpMethod + " against " + this.fullMethodName(invocationMethod) + " (configured allowed methods: " + Arrays.toString((Object[])permittedMethods) + ")");
        HttpServletResponse response = ServletActionContext.getResponse();
        if (response != null) {
            response.setHeader("Allow", Arrays.stream(permittedMethods).map(Enum::toString).collect(Collectors.joining(",")));
        }
        return INVALID_METHOD_RESULT;
    }

    protected HttpMethod[] getPermittedHttpMethods(ActionInvocation invocation, Method invocationMethod) {
        String configParam = (String)invocation.getProxy().getConfig().getParams().get(PERMITTED_METHODS_PARAM_NAME);
        PermittedMethods annotation = invocationMethod.getAnnotation(PermittedMethods.class);
        return RestrictHttpMethodInterceptor.toPermittedMethodArray(configParam, annotation);
    }

    private static HttpMethod[] toPermittedMethodArray(String configParam, PermittedMethods annotation) {
        if (configParam != null && configParam.trim().length() > 0) {
            String[] methodNames = configParam.trim().split("\\s*,\\s*");
            ArrayList<HttpMethod> permittedMethods = new ArrayList<HttpMethod>(methodNames.length);
            for (String methodName : methodNames) {
                try {
                    permittedMethods.add(HttpMethod.valueOf(methodName));
                }
                catch (IllegalArgumentException e) {
                    log.error("XWork configuration error: " + methodName + " is not a recognised HTTP method (method names are case sensitive).");
                }
            }
            return permittedMethods.toArray(new HttpMethod[0]);
        }
        if (annotation != null) {
            return annotation.value();
        }
        return new HttpMethod[0];
    }

    private String fullMethodName(Method invocationMethod) {
        return invocationMethod.getDeclaringClass().getName() + "#" + invocationMethod.getName();
    }

    private String getHttpMethod() {
        HttpServletRequest servletRequest = ServletActionContext.getRequest();
        return servletRequest == null ? "" : servletRequest.getMethod();
    }

    public final void destroy() {
    }

    public final void init() {
    }

    protected SecurityLevel getSecurityLevel() {
        return SecurityLevel.DEFAULT;
    }

    public static enum SecurityLevel {
        NONE{

            @Override
            public boolean isPermitted(String invocationMethodName, HttpMethod[] permittedMethods, String httpMethod) {
                return true;
            }
        }
        ,
        OPT_IN{

            @Override
            public boolean isPermitted(String invocationMethodName, HttpMethod[] permittedMethods, String httpMethod) {
                if (permittedMethods.length == 0) {
                    return true;
                }
                return HttpMethod.anyMatch(httpMethod, permittedMethods);
            }
        }
        ,
        DEFAULT{

            @Override
            public boolean isPermitted(String invocationMethodName, HttpMethod[] permittedMethods, String httpMethod) {
                if (permittedMethods.length == 0) {
                    if (invocationMethodName.equals("doDefault")) {
                        return HttpMethod.anyMatch(httpMethod, HttpMethod.GET, HttpMethod.POST);
                    }
                    return HttpMethod.anyMatch(httpMethod, HttpMethod.POST);
                }
                return HttpMethod.anyMatch(httpMethod, permittedMethods);
            }
        }
        ,
        STRICT{

            @Override
            public boolean isPermitted(String invocationMethodName, HttpMethod[] permittedMethods, String httpMethod) {
                return HttpMethod.anyMatch(httpMethod, permittedMethods);
            }
        };


        public abstract boolean isPermitted(String var1, HttpMethod[] var2, String var3);
    }
}

