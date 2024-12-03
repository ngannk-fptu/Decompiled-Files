/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public interface ActionProxy {
    public Object getAction();

    public String getActionName();

    public ActionConfig getConfig();

    public void setExecuteResult(boolean var1);

    public boolean getExecuteResult();

    public ActionInvocation getInvocation();

    public String getNamespace();

    public String execute() throws Exception;

    public String getMethod();

    public boolean isMethodSpecified();
}

