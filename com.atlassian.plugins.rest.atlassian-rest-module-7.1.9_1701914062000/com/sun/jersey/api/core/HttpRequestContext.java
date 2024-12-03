/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.Traceable;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

public interface HttpRequestContext
extends HttpHeaders,
Request,
SecurityContext,
Traceable {
    public URI getBaseUri();

    public UriBuilder getBaseUriBuilder();

    public URI getRequestUri();

    public UriBuilder getRequestUriBuilder();

    public URI getAbsolutePath();

    public UriBuilder getAbsolutePathBuilder();

    public String getPath();

    public String getPath(boolean var1);

    public List<PathSegment> getPathSegments();

    public List<PathSegment> getPathSegments(boolean var1);

    public MultivaluedMap<String, String> getQueryParameters();

    public MultivaluedMap<String, String> getQueryParameters(boolean var1);

    public String getHeaderValue(String var1);

    @Deprecated
    public MediaType getAcceptableMediaType(List<MediaType> var1);

    @Deprecated
    public List<MediaType> getAcceptableMediaTypes(List<QualitySourceMediaType> var1);

    public MultivaluedMap<String, String> getCookieNameValueMap();

    public <T> T getEntity(Class<T> var1) throws WebApplicationException;

    public <T> T getEntity(Class<T> var1, Type var2, Annotation[] var3) throws WebApplicationException;

    public Form getFormParameters();
}

