/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import java.util.Map;

public interface ActionProxyFactory {
    public ActionProxy createActionProxy(String var1, String var2, String var3, Map<String, Object> var4);

    public ActionProxy createActionProxy(String var1, String var2, String var3, Map<String, Object> var4, boolean var5, boolean var6);

    public ActionProxy createActionProxy(ActionInvocation var1, String var2, String var3, String var4, boolean var5, boolean var6);
}

