/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ValidationAware {
    public void setActionErrors(Collection<String> var1);

    public Collection<String> getActionErrors();

    public void setActionMessages(Collection<String> var1);

    public Collection<String> getActionMessages();

    public void setFieldErrors(Map<String, List<String>> var1);

    public Map<String, List<String>> getFieldErrors();

    public void addActionError(String var1);

    public void addActionMessage(String var1);

    public void addFieldError(String var1, String var2);

    public boolean hasActionErrors();

    public boolean hasActionMessages();

    public boolean hasErrors();

    public boolean hasFieldErrors();
}

