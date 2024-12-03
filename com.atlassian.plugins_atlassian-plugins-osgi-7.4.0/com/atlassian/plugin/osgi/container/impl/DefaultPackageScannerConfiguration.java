/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugin.osgi.container.impl;

import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

public class DefaultPackageScannerConfiguration
implements PackageScannerConfiguration {
    private List<String> jarIncludes = Arrays.asList("*.jar");
    private List<String> jarExcludes = Collections.emptyList();
    private List<String> packageIncludes = Arrays.asList("com.atlassian.*", "com.google.common.*", "javax.*", "net.jcip.*", "org.jfree.*", "org.joda.*", "org.quartz", "org.quartz.*", "com.opensymphony.*", "org.apache.*", "org.ofbiz.*", "org.xml.*", "org.w3c.*", "webwork.*", "org.tuckey.web.filters.urlrewrite.*", "org.bouncycastle*", "org.dom4j*", "org.jdom*", "com.perforce*", "org.slf4j*", "io.atlassian.*", "net.minidev.json*");
    private List<String> packageExcludes = Arrays.asList("com.springframework*", "org.apache.tomcat.*", "org.apache.catalina.*", "org.apache.jasper.*", "org.apache.coyote.*", "org.apache.naming*", "com.atlassian.plugin.osgi.bridge.*");
    private Map<String, String> packageVersions;
    private String hostVersion;
    private ServletContext servletContext;
    private Set<String> osgiPublicPackages = Sets.newHashSet((Object[])new String[]{"*"});
    private Set<String> osgiPublicPackagesExcludes = Sets.newHashSet((Object[])new String[]{"net.minidev.json.*"});
    private Set<String> osgiDeprecatedPackages;
    private boolean treatDeprecatedPackagesAsPublic = true;
    private Set<String> applicationBundledInternalPlugins;

    public DefaultPackageScannerConfiguration() {
        this(null);
    }

    public DefaultPackageScannerConfiguration(String hostVersion) {
        this.hostVersion = hostVersion;
        this.jarIncludes = new ArrayList<String>(this.jarIncludes);
        this.jarExcludes = new ArrayList<String>(this.jarExcludes);
        this.packageIncludes = new ArrayList<String>(this.packageIncludes);
        this.packageExcludes = new ArrayList<String>(this.packageExcludes);
        this.packageVersions = new HashMap<String, String>();
        this.osgiPublicPackages = new HashSet<String>(this.osgiPublicPackages);
        this.osgiPublicPackagesExcludes = new HashSet<String>(this.osgiPublicPackagesExcludes);
        this.osgiDeprecatedPackages = new HashSet<String>();
        this.applicationBundledInternalPlugins = new HashSet<String>();
    }

    public void setJarIncludes(List<String> jarIncludes) {
        this.jarIncludes = jarIncludes;
    }

    public void setJarExcludes(List<String> jarExcludes) {
        this.jarExcludes = jarExcludes;
    }

    public void setPackageIncludes(List<String> packageIncludes) {
        this.packageIncludes = packageIncludes;
    }

    public void setPackageExcludes(List<String> packageExcludes) {
        this.packageExcludes = packageExcludes;
    }

    public void setDeprecatedPackages(Set<String> deprecatedPackages) {
        this.osgiDeprecatedPackages = deprecatedPackages;
    }

    public void setTreatDeprecatedPackagesAsPublic(boolean treatDeprecatedPackagesAsPublic) {
        this.treatDeprecatedPackagesAsPublic = treatDeprecatedPackagesAsPublic;
    }

    public void setJarPatterns(List<String> includes, List<String> excludes) {
        this.jarIncludes = includes;
        this.jarExcludes = excludes;
    }

    public void setPackagePatterns(List<String> includes, List<String> excludes) {
        this.packageIncludes = includes;
        this.packageExcludes = excludes;
    }

    public void setPackageVersions(Map<String, String> packageToVersions) {
        this.packageVersions = packageToVersions;
    }

    public void setOsgiPublicPackages(Set<String> osgiPublicPackages) {
        this.osgiPublicPackages = osgiPublicPackages;
    }

    public void setOsgiPublicPackagesExcludes(Set<String> osgiPublicPackagesExcludes) {
        this.osgiPublicPackagesExcludes = osgiPublicPackagesExcludes;
    }

    public void setApplicationBundledInternalPlugins(Set<String> applicationBundledInternalPlugins) {
        this.applicationBundledInternalPlugins = applicationBundledInternalPlugins;
    }

    @Override
    public List<String> getJarIncludes() {
        return this.jarIncludes;
    }

    @Override
    public List<String> getJarExcludes() {
        return this.jarExcludes;
    }

    @Override
    public List<String> getPackageIncludes() {
        return this.packageIncludes;
    }

    @Override
    public List<String> getPackageExcludes() {
        return this.packageExcludes;
    }

    @Override
    public Map<String, String> getPackageVersions() {
        return this.packageVersions;
    }

    @Override
    public String getCurrentHostVersion() {
        return this.hostVersion;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Set<String> getOsgiPublicPackages() {
        return this.osgiPublicPackages;
    }

    @Override
    public Set<String> getOsgiPublicPackagesExcludes() {
        return this.osgiPublicPackagesExcludes;
    }

    @Override
    public Set<String> getOsgiDeprecatedPackages() {
        return this.osgiDeprecatedPackages;
    }

    @Override
    public boolean treatDeprecatedPackagesAsPublic() {
        return this.treatDeprecatedPackagesAsPublic;
    }

    @Override
    public Set<String> getApplicationBundledInternalPlugins() {
        return this.applicationBundledInternalPlugins;
    }
}

