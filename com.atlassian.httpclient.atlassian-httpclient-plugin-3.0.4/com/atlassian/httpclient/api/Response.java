/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Buildable;
import com.atlassian.httpclient.api.Common;
import com.atlassian.httpclient.api.Message;
import java.io.InputStream;
import java.util.Map;

public interface Response
extends Message {
    public int getStatusCode();

    public String getStatusText();

    public boolean isInformational();

    public boolean isSuccessful();

    public boolean isOk();

    public boolean isCreated();

    public boolean isNoContent();

    public boolean isRedirection();

    public boolean isSeeOther();

    public boolean isNotModified();

    public boolean isClientError();

    public boolean isBadRequest();

    public boolean isUnauthorized();

    public boolean isForbidden();

    public boolean isNotFound();

    public boolean isConflict();

    public boolean isServerError();

    public boolean isInternalServerError();

    public boolean isServiceUnavailable();

    public boolean isError();

    public boolean isNotSuccessful();

    public static interface Builder
    extends Common<Builder>,
    Buildable<Response> {
        @Override
        public Builder setContentType(String var1);

        @Override
        public Builder setContentCharset(String var1);

        @Override
        public Builder setHeaders(Map<String, String> var1);

        @Override
        public Builder setHeader(String var1, String var2);

        @Override
        public Builder setEntity(String var1);

        @Override
        public Builder setEntityStream(InputStream var1, String var2);

        @Override
        public Builder setEntityStream(InputStream var1);

        public Builder setStatusText(String var1);

        public Builder setStatusCode(int var1);
    }
}

