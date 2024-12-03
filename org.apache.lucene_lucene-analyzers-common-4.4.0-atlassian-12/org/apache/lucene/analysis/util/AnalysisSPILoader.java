/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.SPIClassIterator
 */
package org.apache.lucene.analysis.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.util.SPIClassIterator;

final class AnalysisSPILoader<S extends AbstractAnalysisFactory> {
    private volatile Map<String, Class<? extends S>> services = Collections.emptyMap();
    private final Class<S> clazz;
    private final String[] suffixes;

    public AnalysisSPILoader(Class<S> clazz) {
        this(clazz, new String[]{clazz.getSimpleName()});
    }

    public AnalysisSPILoader(Class<S> clazz, ClassLoader loader) {
        this(clazz, new String[]{clazz.getSimpleName()}, loader);
    }

    public AnalysisSPILoader(Class<S> clazz, String[] suffixes) {
        this(clazz, suffixes, Thread.currentThread().getContextClassLoader());
    }

    public AnalysisSPILoader(Class<S> clazz, String[] suffixes, ClassLoader classloader) {
        this.clazz = clazz;
        this.suffixes = suffixes;
        ClassLoader clazzClassloader = clazz.getClassLoader();
        if (clazzClassloader != null && !SPIClassIterator.isParentClassLoader((ClassLoader)clazzClassloader, (ClassLoader)classloader)) {
            this.reload(clazzClassloader);
        }
        this.reload(classloader);
    }

    public synchronized void reload(ClassLoader classloader) {
        LinkedHashMap<String, Class<S>> services = new LinkedHashMap<String, Class<S>>(this.services);
        SPIClassIterator loader = SPIClassIterator.get(this.clazz, (ClassLoader)classloader);
        while (loader.hasNext()) {
            Class service = loader.next();
            String clazzName = service.getSimpleName();
            String name = null;
            for (String suffix : this.suffixes) {
                if (!clazzName.endsWith(suffix)) continue;
                name = clazzName.substring(0, clazzName.length() - suffix.length()).toLowerCase(Locale.ROOT);
                break;
            }
            if (name == null) {
                throw new ServiceConfigurationError("The class name " + service.getName() + " has wrong suffix, allowed are: " + Arrays.toString(this.suffixes));
            }
            if (services.containsKey(name)) continue;
            services.put(name, service);
        }
        this.services = Collections.unmodifiableMap(services);
    }

    public S newInstance(String name, Map<String, String> args) {
        Class<S> service = this.lookupClass(name);
        try {
            return (S)((AbstractAnalysisFactory)service.getConstructor(Map.class).newInstance(args));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("SPI class of type " + this.clazz.getName() + " with name '" + name + "' cannot be instantiated. This is likely due to a misconfiguration of the java class '" + service.getName() + "': ", e);
        }
    }

    public Class<? extends S> lookupClass(String name) {
        Class<? extends S> service = this.services.get(name.toLowerCase(Locale.ROOT));
        if (service != null) {
            return service;
        }
        throw new IllegalArgumentException("A SPI class of type " + this.clazz.getName() + " with name '" + name + "' does not exist. You need to add the corresponding JAR file supporting this SPI to your classpath.The current classpath supports the following names: " + this.availableServices());
    }

    public Set<String> availableServices() {
        return this.services.keySet();
    }
}

