/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpRecoverableException;

public interface MethodRetryHandler {
    public boolean retryMethod(HttpMethod var1, HttpConnection var2, HttpRecoverableException var3, int var4, boolean var5);
}

