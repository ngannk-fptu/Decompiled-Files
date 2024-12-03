/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RulesBase
implements Rules {
    protected HashMap<String, List<Rule>> cache = new HashMap();
    protected Digester digester = null;
    protected String namespaceURI = null;
    protected ArrayList<Rule> rules = new ArrayList();

    @Override
    public Digester getDigester() {
        return this.digester;
    }

    @Override
    public void setDigester(Digester digester) {
        this.digester = digester;
        for (Rule rule : this.rules) {
            rule.setDigester(digester);
        }
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    @Override
    public void add(String pattern, Rule rule) {
        List<Rule> list;
        int patternLength = pattern.length();
        if (patternLength > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, patternLength - 1);
        }
        if ((list = this.cache.get(pattern)) == null) {
            list = new ArrayList<Rule>();
            this.cache.put(pattern, list);
        }
        list.add(rule);
        this.rules.add(rule);
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
        if (this.namespaceURI != null) {
            rule.setNamespaceURI(this.namespaceURI);
        }
    }

    @Override
    public void clear() {
        this.cache.clear();
        this.rules.clear();
    }

    @Override
    @Deprecated
    public List<Rule> match(String pattern) {
        return this.match(null, pattern);
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

