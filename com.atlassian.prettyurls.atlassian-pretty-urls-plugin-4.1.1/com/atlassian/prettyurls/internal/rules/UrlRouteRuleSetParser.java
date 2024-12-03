/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  com.atlassian.prettyurls.api.route.DefaultUrlRouteRuleSetKey
 *  com.atlassian.prettyurls.api.route.RoutePredicate
 *  com.atlassian.prettyurls.api.route.UrlRouteRule
 *  com.atlassian.prettyurls.api.route.UrlRouteRule$ParameterMode
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSet
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSet$Builder
 *  com.atlassian.prettyurls.api.route.UrlRouteRuleSetKey
 *  com.sun.jersey.api.uri.UriTemplate
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.prettyurls.internal.rules;

import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.prettyurls.api.route.DefaultUrlRouteRuleSetKey;
import com.atlassian.prettyurls.api.route.RoutePredicate;
import com.atlassian.prettyurls.api.route.UrlRouteRule;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSet;
import com.atlassian.prettyurls.api.route.UrlRouteRuleSetKey;
import com.atlassian.prettyurls.internal.util.UrlUtils;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlRouteRuleSetParser {
    private static final Logger log = LoggerFactory.getLogger(UrlRouteRuleSetParser.class);
    private static final List<String> HEAD = Collections.singletonList("HEAD");
    private static final List<String> GET = Collections.singletonList("GET");
    private static final List<String> POST = Collections.singletonList("POST");
    private static final List<String> PUT = Collections.singletonList("PUT");
    private static final List<String> DELETE = Collections.singletonList("DELETE");
    private static final List<String> OPTIONS = Collections.singletonList("OPTIONS");
    private static final List<String> PATCH = Collections.singletonList("PATCH");

    public UrlRouteRuleSet parse(String moduleKey, Element element, FilterLocation location, PredicateMaker predicateMaker) {
        Objects.requireNonNull(moduleKey);
        Objects.requireNonNull(element);
        UrlRouteRuleSet.Builder builder = new UrlRouteRuleSet.Builder().setKey((UrlRouteRuleSetKey)new DefaultUrlRouteRuleSetKey(moduleKey)).setLocation(location);
        String path = element.attributeValue("path", "").trim();
        if (!path.isEmpty()) {
            if (!this.validatePath(path)) {
                log.error("'{}' is not an acceptable top level path for URL routing.", (Object)path);
                return null;
            }
        } else {
            log.error("You must provide a path attribute in order to get URL routing.");
            return null;
        }
        builder.addTopLevelPath(UrlUtils.startWithSlash(path));
        builder.setPredicate(predicateMaker.makeRuleSetPredicate(element));
        this.parseRules("route", element, builder, predicateMaker, path, Collections.emptyList());
        this.parseRules("head", element, builder, predicateMaker, path, HEAD);
        this.parseRules("get", element, builder, predicateMaker, path, GET);
        this.parseRules("post", element, builder, predicateMaker, path, POST);
        this.parseRules("put", element, builder, predicateMaker, path, PUT);
        this.parseRules("delete", element, builder, predicateMaker, path, DELETE);
        this.parseRules("options", element, builder, predicateMaker, path, OPTIONS);
        this.parseRules("patch", element, builder, predicateMaker, path, PATCH);
        return builder.build();
    }

    private void parseRules(String elementName, Element element, UrlRouteRuleSet.Builder builder, PredicateMaker predicateMaker, String path, List<String> providedHttpVerbs) {
        List elements = element.elements(elementName);
        for (Element e : elements) {
            String fromStr = e.attributeValue("from");
            String toStr = e.attributeValue("to", "").trim();
            RoutePredicate<UrlRouteRule> routePredicate = predicateMaker.makeRulePredicate(e);
            if (toStr.isEmpty()) {
                log.error("Encountered blank to=\"\" rule.  Ignoring it...");
                continue;
            }
            if (fromStr == null) {
                log.error("Missing from=\"\" rule.  Ignoring it...");
                continue;
            }
            UriTemplate from = fromStr.trim().isEmpty() ? this.createURI(path) : this.createURI(path, fromStr);
            UriTemplate to = this.createURI("", toStr);
            List<String> httpVerbs = providedHttpVerbs;
            if (httpVerbs.isEmpty()) {
                httpVerbs = this.parseHttpVerbs(e);
            }
            if (from == null || to == null) continue;
            builder.addRule(from, to, httpVerbs, routePredicate, UrlRouteRule.ParameterMode.PASS_UNMAPPED);
        }
    }

    private List<String> parseHttpVerbs(Element e) {
        String[] split;
        ArrayList<String> httpVerbs = new ArrayList<String>();
        String verbs = e.attributeValue("verbs", "");
        for (String verb : split = verbs.split(",")) {
            if ((verb = verb.trim()).isEmpty()) continue;
            httpVerbs.add(verb.toUpperCase());
        }
        return httpVerbs;
    }

    private boolean validatePath(String path) {
        return !path.equals("/");
    }

    private UriTemplate createURI(String path, String uriStr) {
        String template = UrlUtils.prependPath(path, uriStr);
        try {
            return new UriTemplate(template);
        }
        catch (IllegalArgumentException e) {
            log.error("Unable to parse routing URI {}", (Object)template);
            return null;
        }
    }

    private UriTemplate createURI(String uriStr) {
        try {
            return new UriTemplate(uriStr);
        }
        catch (IllegalArgumentException e) {
            log.error("Unable to parse routing URI {}", (Object)uriStr);
            return null;
        }
    }

    public static interface PredicateMaker {
        public RoutePredicate<UrlRouteRuleSet> makeRuleSetPredicate(Element var1);

        public RoutePredicate<UrlRouteRule> makeRulePredicate(Element var1);
    }
}

