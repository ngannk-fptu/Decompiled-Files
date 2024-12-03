/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

public class RulesBase
implements Rules {
    protected HashMap cache = new HashMap();
    protected Digester digester = null;
    protected String namespaceURI = null;
    protected ArrayList rules = new ArrayList();

    public Digester getDigester() {
        return this.digester;
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
        Iterator items = this.rules.iterator();
        while (items.hasNext()) {
            Rule item = (Rule)items.next();
            item.setDigester(digester);
        }
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public void add(String pattern, Rule rule) {
        ArrayList<Rule> list;
        int patternLength = pattern.length();
        if (patternLength > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, patternLength - 1);
        }
        if ((list = (ArrayList<Rule>)this.cache.get(pattern)) == null) {
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

    public void clear() {
        this.cache.clear();
        this.rules.clear();
    }

    public List match(String pattern) {
        return this.match(null, pattern);
    }

    public List match(String namespaceURI, String pattern) {
        List rulesList = this.lookup(namespaceURI, pattern);
        if (rulesList == null || rulesList.size() < 1) {
            String longKey = "";
            Iterator keys = this.cache.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                if (!key.startsWith("*/") || !pattern.equals(key.substring(2)) && !pattern.endsWith(key.substring(1)) || key.length() <= longKey.length()) continue;
                rulesList = this.lookup(namespaceURI, key);
                longKey = key;
            }
        }
        if (rulesList == null) {
            rulesList = new ArrayList();
        }
        return rulesList;
    }

    public List rules() {
        return this.rules;
    }

    protected List lookup(String namespaceURI, String pattern) {
        List list = (List)this.cache.get(pattern);
        if (list == null) {
            return null;
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return list;
        }
        ArrayList<Rule> results = new ArrayList<Rule>();
        Iterator items = list.iterator();
        while (items.hasNext()) {
            Rule item = (Rule)items.next();
            if (!namespaceURI.equals(item.getNamespaceURI()) && item.getNamespaceURI() != null) continue;
            results.add(item);
        }
        return results;
    }
}

