/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.server.impl.model.RulesMap;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.PathTemplate;
import com.sun.jersey.server.impl.uri.rules.ResourceClassRule;
import com.sun.jersey.server.impl.uri.rules.ResourceObjectRule;
import com.sun.jersey.server.impl.uri.rules.RightHandPathRule;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.server.impl.wadl.WadlResource;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.uri.rules.UriRule;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RootResourceUriRules {
    private static final Logger LOGGER = Logger.getLogger(RootResourceUriRules.class.getName());
    private final RulesMap<UriRule> rules = new RulesMap();
    private final WebApplicationImpl wa;
    private final WadlFactory wadlFactory;
    private final ResourceConfig resourceConfig;
    private final InjectableProviderFactory injectableFactory;

    public RootResourceUriRules(WebApplicationImpl wa, ResourceConfig resourceConfig, WadlFactory wadlFactory, InjectableProviderFactory injectableFactory) {
        AbstractResource ar;
        this.wa = wa;
        this.resourceConfig = resourceConfig;
        this.wadlFactory = wadlFactory;
        this.injectableFactory = injectableFactory;
        Set<Class<?>> classes = resourceConfig.getRootResourceClasses();
        Set<Object> singletons = resourceConfig.getRootResourceSingletons();
        if (classes.isEmpty() && singletons.isEmpty() && resourceConfig.getExplicitRootResources().isEmpty()) {
            LOGGER.severe(ImplMessages.NO_ROOT_RES_IN_RES_CFG());
            throw new ContainerException(ImplMessages.NO_ROOT_RES_IN_RES_CFG());
        }
        Set<AbstractResource> rootResourcesSet = wa.getAbstractRootResources();
        Map<String, AbstractResource> explicitRootResources = wa.getExplicitAbstractRootResources();
        this.initWadl(rootResourcesSet);
        for (Object object : singletons) {
            ar = wa.getAbstractResource(object);
            wa.initiateResource(ar, object);
            ComponentInjector ci = new ComponentInjector(injectableFactory, object.getClass());
            ci.inject(object);
            this.addRule(ar.getPath().getValue(), object);
        }
        for (Class clazz : classes) {
            ar = wa.getAbstractResource(clazz);
            wa.initiateResource(ar);
            this.addRule(ar.getPath().getValue(), clazz);
        }
        for (Map.Entry entry : resourceConfig.getExplicitRootResources().entrySet()) {
            String path = (String)entry.getKey();
            Object o = entry.getValue();
            if (o instanceof Class) {
                Class c = (Class)o;
                wa.initiateResource(explicitRootResources.get(path));
                this.addRule(path, c);
                continue;
            }
            wa.initiateResource(explicitRootResources.get(path), o);
            ComponentInjector ci = new ComponentInjector(injectableFactory, o.getClass());
            ci.inject(o);
            this.addRule(path, o);
        }
        this.rules.processConflicts(new RulesMap.ConflictClosure(){

            @Override
            public void onConflict(PathPattern p1, PathPattern p2) {
                Errors.error(String.format("Conflicting URI templates. The URI templates %s and %s for root resource classes transform to the same regular expression %s", p1.getTemplate().getTemplate(), p2.getTemplate().getTemplate(), p1));
            }
        });
        this.initWadlResource();
    }

    private void initWadl(Set<AbstractResource> rootResources) {
        if (!this.wadlFactory.isSupported()) {
            return;
        }
        this.wadlFactory.init(this.injectableFactory, rootResources);
    }

    private void initWadlResource() {
        if (!this.wadlFactory.isSupported()) {
            return;
        }
        PathPattern p = new PathPattern(new PathTemplate("application.wadl"));
        if (this.rules.containsKey(p)) {
            return;
        }
        this.wa.initiateResource(WadlResource.class);
        this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceClassRule(p.getTemplate(), WadlResource.class)));
    }

    private void addRule(String path, Class c) {
        PathPattern p = this.getPattern(path, c);
        if (this.isPatternValid(p, c)) {
            this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceClassRule(p.getTemplate(), c)));
        }
    }

    private void addRule(String path, Object o) {
        PathPattern p = this.getPattern(path, o.getClass());
        if (this.isPatternValid(p, o.getClass())) {
            this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceObjectRule(p.getTemplate(), o)));
        }
    }

    private PathPattern getPattern(String path, Class c) {
        PathPattern p = null;
        try {
            p = new PathPattern(new PathTemplate(path));
        }
        catch (IllegalArgumentException ex) {
            Errors.error("Illegal URI template for root resource class " + c.getName() + ": " + ex.getMessage());
        }
        return p;
    }

    private boolean isPatternValid(PathPattern p, Class c) {
        if (p == null) {
            return false;
        }
        PathPattern conflict = this.rules.hasConflict(p);
        if (conflict != null) {
            Errors.error(String.format("Conflicting URI templates. The URI template %s for root resource class %s and the URI template %s transform to the same regular expression %s", p.getTemplate().getTemplate(), c.getName(), conflict.getTemplate().getTemplate(), p));
            return false;
        }
        return true;
    }

    public RulesMap<UriRule> getRules() {
        return this.rules;
    }
}

