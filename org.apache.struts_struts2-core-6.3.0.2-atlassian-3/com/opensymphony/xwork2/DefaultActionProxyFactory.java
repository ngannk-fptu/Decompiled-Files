/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.DefaultActionProxy;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.Map;

public class DefaultActionProxyFactory
implements ActionProxyFactory {
    protected Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext) {
        return this.createActionProxy(namespace, actionName, null, extraContext, true, true);
    }

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext) {
        return this.createActionProxy(namespace, actionName, methodName, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        return this.createActionProxy(namespace, actionName, null, extraContext, executeResult, cleanupContext);
    }

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        ActionInvocation inv = this.createActionInvocation(extraContext, true);
        this.container.inject(inv);
        return this.createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }

    protected ActionInvocation createActionInvocation(Map<String, Object> extraContext, boolean pushAction) {
        return new DefaultActionInvocation(extraContext, pushAction);
    }

    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, boolean executeResult, boolean cleanupContext) {
        return this.createActionProxy(inv, namespace, actionName, null, executeResult, cleanupContext);
    }

    @Override
    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        DefaultActionProxy proxy = new DefaultActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        this.container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

