/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.classpath;

import com.atlassian.confluence.util.classpath.JarSet;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ClasspathClasses
implements Iterable<String> {
    SortedMap<String, Set<URL>> jarUrlsByClass = new TreeMap<String, Set<URL>>();

    public void add(URL jarUrl, String classFileName) {
        if (!this.jarUrlsByClass.containsKey(classFileName)) {
            this.jarUrlsByClass.put(classFileName, new HashSet());
        }
        Set jarUrlsForClass = (Set)this.jarUrlsByClass.get(classFileName);
        jarUrlsForClass.add(jarUrl);
    }

    public void addAll(URL jarUrl, Collection<String> classFileNames) {
        for (String classFileName : classFileNames) {
            this.add(jarUrl, classFileName);
        }
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet(this.jarUrlsByClass.keySet()).iterator();
    }

    public int size() {
        return this.jarUrlsByClass.size();
    }

    public JarSet getJarsForClass(String classFileName) {
        if (!this.jarUrlsByClass.containsKey(classFileName)) {
            return JarSet.EMPTY_JAR_SET;
        }
        return new JarSet((Set)this.jarUrlsByClass.get(classFileName));
    }

    public SortedMap<String, Set<URL>> getJarUrlsByClass() {
        return this.jarUrlsByClass;
    }
}

