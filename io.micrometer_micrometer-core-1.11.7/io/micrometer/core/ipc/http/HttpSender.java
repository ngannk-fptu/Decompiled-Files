/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 */
package io.micrometer.core.ipc.http;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.ipc.http.HttpStatusClass;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;

public interface HttpSender {
    public Response send(Request var1) throws Throwable;

    default public Request.Builder post(String uri) {
        return this.newRequest(uri).withMethod(Method.POST);
    }

    default public Request.Builder head(String uri) {
        return this.newRequest(uri).withMethod(Method.HEAD);
    }

    default public Request.Builder put(String uri) {
        return this.newRequest(uri).withMethod(Method.PUT);
    }

    default public Request.Builder get(String uri) {
        return this.newRequest(uri).withMethod(Method.GET);
    }

    default public Request.Builder delete(String uri) {
        return this.newRequest(uri).withMethod(Method.DELETE);
    }

    default public Request.Builder options(String uri) {
        return this.newRequest(uri).withMethod(Method.OPTIONS);
    }

    default public Request.Builder newRequest(String uri) {
        return new Request.Builder(uri, this);
    }

    public static class Request {
        private final URL url;
        private final byte[] entity;
        private final Method method;
        private final Map<String, String> requestHeaders;

        public Request(URL url, byte[] entity, Method method, Map<String, String> requestHeaders) {
            this.url = url;
            this.entity = entity;
            this.method = method;
            this.requestHeaders = requestHeaders;
        }

        public URL getUrl() {
            return this.url;
        }

        public byte[] getEntity() {
            return this.entity;
        }

        public Method getMethod() {
            return this.method;
        }

        public Map<String, String> getRequestHeaders() {
            return this.requestHeaders;
        }

        public static Builder build(String uri, HttpSender sender) {
            return new Builder(uri, sender);
        }

        public String toString() {
            StringBuilder printed = new StringBuilder(this.method.toString()).append(' ').append(this.url.toString()).append("\n");
            if (this.entity.length == 0) {
                printed.append("<no request body>");
            } else {
                printed.append(new String(this.entity));
            }
            return printed.toString();
        }

        public static class Builder {
            private static final String APPLICATION_JSON = "application/json";
            private static final String TEXT_PLAIN = "text/plain";
            private final URL url;
            private final HttpSender sender;
            private byte[] entity = new byte[0];
            private Method method;
            private Map<String, String> requestHeaders = new LinkedHashMap<String, String>();

            Builder(String uri, HttpSender sender) {
                try {
                    this.url = URI.create(uri).toURL();
                }
                catch (MalformedURLException ex) {
                    throw new UncheckedIOException(ex);
                }
                this.sender = sender;
            }

            public final Builder withHeader(String name, String value) {
                this.requestHeaders.put(name, value);
                return this;
            }

            public final Builder withBasicAuthentication(@Nullable String user, @Nullable String password) {
                if (StringUtils.isNotBlank((String)user)) {
                    String encoded = Base64.getEncoder().encodeToString((user.trim() + ":" + (password == null ? "" : password.trim())).getBytes(StandardCharsets.UTF_8));
                    this.withAuthentication("Basic", encoded);
                }
                return this;
            }

            public final Builder withAuthentication(String type, @Nullable String credentials) {
                if (StringUtils.isNotBlank((String)credentials)) {
                    this.withHeader("Authorization", type + " " + credentials);
                }
                return this;
            }

            public final Builder withJsonContent(String content) {
                return this.withContent(APPLICATION_JSON, content);
            }

            public final Builder withPlainText(String content) {
                return this.withContent(TEXT_PLAIN, content);
            }

            public final Builder withContent(String type, String content) {
                return this.withContent(type, content.getBytes(StandardCharsets.UTF_8));
            }

            public final Builder withContent(String type, byte[] content) {
                this.withHeader("Content-Type", type);
                this.entity = content;
                return this;
            }

            public Builder acceptJson() {
                return this.accept(APPLICATION_JSON);
            }

            public Builder accept(String type) {
                return this.withHeader("Accept", type);
            }

            public final Builder withMethod(Method method) {
                this.method = method;
                return this;
            }

            public final Builder compress() throws IOException {
                this.withHeader("Content-Encoding", "gzip");
                this.entity = Builder.gzip(this.entity);
                return this;
            }

            public final Builder compressWhen(Supplier<Boolean> when) throws IOException {
                if (when.get().booleanValue()) {
                    return this.compress();
                }
                return this;
            }

            private static byte[] gzip(byte[] data) throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
                try (GZIPOutputStream out = new GZIPOutputStream(bos);){
                    out.write(data);
                }
                return bos.toByteArray();
            }

            public final Builder print() {
                System.out.println(new Request(this.url, this.entity, this.method, this.requestHeaders));
                return this;
            }

            public Response send() throws Throwable {
                return this.sender.send(new Request(this.url, this.entity, this.method, this.requestHeaders));
            }
        }
    }

    public static enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        OPTIONS;

    }

    public static class Response {
        public static final String NO_RESPONSE_BODY = "<no response body>";
        private final int code;
        private final String body;

        public Response(int code, @Nullable String body) {
            this.code = code;
            this.body = StringUtils.isBlank((String)body) ? NO_RESPONSE_BODY : body;
        }

        public int code() {
            return this.code;
        }

        public String body() {
            return this.body;
        }

        public Response onSuccess(Consumer<Response> onSuccess) {
            switch (HttpStatusClass.valueOf(this.code)) {
                case INFORMATIONAL: 
                case SUCCESS: {
                    onSuccess.accept(this);
                }
            }
            return this;
        }

        public Response onError(Consumer<Response> onError) {
            switch (HttpStatusClass.valueOf(this.code)) {
                case CLIENT_ERROR: 
                case SERVER_ERROR: {
                    onError.accept(this);
                }
            }
            return this;
        }

        public boolean isSuccessful() {
            switch (HttpStatusClass.valueOf(this.code)) {
                case INFORMATIONAL: 
                case SUCCESS: {
                    return true;
                }
            }
            return false;
        }
    }
}

