/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.api.Buildable;
import com.google.common.base.Preconditions;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Headers {
    private final Map<String, String> headers;
    private final String contentCharset;
    private final String contentType;

    private Headers(Map<String, String> headers, String contentCharset, String contentType) {
        this.headers = headers;
        this.contentCharset = contentCharset;
        this.contentType = contentType;
    }

    public String getContentCharset() {
        return this.contentCharset;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Map<String, String> getHeaders() {
        TreeMap<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        headers.putAll(this.headers);
        if (this.contentType != null) {
            headers.put("Content-Type", this.buildContentType());
        }
        return Collections.unmodifiableMap(headers);
    }

    public String getHeader(String name) {
        String value = name.equalsIgnoreCase("Content-Type") ? this.buildContentType() : this.headers.get(name);
        return value;
    }

    private String buildContentType() {
        String value;
        String string = value = this.contentType != null ? this.contentType : "text/plain";
        if (this.contentCharset != null) {
            value = value + "; charset=" + this.contentCharset;
        }
        return value;
    }

    public static class Names {
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_TYPE = "Content-Type";

        private Names() {
        }
    }

    public static class Builder
    implements Buildable<Headers> {
        private final Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        private String contentType;
        private String contentCharset;

        public Builder setHeaders(Map<String, String> headers) {
            this.headers.clear();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.setHeader(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder setHeader(String name, String value) {
            if (name.equalsIgnoreCase("Content-Type")) {
                this.parseContentType(value);
            } else {
                this.headers.put(name, value);
            }
            return this;
        }

        public Builder setContentLength(long contentLength) {
            Preconditions.checkArgument((contentLength >= 0L ? 1 : 0) != 0, (Object)"Content-Length must be greater than or equal to 0");
            this.setHeader("Content-Length", Long.toString(contentLength));
            return this;
        }

        public Builder setContentCharset(String contentCharset) {
            this.contentCharset = contentCharset != null ? Charset.forName(contentCharset).name() : null;
            return this;
        }

        public Builder setContentType(String contentType) {
            this.parseContentType(contentType);
            return this;
        }

        private void parseContentType(String value) {
            if (value != null) {
                String[] parts = value.split(";");
                if (parts.length >= 1) {
                    this.contentType = parts[0].trim();
                }
                if (parts.length >= 2) {
                    String subtype = parts[1].trim();
                    if (subtype.startsWith("charset=")) {
                        this.setContentCharset(subtype.substring(8));
                    } else if (subtype.startsWith("boundary=")) {
                        this.contentType = this.contentType.concat(';' + subtype);
                    }
                }
            } else {
                this.contentType = null;
            }
        }

        @Override
        public Headers build() {
            return new Headers(this.headers, this.contentCharset, this.contentType);
        }
    }
}

