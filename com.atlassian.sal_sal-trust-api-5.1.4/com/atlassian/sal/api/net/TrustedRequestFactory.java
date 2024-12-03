/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.TrustedRequest;

public interface TrustedRequestFactory<T extends TrustedRequest>
extends RequestFactory {
    public T createTrustedRequest(Request.MethodType var1, String var2);
}

