/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.Test;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

public interface ClassFinderFactory {
    public ClassFinder buildClassFinder(ClassLoaderInterface var1, Collection<URL> var2, boolean var3, Set<String> var4, Test<String> var5);
}

