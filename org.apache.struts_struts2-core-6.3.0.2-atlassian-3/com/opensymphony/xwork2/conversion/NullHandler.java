/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

import java.util.Map;

public interface NullHandler {
    public Object nullMethodResult(Map<String, Object> var1, Object var2, String var3, Object[] var4);

    public Object nullPropertyValue(Map<String, Object> var1, Object var2, Object var3);
}

