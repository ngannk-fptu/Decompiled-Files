/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.Responses;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.impl.template.ViewResourceMethod;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.SubjectSecurityContext;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

public final class HttpMethodRule
implements UriRule {
    public static final String CONTENT_TYPE_PROPERTY = "com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type";
    private final Map<String, ResourceMethodListPair> map = new HashMap<String, ResourceMethodListPair>();
    private final String allow;
    private final boolean isSubResource;
    private final DispatchingListener dispatchingListener;

    public HttpMethodRule(Map<String, List<ResourceMethod>> methods, DispatchingListener dispatchingListener) {
        this(methods, false, dispatchingListener);
    }

    public HttpMethodRule(Map<String, List<ResourceMethod>> methods, boolean isSubResource, DispatchingListener dispatchingListener) {
        for (Map.Entry<String, List<ResourceMethod>> e : methods.entrySet()) {
            this.map.put(e.getKey(), new ResourceMethodListPair(e.getValue()));
        }
        this.isSubResource = isSubResource;
        this.allow = this.getAllow(methods);
        this.dispatchingListener = dispatchingListener;
    }

    private String getAllow(Map<String, List<ResourceMethod>> methods) {
        StringBuilder s = new StringBuilder();
        for (String method : methods.keySet()) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(method);
        }
        return s.toString();
    }

    @Override
    public boolean accept(CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(HttpMethodRule.class.getSimpleName(), path, resource);
        if (path.length() > 0) {
            return false;
        }
        HttpRequestContext request = context.getRequest();
        if (request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
            return true;
        }
        if (context.isTracingEnabled()) {
            String currentPath = context.getUriInfo().getMatchedURIs().get(0);
            if (this.isSubResource) {
                String prevPath = context.getUriInfo().getMatchedURIs().get(1);
                context.trace(String.format("accept sub-resource methods: \"%s\" : \"%s\", %s -> %s", prevPath, currentPath.substring(prevPath.length()), context.getRequest().getMethod(), ReflectionHelper.objectToString(resource)));
            } else {
                context.trace(String.format("accept resource methods: \"%s\", %s -> %s", currentPath, context.getRequest().getMethod(), ReflectionHelper.objectToString(resource)));
            }
        }
        HttpResponseContext response = context.getResponse();
        ResourceMethodListPair methods = this.map.get(request.getMethod());
        if (methods == null) {
            response.setResponse(Responses.methodNotAllowed().header("Allow", this.allow).build());
            return false;
        }
        List<MediaType> accept = HttpMethodRule.getSpecificAcceptableMediaTypes(request.getAcceptableMediaTypes(), methods.priorityMediaTypes);
        Matcher m = new Matcher();
        MatchStatus s = m.match(methods, request.getMediaType(), accept);
        if (s == MatchStatus.MATCH) {
            final ResourceMethod method = m.rmSelected;
            if (method instanceof ViewResourceMethod) {
                if (!m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                    response.getHttpHeaders().putSingle("Content-Type", m.mSelected);
                }
                return false;
            }
            if (this.isSubResource) {
                context.pushResource(resource);
                context.pushMatch(method.getTemplate(), method.getTemplate().getTemplateVariables());
            }
            if (context.isTracingEnabled()) {
                if (this.isSubResource) {
                    context.trace(String.format("matched sub-resource method: @Path(\"%s\") %s", method.getTemplate(), method.getDispatcher()));
                } else {
                    context.trace(String.format("matched resource method: %s", method.getDispatcher()));
                }
            }
            context.pushContainerResponseFilters(method.getResponseFilters());
            ContainerRequest containerRequest = context.getContainerRequest();
            if (!method.getRequestFilters().isEmpty()) {
                for (ContainerRequestFilter f : method.getRequestFilters()) {
                    containerRequest = f.filter(containerRequest);
                    context.setContainerRequest(containerRequest);
                }
            }
            context.pushMethod(method.getAbstractResourceMethod());
            try {
                this.dispatchingListener.onResourceMethod(Thread.currentThread().getId(), method.getAbstractResourceMethod());
                SecurityContext sc = containerRequest.getSecurityContext();
                if (sc instanceof SubjectSecurityContext) {
                    ((SubjectSecurityContext)sc).doAsSubject(new PrivilegedAction(){

                        public Object run() {
                            method.getDispatcher().dispatch(resource, context);
                            return null;
                        }
                    });
                } else {
                    method.getDispatcher().dispatch(resource, context);
                }
            }
            catch (RuntimeException e) {
                if (m.rmSelected.isProducesDeclared() && !m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                    context.getProperties().put(CONTENT_TYPE_PROPERTY, m.mSelected);
                }
                throw e;
            }
            Object contentType = response.getHttpHeaders().getFirst("Content-Type");
            if (contentType == null && m.rmSelected.isProducesDeclared() && !m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                response.getHttpHeaders().putSingle("Content-Type", m.mSelected);
            }
            return true;
        }
        if (s == MatchStatus.NO_MATCH_FOR_CONSUME) {
            response.setResponse(Responses.unsupportedMediaType().build());
            return false;
        }
        if (s == MatchStatus.NO_MATCH_FOR_PRODUCE) {
            response.setResponse(Responses.notAcceptable().build());
            return false;
        }
        return true;
    }

    public static List<MediaType> getSpecificAcceptableMediaTypes(List<MediaType> acceptableMediaType, List<? extends MediaType> priorityMediaTypes) {
        if (priorityMediaTypes != null) {
            for (MediaType mediaType : priorityMediaTypes) {
                for (MediaType amt : acceptableMediaType) {
                    if (!amt.isCompatible(mediaType)) continue;
                    return Collections.singletonList(MediaTypes.mostSpecific(amt, mediaType));
                }
            }
        }
        return acceptableMediaType;
    }

    private static class Matcher
    extends LinkedList<ResourceMethod> {
        private MediaType mSelected = null;
        private ResourceMethod rmSelected = null;

        private Matcher() {
        }

        private MatchStatus match(ResourceMethodListPair methods, MediaType contentType, List<MediaType> acceptableMediaTypes) {
            Matcher selected;
            if (contentType != null) {
                for (ResourceMethod method : methods.normal) {
                    if (!method.consumes(contentType)) continue;
                    this.add(method);
                }
                if (this.isEmpty()) {
                    return MatchStatus.NO_MATCH_FOR_CONSUME;
                }
                selected = this;
            } else {
                selected = methods.wildPriority;
            }
            for (MediaType amt : acceptableMediaTypes) {
                for (ResourceMethod rm : selected) {
                    for (MediaType mediaType : rm.getProduces()) {
                        if (!mediaType.isCompatible(amt)) continue;
                        this.mSelected = MediaTypes.mostSpecific(mediaType, amt);
                        this.rmSelected = rm;
                        return MatchStatus.MATCH;
                    }
                }
            }
            return MatchStatus.NO_MATCH_FOR_PRODUCE;
        }
    }

    private static enum MatchStatus {
        MATCH,
        NO_MATCH_FOR_CONSUME,
        NO_MATCH_FOR_PRODUCE;

    }

    private static final class ResourceMethodListPair {
        final List<ResourceMethod> normal;
        final List<ResourceMethod> wildPriority;
        final List<QualitySourceMediaType> priorityMediaTypes;

        ResourceMethodListPair(List<ResourceMethod> normal) {
            this.normal = normal;
            if (this.correctOrder(normal)) {
                this.wildPriority = normal;
            } else {
                this.wildPriority = new ArrayList<ResourceMethod>(normal.size());
                int i = 0;
                for (ResourceMethod method : normal) {
                    if (method.consumesWild()) {
                        this.wildPriority.add(i++, method);
                        continue;
                    }
                    this.wildPriority.add(method);
                }
            }
            LinkedList<QualitySourceMediaType> pmts = new LinkedList<QualitySourceMediaType>();
            for (ResourceMethod m : normal) {
                for (MediaType mediaType : m.getProduces()) {
                    pmts.add(this.get(mediaType));
                }
            }
            Collections.sort(pmts, MediaTypes.QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR);
            this.priorityMediaTypes = this.retain(pmts) ? pmts : null;
        }

        QualitySourceMediaType get(MediaType mt) {
            if (mt instanceof QualitySourceMediaType) {
                return (QualitySourceMediaType)mt;
            }
            return new QualitySourceMediaType(mt);
        }

        boolean retain(List<QualitySourceMediaType> pmts) {
            for (QualitySourceMediaType mt : pmts) {
                if (mt.getQualitySource() == 1000) continue;
                return true;
            }
            return false;
        }

        boolean correctOrder(List<ResourceMethod> normal) {
            boolean consumesNonWild = false;
            for (ResourceMethod method : normal) {
                if (method.consumesWild()) {
                    if (!consumesNonWild) continue;
                    return false;
                }
                consumesNonWild = true;
            }
            return true;
        }
    }
}

