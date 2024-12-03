/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugins.rest.common.error.jersey.NotFoundExceptionMapper;
import com.atlassian.plugins.rest.common.error.jersey.ThrowableExceptionMapper;
import com.atlassian.plugins.rest.common.error.jersey.UncaughtExceptionEntityWriter;
import com.atlassian.plugins.rest.common.json.JacksonJsonProviderFactory;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.plugins.rest.common.security.jersey.AuthorisationExceptionMapper;
import com.atlassian.plugins.rest.common.security.jersey.SecurityExceptionMapper;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.plugins.rest.module.scanner.AnnotatedClassScanner;
import com.atlassian.plugins.rest.module.xml.XMLStreamReaderContextProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.Module;
import org.osgi.framework.Bundle;

class OsgiResourceConfig
extends DefaultResourceConfig {
    private final Bundle bundle;
    private final Set<Class<?>> classes = Sets.newHashSet((Object[])new Class[]{NotFoundExceptionMapper.class, AuthorisationExceptionMapper.class, SecurityExceptionMapper.class, ThrowableExceptionMapper.class, SysadminOnlyResourceFilter.class, AdminOnlyResourceFilter.class, UncaughtExceptionEntityWriter.class});
    private final Set<Object> instances;
    private Set<Class<?>> scannedClasses;
    private final String[] packages;
    private final boolean indexBundledJars;

    OsgiResourceConfig(Bundle bundle2, Set<String> packages, Collection<? extends ContainerRequestFilter> containerRequestFilters, Collection<? extends ContainerResponseFilter> containerResponseFilters, Collection<? extends ResourceFilterFactory> resourceFilterFactories, Collection<? extends Module> modules, Collection<?> providers, boolean indexBundledJarsFlag) {
        this.packages = packages.toArray(new String[packages.size()]);
        this.bundle = Objects.requireNonNull(bundle2);
        this.getProperties().put("com.sun.jersey.spi.container.ContainerRequestFilters", Lists.newLinkedList(containerRequestFilters));
        this.getProperties().put("com.sun.jersey.spi.container.ContainerResponseFilters", Lists.newLinkedList(containerResponseFilters));
        this.getProperties().put("com.sun.jersey.spi.container.ResourceFilters", Lists.newLinkedList(resourceFilterFactories));
        this.instances = Sets.newHashSet((Iterable)Objects.requireNonNull(providers));
        this.instances.add(new JacksonJsonProviderFactory().create(modules));
        this.instances.add(new XMLStreamReaderContextProvider(this));
        this.addInstancesClassesToClasses();
        this.indexBundledJars = indexBundledJarsFlag;
    }

    private void addInstancesClassesToClasses() {
        for (Object o : this.instances) {
            this.classes.add(o.getClass());
        }
    }

    @Override
    public synchronized Set<Class<?>> getClasses() {
        if (this.scannedClasses == null) {
            this.scannedClasses = this.scanForAnnotatedClasses();
            this.classes.addAll(this.scannedClasses);
        }
        return this.classes;
    }

    private Set<Class<?>> scanForAnnotatedClasses() {
        return new AnnotatedClassScanner(this.bundle, this.indexBundledJars, Provider.class, Path.class).scan(this.packages);
    }

    public Set<?> getInstances() {
        return Collections.unmodifiableSet(this.instances);
    }
}

