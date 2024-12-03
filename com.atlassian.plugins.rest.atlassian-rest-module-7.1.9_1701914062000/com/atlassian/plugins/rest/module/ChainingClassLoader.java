/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainingClassLoader
extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(ChainingClassLoader.class);
    private static final String SERVICES_PREFIX = "META-INF/services";
    private static final String ALTERNATE_SERVICES_PREFIX = "META-INF/alternate-services";
    private static final List<String> ALTERNATE_SERVICES = Collections.unmodifiableList(Arrays.asList("META-INF/services/com.sun.jersey.server.impl.model.method.dispatch.ResourceMethodDispatchProvider", "META-INF/services/com.sun.jersey.spi.container.ContainerProvider", "META-INF/services/com.sun.jersey.spi.container.ContainerRequestFilter", "META-INF/services/com.sun.jersey.spi.container.WebApplicationProvider", "META-INF/services/com.sun.jersey.spi.HeaderDelegateProvider", "META-INF/services/com.sun.jersey.spi.StringReaderProvider", "META-INF/services/javax.ws.rs.ext.MessageBodyReader", "META-INF/services/javax.ws.rs.ext.MessageBodyWriter", "META-INF/services/javax.ws.rs.ext.RuntimeDelegate"));
    private final List<ClassLoader> classLoaders;

    public ChainingClassLoader(ClassLoader ... classLoaders) {
        Validate.noNullElements((Object[])classLoaders, (String)"ClassLoader arguments cannot be null", (Object[])new Object[0]);
        this.classLoaders = Arrays.asList(classLoaders);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader classloader : this.classLoaders) {
            try {
                return classloader.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return new ResourcesEnumeration(this.getAlternativeResourceName(name), this.classLoaders);
    }

    @Override
    public URL getResource(String name) {
        String realResourceName = this.getAlternativeResourceName(name);
        for (ClassLoader classloader : this.classLoaders) {
            URL url = classloader.getResource(realResourceName);
            if (url == null) continue;
            return url;
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream inputStream;
        String realResourceName = this.getAlternativeResourceName(name);
        for (ClassLoader classloader : this.classLoaders) {
            inputStream = classloader.getResourceAsStream(realResourceName);
            if (inputStream == null) continue;
            return inputStream;
        }
        if (!name.equals(realResourceName)) {
            log.debug("No resource found with alternate resourceName '{}'. Falling back to original name '{}'.", (Object)realResourceName, (Object)name);
            for (ClassLoader classloader : this.classLoaders) {
                inputStream = classloader.getResourceAsStream(name);
                if (inputStream == null) continue;
                return inputStream;
            }
        }
        return null;
    }

    private String getAlternativeResourceName(String name) {
        if (ALTERNATE_SERVICES.contains(name)) {
            log.debug("Service '{}' is registered as an alternate service.", (Object)name);
            return StringUtils.replace((String)name, (String)SERVICES_PREFIX, (String)ALTERNATE_SERVICES_PREFIX, (int)1);
        }
        return name;
    }

    @Override
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setDefaultAssertionStatus(enabled);
        }
    }

    @Override
    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setPackageAssertionStatus(packageName, enabled);
        }
    }

    @Override
    public synchronized void setClassAssertionStatus(String className, boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setClassAssertionStatus(className, enabled);
        }
    }

    @Override
    public synchronized void clearAssertionStatus() {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.clearAssertionStatus();
        }
    }

    private static final class ResourcesEnumeration
    implements Enumeration<URL> {
        private final List<Enumeration<URL>> resources;
        private final String resourceName;

        ResourcesEnumeration(String resourceName, List<ClassLoader> classLoaders) throws IOException {
            this.resourceName = resourceName;
            this.resources = new LinkedList<Enumeration<URL>>();
            for (ClassLoader classLoader : classLoaders) {
                this.resources.add(classLoader.getResources(resourceName));
            }
        }

        @Override
        public boolean hasMoreElements() {
            for (Enumeration<URL> resource : this.resources) {
                if (!resource.hasMoreElements()) continue;
                return true;
            }
            return false;
        }

        @Override
        public URL nextElement() {
            for (Enumeration<URL> resource : this.resources) {
                if (!resource.hasMoreElements()) continue;
                return resource.nextElement();
            }
            throw new NoSuchElementException(this.resourceName);
        }
    }
}

