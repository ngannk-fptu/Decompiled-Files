/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.support.http;

import com.atlassian.plugin.webresource.ResourceUtils;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.support.http.ServingType;
import com.atlassian.plugin.webresource.models.LooselyTypedRequestExpander;
import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class Request {
    private final String path;
    private final Map<String, String> params;
    private final HttpServletRequest originalRequest;
    private final boolean isCacheable;
    private final Globals globals;
    private final String url;
    private boolean sourcemap = false;
    private String filetype = "";
    private String resourceName = "";
    private ServingType servingType = ServingType.UNKNOWN;
    private RawRequest requested;

    public Request(Globals globals, String path, Map<String, String> params) {
        this.globals = globals;
        this.originalRequest = null;
        this.params = params;
        this.path = path;
        this.isCacheable = ResourceUtils.canRequestedResourcesContentBeAssumedConstant(this.getParams());
        this.url = Router.buildUrl(path, params);
        this.setFiletype(path);
    }

    public Request(Globals globals, HttpServletRequest request, String encoding) {
        this.globals = globals;
        this.originalRequest = request;
        this.params = ResourceUtils.getQueryParameters(request);
        try {
            this.path = URLDecoder.decode(request.getRequestURI(), encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.isCacheable = ResourceUtils.canRequestedResourcesContentBeAssumedConstant(this.getParams());
        this.url = Router.buildUrl(this.path, this.params);
        this.setFiletype(this.path);
    }

    public static String getType(String path) {
        List<String> parsed = Router.parseWithRe(path, "\\.([^\\.]+)$");
        return parsed.size() > 0 ? parsed.get(0).toLowerCase() : null;
    }

    private void setFiletype(String path) {
        String type = Request.getType(path);
        if ("map".equals(type)) {
            this.sourcemap = true;
            List<String> matches = Router.parseWithRe(path, "\\.([^\\.]+).map$");
            if (matches.size() > 0) {
                this.filetype = matches.get(0);
            }
        } else {
            this.sourcemap = false;
            this.filetype = type;
        }
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public HttpServletRequest getOriginalRequest() {
        return this.originalRequest;
    }

    public String getType() {
        return this.filetype;
    }

    public boolean isSourceMap() {
        return this.sourcemap;
    }

    public String getContentType() {
        return this.globals.getConfig().getContentType(this.getPath());
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isCacheable() {
        return this.isCacheable;
    }

    public Globals getGlobals() {
        return this.globals;
    }

    public ServingType getServingType() {
        return this.servingType;
    }

    public void setServingType(ServingType servingType) {
        this.servingType = servingType;
    }

    public void setRequestedResources(Collection<String> included) {
        this.setRequestedResources(included, null);
    }

    public void setRequestedResources(Collection<String> included, Collection<String> excluded) {
        this.requested = new RawRequest();
        this.normalised(included).forEach(item -> this.requested.include((Requestable)item));
        this.normalised(excluded).forEach(item -> this.requested.exclude((Requestable)item));
    }

    public void setRequestedResources(RawRequest request) {
        this.requested = request;
    }

    private Stream<Requestable> normalised(Collection<String> items) {
        return (items != null ? items : Collections.emptyList()).stream().filter(StringUtils::isNotBlank).map(String::toLowerCase).map(key -> key.startsWith("_context") ? new WebResourceContextKey(key.substring("_context".length())) : new WebResourceKey((String)key));
    }

    public String getRelativeResourceName() {
        return this.resourceName;
    }

    public void setRelativeResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getRequestHash() {
        ArrayList<String> key = new ArrayList<String>();
        String sortedParamsAsString = this.getParams().entrySet().stream().map(e -> (String)e.getKey() + "=" + (String)e.getValue()).sorted().collect(Collectors.joining(","));
        LooselyTypedRequestExpander resources = new LooselyTypedRequestExpander(this.requested).normalise();
        key.add("min=" + this.globals.getConfig().isMinificationEnabled());
        key.add("urltype=" + (Object)((Object)this.getServingType()));
        key.add("included=" + String.join((CharSequence)",", resources.getIncluded()));
        key.add("excluded=" + String.join((CharSequence)",", resources.getExcluded()));
        key.add("resource=" + this.getRelativeResourceName());
        key.add("filetype=" + this.filetype);
        key.add("sourcemap=" + this.sourcemap);
        key.add("params={{" + sortedParamsAsString + "}}");
        return String.join((CharSequence)"|", key);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request)o;
        return this.getRequestHash().equals(request.getRequestHash());
    }

    public int hashCode() {
        return Objects.hash(this.getRequestHash());
    }
}

