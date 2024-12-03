/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

public class WithDefaultsRulesWrapper
implements Rules {
    private Rules wrappedRules;
    private List defaultRules = new ArrayList();
    private List allRules = new ArrayList();

    public WithDefaultsRulesWrapper(Rules wrappedRules) {
        if (wrappedRules == null) {
            throw new IllegalArgumentException("Wrapped rules must not be null");
        }
        this.wrappedRules = wrappedRules;
    }

    public Digester getDigester() {
        return this.wrappedRules.getDigester();
    }

    public void setDigester(Digester digester) {
        this.wrappedRules.setDigester(digester);
        Iterator it = this.defaultRules.iterator();
        while (it.hasNext()) {
            Rule rule = (Rule)it.next();
            rule.setDigester(digester);
        }
    }

    public String getNamespaceURI() {
        return this.wrappedRules.getNamespaceURI();
    }

    public void setNamespaceURI(String namespaceURI) {
        this.wrappedRules.setNamespaceURI(namespaceURI);
    }

    public List getDefaults() {
        return this.defaultRules;
    }

    public List match(String pattern) {
        return this.match("", pattern);
    }

    public List match(String namespaceURI, String pattern) {
        List matches = this.wrappedRules.match(namespaceURI, pattern);
        if (matches == null || matches.isEmpty()) {
            return new ArrayList(this.defaultRules);
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

    public List rules() {
        return this.allRules;
    }

    public void clear() {
        this.wrappedRules.clear();
        this.allRules.clear();
        this.defaultRules.clear();
    }

    public void add(String pattern, Rule rule) {
        this.wrappedRules.add(pattern, rule);
        this.allRules.add(rule);
    }
}

