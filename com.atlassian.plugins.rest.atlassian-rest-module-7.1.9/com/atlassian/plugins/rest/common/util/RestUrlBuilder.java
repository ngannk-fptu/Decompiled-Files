/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.util;

import java.net.URI;
import javax.ws.rs.core.Response;

public interface RestUrlBuilder {
    public URI getURI(Response var1);

    public <T> T getUrlFor(URI var1, Class<T> var2);
}

