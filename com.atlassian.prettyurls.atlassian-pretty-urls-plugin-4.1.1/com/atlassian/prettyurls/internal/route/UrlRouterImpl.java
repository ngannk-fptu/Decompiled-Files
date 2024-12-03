/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  com.atlassian.prettyurls.api.route.RoutePredicate
 *  com.atlassian.prettyurls.api.route.RouteService
 *  com.atlassian.prettyurls.api.route.UrlRouteRule
 *  com.atlassian.prettyurls.api.route.UrlRouteRule$ParameterMode
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSet
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.prettyurls.internal.route;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.prettyurls.api.route.RoutePredicate;
import com.atlassian.prettyurls.api.route.RouteService;
import com.atlassian.prettyurls.api.route.UrlRouteRule;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSet;
import com.atlassian.prettyurls.internal.route.UrlMatcher;
import com.atlassian.prettyurls.internal.route.UrlRouter;
import com.atlassian.prettyurls.internal.util.LogUtils;
import com.atlassian.prettyurls.internal.util.UrlUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UrlRouterImpl
implements UrlRouter {
    private static final Logger log = LoggerFactory.getLogger(UrlRouterImpl.class);
    private final RouteService routeService;

    @Autowired
    public UrlRouterImpl(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public Result route(HttpServletRequest httpRequest, FilterLocation filterLocation) {
        String requestURI;
        try {
            requestURI = this.makeRequestURI(httpRequest);
        }
        catch (URISyntaxException e) {
            log.debug("Unable to route {}: {}", (Object)httpRequest.getRequestURI(), (Object)e.getMessage());
            return new Result(null, false);
        }
        Set<Object> urlRouteRuleSets = this.getRouteRuleSets(filterLocation, requestURI);
        urlRouteRuleSets = urlRouteRuleSets.stream().filter(ruleSet -> this.ruleSetEnabled(httpRequest, (UrlRouteRuleSet)ruleSet)).collect(Collectors.toSet());
        UrlMatcher.Strategy matchStrategy = urlRouteRuleSets.size() > 1 ? UrlMatcher.Strategy.JAX_RS_MATCHING : UrlMatcher.Strategy.LIST_ORDER_MATCHING;
        List<UrlRouteRule> urlRouteRules = urlRouteRuleSets.stream().flatMap(ruleSet -> ruleSet.getUrlRouteRules().stream()).filter(rule -> this.ruleEnabled(httpRequest, (UrlRouteRule)rule)).filter(rule -> this.httpVerbsMatch(httpRequest, (UrlRouteRule)rule)).collect(Collectors.toList());
        UrlMatcher.Result matchResult = new UrlMatcher().getMatchingRule(requestURI, urlRouteRules, matchStrategy);
        if (matchResult.matches()) {
            String toURI = this.buildToURI((UrlRouteRule)matchResult.getMatchingRule().get(), httpRequest, matchResult.getParsedVariableValues());
            return new Result(toURI, true);
        }
        return new Result(null, false);
    }

    private boolean httpVerbsMatch(HttpServletRequest httpRequest, UrlRouteRule urlRouteRule) {
        List httpVerbs = urlRouteRule.getHttpVerbs();
        if (httpVerbs.isEmpty()) {
            return true;
        }
        String method = httpRequest.getMethod();
        method = method == null ? "" : method.toUpperCase();
        for (String httpVerb : httpVerbs) {
            if (!method.equals(httpVerb)) continue;
            return true;
        }
        return false;
    }

    private String buildToURI(UrlRouteRule urlRouteRule, HttpServletRequest request, Map<String, String> parsedFromValues) {
        Map<String, String> parametersToPass;
        UrlRouteRule.ParameterMode parameterMode = urlRouteRule.getParameterMode();
        String toURI = urlRouteRule.getToUriGenerator().generate(request, parsedFromValues);
        switch (parameterMode) {
            case PASS_ALL: {
                parametersToPass = parsedFromValues;
                break;
            }
            case PASS_UNMAPPED: {
                HashMap<String, String> unmappedFromValues = new HashMap<String, String>(parsedFromValues);
                if (urlRouteRule.getTo() != null) {
                    for (String variable : urlRouteRule.getTo().getTemplateVariables()) {
                        unmappedFromValues.remove(variable);
                    }
                }
                parametersToPass = unmappedFromValues;
                break;
            }
            case PASS_NONE: {
                parametersToPass = Collections.emptyMap();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized " + UrlRouteRule.ParameterMode.class.getSimpleName() + " value: " + parameterMode);
            }
        }
        UriBuilder uriBuilder = UriBuilder.fromUri((String)UrlUtils.startWithSlash(toURI));
        for (Map.Entry<String, String> entry : parametersToPass.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), new Object[]{entry.getValue()});
        }
        toURI = uriBuilder.build(new Object[0]).toString();
        return toURI;
    }

    private boolean ruleSetEnabled(HttpServletRequest httpRequest, UrlRouteRuleSet urlRouteRuleSet) {
        RoutePredicate predicate = urlRouteRuleSet.getPredicate();
        return this.runPredicateSafely(httpRequest, urlRouteRuleSet, predicate);
    }

    private boolean ruleEnabled(HttpServletRequest httpRequest, UrlRouteRule urlRouteRule) {
        RoutePredicate predicate = urlRouteRule.getPredicate();
        return this.runPredicateSafely(httpRequest, urlRouteRule, predicate);
    }

    private <T> boolean runPredicateSafely(HttpServletRequest httpRequest, T rule, RoutePredicate<T> predicate) {
        Optional<Object> result = Optional.empty();
        try {
            result = Optional.ofNullable(predicate.apply(httpRequest, rule));
        }
        catch (Exception | LinkageError e) {
            LogUtils.logExceptionEvent(log, e, "Error while running predicate.");
        }
        return result.orElse(false);
    }

    @VisibleForTesting
    Set<UrlRouteRuleSet> getRouteRuleSets(FilterLocation filterLocation, String requestURI) {
        return this.routeService.getRouteRuleSets(filterLocation, requestURI);
    }

    @VisibleForTesting
    String makeRequestURI(HttpServletRequest httpServletRequest) throws URISyntaxException {
        String context;
        String requestURI = new URI(httpServletRequest.getRequestURI()).normalize().toString();
        if (requestURI.startsWith(context = httpServletRequest.getContextPath())) {
            requestURI = requestURI.substring(context.length());
        }
        return requestURI;
    }

    static class Result
    implements UrlRouter.Result {
        private final String toURI;
        private final boolean routed;

        private Result(String toURI, boolean routed) {
            this.toURI = toURI;
            this.routed = routed;
        }

        @Override
        public String toURI() {
            return this.toURI;
        }

        @Override
        public boolean isRouted() {
            return this.routed;
        }
    }
}

