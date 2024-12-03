/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WithDefaultsRulesWrapper
implements Rules {
    private Rules wrappedRules;
    private List<Rule> defaultRules = new ArrayList<Rule>();
    private List<Rule> allRules = new ArrayList<Rule>();

    public WithDefaultsRulesWrapper(Rules wrappedRules) {
        if (wrappedRules == null) {
            throw new IllegalArgumentException("Wrapped rules must not be null");
        }
        this.wrappedRules = wrappedRules;
    }

    @Override
    public Digester getDigester() {
        return this.wrappedRules.getDigester();
    }

    @Override
    public void setDigester(Digester digester) {
        this.wrappedRules.setDigester(digester);
        for (Rule rule : this.defaultRules) {
            rule.setDigester(digester);
        }
    }

    @Override
    public String getNamespaceURI() {
        return this.wrappedRules.getNamespaceURI();
    }

    @Override
    public void setNamespaceURI(String namespaceURI) {
        this.wrappedRules.setNamespaceURI(namespaceURI);
    }

    public List<Rule> getDefaults() {
        return this.defaultRules;
    }

    @Override
    public List<Rule> match(String pattern) {
        return this.match("", pattern);
    }

    @Override
    public List<Rule> match(String namespaceURI, String pattern) {
        List<Rule> matches = this.wrappedRules.match(namespaceURI, pattern);
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<Rule>(this.defaultRules);
        }
        return matches;
    }

    public void addDefault(Rule rule) {
        if (this.wrappedRules.getDigester() != null) {
            rule.setDigester(this.wrappedRules.getDigester());
        }
        if (this.wrappedRules.getNamespaceURI() != null) {
            rule.setNamespaceURI(this.wrappedRules.getNamespaceURI());
        }
        this.defaultRules.add(rule);
        this.allRules.add(rule);
    }

    @Override
    public List<Rule> rules() {
        return this.allRules;
    }

    @Override
    public void clear() {
        this.wrappedRules.clear();
        this.allRules.clear();
        this.defaultRules.clear();
    }

    @Override
    public void add(String pattern, Rule rule) {
        this.wrappedRules.add(pattern, rule);
        this.allRules.add(rule);
    }
}

