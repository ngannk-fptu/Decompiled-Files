/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.CommonBuilder;
import com.atlassian.httpclient.apache.httpcomponents.DefaultMessage;
import com.atlassian.httpclient.apache.httpcomponents.Headers;
import com.atlassian.httpclient.api.Response;
import io.atlassian.fugue.Option;
import java.io.InputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultResponse
extends DefaultMessage
implements Response {
    private int statusCode;
    private String statusText;
    private Logger log = LoggerFactory.getLogger(DefaultResponse.class);

    public DefaultResponse(Headers headers, InputStream entityStream, Option<Long> maxEntitySize, int statusCode, String statusText) {
        super(headers, entityStream, maxEntitySize);
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static DefaultResponseBuilder builder() {
        return new DefaultResponseBuilder();
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getStatusText() {
        return this.statusText;
    }

    @Override
    public boolean isInformational() {
        return this.statusCode >= 100 && this.statusCode < 200;
    }

    @Override
    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    @Override
    public boolean isOk() {
        return this.statusCode == 200;
    }

    @Override
    public boolean isCreated() {
        return this.statusCode == 201;
    }

    @Override
    public boolean isNoContent() {
        return this.statusCode == 204;
    }

    @Override
    public boolean isRedirection() {
        return this.statusCode >= 300 && this.statusCode < 400;
    }

    @Override
    public boolean isSeeOther() {
        return this.statusCode == 303;
    }

    @Override
    public boolean isNotModified() {
        return this.statusCode == 304;
    }

    @Override
    public boolean isClientError() {
        return this.statusCode >= 400 && this.statusCode < 500;
    }

    @Override
    public boolean isBadRequest() {
        return this.statusCode == 400;
    }

    @Override
    public boolean isUnauthorized() {
        return this.statusCode == 401;
    }

    @Override
    public boolean isForbidden() {
        return this.statusCode == 403;
    }

    @Override
    public boolean isNotFound() {
        return this.statusCode == 404;
    }

    @Override
    public boolean isConflict() {
        return this.statusCode == 409;
    }

    @Override
    public boolean isServerError() {
        return this.statusCode >= 500 && this.statusCode < 600;
    }

    @Override
    public boolean isInternalServerError() {
        return this.statusCode == 500;
    }

    @Override
    public boolean isServiceUnavailable() {
        return this.statusCode == 503;
    }

    @Override
    public boolean isError() {
        return this.isClientError() || this.isServerError();
    }

    @Override
    public boolean isNotSuccessful() {
        return this.isInformational() || this.isRedirection() || this.isError();
    }

    @Override
    public Option<Long> getContentLength() {
        String lengthString = this.getHeader("Content-Length");
        if (lengthString != null) {
            try {
                Option parsedLength = Option.some((Object)Long.parseLong(lengthString));
                return parsedLength.flatMap(aLong -> {
                    if (aLong < 0L) {
                        this.log.warn("Unable to parse content length. Received out of range value {}", aLong);
                        return Option.none();
                    }
                    return Option.some((Object)aLong);
                });
            }
            catch (NumberFormatException e) {
                this.log.warn("Unable to parse content length {}", (Object)lengthString);
                return Option.none();
            }
        }
        return Option.none();
    }

    public static class DefaultResponseBuilder
    implements Response.Builder {
        private final CommonBuilder<DefaultResponse> commonBuilder = new CommonBuilder();
        private String statusText;
        private int statusCode;
        private long maxEntitySize;

        private DefaultResponseBuilder() {
        }

        @Override
        public DefaultResponseBuilder setContentType(String contentType) {
            this.commonBuilder.setContentType(contentType);
            return this;
        }

        @Override
        public DefaultResponseBuilder setContentCharset(String contentCharset) {
            this.commonBuilder.setContentCharset(contentCharset);
            return this;
        }

        @Override
        public DefaultResponseBuilder setHeaders(Map<String, String> headers) {
            this.commonBuilder.setHeaders((Map)headers);
            return this;
        }

        @Override
        public DefaultResponseBuilder setHeader(String name, String value) {
            this.commonBuilder.setHeader(name, value);
            return this;
        }

        @Override
        public DefaultResponseBuilder setEntity(String entity) {
            this.commonBuilder.setEntity(entity);
            return this;
        }

        @Override
        public DefaultResponseBuilder setEntityStream(InputStream entityStream, String encoding) {
            this.commonBuilder.setEntityStream(entityStream);
            this.commonBuilder.setContentCharset(encoding);
            return this;
        }

        @Override
        public DefaultResponseBuilder setEntityStream(InputStream entityStream) {
            this.commonBuilder.setEntityStream(entityStream);
            return this;
        }

        @Override
        public DefaultResponseBuilder setStatusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        @Override
        public DefaultResponseBuilder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public DefaultResponseBuilder setMaxEntitySize(long maxEntitySize) {
            this.maxEntitySize = maxEntitySize;
            return this;
        }

        @Override
        public DefaultResponse build() {
            return new DefaultResponse(this.commonBuilder.getHeaders(), this.commonBuilder.getEntityStream(), (Option<Long>)Option.option((Object)this.maxEntitySize), this.statusCode, this.statusText);
        }
    }
}

