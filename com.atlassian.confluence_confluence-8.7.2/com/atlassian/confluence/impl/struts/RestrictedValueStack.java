/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionContext
 *  com.opensymphony.xwork2.util.CompoundRoot
 *  com.opensymphony.xwork2.util.ValueStack
 */
package com.atlassian.confluence.impl.struts;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Map;

public class RestrictedValueStack
implements ValueStack {
    private final ValueStack delegate;

    public RestrictedValueStack(ValueStack delegate) {
        this.delegate = delegate;
    }

    public Map<String, Object> getContext() {
        throw new UnsupportedOperationException();
    }

    public ActionContext getActionContext() {
        throw new UnsupportedOperationException();
    }

    public void setDefaultType(Class defaultType) {
        throw new UnsupportedOperationException();
    }

    public void setExprOverrides(Map<Object, Object> overrides) {
        throw new UnsupportedOperationException();
    }

    public Map<Object, Object> getExprOverrides() {
        throw new UnsupportedOperationException();
    }

    public CompoundRoot getRoot() {
        throw new UnsupportedOperationException();
    }

    public void setValue(String expr, Object value) {
        throw new UnsupportedOperationException();
    }

    public void setParameter(String expr, Object value) {
        throw new UnsupportedOperationException();
    }

    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        throw new UnsupportedOperationException();
    }

    public String findString(String expr) {
        return this.delegate.findString(expr);
    }

    public String findString(String expr, boolean throwExceptionOnFailure) {
        return this.delegate.findString(expr, throwExceptionOnFailure);
    }

    public Object findValue(String expr) {
        return this.delegate.findValue(expr);
    }

    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        return this.delegate.findValue(expr, throwExceptionOnFailure);
    }

    public Object findValue(String expr, Class asType) {
        return this.delegate.findValue(expr, asType);
    }

    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        return this.delegate.findValue(expr, asType, throwExceptionOnFailure);
    }

    public Object peek() {
        return this.delegate.peek();
    }

    public Object pop() {
        return this.delegate.pop();
    }

    public void push(Object o) {
        this.delegate.push(o);
    }

    public void set(String key, Object o) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return this.delegate.size();
    }
}

