/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.ProfilingUtils
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.ActionProxy
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.util.profiling.ProfilingUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.struts2.ServletActionContext;

public class XWorkProfilingInterceptor
implements Interceptor {
    String location;

    public String intercept(ActionInvocation invocation) throws Exception {
        this.before(invocation);
        String result = invocation.invoke();
        this.after(invocation, result);
        return result;
    }

    private void before(ActionInvocation actionInvocation) throws Exception {
        UtilTimerStack.push((String)this.makeStackKey(actionInvocation.getProxy()));
        ServletActionContext.getRequest();
    }

    private void after(ActionInvocation actionInvocation, String string) throws Exception {
        UtilTimerStack.pop((String)this.makeStackKey(actionInvocation.getProxy()));
    }

    private String makeStackKey(ActionProxy proxy) {
        String methodName = proxy.getConfig().getMethodName();
        if (methodName == null) {
            methodName = "execute";
        }
        String actionClazz = ProfilingUtils.getJustClassName((String)proxy.getConfig().getClassName());
        return "XW Interceptor: " + (this.location != null ? this.location + ": " : "") + proxy.getNamespace() + "/" + proxy.getActionName() + ".action (" + actionClazz + "." + methodName + "())";
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void destroy() {
    }

    public void init() {
    }
}

