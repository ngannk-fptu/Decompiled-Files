/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.rules.UriRulesFactory;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.Iterator;
import java.util.Map;

public final class RootResourceClassesRule
implements UriRule {
    private final UriRules<UriRule> rules;

    public RootResourceClassesRule(Map<PathPattern, UriRule> rulesMap) {
        this.rules = UriRulesFactory.create(rulesMap);
    }

    @Override
    public boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(RootResourceClassesRule.class.getSimpleName(), path, resource);
        if (context.isTracingEnabled()) {
            context.trace("accept root resource classes: \"" + path + "\"");
        }
        Iterator<UriRule> matches = this.rules.match(path, context);
        while (matches.hasNext()) {
            if (!matches.next().accept(path, resource, context)) continue;
            return true;
        }
        return false;
    }
}

