/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Request;

public interface RequestFactory<T extends Request<?, ?>> {
    public T createRequest(Request.MethodType var1, String var2);

    public boolean supportsHeader();
}

