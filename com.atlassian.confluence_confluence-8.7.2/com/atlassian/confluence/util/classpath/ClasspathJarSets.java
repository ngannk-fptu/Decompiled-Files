/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.collections.Predicate
 *  org.apache.commons.collections.PredicateUtils
 */
package com.atlassian.confluence.util.classpath;

import com.atlassian.confluence.util.classpath.ClasspathClasses;
import com.atlassian.confluence.util.classpath.DuplicateClassFinder;
import com.atlassian.confluence.util.classpath.JarSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public class ClasspathJarSets
implements DuplicateClassFinder {
    private final Map<JarSet, Set<String>> classesByJarSet = new HashMap<JarSet, Set<String>>();

    public ClasspathJarSets() {
    }

    public ClasspathJarSets(ClasspathClasses classpathClasses) {
        this(classpathClasses, PredicateUtils.truePredicate());
    }

    public ClasspathJarSets(ClasspathClasses classpathClasses, Predicate classFileNamePredicate) {
        for (String classFileName : classpathClasses) {
            if (!classFileNamePredicate.evaluate((Object)classFileName)) continue;
            this.addClass(classFileName, classpathClasses.getJarsForClass(classFileName));
        }
    }

    public void addClass(String classFileName, JarSet jars) {
        if (!this.classesByJarSet.containsKey(jars)) {
            this.classesByJarSet.put(jars, new HashSet());
        }
        this.classesByJarSet.get(jars).add(classFileName);
    }

    @Override
    public Set<JarSet> getJarSetsWithCommonClasses() {
        HashSet<JarSet> jarSets = new HashSet<JarSet>(this.classesByJarSet.keySet());
        CollectionUtils.filter(jarSets, object -> {
            if (!(object instanceof JarSet)) {
                return false;
            }
            return ((JarSet)object).size() > 1;
        });
        return Collections.unmodifiableSet(jarSets);
    }

    @Override
    public SortedSet<String> getClassFileNames(JarSet jars) {
        if (!this.classesByJarSet.containsKey(jars)) {
            return new TreeSet<String>();
        }
        return new TreeSet<String>((Collection)this.classesByJarSet.get(jars));
    }

    @Override
    public SortedSet<String> getPackageNames(JarSet jars) {
        SortedSet<String> result = this.getClassFileNames(jars);
        CollectionUtils.transform(result, classFileName -> this.getPackageName((String)classFileName));
        return result;
    }

    @Override
    public SortedSet<String> getClassNames(JarSet jars) {
        return this.getClassFileNames(jars);
    }

    private String getPackageName(String classFileName) {
        int index = classFileName.lastIndexOf("/");
        if (index == -1) {
            return "";
        }
        return classFileName.substring(0, index);
    }
}

