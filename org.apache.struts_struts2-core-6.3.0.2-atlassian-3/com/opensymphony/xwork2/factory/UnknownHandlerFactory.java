/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.UnknownHandler;
import java.util.Map;

public interface UnknownHandlerFactory {
    public UnknownHandler buildUnknownHandler(String var1, Map<String, Object> var2) throws Exception;
}

