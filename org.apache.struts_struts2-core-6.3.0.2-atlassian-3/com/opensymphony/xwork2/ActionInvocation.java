/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

public interface ActionInvocation {
    public Object getAction();

    public boolean isExecuted();

    public ActionContext getInvocationContext();

    public ActionProxy getProxy();

    public Result getResult() throws Exception;

    public String getResultCode();

    public void setResultCode(String var1);

    public ValueStack getStack();

    public void addPreResultListener(PreResultListener var1);

    public String invoke() throws Exception;

    public String invokeActionOnly() throws Exception;

    public void setActionEventListener(ActionEventListener var1);

    public void init(ActionProxy var1);
}

