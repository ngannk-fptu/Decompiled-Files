/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.internal.Nullable
 */
package brave.http;

import brave.Span;
import brave.http.HttpServerAdapter;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.internal.Nullable;

@Deprecated
final class HttpServerAdapters {
    HttpServerAdapters() {
    }

    @Deprecated
    static final class FromResponseAdapter<Res>
    extends HttpServerResponse {
        final HttpServerAdapter<?, Res> adapter;
        final Res response;
        @Nullable
        final Throwable error;

        FromResponseAdapter(HttpServerAdapter<?, Res> adapter, Res response, @Nullable Throwable error) {
            if (adapter == null) {
                throw new NullPointerException("adapter == null");
            }
            if (response == null) {
                throw new NullPointerException("response == null");
            }
            this.adapter = adapter;
            this.response = response;
            this.error = error;
        }

        public Object unwrap() {
            return this.response;
        }

        @Override
        public Throwable error() {
            return this.error;
        }

        @Override
        public String method() {
            return this.adapter.methodFromResponse(this.response);
        }

        @Override
        public String route() {
            return this.adapter.route(this.response);
        }

        @Override
        public int statusCode() {
            return this.adapter.statusCodeAsInt(this.response);
        }

        @Override
        public long finishTimestamp() {
            return this.adapter.finishTimestamp(this.response);
        }

        public String toString() {
            return this.response.toString();
        }
    }

    @Deprecated
    static final class ToResponseAdapter
    extends HttpServerAdapter<Void, Object> {
        final HttpServerResponse delegate;
        final Object unwrapped;

        ToResponseAdapter(HttpServerResponse delegate, Object unwrapped) {
            if (delegate == null) {
                throw new NullPointerException("delegate == null");
            }
            if (unwrapped == null) {
                throw new NullPointerException("unwrapped == null");
            }
            this.delegate = delegate;
            this.unwrapped = unwrapped;
        }

        @Override
        public final String method(Void request) {
            return null;
        }

        @Override
        public final String path(Void request) {
            return null;
        }

        @Override
        public final String url(Void request) {
            return null;
        }

        @Override
        public final String requestHeader(Void request, String name) {
            return null;
        }

        @Override
        public final long startTimestamp(Void request) {
            return 0L;
        }

        @Override
        public final String methodFromResponse(Object response) {
            if (response == this.unwrapped) {
                return this.delegate.method();
            }
            return null;
        }

        @Override
        public final String route(Object response) {
            if (response == this.unwrapped) {
                return this.delegate.route();
            }
            return null;
        }

        @Override
        @Nullable
        public final Integer statusCode(Object response) {
            int result = this.statusCodeAsInt(response);
            return result == 0 ? null : Integer.valueOf(result);
        }

        @Override
        public final int statusCodeAsInt(Object response) {
            if (response == this.unwrapped) {
                return this.delegate.statusCode();
            }
            return 0;
        }

        @Override
        public final long finishTimestamp(Object response) {
            if (response == this.unwrapped) {
                return this.delegate.finishTimestamp();
            }
            return 0L;
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    @Deprecated
    static final class FromRequestAdapter<Req>
    extends HttpServerRequest {
        final HttpServerAdapter<Req, ?> adapter;
        final Req request;

        FromRequestAdapter(HttpServerAdapter<Req, ?> adapter, Req request) {
            if (adapter == null) {
                throw new NullPointerException("adapter == null");
            }
            this.adapter = adapter;
            if (request == null) {
                throw new NullPointerException("request == null");
            }
            this.request = request;
        }

        public Object unwrap() {
            return this.request;
        }

        @Override
        public long startTimestamp() {
            return this.adapter.startTimestamp(this.request);
        }

        @Override
        public String method() {
            return this.adapter.method(this.request);
        }

        @Override
        public String path() {
            return this.adapter.path(this.request);
        }

        @Override
        public String url() {
            return this.adapter.url(this.request);
        }

        @Override
        public String header(String name) {
            return this.adapter.requestHeader(this.request, name);
        }

        @Override
        public boolean parseClientIpAndPort(Span span) {
            return this.adapter.parseClientIpAndPort(this.request, span);
        }

        public final String toString() {
            return this.request.toString();
        }
    }

    @Deprecated
    static final class ToRequestAdapter
    extends HttpServerAdapter<Object, Void> {
        final HttpServerRequest delegate;
        final Object unwrapped;

        ToRequestAdapter(HttpServerRequest delegate, Object unwrapped) {
            if (delegate == null) {
                throw new NullPointerException("delegate == null");
            }
            if (unwrapped == null) {
                throw new NullPointerException("unwrapped == null");
            }
            this.delegate = delegate;
            this.unwrapped = unwrapped;
        }

        @Override
        public final boolean parseClientIpAndPort(Object req, Span span) {
            if (req == this.unwrapped) {
                if (this.parseClientIpFromXForwardedFor(req, span)) {
                    return true;
                }
                return this.delegate.parseClientIpAndPort(span);
            }
            return false;
        }

        @Override
        public final long startTimestamp(Object request) {
            if (request == this.unwrapped) {
                return this.delegate.startTimestamp();
            }
            return 0L;
        }

        @Override
        public final String method(Object request) {
            if (request == this.unwrapped) {
                return this.delegate.method();
            }
            return null;
        }

        @Override
        public final String url(Object request) {
            if (request == this.unwrapped) {
                return this.delegate.url();
            }
            return null;
        }

        @Override
        public final String requestHeader(Object request, String name) {
            if (request == this.unwrapped) {
                return this.delegate.header(name);
            }
            return null;
        }

        @Override
        public final String path(Object request) {
            if (request == this.unwrapped) {
                return this.delegate.path();
            }
            return null;
        }

        public final String toString() {
            return this.delegate.toString();
        }

        @Override
        public final String methodFromResponse(Void response) {
            return null;
        }

        @Override
        public final String route(Void response) {
            return null;
        }

        @Override
        public final int statusCodeAsInt(Void response) {
            return 0;
        }

        @Override
        @Nullable
        public final Integer statusCode(Void response) {
            return null;
        }

        @Override
        public final long finishTimestamp(Void response) {
            return 0L;
        }
    }
}

