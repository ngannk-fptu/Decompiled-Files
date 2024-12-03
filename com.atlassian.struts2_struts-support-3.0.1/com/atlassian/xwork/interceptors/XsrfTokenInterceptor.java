/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  com.opensymphony.xwork2.interceptor.ValidationAware
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.RequireSecurityToken;
import com.atlassian.xwork.SimpleXsrfTokenGenerator;
import com.atlassian.xwork.XsrfTokenGenerator;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class XsrfTokenInterceptor
implements Interceptor {
    public static final String REQUEST_PARAM_NAME = "atl_token";
    public static final String CONFIG_PARAM_NAME = "RequireSecurityToken";
    public static final String VALIDATION_FAILED_ERROR_KEY = "atlassian.xwork.xsrf.badtoken";
    public static final String SECURITY_TOKEN_REQUIRED_ERROR_KEY = "atlassian.xwork.xsrf.notoken";
    public static final String OVERRIDE_HEADER_NAME = "X-Atlassian-Token";
    public static final String OVERRIDE_HEADER_VALUE = "no-check";
    private final XsrfTokenGenerator tokenGenerator;

    public XsrfTokenInterceptor() {
        this(new SimpleXsrfTokenGenerator());
    }

    public XsrfTokenInterceptor(XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Method invocationMethod = XsrfTokenInterceptor.extractMethod(invocation);
        String configParam = (String)invocation.getProxy().getConfig().getParams().get(CONFIG_PARAM_NAME);
        RequireSecurityToken legacyAnnotation = invocationMethod.getAnnotation(RequireSecurityToken.class);
        XsrfProtectionExcluded annotation = invocationMethod.getAnnotation(XsrfProtectionExcluded.class);
        boolean isProtected = this.methodRequiresProtection(configParam, annotation, legacyAnnotation);
        String token = ServletActionContext.getRequest().getParameter(REQUEST_PARAM_NAME);
        boolean validToken = this.tokenGenerator.validateToken(ServletActionContext.getRequest(), token);
        if (isProtected && !validToken) {
            Action action = (Action)invocation.getAction();
            String errorMessageKey = token == null ? SECURITY_TOKEN_REQUIRED_ERROR_KEY : VALIDATION_FAILED_ERROR_KEY;
            this.addInvalidTokenError(action, errorMessageKey);
            ServletActionContext.getResponse().setStatus(403);
            return "input";
        }
        return invocation.invoke();
    }

    private static String getHttpMethod() {
        HttpServletRequest servletRequest = ServletActionContext.getRequest();
        return servletRequest == null ? "" : servletRequest.getMethod();
    }

    private static Method extractMethod(ActionInvocation invocation) throws NoSuchMethodException {
        Class<?> actionClass = invocation.getAction().getClass();
        String methodName = invocation.getProxy().getMethod();
        return actionClass.getMethod(methodName, new Class[0]);
    }

    private boolean methodRequiresProtection(String configParam, XsrfProtectionExcluded annotation, RequireSecurityToken legacyAnnotation) {
        if (this.isOverrideHeaderPresent()) {
            return false;
        }
        if (annotation != null) {
            return false;
        }
        if (configParam != null) {
            return Boolean.parseBoolean(configParam);
        }
        if (legacyAnnotation != null) {
            return legacyAnnotation.value();
        }
        return this.getSecurityLevel().getDefaultProtection();
    }

    protected void addInvalidTokenError(Action action, String errorMessageKey) {
        if (action instanceof ValidationAware) {
            ((ValidationAware)action).addActionError(this.internationaliseErrorMessage(action, errorMessageKey));
        }
    }

    protected String internationaliseErrorMessage(Action action, String messageKey) {
        return messageKey;
    }

    private boolean isOverrideHeaderPresent() {
        return OVERRIDE_HEADER_VALUE.equals(ServletActionContext.getRequest().getHeader(OVERRIDE_HEADER_NAME));
    }

    public void destroy() {
    }

    public void init() {
    }

    protected SecurityLevel getSecurityLevel() {
        return SecurityLevel.DEFAULT;
    }

    public static enum SecurityLevel {
        OPT_IN{

            @Override
            public boolean getDefaultProtection() {
                return false;
            }
        }
        ,
        OPT_OUT{

            @Override
            public boolean getDefaultProtection() {
                return true;
            }
        }
        ,
        DEFAULT{

            @Override
            public boolean getDefaultProtection() {
                String httpMethod = XsrfTokenInterceptor.getHttpMethod();
                return !HttpMethod.anyMatch(httpMethod, HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE);
            }
        };


        public abstract boolean getDefaultProtection();
    }
}

