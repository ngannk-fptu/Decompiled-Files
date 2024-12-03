/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.classpath;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class JarSet {
    public static final JarSet EMPTY_JAR_SET = new JarSet(Collections.EMPTY_SET);
    private final Set urls;

    public JarSet(Set urls) {
        this.urls = urls;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JarSet jarSet = (JarSet)o;
        return this.urls.equals(jarSet.urls);
    }

    public int hashCode() {
        return this.urls.hashCode();
    }

    public int size() {
        return this.urls.size();
    }

    public Iterator iterator() {
        TreeSet result = new TreeSet((o1, o2) -> o1.toString().compareTo(o2.toString()));
        result.addAll(this.urls);
        return Collections.unmodifiableSortedSet(result).iterator();
    }
}

