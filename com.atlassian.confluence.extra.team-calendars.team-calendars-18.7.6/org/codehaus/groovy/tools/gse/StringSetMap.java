/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.gse;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class StringSetMap
extends LinkedHashMap<String, Set<String>> {
    public StringSetMap() {
    }

    public StringSetMap(StringSetMap other) {
        for (String key : other.keySet()) {
            this.get(key).addAll(other.get(key));
        }
    }

    @Override
    public Set<String> get(Object o) {
        String name = (String)o;
        LinkedHashSet set = (LinkedHashSet)super.get(name);
        if (set == null) {
            set = new LinkedHashSet();
            this.put(name, set);
        }
        return set;
    }

    public void makeTransitiveHull() {
        TreeSet nameSet = new TreeSet(this.keySet());
        for (String k : nameSet) {
            StringSetMap delta = new StringSetMap();
            for (String i : nameSet) {
                for (String j : nameSet) {
                    Object iSet = this.get(i);
                    if (!iSet.contains(k) || !this.get(k).contains(j)) continue;
                    delta.get(i).add(j);
                }
            }
            for (String i : nameSet) {
                this.get(i).addAll(delta.get(i));
            }
        }
    }
}

