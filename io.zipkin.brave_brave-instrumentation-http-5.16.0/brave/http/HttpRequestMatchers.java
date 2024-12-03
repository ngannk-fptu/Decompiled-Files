/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.sampler.Matcher
 */
package brave.http;

import brave.http.HttpRequest;
import brave.sampler.Matcher;

public final class HttpRequestMatchers {
    public static Matcher<HttpRequest> methodEquals(String method) {
        if (method == null) {
            throw new NullPointerException("method == null");
        }
        if (method.isEmpty()) {
            throw new NullPointerException("method is empty");
        }
        return new MethodEquals(method);
    }

    public static Matcher<HttpRequest> pathStartsWith(String pathPrefix) {
        if (pathPrefix == null) {
            throw new NullPointerException("pathPrefix == null");
        }
        if (pathPrefix.isEmpty()) {
            throw new NullPointerException("pathPrefix is empty");
        }
        return new PathStartsWith(pathPrefix);
    }

    static final class PathStartsWith
    implements Matcher<HttpRequest> {
        final String pathPrefix;

        PathStartsWith(String pathPrefix) {
            this.pathPrefix = pathPrefix;
        }

        public boolean matches(HttpRequest request) {
            String requestPath = request.path();
            return requestPath != null && requestPath.startsWith(this.pathPrefix);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PathStartsWith)) {
                return false;
            }
            PathStartsWith that = (PathStartsWith)o;
            return this.pathPrefix.equals(that.pathPrefix);
        }

        public int hashCode() {
            return this.pathPrefix.hashCode();
        }

        public String toString() {
            return "PathStartsWith(" + this.pathPrefix + ")";
        }
    }

    static final class MethodEquals
    implements Matcher<HttpRequest> {
        final String method;

        MethodEquals(String method) {
            this.method = method;
        }

        public boolean matches(HttpRequest request) {
            return this.method.equals(request.method());
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MethodEquals)) {
                return false;
            }
            MethodEquals that = (MethodEquals)o;
            return this.method.equals(that.method);
        }

        public int hashCode() {
            return this.method.hashCode();
        }

        public String toString() {
            return "MethodEquals(" + this.method + ")";
        }
    }
}

