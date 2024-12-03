/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import java.util.List;
import java.util.Map;

public interface Request<T extends Request<?, ?>, RESP extends Response> {
    public T setConnectionTimeout(int var1);

    public T setSoTimeout(int var1);

    public T setUrl(String var1);

    public T setRequestBody(String var1);

    public T setRequestBody(String var1, String var2);

    public T setFiles(List<RequestFilePart> var1);

    public T setEntity(Object var1);

    public T addRequestParameters(String ... var1);

    public T addBasicAuthentication(String var1, String var2, String var3);

    public T addHeader(String var1, String var2);

    public T setHeader(String var1, String var2);

    public T setFollowRedirects(boolean var1);

    public Map<String, List<String>> getHeaders();

    public void execute(ResponseHandler<? super RESP> var1) throws ResponseException;

    public String execute() throws ResponseException;

    public <RET> RET executeAndReturn(ReturningResponseHandler<? super RESP, RET> var1) throws ResponseException;

    public static enum MethodType {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        TRACE,
        OPTIONS,
        PATCH;

    }
}

