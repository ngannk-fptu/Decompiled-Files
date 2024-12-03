/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.List;
import java.util.regex.MatchResult;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

public interface ExtendedUriInfo
extends UriInfo {
    public AbstractResourceMethod getMatchedMethod();

    public Throwable getMappedThrowable();

    public List<MatchResult> getMatchedResults();

    public List<UriTemplate> getMatchedTemplates();

    public List<PathSegment> getPathSegments(String var1);

    public List<PathSegment> getPathSegments(String var1, boolean var2);
}

