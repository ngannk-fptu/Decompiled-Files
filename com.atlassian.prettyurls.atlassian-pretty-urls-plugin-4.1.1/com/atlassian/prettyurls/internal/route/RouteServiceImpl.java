/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.prettyurls.api.route.RouteService
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSet
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSetKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.prettyurls.internal.route;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.prettyurls.api.route.RouteService;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSet;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSetKey;
import com.atlassian.prettyurls.internal.util.UrlUtils;
import com.atlassian.prettyurls.module.UrlRouteModuleDescriptor;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService
@Component
public class RouteServiceImpl
implements RouteService,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final PluginAccessor pluginAccessor;
    private final PluginEventManager pluginEventManager;
    private final Map<UrlRouteRuleSetKey, UrlRouteRuleSet> dynamicallyRegistered;
    private volatile PluginModuleTracker<Object, UrlRouteModuleDescriptor> moduleTracker;

    @Autowired
    public RouteServiceImpl(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginEventManager = pluginEventManager;
        this.dynamicallyRegistered = new ConcurrentHashMap<UrlRouteRuleSetKey, UrlRouteRuleSet>();
    }

    public void afterPropertiesSet() throws Exception {
        this.moduleTracker = new DefaultPluginModuleTracker(this.pluginAccessor, this.pluginEventManager, UrlRouteModuleDescriptor.class);
    }

    public void destroy() throws Exception {
        this.moduleTracker.close();
    }

    public Set<UrlRouteRuleSet> getRoutes() {
        LinkedHashSet<UrlRouteRuleSet> urlRouteRuleSets = new LinkedHashSet<UrlRouteRuleSet>();
        for (UrlRouteModuleDescriptor md : this.moduleTracker.getModuleDescriptors()) {
            try {
                UrlRouteRuleSet ruleSet = md.getRuleSet();
                urlRouteRuleSets.add(ruleSet);
            }
            catch (Exception exception) {}
        }
        urlRouteRuleSets.addAll(this.dynamicallyRegistered.values());
        return urlRouteRuleSets;
    }

    public Set<UrlRouteRuleSet> getRouteRuleSets(FilterLocation filterLocation, String requestURI) {
        LinkedHashSet<UrlRouteRuleSet> urlRouteRuleSets = new LinkedHashSet<UrlRouteRuleSet>();
        for (UrlRouteModuleDescriptor md : this.moduleTracker.getModuleDescriptors()) {
            UrlRouteRuleSet ruleSet = this.safelyUse(requestURI, filterLocation, md.getRuleSet());
            if (ruleSet == null) continue;
            urlRouteRuleSets.add(ruleSet);
        }
        for (UrlRouteRuleSet ruleSet : this.dynamicallyRegistered.values()) {
            if ((ruleSet = this.safelyUse(requestURI, filterLocation, ruleSet)) == null) continue;
            urlRouteRuleSets.add(ruleSet);
        }
        return urlRouteRuleSets;
    }

    private UrlRouteRuleSet safelyUse(String requestURI, FilterLocation filterLocation, UrlRouteRuleSet routeRuleSet) {
        if (routeRuleSet != null) {
            try {
                if (filterLocation.equals((Object)routeRuleSet.getFilterLocation()) && this.matchesTopLevelPath(routeRuleSet, requestURI)) {
                    return routeRuleSet;
                }
            }
            catch (RuntimeException e) {
                log.debug("Unable to use UrlRouteModuleDescriptor.  Ignoring...");
            }
        }
        return null;
    }

    private boolean matchesTopLevelPath(UrlRouteRuleSet urlRouteRuleSet, String requestURI) {
        for (String path : urlRouteRuleSet.getTopLevelPaths()) {
            if (!requestURI.startsWith(UrlUtils.startWithSlash(path))) continue;
            return true;
        }
        return false;
    }

    public void registerRoutes(UrlRouteRuleSet urlRouteRuleSet) {
        Objects.requireNonNull(urlRouteRuleSet);
        this.dynamicallyRegistered.put(Objects.requireNonNull(urlRouteRuleSet.getKey()), urlRouteRuleSet);
    }

    public UrlRouteRuleSet unregisterRoutes(UrlRouteRuleSetKey key) {
        return this.dynamicallyRegistered.remove(Objects.requireNonNull(key));
    }
}

