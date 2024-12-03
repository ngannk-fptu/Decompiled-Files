/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.LoadErrorHandler;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.utils.ServiceLoaderUtils;

public class ServiceLoader {
    private static final Map<Object, RankedService> SERVICES = new HashMap<Object, RankedService>();
    private static final Pattern COMMENT = Pattern.compile("#.*");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static volatile ClassLoader CONTEXT_CLASS_LOADER = null;
    private final ClassLoader loader;
    private final LoadErrorHandler handler;
    private final InitializableProblemHandler initializableProblemHandler;
    private final boolean dynamic;

    public ServiceLoader(ClassLoader loader, LoadErrorHandler handler, InitializableProblemHandler initializableProblemHandler, boolean dynamic) {
        this.loader = loader;
        this.handler = handler;
        this.initializableProblemHandler = initializableProblemHandler;
        this.dynamic = dynamic;
    }

    public ServiceLoader(ClassLoader loader, LoadErrorHandler handler, boolean dynamic) {
        this(loader, handler, InitializableProblemHandler.WARN, dynamic);
    }

    public ServiceLoader(ClassLoader loader, LoadErrorHandler handler) {
        this(loader, handler, false);
    }

    public ServiceLoader(ClassLoader loader) {
        this(loader, Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE);
    }

    public ServiceLoader() {
        this(ServiceLoader.getContextClassLoader(), Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE, true);
    }

    static ClassLoader getContextClassLoader() {
        ClassLoader loader = CONTEXT_CLASS_LOADER;
        if (loader == null) {
            loader = ServiceLoader.class.getClassLoader();
        }
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

    public static void setContextClassLoader(ClassLoader loader) {
        CONTEXT_CLASS_LOADER = loader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void addService(Object reference, Object service, int rank) {
        Map<Object, RankedService> map = SERVICES;
        synchronized (map) {
            SERVICES.put(reference, new RankedService(service, rank));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Object removeService(Object reference) {
        Map<Object, RankedService> map = SERVICES;
        synchronized (map) {
            return SERVICES.remove(reference);
        }
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public LoadErrorHandler getLoadErrorHandler() {
        return this.handler;
    }

    public InitializableProblemHandler getInitializableProblemHandler() {
        return this.initializableProblemHandler;
    }

    public InputStream getResourceAsStream(String name) {
        if (this.loader != null) {
            return this.loader.getResourceAsStream(name);
        }
        return null;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public <T> Class<? extends T> getServiceClass(Class<T> iface, String name) throws ClassNotFoundException {
        if (this.loader == null) {
            throw new ClassNotFoundException("Service class " + name + " is not available");
        }
        Class<?> klass = Class.forName(name, true, this.loader);
        if (klass.isInterface()) {
            throw new ClassNotFoundException("Service class " + name + " is an interface");
        }
        if (!iface.isAssignableFrom(klass)) {
            throw new ClassNotFoundException("Service class " + name + " does not implement " + iface.getName());
        }
        return klass;
    }

    public Enumeration<URL> findServiceResources(String filePattern) {
        try {
            return this.loader.getResources(filePattern);
        }
        catch (IOException ignore) {
            List empty = Collections.emptyList();
            return Collections.enumeration(empty);
        }
    }

    public <T> List<T> loadServiceProviders(Class<T> iface) {
        ArrayList<T> providers = new ArrayList<T>();
        providers.addAll(this.loadDynamicServiceProviders(iface));
        providers.addAll(this.loadStaticServiceProviders(iface));
        return providers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> List<T> loadDynamicServiceProviders(Class<T> iface) {
        if (this.dynamic) {
            Map<Object, RankedService> map = SERVICES;
            synchronized (map) {
                ArrayList<RankedService> list = new ArrayList<RankedService>(SERVICES.values());
                Collections.sort(list);
                ArrayList<Object> providers = new ArrayList<Object>(list.size());
                for (RankedService service : list) {
                    if (!service.isInstanceOf(iface)) continue;
                    providers.add(service.service);
                }
                return providers;
            }
        }
        return Collections.EMPTY_LIST;
    }

    protected <T> List<String> identifyStaticServiceProviders(Class<T> iface) {
        ArrayList<String> names = new ArrayList<String>();
        if (this.loader != null) {
            String serviceName = iface.getName();
            Enumeration<URL> resources = this.findServiceResources("META-INF/services/" + serviceName);
            for (URL resource : Collections.list(resources)) {
                try {
                    this.collectServiceClassNames(resource, names);
                }
                catch (IOException e) {
                    this.handler.handleLoadError(serviceName, e);
                }
            }
        }
        return names;
    }

    public <T> List<T> loadStaticServiceProviders(Class<T> iface) {
        return this.loadStaticServiceProviders(iface, Collections.EMPTY_SET);
    }

    public <T> List<T> loadStaticServiceProviders(Class<T> iface, Collection<Class<? extends T>> excludes) {
        ArrayList providers = new ArrayList();
        if (this.loader != null) {
            List<String> names = this.identifyStaticServiceProviders(iface);
            for (String name : names) {
                try {
                    Class<?> klass = this.loader.loadClass(name);
                    if (iface.isAssignableFrom(klass)) {
                        boolean shouldExclude = false;
                        for (Class<T> ex : excludes) {
                            if (!ex.isAssignableFrom(klass)) continue;
                            shouldExclude = true;
                            break;
                        }
                        if (shouldExclude) continue;
                        Object instance = ServiceLoaderUtils.newInstance(klass, this);
                        if (instance instanceof Initializable) {
                            ((Initializable)instance).initialize(Collections.EMPTY_MAP);
                            ((Initializable)instance).checkInitialization(this.initializableProblemHandler);
                        }
                        providers.add(instance);
                        continue;
                    }
                    throw new TikaConfigException("Class " + name + " is not of type: " + iface);
                }
                catch (Throwable t) {
                    this.handler.handleLoadError(name, t);
                }
            }
        }
        return providers;
    }

    private void collectServiceClassNames(URL resource, Collection<String> names) throws IOException {
        try (InputStream stream = resource.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
            String line = reader.readLine();
            while (line != null) {
                line = COMMENT.matcher(line).replaceFirst("");
                if ((line = WHITESPACE.matcher(line).replaceAll("")).length() > 0) {
                    names.add(line);
                }
                line = reader.readLine();
            }
        }
    }

    private static class RankedService
    implements Comparable<RankedService> {
        private final Object service;
        private final int rank;

        public RankedService(Object service, int rank) {
            this.service = service;
            this.rank = rank;
        }

        public boolean isInstanceOf(Class<?> iface) {
            return iface.isAssignableFrom(this.service.getClass());
        }

        @Override
        public int compareTo(RankedService that) {
            return that.rank - this.rank;
        }
    }
}

