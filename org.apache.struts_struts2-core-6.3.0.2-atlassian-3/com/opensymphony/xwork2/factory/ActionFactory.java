/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.util.Map;

public interface ActionFactory {
    public Object buildAction(String var1, String var2, ActionConfig var3, Map<String, Object> var4) throws Exception;
}

