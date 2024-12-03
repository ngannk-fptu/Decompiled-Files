/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.internal.Nullable
 */
package brave.http;

import brave.internal.Nullable;
import java.net.URI;

@Deprecated
public abstract class HttpAdapter<Req, Resp> {
    public abstract String method(Req var1);

    @Nullable
    public String path(Req request) {
        String url = this.url(request);
        if (url == null) {
            return null;
        }
        return URI.create(url).getPath();
    }

    @Nullable
    public abstract String url(Req var1);

    @Nullable
    public abstract String requestHeader(Req var1, String var2);

    public long startTimestamp(Req request) {
        return 0L;
    }

    @Nullable
    public String methodFromResponse(Resp resp) {
        return null;
    }

    @Nullable
    public String route(Resp response) {
        return null;
    }

    @Deprecated
    @Nullable
    public abstract Integer statusCode(Resp var1);

    public int statusCodeAsInt(Resp response) {
        Integer maybeStatus = this.statusCode(response);
        return maybeStatus != null ? maybeStatus : 0;
    }

    public long finishTimestamp(Resp response) {
        return 0L;
    }

    HttpAdapter() {
    }
}

