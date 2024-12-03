/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  com.atlassian.webhooks.request.Method
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpRequest$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.webhooks.WebhookPayloadBuilder;
import com.atlassian.webhooks.request.Method;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RawRequest
implements WebhookHttpRequest {
    private final byte[] content;
    private final String contentType;
    private final Map<String, String> headers;
    private final Method method;
    private final Map<String, List<String>> queryParameters;
    private final String url;

    private RawRequest(Builder builder) {
        this.content = builder.getBody();
        this.contentType = builder.getHeaders().getOrDefault("Content-Type", "application/json");
        this.headers = Collections.unmodifiableMap(new HashMap<String, String>(builder.getHeaders()));
        this.method = builder.getMethod();
        this.queryParameters = Collections.unmodifiableMap(new HashMap<String, List<String>>(builder.getParameters()));
        this.url = builder.getUrl();
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(@Nonnull Method post, @Nonnull String url) {
        return new Builder(post, url);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RawRequest that = (RawRequest)o;
        return Arrays.equals(this.getContent(), that.getContent()) && Objects.equals(this.getHeaders(), that.getHeaders()) && this.getMethod() == that.getMethod() && Objects.equals(this.getQueryParameters(), that.getQueryParameters()) && Objects.equals(this.getUrl(), that.getUrl());
    }

    @Nullable
    public byte[] getContent() {
        return this.content;
    }

    @Nonnull
    public Optional<String> getContentType() {
        return Optional.of(this.contentType);
    }

    @Nonnull
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Nonnull
    public Method getMethod() {
        return this.method;
    }

    @Nonnull
    public Map<String, List<String>> getQueryParameters() {
        return this.queryParameters;
    }

    @Nonnull
    public String getUrl() {
        URI uri = URI.create(this.url);
        String queryParams = uri.getQuery();
        ArrayList paramList = new ArrayList();
        this.getQueryParameters().forEach((key, value1) -> {
            for (String value : value1) {
                paramList.add(key + "=" + value);
            }
        });
        if (queryParams == null && !paramList.isEmpty()) {
            queryParams = String.join((CharSequence)"&", paramList);
        } else if (paramList.size() > 0) {
            queryParams = queryParams + "&" + String.join((CharSequence)"&", paramList);
        }
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), queryParams, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            return "";
        }
    }

    public int hashCode() {
        return Objects.hash(this.getContent(), this.getHeaders(), this.getMethod(), this.getQueryParameters(), this.getUrl());
    }

    public static class Builder
    implements WebhookHttpRequest.Builder {
        private final Map<String, String> headers = new HashMap<String, String>();
        private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        private byte[] body;
        private Method method;
        private String url;

        private Builder() {
        }

        private Builder(@Nonnull Method method, @Nonnull String url) {
            this();
            this.method = Objects.requireNonNull(method, "method");
            this.url = Objects.requireNonNull(url, "url");
        }

        @Nonnull
        public WebhookPayloadBuilder asPayloadBuilder() {
            return new PayloadBuilder();
        }

        @Nonnull
        public RawRequest build() {
            return new RawRequest(this);
        }

        @Nullable
        public byte[] getBody() {
            return this.body;
        }

        @Nonnull
        public Map<String, String> getHeaders() {
            return Collections.unmodifiableMap(this.headers);
        }

        @Nonnull
        public Method getMethod() {
            return this.method;
        }

        @Nonnull
        public Map<String, List<String>> getParameters() {
            return Collections.unmodifiableMap(this.parameters);
        }

        @Nonnull
        public String getUrl() {
            return this.url;
        }

        @Nonnull
        public Builder header(@Nonnull String name, @Nullable String value) {
            this.headers.put(Objects.requireNonNull(name, "name"), value);
            return this;
        }

        @Nonnull
        public Builder method(@Nonnull Method value) {
            this.method = Objects.requireNonNull(value, "method");
            return this;
        }

        @Nonnull
        public Builder parameter(@Nonnull String name, String ... values) {
            this.parameters.putIfAbsent(name, new ArrayList());
            if (values != null) {
                Collections.addAll((Collection)this.parameters.get(name), values);
            }
            return this;
        }

        @Nonnull
        public Builder removeHeader(@Nonnull String name) {
            this.headers.remove(Objects.requireNonNull(name, "name"));
            return this;
        }

        @Nonnull
        public Builder url(@Nonnull String value) {
            this.url = Objects.requireNonNull(value, "url");
            return this;
        }

        @Nonnull
        public Builder url(@Nonnull URI value) {
            this.url = Objects.requireNonNull(value, "url").toASCIIString();
            return this;
        }

        static /* synthetic */ byte[] access$402(Builder x0, byte[] x1) {
            x0.body = x1;
            return x1;
        }

        private class PayloadBuilder
        implements WebhookPayloadBuilder {
            private PayloadBuilder() {
            }

            @Nonnull
            public PayloadBuilder body(@Nullable byte[] body, @Nullable String contentType) {
                Builder.access$402(Builder.this, body);
                if (contentType == null) {
                    Builder.this.headers.remove("Content-Type");
                } else {
                    Builder.this.headers.put("Content-Type", contentType);
                }
                return this;
            }

            @Nonnull
            public PayloadBuilder header(@Nonnull String name, @Nullable String value) {
                Builder.this.header(name, value);
                return this;
            }
        }
    }
}

