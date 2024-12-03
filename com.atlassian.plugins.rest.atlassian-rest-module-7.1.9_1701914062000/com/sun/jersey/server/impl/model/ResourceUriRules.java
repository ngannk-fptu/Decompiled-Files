/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractImplicitViewMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.uri.UriPattern;
import com.sun.jersey.api.view.ImplicitProduces;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.impl.model.ResourceMethodMap;
import com.sun.jersey.server.impl.model.RulesMap;
import com.sun.jersey.server.impl.model.method.ResourceHeadWrapperMethod;
import com.sun.jersey.server.impl.model.method.ResourceHttpMethod;
import com.sun.jersey.server.impl.model.method.ResourceHttpOptionsMethod;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.impl.template.ViewResourceMethod;
import com.sun.jersey.server.impl.template.ViewableRule;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.PathTemplate;
import com.sun.jersey.server.impl.uri.rules.CombiningMatchingPatterns;
import com.sun.jersey.server.impl.uri.rules.HttpMethodRule;
import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import com.sun.jersey.server.impl.uri.rules.RightHandPathRule;
import com.sun.jersey.server.impl.uri.rules.SequentialMatchingPatterns;
import com.sun.jersey.server.impl.uri.rules.SubLocatorRule;
import com.sun.jersey.server.impl.uri.rules.TerminatingRule;
import com.sun.jersey.server.impl.uri.rules.UriRulesFactory;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResourceUriRules {
    private final UriRules<UriRule> rules;
    private final ResourceConfig resourceConfig;
    private final ResourceMethodDispatchProvider dp;
    private final ServerInjectableProviderContext injectableContext;
    private final FilterFactory ff;
    private final WadlFactory wadlFactory;
    private final DispatchingListener dispatchingListener;

    public ResourceUriRules(ResourceConfig resourceConfig, ResourceMethodDispatchProvider dp, ServerInjectableProviderContext injectableContext, FilterFactory ff, WadlFactory wadlFactory, DispatchingListener dispatchingListener, final AbstractResource resource) {
        ImplicitProduces ip;
        this.resourceConfig = resourceConfig;
        this.dp = dp;
        this.injectableContext = injectableContext;
        this.ff = ff;
        this.wadlFactory = wadlFactory;
        this.dispatchingListener = dispatchingListener;
        boolean implicitViewables = resourceConfig.getFeature("com.sun.jersey.config.feature.ImplicitViewables");
        List<QualitySourceMediaType> implictProduces = null;
        if (implicitViewables && (ip = resource.getAnnotation(ImplicitProduces.class)) != null && ip.value() != null && ip.value().length > 0) {
            implictProduces = MediaTypes.createQualitySourceMediaTypes(ip.value());
        }
        RulesMap<UriRule> rulesMap = new RulesMap<UriRule>();
        this.processSubResourceLocators(resource, rulesMap);
        this.processSubResourceMethods(resource, implictProduces, rulesMap);
        this.processMethods(resource, implictProduces, rulesMap);
        rulesMap.processConflicts(new RulesMap.ConflictClosure(){

            @Override
            public void onConflict(PathPattern p1, PathPattern p2) {
                Errors.error(String.format("Conflicting URI templates. The URI templates %s and %s for sub-resource methods and/or sub-resource locators of resource class %s transform to the same regular expression %s", p1.getTemplate().getTemplate(), p2.getTemplate().getTemplate(), resource.getResourceClass().getName(), p1));
            }
        });
        UriRules<UriRule> atomicRules = UriRulesFactory.create(rulesMap);
        ArrayList patterns = new ArrayList();
        if (resourceConfig.getFeature("com.sun.jersey.config.feature.ImplicitViewables")) {
            AbstractImplicitViewMethod method = new AbstractImplicitViewMethod(resource);
            List<ResourceFilter> resourceFilters = ff.getResourceFilters(method);
            ViewableRule r = new ViewableRule(implictProduces, FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters));
            ComponentInjector<ViewableRule> ci = new ComponentInjector<ViewableRule>(injectableContext, ViewableRule.class);
            ci.inject(r);
            patterns.add(new PatternRulePair<ViewableRule>(new UriPattern("/([^/]+)"), r));
            patterns.add(new PatternRulePair<ViewableRule>(UriPattern.EMPTY, r));
        }
        patterns.add(new PatternRulePair<TerminatingRule>(new UriPattern(".*"), new TerminatingRule()));
        patterns.add(new PatternRulePair<TerminatingRule>(UriPattern.EMPTY, new TerminatingRule()));
        SequentialMatchingPatterns sequentialRules = new SequentialMatchingPatterns(patterns);
        CombiningMatchingPatterns<UriRule> combiningRules = new CombiningMatchingPatterns<UriRule>(Arrays.asList(atomicRules, sequentialRules));
        this.rules = combiningRules;
    }

    public UriRules<UriRule> getRules() {
        return this.rules;
    }

    private void processSubResourceLocators(AbstractResource resource, RulesMap<UriRule> rulesMap) {
        for (AbstractSubResourceLocator locator : resource.getSubResourceLocators()) {
            PathPattern p = null;
            try {
                p = new PathPattern(new PathTemplate(locator.getPath().getValue()));
            }
            catch (IllegalArgumentException ex) {
                Errors.error(String.format("Illegal URI template for sub-resource locator %s: %s", locator.getMethod(), ex.getMessage()));
                continue;
            }
            PathPattern conflict = rulesMap.hasConflict(p);
            if (conflict != null) {
                Errors.error(String.format("Conflicting URI templates. The URI template %s for sub-resource locator %s and the URI template %s transform to the same regular expression %s", p.getTemplate().getTemplate(), locator.getMethod(), conflict.getTemplate().getTemplate(), p));
                continue;
            }
            List<Injectable> is = this.injectableContext.getInjectable((AccessibleObject)locator.getMethod(), locator.getParameters(), ComponentScope.PerRequest);
            if (is.contains(null)) {
                for (int i = 0; i < is.size(); ++i) {
                    if (is.get(i) != null) continue;
                    Errors.missingDependency(locator.getMethod(), i);
                }
            }
            List<ResourceFilter> resourceFilters = this.ff.getResourceFilters(locator);
            SubLocatorRule r = new SubLocatorRule(p.getTemplate(), is, FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters), this.dispatchingListener, locator);
            rulesMap.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), r));
        }
    }

    private void processSubResourceMethods(AbstractResource resource, List<QualitySourceMediaType> implictProduces, RulesMap<UriRule> rulesMap) {
        PathPattern p;
        HashMap<PathPattern, ResourceMethodMap> patternMethodMap = new HashMap<PathPattern, ResourceMethodMap>();
        for (AbstractSubResourceMethod abstractSubResourceMethod : resource.getSubResourceMethods()) {
            try {
                p = new PathPattern(new PathTemplate(abstractSubResourceMethod.getPath().getValue()), "(/)?");
            }
            catch (IllegalArgumentException ex) {
                Errors.error(String.format("Illegal URI template for sub-resource method %s: %s", abstractSubResourceMethod.getMethod(), ex.getMessage()));
                continue;
            }
            ResourceHttpMethod rm = new ResourceHttpMethod(this.dp, this.ff, p.getTemplate(), abstractSubResourceMethod);
            ResourceMethodMap rmm = (ResourceMethodMap)patternMethodMap.get(p);
            if (rmm == null) {
                rmm = new ResourceMethodMap();
                patternMethodMap.put(p, rmm);
            }
            if (this.isValidResourceMethod(rm, rmm)) {
                rmm.put(rm);
            }
            rmm.put(rm);
        }
        for (Map.Entry entry : patternMethodMap.entrySet()) {
            this.addImplicitMethod(implictProduces, (ResourceMethodMap)entry.getValue());
            p = (PathPattern)entry.getKey();
            ResourceMethodMap rmm = (ResourceMethodMap)entry.getValue();
            this.processHead(rmm);
            this.processOptions(rmm, resource, p);
            rmm.sort();
            rulesMap.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new HttpMethodRule(rmm, true, this.dispatchingListener)));
        }
    }

    private void processMethods(AbstractResource resource, List<QualitySourceMediaType> implictProduces, RulesMap<UriRule> rulesMap) {
        ResourceMethodMap rmm = new ResourceMethodMap();
        for (AbstractResourceMethod resourceMethod : resource.getResourceMethods()) {
            ResourceHttpMethod rm = new ResourceHttpMethod(this.dp, this.ff, resourceMethod);
            if (!this.isValidResourceMethod(rm, rmm)) continue;
            rmm.put(rm);
        }
        this.addImplicitMethod(implictProduces, rmm);
        this.processHead(rmm);
        this.processOptions(rmm, resource, null);
        rmm.sort();
        if (!rmm.isEmpty()) {
            rulesMap.put(PathPattern.EMPTY_PATH, new HttpMethodRule(rmm, this.dispatchingListener));
        }
    }

    private void addImplicitMethod(List<QualitySourceMediaType> implictProduces, ResourceMethodMap rmm) {
        List getList;
        if (implictProduces != null && (getList = (List)rmm.get("GET")) != null && !getList.isEmpty()) {
            rmm.put(new ViewResourceMethod(implictProduces));
        }
    }

    private boolean isValidResourceMethod(ResourceMethod rm, ResourceMethodMap rmm) {
        List rml = (List)rmm.get(rm.getHttpMethod());
        if (rml != null) {
            boolean conflict = false;
            ResourceMethod erm = null;
            for (int i = 0; i < rml.size() && !conflict; ++i) {
                erm = (ResourceMethod)rml.get(i);
                conflict = MediaTypes.intersects(rm.getConsumes(), erm.getConsumes()) && MediaTypes.intersects(rm.getProduces(), erm.getProduces());
            }
            if (conflict) {
                if (rm.getAbstractResourceMethod().hasEntity()) {
                    Errors.error(String.format("Consuming media type conflict. The resource methods %s and %s can consume the same media type", rm.getAbstractResourceMethod().getMethod(), erm.getAbstractResourceMethod().getMethod()));
                } else {
                    Errors.error(String.format("Producing media type conflict. The resource methods %s and %s can produce the same media type", rm.getAbstractResourceMethod().getMethod(), erm.getAbstractResourceMethod().getMethod()));
                }
            }
            if (conflict) {
                return false;
            }
        }
        return true;
    }

    private void processHead(ResourceMethodMap methodMap) {
        List getList = (List)methodMap.get("GET");
        if (getList == null || getList.isEmpty()) {
            return;
        }
        List<ResourceMethod> headList = (ArrayList<ResourceMethod>)methodMap.get("HEAD");
        if (headList == null) {
            headList = new ArrayList<ResourceMethod>();
        }
        for (ResourceMethod getMethod : getList) {
            if (this.containsMediaOfMethod(headList, getMethod)) continue;
            ResourceHeadWrapperMethod headMethod = new ResourceHeadWrapperMethod(getMethod);
            methodMap.put(headMethod);
            headList = (List)methodMap.get("HEAD");
        }
    }

    private boolean containsMediaOfMethod(List<ResourceMethod> methods, ResourceMethod method) {
        for (ResourceMethod m : methods) {
            if (!method.mediaEquals(m)) continue;
            return true;
        }
        return false;
    }

    private void processOptions(ResourceMethodMap methodMap, AbstractResource resource, PathPattern p) {
        List l = (List)methodMap.get("OPTIONS");
        if (l != null) {
            return;
        }
        ResourceMethod optionsMethod = this.wadlFactory.createWadlOptionsMethod(methodMap, resource, p);
        if (optionsMethod == null) {
            optionsMethod = new ResourceHttpOptionsMethod(methodMap);
        }
        methodMap.put(optionsMethod);
    }
}

