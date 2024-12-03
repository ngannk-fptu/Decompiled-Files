/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RulesBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExtendedBaseRules
extends RulesBase {
    private int counter = 0;
    private Map<Rule, Integer> order = new HashMap<Rule, Integer>();

    @Override
    public void add(String pattern, Rule rule) {
        super.add(pattern, rule);
        ++this.counter;
        this.order.put(rule, this.counter);
    }

    @Override
    public List<Rule> match(String namespace, String pattern) {
        String parentPattern = "";
        int lastIndex = pattern.lastIndexOf(47);
        boolean hasParent = true;
        if (lastIndex == -1) {
            hasParent = false;
        } else {
            parentPattern = pattern.substring(0, lastIndex);
        }
        ArrayList<Rule> universalList = new ArrayList<Rule>(this.counter);
        List tempList = (List)this.cache.get("!*");
        if (tempList != null) {
            universalList.addAll(tempList);
        }
        if ((tempList = (List)this.cache.get("!" + parentPattern + "/?")) != null) {
            universalList.addAll(tempList);
        }
        boolean ignoreBasicMatches = false;
        List rulesList = (List)this.cache.get(pattern);
        if (rulesList != null) {
            ignoreBasicMatches = true;
        } else if (hasParent) {
            rulesList = (List)this.cache.get(parentPattern + "/?");
            if (rulesList != null) {
                ignoreBasicMatches = true;
            } else {
                rulesList = this.findExactAncesterMatch(pattern);
                if (rulesList != null) {
                    ignoreBasicMatches = true;
                }
            }
        }
        String longKey = "";
        int longKeyLength = 0;
        for (String key : this.cache.keySet()) {
            boolean isUniversal = key.startsWith("!");
            if (isUniversal) {
                key = key.substring(1, key.length());
            }
            boolean wildcardMatchStart = key.startsWith("*/");
            boolean wildcardMatchEnd = key.endsWith("/*");
            if (!wildcardMatchStart && (!isUniversal || !wildcardMatchEnd)) continue;
            boolean parentMatched = false;
            boolean basicMatched = false;
            boolean ancesterMatched = false;
            boolean parentMatchEnd = key.endsWith("/?");
            if (parentMatchEnd) {
                parentMatched = this.parentMatch(key, pattern, parentPattern);
            } else if (wildcardMatchEnd) {
                String bodyPattern;
                String patternBody;
                ancesterMatched = wildcardMatchStart ? (pattern.endsWith(patternBody = key.substring(2, key.length() - 2)) ? true : pattern.indexOf(patternBody + "/") > -1) : (pattern.startsWith(bodyPattern = key.substring(0, key.length() - 2)) ? (pattern.length() == bodyPattern.length() ? true : pattern.charAt(bodyPattern.length()) == '/') : false);
            } else {
                basicMatched = this.basicMatch(key, pattern);
            }
            if (!parentMatched && !basicMatched && !ancesterMatched) continue;
            if (isUniversal) {
                tempList = (List)this.cache.get("!" + key);
                if (tempList == null) continue;
                universalList.addAll(tempList);
                continue;
            }
            if (ignoreBasicMatches) continue;
            int keyLength = key.length();
            if (wildcardMatchStart) {
                --keyLength;
            }
            if (wildcardMatchEnd) {
                --keyLength;
            } else if (parentMatchEnd) {
                --keyLength;
            }
            if (keyLength <= longKeyLength) continue;
            rulesList = (List)this.cache.get(key);
            longKey = key;
            longKeyLength = keyLength;
        }
        if (rulesList == null) {
            rulesList = (List)this.cache.get("*");
        }
        if (rulesList != null) {
            universalList.addAll(rulesList);
        }
        if (namespace != null) {
            Iterator it = universalList.iterator();
            while (it.hasNext()) {
                Rule rule = (Rule)it.next();
                String ns_uri = rule.getNamespaceURI();
                if (ns_uri == null || ns_uri.equals(namespace)) continue;
                it.remove();
            }
        }
        Collections.sort(universalList, new Comparator<Rule>(){

            @Override
            public int compare(Rule r1, Rule r2) throws ClassCastException {
                Integer i1 = (Integer)ExtendedBaseRules.this.order.get(r1);
                Integer i2 = (Integer)ExtendedBaseRules.this.order.get(r2);
                if (i1 == null) {
                    if (i2 == null) {
                        return 0;
                    }
                    return -1;
                }
                if (i2 == null) {
                    return 1;
                }
                return i1 - i2;
            }
        });
        return universalList;
    }

    private boolean parentMatch(String key, String pattern, String parentPattern) {
        return parentPattern.endsWith(key.substring(1, key.length() - 2));
    }

    private boolean basicMatch(String key, String pattern) {
        return pattern.equals(key.substring(2)) || pattern.endsWith(key.substring(1));
    }

    private List<Rule> findExactAncesterMatch(String parentPattern) {
        List matchingRules = null;
        int lastIndex = parentPattern.length();
        while (lastIndex-- > 0) {
            if ((lastIndex = parentPattern.lastIndexOf(47, lastIndex)) <= 0 || (matchingRules = (List)this.cache.get(parentPattern.substring(0, lastIndex) + "/*")) == null) continue;
            return matchingRules;
        }
        return null;
    }
}

