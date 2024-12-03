/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.MapInterceptor;

public interface MapServiceContextInterceptorSupport {
    public void interceptAfterGet(String var1, Object var2);

    public Object interceptPut(String var1, Object var2, Object var3);

    public void interceptAfterPut(String var1, Object var2);

    public Object interceptRemove(String var1, Object var2);

    public void interceptAfterRemove(String var1, Object var2);

    public String generateInterceptorId(String var1, MapInterceptor var2);

    public void addInterceptor(String var1, String var2, MapInterceptor var3);

    public void removeInterceptor(String var1, String var2);

    public Object interceptGet(String var1, Object var2);

    public boolean hasInterceptor(String var1);
}

