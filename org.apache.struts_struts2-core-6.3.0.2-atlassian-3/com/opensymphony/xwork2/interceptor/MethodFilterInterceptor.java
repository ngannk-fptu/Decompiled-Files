/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collections;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MethodFilterInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(MethodFilterInterceptor.class);
    protected Set<String> excludeMethods = Collections.emptySet();
    protected Set<String> includeMethods = Collections.emptySet();

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = TextParseUtil.commaDelimitedStringToSet(excludeMethods);
    }

    public Set<String> getExcludeMethodsSet() {
        return this.excludeMethods;
    }

    public void setIncludeMethods(String includeMethods) {
        this.includeMethods = TextParseUtil.commaDelimitedStringToSet(includeMethods);
    }

    public Set<String> getIncludeMethodsSet() {
        return this.includeMethods;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (this.applyInterceptor(invocation)) {
            return this.doIntercept(invocation);
        }
        return invocation.invoke();
    }

    protected boolean applyInterceptor(ActionInvocation invocation) {
        String method = invocation.getProxy().getMethod();
        boolean applyMethod = MethodFilterInterceptorUtil.applyMethod(this.excludeMethods, this.includeMethods, method);
        if (!applyMethod) {
            LOG.debug("Skipping Interceptor... Method [{}] found in exclude list.", (Object)method);
        }
        return applyMethod;
    }

    protected abstract String doIntercept(ActionInvocation var1) throws Exception;
}

