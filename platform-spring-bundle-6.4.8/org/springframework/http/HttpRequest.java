/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.net.URI;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

public interface HttpRequest
extends HttpMessage {
    @Nullable
    default public HttpMethod getMethod() {
        return HttpMethod.resolve(this.getMethodValue());
    }

    public String getMethodValue();

    public URI getURI();
}

