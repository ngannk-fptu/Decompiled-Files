/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.internal.Nullable
 */
package brave.http;

import brave.http.HttpClientAdapter;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.internal.Nullable;

@Deprecated
final class HttpClientAdapters {
    HttpClientAdapters() {
    }

    @Deprecated
    static final class FromResponseAdapter<Res>
    extends HttpClientResponse {
        final HttpClientAdapter<?, Res> adapter;
        final Res response;
        @Nullable
        final Throwable error;

        FromResponseAdapter(HttpClientAdapter<?, Res> adapter, Res response, @Nullable Throwable error) {
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

        @Override
        public Throwable error() {
            return this.error;
        }

        public Object unwrap() {
            return this.response;
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
    extends HttpClientAdapter<Void, Object> {
        final HttpClientResponse delegate;
        final Object unwrapped;

        ToResponseAdapter(HttpClientResponse delegate, Object unwrapped) {
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
    extends HttpClientRequest {
        final HttpClientAdapter<Req, ?> adapter;
        final Req request;

        FromRequestAdapter(HttpClientAdapter<Req, ?> adapter, Req request) {
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
        public void header(String name, String value) {
        }

        public final String toString() {
            return this.request.toString();
        }
    }

    @Deprecated
    static final class ToRequestAdapter
    extends HttpClientAdapter<Object, Void> {
        final HttpClientRequest delegate;
        final Object unwrapped;

        ToRequestAdapter(HttpClientRequest delegate, Object unwrapped) {
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

