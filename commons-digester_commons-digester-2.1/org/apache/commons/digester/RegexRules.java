/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.digester.AbstractRulesImpl;
import org.apache.commons.digester.RegexMatcher;
import org.apache.commons.digester.Rule;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RegexRules
extends AbstractRulesImpl {
    private ArrayList<RegisteredRule> registeredRules = new ArrayList();
    private RegexMatcher matcher;

    public RegexRules(RegexMatcher matcher) {
        this.setRegexMatcher(matcher);
    }

    public RegexMatcher getRegexMatcher() {
        return this.matcher;
    }

    public void setRegexMatcher(RegexMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("RegexMatcher must not be null.");
        }
        this.matcher = matcher;
    }

    @Override
    protected void registerRule(String pattern, Rule rule) {
        this.registeredRules.add(new RegisteredRule(pattern, rule));
    }

    @Override
    public void clear() {
        this.registeredRules.clear();
    }

    @Override
    public List<Rule> match(String namespaceURI, String pattern) {
        ArrayList<Rule> rules = new ArrayList<Rule>(this.registeredRules.size());
        for (RegisteredRule rr : this.registeredRules) {
            if (!this.matcher.match(pattern, rr.pattern)) continue;
            rules.add(rr.rule);
        }
        return rules;
    }

    @Override
    public List<Rule> rules() {
        ArrayList<Rule> rules = new ArrayList<Rule>(this.registeredRules.size());
        for (RegisteredRule rr : this.registeredRules) {
            rules.add(rr.rule);
        }
        return rules;
    }

    private class RegisteredRule {
        String pattern;
        Rule rule;

        RegisteredRule(String pattern, Rule rule) {
            this.pattern = pattern;
            this.rule = rule;
        }
    }
}

