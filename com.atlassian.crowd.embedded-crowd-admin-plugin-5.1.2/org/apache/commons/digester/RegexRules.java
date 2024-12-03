/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.digester.AbstractRulesImpl;
import org.apache.commons.digester.RegexMatcher;
import org.apache.commons.digester.Rule;

public class RegexRules
extends AbstractRulesImpl {
    private ArrayList registeredRules = new ArrayList();
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

    protected void registerRule(String pattern, Rule rule) {
        this.registeredRules.add(new RegisteredRule(pattern, rule));
    }

    public void clear() {
        this.registeredRules.clear();
    }

    public List match(String namespaceURI, String pattern) {
        ArrayList<Rule> rules = new ArrayList<Rule>(this.registeredRules.size());
        Iterator it = this.registeredRules.iterator();
        while (it.hasNext()) {
            RegisteredRule next = (RegisteredRule)it.next();
            if (!this.matcher.match(pattern, next.pattern)) continue;
            rules.add(next.rule);
        }
        return rules;
    }

    public List rules() {
        ArrayList<Rule> rules = new ArrayList<Rule>(this.registeredRules.size());
        Iterator it = this.registeredRules.iterator();
        while (it.hasNext()) {
            rules.add(((RegisteredRule)it.next()).rule);
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

