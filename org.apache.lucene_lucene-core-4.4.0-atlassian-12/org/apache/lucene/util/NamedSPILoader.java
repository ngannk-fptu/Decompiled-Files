/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;
import org.apache.lucene.util.SPIClassIterator;

public final class NamedSPILoader<S extends NamedSPI>
implements Iterable<S> {
    private volatile Map<String, S> services = Collections.emptyMap();
    private final Class<S> clazz;

    public NamedSPILoader(Class<S> clazz) {
        this(clazz, Thread.currentThread().getContextClassLoader());
    }

    public NamedSPILoader(Class<S> clazz, ClassLoader classloader) {
        this.clazz = clazz;
        ClassLoader clazzClassloader = clazz.getClassLoader();
        if (clazzClassloader != null && !SPIClassIterator.isParentClassLoader(clazzClassloader, classloader)) {
            this.reload(clazzClassloader);
        }
        this.reload(classloader);
    }

    public synchronized void reload(ClassLoader classloader) {
        LinkedHashMap<String, S> services = new LinkedHashMap<String, S>(this.services);
        SPIClassIterator<S> loader = SPIClassIterator.get(this.clazz, classloader);
        while (loader.hasNext()) {
            Object c = loader.next();
            try {
                NamedSPI service = (NamedSPI)((Class)c).newInstance();
                String name = service.getName();
                if (services.containsKey(name)) continue;
                NamedSPILoader.checkServiceName(name);
                services.put(name, service);
            }
            catch (Exception e) {
                throw new ServiceConfigurationError("Cannot instantiate SPI class: " + ((Class)c).getName(), e);
            }
        }
        this.services = Collections.unmodifiableMap(services);
    }

    public static void checkServiceName(String name) {
        if (name.length() >= 128) {
            throw new IllegalArgumentException("Illegal service name: '" + name + "' is too long (must be < 128 chars).");
        }
        int len = name.length();
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            if (NamedSPILoader.isLetterOrDigit(c)) continue;
            throw new IllegalArgumentException("Illegal service name: '" + name + "' must be simple ascii alphanumeric.");
        }
    }

    private static boolean isLetterOrDigit(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9';
    }

    public S lookup(String name) {
        NamedSPI service = (NamedSPI)this.services.get(name);
        if (service != null) {
            return (S)service;
        }
        throw new IllegalArgumentException("A SPI class of type " + this.clazz.getName() + " with name '" + name + "' does not exist. You need to add the corresponding JAR file supporting this SPI to your classpath.The current classpath supports the following names: " + this.availableServices());
    }

    public Set<String> availableServices() {
        return this.services.keySet();
    }

    @Override
    public Iterator<S> iterator() {
        return this.services.values().iterator();
    }

    public static interface NamedSPI {
        public String getName();
    }
}

