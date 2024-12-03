/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.struts2.StrutsException;

public interface UnknownHandler {
    public ActionConfig handleUnknownAction(String var1, String var2) throws StrutsException;

    public Result handleUnknownResult(ActionContext var1, String var2, ActionConfig var3, String var4) throws StrutsException;

    public Object handleUnknownActionMethod(Object var1, String var2);
}

