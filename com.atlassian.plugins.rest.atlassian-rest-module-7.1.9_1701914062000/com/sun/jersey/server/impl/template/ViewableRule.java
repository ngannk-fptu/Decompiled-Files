/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.template;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.uri.rules.HttpMethodRule;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

public class ViewableRule
implements UriRule {
    private final List<QualitySourceMediaType> priorityMediaTypes;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    @Context
    TemplateContext tc;

    public ViewableRule(List<QualitySourceMediaType> priorityMediaTypes, List<ContainerRequestFilter> requestFilters, List<ContainerResponseFilter> responseFilters) {
        this.priorityMediaTypes = priorityMediaTypes;
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }

    @Override
    public final boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(ViewableRule.class.getSimpleName(), path, resource);
        HttpRequestContext request = context.getRequest();
        if (request.getMethod().equals("GET") || request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
            String templatePath = path.length() > 0 ? context.getMatchResult().group(1) : "";
            Viewable v = new Viewable(templatePath, resource);
            ResolvedViewable rv = this.tc.resolveViewable(v);
            if (rv == null) {
                return false;
            }
            if (request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
                return true;
            }
            if (context.isTracingEnabled()) {
                context.trace(String.format("accept implicit view: \"%s\" -> %s, %s", templatePath, ReflectionHelper.objectToString(resource), rv.getTemplateName()));
            }
            context.pushContainerResponseFilters(this.responseFilters);
            if (!this.requestFilters.isEmpty()) {
                ContainerRequest containerRequest = context.getContainerRequest();
                for (ContainerRequestFilter f : this.requestFilters) {
                    containerRequest = f.filter(containerRequest);
                    context.setContainerRequest(containerRequest);
                }
            }
            HttpResponseContext response = context.getResponse();
            response.setStatus(200);
            response.setEntity(rv);
            if (!response.getHttpHeaders().containsKey("Content-Type")) {
                MediaType contentType = this.getContentType(request, response);
                response.getHttpHeaders().putSingle("Content-Type", contentType);
            }
            return true;
        }
        return false;
    }

    private MediaType getContentType(HttpRequestContext request, HttpResponseContext response) {
        MediaType contentType;
        List<MediaType> accept;
        List<MediaType> list = accept = this.priorityMediaTypes == null ? request.getAcceptableMediaTypes() : HttpMethodRule.getSpecificAcceptableMediaTypes(request.getAcceptableMediaTypes(), this.priorityMediaTypes);
        if (!(accept.isEmpty() || (contentType = accept.get(0)).isWildcardType() || contentType.isWildcardSubtype())) {
            return contentType;
        }
        return null;
    }
}

