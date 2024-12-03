/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.util.List;

public interface UnknownHandlerManager {
    public Result handleUnknownResult(ActionContext var1, String var2, ActionConfig var3, String var4);

    public Object handleUnknownMethod(Object var1, String var2) throws NoSuchMethodException;

    public ActionConfig handleUnknownAction(String var1, String var2);

    public boolean hasUnknownHandlers();

    public List<UnknownHandler> getUnknownHandlers();
}

