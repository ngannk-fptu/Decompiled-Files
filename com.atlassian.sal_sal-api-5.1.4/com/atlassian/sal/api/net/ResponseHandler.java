/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

public interface ResponseHandler<T extends Response> {
    public void handle(T var1) throws ResponseException;
}

