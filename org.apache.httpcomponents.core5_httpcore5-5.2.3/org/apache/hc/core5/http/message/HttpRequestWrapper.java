/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.AbstractMessageWrapper;
import org.apache.hc.core5.net.URIAuthority;

public class HttpRequestWrapper
extends AbstractMessageWrapper<HttpRequest>
implements HttpRequest {
    public HttpRequestWrapper(HttpRequest message) {
        super(message);
    }

    @Override
    public String getMethod() {
        return ((HttpRequest)this.getMessage()).getMethod();
    }

    @Override
    public String getPath() {
        return ((HttpRequest)this.getMessage()).getPath();
    }

    @Override
    public void setPath(String path) {
        ((HttpRequest)this.getMessage()).setPath(path);
    }

    @Override
    public String getScheme() {
        return ((HttpRequest)this.getMessage()).getScheme();
    }

    @Override
    public void setScheme(String scheme) {
        ((HttpRequest)this.getMessage()).setScheme(scheme);
    }

    @Override
    public URIAuthority getAuthority() {
        return ((HttpRequest)this.getMessage()).getAuthority();
    }

    @Override
    public void setAuthority(URIAuthority authority) {
        ((HttpRequest)this.getMessage()).setAuthority(authority);
    }

    @Override
    public String getRequestUri() {
        return ((HttpRequest)this.getMessage()).getRequestUri();
    }

    @Override
    public URI getUri() throws URISyntaxException {
        return ((HttpRequest)this.getMessage()).getUri();
    }

    @Override
    public void setUri(URI requestUri) {
        ((HttpRequest)this.getMessage()).setUri(requestUri);
    }
}

