/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.Rules;

public class RulesBase
implements Rules {
    protected HashMap<String, List<Rule>> cache = new HashMap();
    protected Digester digester = null;
    protected ArrayList<Rule> rules = new ArrayList();

    @Override
    public Digester getDigester() {
        return this.digester;
    }

    @Override
    public void setDigester(Digester digester) {
        this.digester = digester;
        for (Rule item : this.rules) {
            item.setDigester(digester);
        }
    }

    @Override
    public void add(String pattern, Rule rule) {
        int patternLength = pattern.length();
        if (patternLength > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, patternLength - 1);
        }
        this.cache.computeIfAbsent(pattern, k -> new ArrayList()).add(rule);
        this.rules.add(rule);
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
    }

    @Override
    public void clear() {
        this.cache.clear();
        this.rules.clear();
    }

    @Override
    public List<Rule> match(String namespaceURI, String pattern) {
        List<Rule> rulesList = this.lookup(namespaceURI, pattern);
        if (rulesList == null || rulesList.size() < 1) {
            String longKey = "";
            for (String key : this.cache.keySet()) {
                if (!key.startsWith("*/") || !pattern.equals(key.substring(2)) && !pattern.endsWith(key.substring(1)) || key.length() <= longKey.length()) continue;
                rulesList = this.lookup(namespaceURI, key);
                longKey = key;
            }
        }
        if (rulesList == null) {
            rulesList = new ArrayList<Rule>();
        }
        return rulesList;
    }

    @Override
    public List<Rule> rules() {
        return this.rules;
    }

    protected List<Rule> lookup(String namespaceURI, String pattern) {
        List<Rule> list = this.cache.get(pattern);
        if (list == null) {
            return null;
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return list;
        }
        ArrayList<Rule> results = new ArrayList<Rule>();
        for (Rule item : list) {
            if (!namespaceURI.equals(item.getNamespaceURI()) && item.getNamespaceURI() != null) continue;
            results.add(item);
        }
        return results;
    }
}

