/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.ClasspathUtils;
import com.atlassian.confluence.util.classpath.ClasspathJarDuplicateClassFinder;
import com.atlassian.confluence.util.classpath.DuplicateClassFinder;
import com.atlassian.confluence.util.classpath.JarSet;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebSudoRequired
@AdminOnly
public class ViewSystemClasspathAction
extends ConfluenceActionSupport
implements Beanable {
    DuplicateClassFinder duplicateClassFinder = new ClasspathJarDuplicateClassFinder(ClasspathJarDuplicateClassFinder.EXCLUDE_KNOWN_DUPLICATES);

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public List<ClassLoader> getThreadContextClassLoaders() {
        return ClasspathUtils.getThreadContentClassLoaderHierarchy();
    }

    public List<URL> getClassLoaderClasspath(ClassLoader classloader) {
        return ClasspathUtils.getClassLoaderClasspath(classloader);
    }

    public Set<JarSet> getJarSetsWithCommonClasses() {
        return this.duplicateClassFinder.getJarSetsWithCommonClasses();
    }

    public Set<String> getCommonPackages(JarSet jars) {
        return this.duplicateClassFinder.getPackageNames(jars);
    }

    public List<String> getKnownDuplicates() {
        return ClasspathJarDuplicateClassFinder.KNOWN_DUPLICATE_PACKAGES;
    }

    @Override
    public Map<String, Object> getBean() {
        return ImmutableMap.of((Object)"jarSetsWithCommonClasses", this.getJarSetsWithCommonClasses().stream().map(jarSet -> ImmutableMap.of((Object)"jars", (Object)Lists.newArrayList((Iterator)Iterators.transform((Iterator)jarSet.iterator(), URL::toString)), (Object)"commonPackages", this.getCommonPackages((JarSet)jarSet).stream().sorted().collect(Collectors.toList()))).collect(Collectors.toList()));
    }
}

