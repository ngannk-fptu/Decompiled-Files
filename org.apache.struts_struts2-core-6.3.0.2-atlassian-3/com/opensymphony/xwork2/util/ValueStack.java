/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.CompoundRoot;
import java.util.Map;

public interface ValueStack {
    public static final String VALUE_STACK = "com.opensymphony.xwork2.util.ValueStack.ValueStack";
    public static final String REPORT_ERRORS_ON_NO_PROP = "com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp";

    public Map<String, Object> getContext();

    public ActionContext getActionContext();

    public void setDefaultType(Class var1);

    public void setExprOverrides(Map<Object, Object> var1);

    public Map<Object, Object> getExprOverrides();

    public CompoundRoot getRoot();

    public void setValue(String var1, Object var2);

    public void setParameter(String var1, Object var2);

    public void setValue(String var1, Object var2, boolean var3);

    public String findString(String var1);

    public String findString(String var1, boolean var2);

    public Object findValue(String var1);

    public Object findValue(String var1, boolean var2);

    public Object findValue(String var1, Class var2);

    public Object findValue(String var1, Class var2, boolean var3);

    public Object peek();

    public Object pop();

    public void push(Object var1);

    public void set(String var1, Object var2);

    public int size();
}

