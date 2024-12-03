/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.prettyurls.api.route.UrlRouteRule
 *  com.sun.jersey.api.uri.UriTemplate
 *  io.atlassian.fugue.Option
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.prettyurls.internal.route;

import com.atlassian.prettyurls.api.route.UrlRouteRule;
import com.sun.jersey.api.uri.UriTemplate;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class UrlMatcher {
    UrlMatcher() {
    }

    Result getMatchingRule(String requestURI, List<UrlRouteRule> routeRules, Strategy strategy) {
        List<UrlRouteRule> sortedRules = strategy.sortRules(routeRules);
        HashMap<String, String> parsedVariableValues = new HashMap<String, String>();
        for (UrlRouteRule routeRule : sortedRules) {
            if (!routeRule.getFrom().match((CharSequence)requestURI, parsedVariableValues)) continue;
            return new Result((Option<UrlRouteRule>)Option.some((Object)routeRule), parsedVariableValues);
        }
        return new Result((Option<UrlRouteRule>)Option.none(), parsedVariableValues);
    }

    static class Result {
        private final Map<String, String> parsedVariableValues;
        private final Option<UrlRouteRule> matchingRule;

        public Result(Option<UrlRouteRule> matchingRule, Map<String, String> parsedVariableValues) {
            this.parsedVariableValues = parsedVariableValues;
            this.matchingRule = matchingRule;
        }

        public Option<UrlRouteRule> getMatchingRule() {
            return this.matchingRule;
        }

        public Map<String, String> getParsedVariableValues() {
            return this.parsedVariableValues;
        }

        public boolean matches() {
            return this.matchingRule.isDefined();
        }
    }

    static enum Strategy {
        JAX_RS_MATCHING{

            @Override
            List<UrlRouteRule> sortRules(List<UrlRouteRule> routeRules) {
                return routeRules.stream().sorted(Comparator.comparing(UrlRouteRule::getFrom, UriTemplate.COMPARATOR)).collect(Collectors.toCollection(ArrayList::new));
            }
        }
        ,
        LIST_ORDER_MATCHING{

            @Override
            List<UrlRouteRule> sortRules(List<UrlRouteRule> routeRules) {
                return routeRules;
            }
        };


        abstract List<UrlRouteRule> sortRules(List<UrlRouteRule> var1);
    }
}

