/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.net.URI;
import org.springframework.http.server.DefaultRequestPath;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;

public interface RequestPath
extends PathContainer {
    public PathContainer contextPath();

    public PathContainer pathWithinApplication();

    public RequestPath modifyContextPath(String var1);

    public static RequestPath parse(URI uri, @Nullable String contextPath) {
        return RequestPath.parse(uri.getRawPath(), contextPath);
    }

    public static RequestPath parse(String rawPath, @Nullable String contextPath) {
        return new DefaultRequestPath(rawPath, contextPath);
    }
}

