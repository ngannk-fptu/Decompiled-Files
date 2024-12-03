/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.httpmethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.httpmethod.AllowedHttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpDelete;
import org.apache.struts2.interceptor.httpmethod.HttpGet;
import org.apache.struts2.interceptor.httpmethod.HttpGetOrPost;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpMethodAware;
import org.apache.struts2.interceptor.httpmethod.HttpPost;
import org.apache.struts2.interceptor.httpmethod.HttpPut;

public class HttpMethodInterceptor
extends AbstractInterceptor {
    private static final Class<? extends Annotation>[] HTTP_METHOD_ANNOTATIONS = new Class[]{AllowedHttpMethod.class, HttpGet.class, HttpPost.class, HttpGetOrPost.class, HttpPut.class, HttpDelete.class};
    private static final Logger LOG = LogManager.getLogger(HttpMethodInterceptor.class);
    private String badRequestResultName = "bad-request";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        if (action instanceof HttpMethodAware) {
            LOG.debug("Action: {} implements: {}, setting request method: {}", action, (Object)HttpMethodAware.class.getSimpleName(), (Object)request.getMethod());
            ((HttpMethodAware)action).setMethod(HttpMethod.parse(request.getMethod()));
        }
        if (invocation.getProxy().isMethodSpecified()) {
            Method method = action.getClass().getMethod(invocation.getProxy().getMethod(), new Class[0]);
            if (AnnotationUtils.isAnnotatedBy(method, HTTP_METHOD_ANNOTATIONS)) {
                LOG.debug("Action's method: {} annotated with: {}, checking if request: {} meets allowed methods!", (Object)invocation.getProxy().getMethod(), (Object)AllowedHttpMethod.class.getSimpleName(), (Object)request.getMethod());
                return this.doIntercept(invocation, method);
            }
        } else if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
            LOG.debug("Action: {} annotated with: {}, checking if request: {} meets allowed methods!", action, (Object)AllowedHttpMethod.class.getSimpleName(), (Object)request.getMethod());
            return this.doIntercept(invocation, action.getClass());
        }
        return invocation.invoke();
    }

    protected String doIntercept(ActionInvocation invocation, AnnotatedElement element) throws Exception {
        HttpServletRequest request;
        HttpMethod requestedMethod;
        List<HttpMethod> allowedMethods = this.readAllowedMethods(element);
        if (allowedMethods.contains((Object)(requestedMethod = HttpMethod.parse((request = invocation.getInvocationContext().getServletRequest()).getMethod())))) {
            LOG.trace("Request method: {} matches allowed methods: {}, continuing invocation!", (Object)requestedMethod, allowedMethods);
            return invocation.invoke();
        }
        LOG.trace("Request method: {} doesn't match allowed methods: {}, continuing invocation!", (Object)requestedMethod, allowedMethods);
        return this.getBadRequestResultName(invocation);
    }

    protected List<HttpMethod> readAllowedMethods(AnnotatedElement element) {
        ArrayList<HttpMethod> allowedMethods = new ArrayList<HttpMethod>();
        if (AnnotationUtils.isAnnotatedBy(element, AllowedHttpMethod.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(AllowedHttpMethod.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGet.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGet.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPost.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPut.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPut.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpDelete.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpDelete.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGetOrPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGetOrPost.class).value()));
        }
        return Collections.unmodifiableList(allowedMethods);
    }

    protected String getBadRequestResultName(ActionInvocation invocation) {
        String actionResultName;
        Object action = invocation.getAction();
        String resultName = this.badRequestResultName;
        if (action instanceof HttpMethodAware && (actionResultName = ((HttpMethodAware)action).getBadRequestResultName()) != null) {
            resultName = actionResultName;
        }
        LOG.trace("Bad request result name is: {}", (Object)resultName);
        return resultName;
    }

    public void setBadRequestResultName(String badRequestResultName) {
        this.badRequestResultName = badRequestResultName;
    }
}

