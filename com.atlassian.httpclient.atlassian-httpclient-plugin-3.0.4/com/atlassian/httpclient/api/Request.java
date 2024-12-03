/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Buildable;
import com.atlassian.httpclient.api.Common;
import com.atlassian.httpclient.api.EntityBuilder;
import com.atlassian.httpclient.api.Message;
import com.atlassian.httpclient.api.ResponsePromise;
import java.net.URI;
import java.util.Map;

public interface Request
extends Message {
    public URI getUri();

    public String getAccept();

    public String getAttribute(String var1);

    public Map<String, String> getAttributes();

    public boolean isCacheDisabled();

    public Method getMethod();

    public static interface Builder
    extends Common<Builder>,
    Buildable<Request> {
        public Builder setUri(URI var1);

        public Builder setAccept(String var1);

        public Builder setCacheDisabled();

        public Builder setAttribute(String var1, String var2);

        public Builder setAttributes(Map<String, String> var1);

        public Builder setContentLength(long var1);

        public Builder setEntity(EntityBuilder var1);

        @Override
        public Builder setHeader(String var1, String var2);

        public ResponsePromise get();

        public ResponsePromise post();

        public ResponsePromise put();

        public ResponsePromise delete();

        public ResponsePromise options();

        public ResponsePromise head();

        public ResponsePromise trace();

        public ResponsePromise execute(Method var1);
    }

    public static enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        OPTIONS,
        HEAD,
        TRACE;

    }
}

