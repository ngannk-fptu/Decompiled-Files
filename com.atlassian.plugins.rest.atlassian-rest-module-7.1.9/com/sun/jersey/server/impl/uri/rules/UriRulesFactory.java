/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.rules.AtomicMatchingPatterns;
import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import com.sun.jersey.server.impl.uri.rules.automata.AutomataMatchingUriTemplateRules;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UriRulesFactory {
    private UriRulesFactory() {
    }

    public static UriRules<UriRule> create(Map<PathPattern, UriRule> rulesMap) {
        return UriRulesFactory.create(rulesMap, null);
    }

    public static UriRules<UriRule> create(Map<PathPattern, UriRule> rulesMap, List<PatternRulePair<UriRule>> rules) {
        ArrayList<PatternRulePair<UriRule>> l = new ArrayList<PatternRulePair<UriRule>>();
        for (Map.Entry<PathPattern, UriRule> e : rulesMap.entrySet()) {
            l.add(new PatternRulePair<UriRule>(e.getKey(), e.getValue()));
        }
        if (rules != null) {
            l.addAll(rules);
        }
        return UriRulesFactory.create(l);
    }

    public static UriRules<UriRule> create(List<PatternRulePair<UriRule>> rules) {
        if (rules.size() < Integer.MAX_VALUE) {
            return new AtomicMatchingPatterns<UriRule>(rules);
        }
        return new AutomataMatchingUriTemplateRules<UriRule>(rules);
    }
}

