/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.plugin.web.descriptors.ConditionElementParser
 *  com.atlassian.plugin.web.descriptors.ConditionElementParser$ConditionFactory
 *  com.atlassian.prettyurls.api.route.RoutePredicate
 *  com.atlassian.prettyurls.api.route.RoutePredicates
 *  com.atlassian.prettyurls.api.route.UrlRouteRule
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSet
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.prettyurls.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.plugin.web.descriptors.ConditionElementParser;
import com.atlassian.prettyurls.api.route.RoutePredicate;
import com.atlassian.prettyurls.api.route.RoutePredicates;
import com.atlassian.prettyurls.api.route.UrlRouteRule;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSet;
import com.atlassian.prettyurls.internal.rules.UrlRouteRuleSetParser;
import com.atlassian.prettyurls.internal.util.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlRouteModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    private UrlRouteRuleSet urlRouteRuleSet;
    private FilterLocation location;
    private final HostContainer hostContainer;
    private ConditionElementParser conditionElementParser;
    private Element element;
    private static final Logger log = LoggerFactory.getLogger(UrlRouteModuleDescriptor.class);
    private static final Condition ALWAYS_FALSE = new Condition(){

        public void init(Map<String, String> params) throws PluginParseException {
        }

        public boolean shouldDisplay(Map<String, Object> context) {
            return false;
        }
    };

    public UrlRouteModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, HostContainer hostContainer) {
        super(moduleFactory);
        this.hostContainer = hostContainer;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        this.element = element;
        super.init(plugin, element);
        this.location = FilterLocation.parse((String)element.attributeValue("location", "before-dispatch"));
        this.conditionElementParser = new ConditionElementParser(new ConditionElementParser.ConditionFactory(){

            public Condition create(String className, Plugin plugin) throws ConditionLoadingException {
                Optional<Object> optional = Optional.empty();
                try {
                    optional = Optional.ofNullable(UrlRouteModuleDescriptor.this.autowire(className, plugin));
                }
                catch (Exception | LinkageError e) {
                    LogUtils.logExceptionEvent(log, e, "Error while autowiring condition class.");
                }
                return optional.orElse(ALWAYS_FALSE);
            }
        });
    }

    public void enabled() {
        super.enabled();
        this.urlRouteRuleSet = new UrlRouteRuleSetParser().parse(this.getCompleteKey(), this.element, this.location, this.predicateMaker(this.plugin, this.conditionElementParser));
    }

    public UrlRouteRuleSet getRuleSet() {
        return this.urlRouteRuleSet;
    }

    public FilterLocation getLocation() {
        return this.location;
    }

    public Object getModule() {
        throw new UnsupportedOperationException("It doesn't work this way");
    }

    private UrlRouteRuleSetParser.PredicateMaker predicateMaker(final Plugin plugin, final ConditionElementParser conditionElementParser) {
        return new UrlRouteRuleSetParser.PredicateMaker(){

            private Optional<Condition> makeCondition(Element element) {
                Optional<Condition> optional = Optional.empty();
                try {
                    optional = Optional.ofNullable(conditionElementParser.makeConditions(plugin, element, 1));
                }
                catch (Exception | LinkageError e) {
                    LogUtils.logExceptionEvent(log, e, "Error while making condition.");
                }
                return optional;
            }

            private boolean runCondition(Optional<Condition> condition, Map<String, Object> contextMap) {
                Optional<Boolean> result = Optional.empty();
                try {
                    if (condition.isPresent()) {
                        result = Optional.ofNullable(condition.get().shouldDisplay(contextMap));
                    }
                }
                catch (Exception | LinkageError e) {
                    LogUtils.logExceptionEvent(log, e, "Error while running condition.");
                }
                return result.orElse(false);
            }

            @Override
            public RoutePredicate<UrlRouteRuleSet> makeRuleSetPredicate(Element routing) {
                Optional<Condition> condition = this.makeCondition(routing);
                if (!condition.isPresent()) {
                    return RoutePredicates.alwaysTrue();
                }
                return (httpServletRequest, routeRuleSet) -> {
                    HashMap<String, Object> contextMap = new HashMap<String, Object>();
                    contextMap.put("request", httpServletRequest);
                    contextMap.put("routing", routeRuleSet);
                    return this.runCondition(condition, contextMap);
                };
            }

            @Override
            public RoutePredicate<UrlRouteRule> makeRulePredicate(Element route) {
                Optional<Condition> condition = this.makeCondition(route);
                if (!condition.isPresent()) {
                    return RoutePredicates.alwaysTrue();
                }
                return (httpServletRequest, routeRule) -> {
                    HashMap<String, Object> contextMap = new HashMap<String, Object>();
                    contextMap.put("request", httpServletRequest);
                    contextMap.put("route", routeRule);
                    return this.runCondition(condition, contextMap);
                };
            }
        };
    }

    private <T> T autowire(String className, Plugin plugin) {
        try {
            Class conditionClass = plugin.loadClass(className, ((Object)((Object)this)).getClass());
            if (plugin instanceof ContainerManagedPlugin) {
                return (T)((ContainerManagedPlugin)plugin).getContainerAccessor().createBean(conditionClass);
            }
            return (T)this.hostContainer.create(conditionClass);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

