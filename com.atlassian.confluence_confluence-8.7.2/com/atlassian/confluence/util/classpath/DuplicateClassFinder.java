/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.classpath;

import com.atlassian.confluence.util.classpath.JarSet;
import java.util.Set;
import java.util.SortedSet;

public interface DuplicateClassFinder {
    public Set<JarSet> getJarSetsWithCommonClasses();

    public SortedSet<String> getClassFileNames(JarSet var1);

    public SortedSet<String> getPackageNames(JarSet var1);

    public SortedSet<String> getClassNames(JarSet var1);
}

