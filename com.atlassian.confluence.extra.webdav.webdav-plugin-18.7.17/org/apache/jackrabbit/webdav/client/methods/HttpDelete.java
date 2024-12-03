/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.net.URI;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;

public class HttpDelete
extends BaseDavRequest {
    public HttpDelete(URI uri) {
        super(uri);
    }

    public HttpDelete(String uri) {
        this(URI.create(uri));
    }

    public String getMethod() {
        return "DELETE";
    }
}

