/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.Traceable;
import java.util.Map;

public interface HttpContext
extends Traceable {
    public ExtendedUriInfo getUriInfo();

    public HttpRequestContext getRequest();

    public HttpResponseContext getResponse();

    public Map<String, Object> getProperties();
}

