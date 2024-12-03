/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockActionInvocation
implements ActionInvocation {
    private Object action;
    private ActionContext invocationContext;
    private ActionEventListener actionEventListener;
    private ActionProxy proxy;
    private Result result;
    private String resultCode;
    private ValueStack stack;
    private List<PreResultListener> preResultListeners = new ArrayList<PreResultListener>();

    @Override
    public Object getAction() {
        return this.action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    @Override
    public ActionContext getInvocationContext() {
        return this.invocationContext;
    }

    public void setInvocationContext(ActionContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    @Override
    public ActionProxy getProxy() {
        return this.proxy;
    }

    public void setProxy(ActionProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String getResultCode() {
        return this.resultCode;
    }

    @Override
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public ValueStack getStack() {
        return this.stack;
    }

    public void setStack(ValueStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void addPreResultListener(PreResultListener listener) {
        this.preResultListeners.add(listener);
    }

    @Override
    public String invoke() throws Exception {
        Iterator<PreResultListener> iterator = this.preResultListeners.iterator();
        while (iterator.hasNext()) {
            PreResultListener preResultListener;
            PreResultListener listener = preResultListener = iterator.next();
            listener.beforeResult(this, this.resultCode);
        }
        return this.resultCode;
    }

    @Override
    public String invokeActionOnly() throws Exception {
        return this.resultCode;
    }

    @Override
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    public ActionEventListener getActionEventListener() {
        return this.actionEventListener;
    }

    @Override
    public void init(ActionProxy proxy) {
    }
}

