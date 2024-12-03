/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Request
 *  brave.internal.Nullable
 */
package brave.http;

import brave.Request;
import brave.internal.Nullable;

public abstract class HttpRequest
extends Request {
    public long startTimestamp() {
        return 0L;
    }

    public abstract String method();

    @Nullable
    public abstract String path();

    @Nullable
    public String route() {
        return null;
    }

    @Nullable
    public abstract String url();

    @Nullable
    public abstract String header(String var1);

    HttpRequest() {
    }
}

