/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.commons.lang3.StringUtils;

public class MockActionProxy
implements ActionProxy {
    Object action;
    String actionName;
    ActionConfig config;
    boolean executeResult;
    ActionInvocation invocation;
    String namespace;
    String method;
    boolean executedCalled;
    String returnedResult;
    Configuration configuration;
    boolean methodSpecified;

    public void prepare() throws Exception {
    }

    @Override
    public String execute() throws Exception {
        this.executedCalled = true;
        return this.returnedResult;
    }

    public void setReturnedResult(String returnedResult) {
        this.returnedResult = returnedResult;
    }

    public boolean isExecutedCalled() {
        return this.executedCalled;
    }

    @Override
    public Object getAction() {
        return this.action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public ActionConfig getConfig() {
        return this.config;
    }

    public void setConfig(ActionConfig config) {
        this.config = config;
    }

    @Override
    public boolean getExecuteResult() {
        return this.executeResult;
    }

    @Override
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    @Override
    public ActionInvocation getInvocation() {
        return this.invocation;
    }

    public void setInvocation(ActionInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
        this.methodSpecified = StringUtils.isNotEmpty((CharSequence)method);
    }

    @Override
    public boolean isMethodSpecified() {
        return this.methodSpecified;
    }

    public void setMethodSpecified(boolean methodSpecified) {
        this.methodSpecified = methodSpecified;
    }
}

