/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.TraceInformation;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;

public final class WebApplicationContext
implements UriRuleContext,
ExtendedUriInfo {
    public static final String HTTP_METHOD_MATCH_RESOURCE = "com.sun.jersey.MATCH_RESOURCE";
    private final WebApplicationImpl app;
    private final boolean isTraceEnabled;
    private ContainerRequest request;
    private ContainerResponse response;
    private List<ContainerResponseFilter> responseFilters;
    private MatchResult matchResult;
    private final LinkedList<Object> resources = new LinkedList();
    private final LinkedList<MatchResult> matchResults = new LinkedList();
    private final LinkedList<String> paths = new LinkedList();
    private final LinkedList<UriTemplate> templates = new LinkedList();
    private AbstractResourceMethod arm;
    private MultivaluedMapImpl encodedTemplateValues;
    private MultivaluedMapImpl decodedTemplateValues;

    public WebApplicationContext(WebApplicationImpl app, ContainerRequest request, ContainerResponse response) {
        this.app = app;
        this.isTraceEnabled = app.isTracingEnabled();
        this.request = request;
        this.response = response;
        this.responseFilters = Collections.EMPTY_LIST;
        if (this.isTracingEnabled()) {
            this.getProperties().put(TraceInformation.class.getName(), new TraceInformation(this));
        }
    }

    public WebApplicationContext createMatchResourceContext(URI u) {
        URI base = this.request.getBaseUri();
        if (u.isAbsolute()) {
            URI r = base.relativize(u);
            if (r == u) {
                throw new ContainerException("The URI " + u + " is not relative to the base URI " + base);
            }
        } else {
            u = UriBuilder.fromUri(base).path(u.getRawPath()).replaceQuery(u.getRawQuery()).fragment(u.getRawFragment()).build(new Object[0]);
        }
        ContainerRequest _request = new ContainerRequest(this.app, HTTP_METHOD_MATCH_RESOURCE, base, u, new InBoundHeaders(), new ByteArrayInputStream(new byte[0]));
        _request.setSecurityContext(this.request.getSecurityContext());
        ContainerResponse _response = new ContainerResponse(this.app, _request, null);
        return new WebApplicationContext(this.app, _request, _response);
    }

    public List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }

    @Override
    public HttpRequestContext getRequest() {
        return this.request;
    }

    @Override
    public HttpResponseContext getResponse() {
        return this.response;
    }

    @Override
    public ExtendedUriInfo getUriInfo() {
        return this;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.request.getProperties();
    }

    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }

    @Override
    public void trace(String message) {
        if (!this.isTracingEnabled()) {
            return;
        }
        this.request.trace(message);
    }

    @Override
    public MatchResult getMatchResult() {
        return this.matchResult;
    }

    @Override
    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    @Override
    public ContainerRequest getContainerRequest() {
        return this.request;
    }

    @Override
    public void setContainerRequest(ContainerRequest request) {
        this.request = request;
        this.response.setContainerRequest(request);
    }

    @Override
    public ContainerResponse getContainerResponse() {
        return this.response;
    }

    @Override
    public void setContainerResponse(ContainerResponse response) {
        this.response = response;
    }

    @Override
    public void pushContainerResponseFilters(List<ContainerResponseFilter> filters) {
        if (filters.isEmpty()) {
            return;
        }
        if (this.responseFilters == Collections.EMPTY_LIST) {
            this.responseFilters = new LinkedList<ContainerResponseFilter>();
        }
        for (ContainerResponseFilter f : filters) {
            this.responseFilters.add(0, f);
        }
    }

    @Override
    public Object getResource(Class resourceClass) {
        return this.app.getResourceComponentProvider(resourceClass).getInstance(this);
    }

    @Override
    public UriRules<UriRule> getRules(Class resourceClass) {
        return this.app.getUriRules(resourceClass);
    }

    @Override
    public void pushMatch(UriTemplate template, List<String> names) {
        this.matchResults.addFirst(this.matchResult);
        this.templates.addFirst(template);
        if (this.encodedTemplateValues == null) {
            this.encodedTemplateValues = new MultivaluedMapImpl();
        }
        int i = 1;
        for (String name : names) {
            String value = this.matchResult.group(i++);
            this.encodedTemplateValues.addFirst(name, value);
            if (this.decodedTemplateValues == null) continue;
            this.decodedTemplateValues.addFirst(UriComponent.decode(name, UriComponent.Type.PATH_SEGMENT), UriComponent.decode(value, UriComponent.Type.PATH));
        }
    }

    @Override
    public void pushResource(Object resource) {
        this.resources.addFirst(resource);
    }

    @Override
    public void pushMethod(AbstractResourceMethod arm) {
        this.arm = arm;
    }

    @Override
    public void pushRightHandPathLength(int rhpathlen) {
        String ep = this.request.getPath(false);
        this.paths.addFirst(ep.substring(0, ep.length() - rhpathlen));
    }

    @Override
    public URI getBaseUri() {
        return this.request.getBaseUri();
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        return this.request.getBaseUriBuilder();
    }

    @Override
    public URI getAbsolutePath() {
        return this.request.getAbsolutePath();
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return this.request.getAbsolutePathBuilder();
    }

    @Override
    public URI getRequestUri() {
        return this.request.getRequestUri();
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return this.request.getRequestUriBuilder();
    }

    @Override
    public String getPath() {
        return this.request.getPath(true);
    }

    @Override
    public String getPath(boolean decode) {
        return this.request.getPath(decode);
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return this.request.getPathSegments(true);
    }

    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        return this.request.getPathSegments(decode);
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.request.getQueryParameters(true);
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        return this.request.getQueryParameters(decode);
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return this.getPathParameters(true);
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        if (decode) {
            if (this.decodedTemplateValues != null) {
                return this.decodedTemplateValues;
            }
            this.decodedTemplateValues = new MultivaluedMapImpl();
            for (Map.Entry e : this.encodedTemplateValues.entrySet()) {
                ArrayList<String> l = new ArrayList<String>();
                for (String v : (List)e.getValue()) {
                    l.add(UriComponent.decode(v, UriComponent.Type.PATH));
                }
                this.decodedTemplateValues.put(UriComponent.decode((String)e.getKey(), UriComponent.Type.PATH_SEGMENT), l);
            }
            return this.decodedTemplateValues;
        }
        return this.encodedTemplateValues;
    }

    @Override
    public List<String> getMatchedURIs() {
        return this.getMatchedURIs(true);
    }

    @Override
    public List<String> getMatchedURIs(boolean decode) {
        AbstractList result;
        if (decode) {
            result = new ArrayList(this.paths.size());
            for (String path : this.paths) {
                result.add((String)UriComponent.decode(path, UriComponent.Type.PATH));
            }
        } else {
            result = this.paths;
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Object> getMatchedResources() {
        return this.resources;
    }

    @Override
    public AbstractResourceMethod getMatchedMethod() {
        return this.arm;
    }

    @Override
    public Throwable getMappedThrowable() {
        return this.response.getMappedThrowable();
    }

    @Override
    public List<MatchResult> getMatchedResults() {
        return this.matchResults;
    }

    @Override
    public List<UriTemplate> getMatchedTemplates() {
        return this.templates;
    }

    @Override
    public List<PathSegment> getPathSegments(String name) {
        return this.getPathSegments(name, true);
    }

    @Override
    public List<PathSegment> getPathSegments(String name, boolean decode) {
        int[] bounds = this.getPathParameterBounds(name);
        if (bounds != null) {
            String path = this.matchResults.getLast().group();
            int segmentsStart = 0;
            for (int x = 0; x < bounds[0]; ++x) {
                if (path.charAt(x) != '/') continue;
                ++segmentsStart;
            }
            int segmentsEnd = segmentsStart;
            for (int x = bounds[0]; x < bounds[1]; ++x) {
                if (path.charAt(x) != '/') continue;
                ++segmentsEnd;
            }
            return this.getPathSegments(decode).subList(segmentsStart - 1, segmentsEnd);
        }
        return Collections.emptyList();
    }

    private int[] getPathParameterBounds(String name) {
        Iterator iTemplate = this.templates.iterator();
        Iterator iMatchResult = this.matchResults.iterator();
        while (iTemplate.hasNext()) {
            MatchResult mr = (MatchResult)iMatchResult.next();
            int pIndex = this.getLastPathParameterIndex(name, (UriTemplate)iTemplate.next());
            if (pIndex == -1) continue;
            int pathLength = mr.group().length();
            int segmentIndex = mr.end(pIndex + 1);
            int groupLength = segmentIndex - mr.start(pIndex + 1);
            while (iMatchResult.hasNext()) {
                mr = (MatchResult)iMatchResult.next();
                segmentIndex += mr.group().length() - pathLength;
                pathLength = mr.group().length();
            }
            int[] bounds = new int[]{segmentIndex - groupLength, segmentIndex};
            return bounds;
        }
        return null;
    }

    private int getLastPathParameterIndex(String name, UriTemplate t) {
        int i = 0;
        int pIndex = -1;
        for (String parameterName : t.getTemplateVariables()) {
            if (parameterName.equals(name)) {
                pIndex = i;
            }
            ++i;
        }
        return pIndex;
    }
}

