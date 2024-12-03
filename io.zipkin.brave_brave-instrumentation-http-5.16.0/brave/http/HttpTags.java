/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.propagation.TraceContext
 */
package brave.http;

import brave.Tag;
import brave.http.HttpRequest;
import brave.http.HttpResponse;
import brave.propagation.TraceContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpTags {
    static final Map<Integer, String> CACHED_STATUS_CODES = new ConcurrentHashMap<Integer, String>();
    public static final Tag<HttpRequest> METHOD = new Tag<HttpRequest>("http.method"){

        protected String parseValue(HttpRequest input, TraceContext context) {
            return input.method();
        }
    };
    public static final Tag<HttpRequest> PATH = new Tag<HttpRequest>("http.path"){

        protected String parseValue(HttpRequest input, TraceContext context) {
            return input.path();
        }
    };
    public static final Tag<HttpRequest> ROUTE = new Tag<HttpRequest>("http.route"){

        protected String parseValue(HttpRequest input, TraceContext context) {
            return input.route();
        }
    };
    public static final Tag<HttpRequest> URL = new Tag<HttpRequest>("http.url"){

        protected String parseValue(HttpRequest input, TraceContext context) {
            return input.url();
        }
    };
    public static final Tag<HttpResponse> STATUS_CODE = new Tag<HttpResponse>("http.status_code"){

        protected String parseValue(HttpResponse input, TraceContext context) {
            int statusCode = input.statusCode();
            return HttpTags.statusCodeString(statusCode);
        }
    };

    public static Tag<HttpRequest> requestHeader(String headerName) {
        return HttpTags.requestHeader(headerName, headerName);
    }

    public static Tag<HttpRequest> requestHeader(String key, final String headerName) {
        return new Tag<HttpRequest>(key){
            String name;
            {
                super(arg0);
                this.name = 6.validateNonEmpty((String)"headerName", (String)headerName);
            }

            protected String parseValue(HttpRequest input, TraceContext context) {
                return input.header(this.name);
            }
        };
    }

    static String statusCodeString(int statusCode) {
        if (statusCode < 100 || statusCode > 599) {
            return null;
        }
        String cached = CACHED_STATUS_CODES.get(statusCode);
        if (cached != null) {
            return cached;
        }
        String result = String.valueOf(statusCode);
        CACHED_STATUS_CODES.put(statusCode, result);
        return result;
    }

    HttpTags() {
    }
}

