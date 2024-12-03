/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.collections.Predicate
 *  org.apache.commons.collections.PredicateUtils
 */
package com.atlassian.confluence.util.classpath;

import com.atlassian.confluence.util.ClasspathUtils;
import com.atlassian.confluence.util.classpath.ClasspathClasses;
import com.atlassian.confluence.util.classpath.ClasspathJarSets;
import com.atlassian.confluence.util.classpath.DuplicateClassFinder;
import com.atlassian.confluence.util.classpath.JarSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public class ClasspathJarDuplicateClassFinder
implements DuplicateClassFinder {
    public static final List<String> KNOWN_DUPLICATE_PACKAGES = ImmutableList.of((Object)"javax/media/jai", (Object)"com/sun/media/jai", (Object)"org/apache/jasper/compiler/", (Object)"org/apache/commons/logging/", (Object)"org/apache/naming/", (Object)"org/apache/html/", (Object)"org/apache/wml/", (Object)"org/apache/xerces/", (Object)"org/apache/catalina/loader", (Object)"org/apache/catalina/security", (Object)"org/apache/catalina/startup", (Object)"org/apache/catalina/util", (Object[])new String[]{"org/eclipse/jdt", "org/apache/tomcat/util/file", "org/apache/tomcat/util/buf", "org/apache/catalina/webresources/war", "org/osgi/framework", "org/osgi/resource", "org/osgi/service/packageadmin", "org/osgi/service/startlevel", "org/osgi/service/url", "org/osgi/util/tracker", "org/apache/felix/", "org/apache/log", "javax/crypto/", "javax/swing/", "sun/tools/jconsole/", "javax/transaction/", "org/apache/xalan/", "org/apache/xml/", "org/apache/xpath/", "com/sun/java/accessibility/util", "org/apache/regexp/", "javax/annotation/", "javax/annotation/security/", "javax/annotation/sql/", "javax/persistence/", "org/xmlpull/v1", "com/rometools/utils"});
    public static final Set<String> KNOWN_DUPLICATE_CLASSES = ImmutableSet.of((Object)"module-info.class", (Object)"META-INF/versions/9/module-info.class", (Object)"javax/xml/namespace/QName.class", (Object)"org/w3c/dom/html/HTMLDOMImplementation.class", (Object)"javax/xml/XMLConstants.class", (Object)"javax/xml/namespace/NamespaceContext.class", (Object[])new String[]{"org/apache/AnnotationProcessor.class", "org/apache/PeriodicEventListener.class", "org/w3c/dom/UserDataHandler.class", "org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.class"});
    public static final Predicate EXCLUDE_KNOWN_DUPLICATES = object -> {
        if (!(object instanceof String)) {
            return false;
        }
        String classFileName = (String)object;
        return !ClasspathJarDuplicateClassFinder.isKnownDuplicate(classFileName);
    };
    private ClasspathJarSets classpathJarSets;
    private final Predicate classFileNamePredicate;

    public ClasspathJarDuplicateClassFinder() {
        this(PredicateUtils.truePredicate());
    }

    public ClasspathJarDuplicateClassFinder(Predicate classFileNamePredicate) {
        this.classFileNamePredicate = classFileNamePredicate;
    }

    @Override
    public Set<JarSet> getJarSetsWithCommonClasses() {
        return this.getClasspathJarSets().getJarSetsWithCommonClasses();
    }

    private synchronized ClasspathJarSets getClasspathJarSets() {
        if (this.classpathJarSets == null) {
            ClasspathClasses classpathClasses = ClasspathUtils.getClassesInClasspathJars();
            this.classpathJarSets = new ClasspathJarSets(classpathClasses, this.classFileNamePredicate);
        }
        return this.classpathJarSets;
    }

    @Override
    public SortedSet<String> getClassFileNames(JarSet jars) {
        return this.getClasspathJarSets().getClassFileNames(jars);
    }

    @Override
    public SortedSet<String> getPackageNames(JarSet jars) {
        return this.getClasspathJarSets().getPackageNames(jars);
    }

    @Override
    public SortedSet<String> getClassNames(JarSet jars) {
        return this.getClasspathJarSets().getClassNames(jars);
    }

    public static boolean isKnownDuplicate(String classFileName) {
        if (KNOWN_DUPLICATE_CLASSES.contains(classFileName)) {
            return true;
        }
        for (String packageName : KNOWN_DUPLICATE_PACKAGES) {
            if (!classFileName.startsWith(packageName)) continue;
            return true;
        }
        return false;
    }
}

