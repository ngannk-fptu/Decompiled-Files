/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.service;

import com.sun.jersey.core.osgi.OsgiRegistry;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.impl.SpiMessages;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.ReflectPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServiceFinder<T>
implements Iterable<T> {
    private static final Logger LOGGER = Logger.getLogger(ServiceFinder.class.getName());
    private static final String MODULE_VERSION = "META-INF/jersey-module-version";
    private static final String PREFIX = "META-INF/services/";
    private static final String MODULE_VERSION_VALUE = ServiceFinder.getModuleVersion();
    private static final Set<String> MODULES_BLACKLIST = new HashSet<String>(){
        {
            this.add("jersey-client");
            this.add("jersey-core");
            this.add("jersey-gf-server");
            this.add("jersey-gf-servlet");
            this.add("jersey-gf-statsproviders");
            this.add("jersey-grizzly");
            this.add("jersey-json");
            this.add("jersey-moxy");
            this.add("jersey-multipart");
            this.add("jersey-server");
            this.add("jersey-servlet");
            this.add("jersey-statsproviders");
            this.add("glassfish-embedded");
        }
    };
    private final Class<T> serviceClass;
    private final String serviceName;
    private final ClassLoader classLoader;
    private final boolean ignoreOnClassNotFound;

    private static String getModuleVersion() {
        try {
            String resource = ServiceFinder.class.getName().replace(".", "/") + ".class";
            URL url = ServiceFinder.getResource(ServiceFinder.class.getClassLoader(), resource);
            if (url == null) {
                LOGGER.log(Level.FINE, "Error getting " + ServiceFinder.class.getName() + " class as a resource");
                return null;
            }
            return ServiceFinder.getJerseyModuleVersion(ServiceFinder.class.getName(), url);
        }
        catch (IOException ioe) {
            LOGGER.log(Level.FINE, "Error loading META-INF/jersey-module-version associated with " + ServiceFinder.class.getName(), ioe);
            return null;
        }
    }

    private static Enumeration<URL> filterServiceURLsWithVersion(String serviceName, Enumeration<URL> serviceUrls) {
        if (MODULE_VERSION_VALUE == null || !serviceUrls.hasMoreElements()) {
            return serviceUrls;
        }
        ArrayList<URL> urls = Collections.list(serviceUrls);
        ListIterator li = urls.listIterator();
        while (li.hasNext()) {
            String jerseyModuleVersion;
            URL url = (URL)li.next();
            if (!ServiceFinder.isServiceInBlacklistedModule(url) || (jerseyModuleVersion = ServiceFinder.getJerseyModuleVersion(serviceName, url)) == null || MODULE_VERSION_VALUE.equals(jerseyModuleVersion)) continue;
            li.remove();
        }
        return Collections.enumeration(urls);
    }

    private static boolean isServiceInBlacklistedModule(URL serviceUrl) {
        String service = serviceUrl.toString();
        for (String module : MODULES_BLACKLIST) {
            if (!service.contains(module)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getJerseyModuleVersion(String serviceName, URL serviceUrl) {
        InputStream stream = null;
        try {
            String url = serviceUrl.toString();
            String resource = url.endsWith("class") ? serviceName.replace(".", "/") + ".class" : serviceName;
            URL moduleVersionURL = new URL(url.replace(resource, MODULE_VERSION));
            stream = moduleVersionURL.openStream();
            String string = new BufferedReader(new InputStreamReader(stream)).readLine();
            return string;
        }
        catch (IOException ioe) {
            LOGGER.log(Level.FINE, "Error loading META-INF/jersey-module-version associated with " + ServiceFinder.class.getName());
            String string = null;
            return string;
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException ioe) {
                    LOGGER.log(Level.FINE, "Error closing stream associated with " + ServiceFinder.class.getName());
                }
            }
        }
    }

    private static URL getResource(ClassLoader loader, String name) throws IOException {
        if (loader == null) {
            return ServiceFinder.getResource(name);
        }
        URL resource = loader.getResource(name);
        if (resource != null) {
            return resource;
        }
        return ServiceFinder.getResource(name);
    }

    private static URL getResource(String name) throws IOException {
        if (ServiceFinder.class.getClassLoader() != null) {
            return ServiceFinder.class.getClassLoader().getResource(name);
        }
        return ClassLoader.getSystemResource(name);
    }

    private static Enumeration<URL> getResources(ClassLoader loader, String name) throws IOException {
        if (loader == null) {
            return ServiceFinder.getResources(name);
        }
        Enumeration<URL> resources = loader.getResources(name);
        if (resources != null && resources.hasMoreElements()) {
            return resources;
        }
        return ServiceFinder.getResources(name);
    }

    private static Enumeration<URL> getResources(String name) throws IOException {
        if (ServiceFinder.class.getClassLoader() != null) {
            return ServiceFinder.class.getClassLoader().getResources(name);
        }
        return ClassLoader.getSystemResources(name);
    }

    public static <T> ServiceFinder<T> find(Class<T> service, ClassLoader loader) throws ServiceConfigurationError {
        return ServiceFinder.find(service, loader, false);
    }

    public static <T> ServiceFinder<T> find(Class<T> service, ClassLoader loader, boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
        return new ServiceFinder<T>(service, loader, ignoreOnClassNotFound);
    }

    public static <T> ServiceFinder<T> find(Class<T> service) throws ServiceConfigurationError {
        return ServiceFinder.find(service, Thread.currentThread().getContextClassLoader(), false);
    }

    public static <T> ServiceFinder<T> find(Class<T> service, boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
        return ServiceFinder.find(service, Thread.currentThread().getContextClassLoader(), ignoreOnClassNotFound);
    }

    public static ServiceFinder<?> find(String serviceName) throws ServiceConfigurationError {
        return new ServiceFinder<Object>(Object.class, serviceName, Thread.currentThread().getContextClassLoader(), false);
    }

    public static void setIteratorProvider(ServiceIteratorProvider sip) throws SecurityException {
        ServiceIteratorProvider.setInstance(sip);
    }

    private ServiceFinder(Class<T> service, ClassLoader loader, boolean ignoreOnClassNotFound) {
        this(service, service.getName(), loader, ignoreOnClassNotFound);
    }

    private ServiceFinder(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
        this.serviceClass = service;
        this.serviceName = serviceName;
        this.classLoader = loader;
        this.ignoreOnClassNotFound = ignoreOnClassNotFound;
    }

    @Override
    public Iterator<T> iterator() {
        return ServiceIteratorProvider.getInstance().createIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);
    }

    private Iterator<Class<T>> classIterator() {
        return ServiceIteratorProvider.getInstance().createClassIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);
    }

    public T[] toArray() throws ServiceConfigurationError {
        ArrayList<T> result = new ArrayList<T>();
        for (T t : this) {
            result.add(t);
        }
        return result.toArray((Object[])Array.newInstance(this.serviceClass, result.size()));
    }

    public Class<T>[] toClassArray() throws ServiceConfigurationError {
        ArrayList<Class<T>> result = new ArrayList<Class<T>>();
        Iterator<Class<T>> i = this.classIterator();
        while (i.hasNext()) {
            result.add(i.next());
        }
        return result.toArray((Class[])Array.newInstance(Class.class, result.size()));
    }

    private static void fail(String serviceName, String msg, Throwable cause) throws ServiceConfigurationError {
        ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + msg);
        sce.initCause(cause);
        throw sce;
    }

    private static void fail(String serviceName, String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(serviceName + ": " + msg);
    }

    private static void fail(String serviceName, URL u, int line, String msg) throws ServiceConfigurationError {
        ServiceFinder.fail(serviceName, u + ":" + line + ": " + msg);
    }

    private static int parseLine(String serviceName, URL u, BufferedReader r, int lc, List<String> names, Set<String> returned) throws IOException, ServiceConfigurationError {
        int n;
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf(35);
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        if ((n = (ln = ln.trim()).length()) != 0) {
            int cp;
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
                ServiceFinder.fail(serviceName, u, lc, SpiMessages.ILLEGAL_CONFIG_SYNTAX());
            }
            if (!Character.isJavaIdentifierStart(cp = ln.codePointAt(0))) {
                ServiceFinder.fail(serviceName, u, lc, SpiMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (Character.isJavaIdentifierPart(cp) || cp == 46) continue;
                ServiceFinder.fail(serviceName, u, lc, SpiMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
            }
            if (!returned.contains(ln)) {
                names.add(ln);
                returned.add(ln);
            }
        }
        return lc + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Iterator<String> parse(String serviceName, URL u, Set<String> returned) throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<String>();
        try {
            URLConnection uConn = u.openConnection();
            uConn.setUseCaches(false);
            in = uConn.getInputStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = ServiceFinder.parseLine(serviceName, u, r, lc, names, returned)) >= 0) {
            }
        }
        catch (IOException x) {
            ServiceFinder.fail(serviceName, ": " + x);
        }
        finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException y) {
                ServiceFinder.fail(serviceName, ": " + y);
            }
        }
        return names.iterator();
    }

    static {
        OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
        if (osgiRegistry != null) {
            LOGGER.log(Level.CONFIG, "Running in an OSGi environment");
        } else {
            LOGGER.log(Level.CONFIG, "Running in a non-OSGi environment");
        }
    }

    public static final class DefaultServiceIteratorProvider<T>
    extends ServiceIteratorProvider<T> {
        @Override
        public Iterator<T> createIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            return new LazyObjectIterator(service, serviceName, loader, ignoreOnClassNotFound);
        }

        @Override
        public Iterator<Class<T>> createClassIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            return new LazyClassIterator(service, serviceName, loader, ignoreOnClassNotFound);
        }
    }

    public static abstract class ServiceIteratorProvider<T> {
        private static volatile ServiceIteratorProvider sip;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private static ServiceIteratorProvider getInstance() {
            DefaultServiceIteratorProvider result = sip;
            if (result != null) return result;
            Class<ServiceIteratorProvider> clazz = ServiceIteratorProvider.class;
            synchronized (ServiceIteratorProvider.class) {
                result = sip;
                if (result != null) return result;
                sip = result = new DefaultServiceIteratorProvider();
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return result;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private static void setInstance(ServiceIteratorProvider sip) throws SecurityException {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
                security.checkPermission(rp);
            }
            Class<ServiceIteratorProvider> clazz = ServiceIteratorProvider.class;
            synchronized (ServiceIteratorProvider.class) {
                ServiceIteratorProvider.sip = sip;
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return;
            }
        }

        public abstract Iterator<T> createIterator(Class<T> var1, String var2, ClassLoader var3, boolean var4);

        public abstract Iterator<Class<T>> createClassIterator(Class<T> var1, String var2, ClassLoader var3, boolean var4);
    }

    private static final class LazyObjectIterator<T>
    extends AbstractLazyIterator<T>
    implements Iterator<T> {
        private T t;

        private LazyObjectIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            super(service, serviceName, loader, ignoreOnClassNotFound);
        }

        @Override
        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            this.setConfigs();
            while (this.nextName == null) {
                while (this.pending == null || !this.pending.hasNext()) {
                    if (!this.configs.hasMoreElements()) {
                        return false;
                    }
                    this.pending = ServiceFinder.parse(this.serviceName, (URL)this.configs.nextElement(), this.returned);
                }
                this.nextName = (String)this.pending.next();
                try {
                    this.t = this.service.cast(AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader)).newInstance());
                }
                catch (InstantiationException ex) {
                    if (this.ignoreOnClassNotFound) {
                        if (LOGGER.isLoggable(Level.CONFIG)) {
                            LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex.getLocalizedMessage()));
                        }
                        this.nextName = null;
                        continue;
                    }
                    ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex.getLocalizedMessage()), ex);
                }
                catch (IllegalAccessException ex) {
                    ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex.getLocalizedMessage()), ex);
                }
                catch (ClassNotFoundException ex) {
                    this.handleClassNotFoundException();
                }
                catch (NoClassDefFoundError ex) {
                    if (this.ignoreOnClassNotFound) {
                        if (LOGGER.isLoggable(Level.CONFIG)) {
                            LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(ex.getLocalizedMessage(), this.nextName, this.service));
                        }
                        this.nextName = null;
                        continue;
                    }
                    ServiceFinder.fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(ex.getLocalizedMessage(), this.nextName, this.service), ex);
                }
                catch (PrivilegedActionException pae) {
                    Throwable cause = pae.getCause();
                    if (cause instanceof ClassNotFoundException) {
                        this.handleClassNotFoundException();
                        continue;
                    }
                    if (cause instanceof ClassFormatError) {
                        if (this.ignoreOnClassNotFound) {
                            if (LOGGER.isLoggable(Level.CONFIG)) {
                                LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service));
                            }
                            this.nextName = null;
                            continue;
                        }
                        ServiceFinder.fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service), cause);
                        continue;
                    }
                    ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, cause.getLocalizedMessage()), cause);
                }
            }
            return true;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            String cn = this.nextName;
            this.nextName = null;
            return this.t;
        }

        private void handleClassNotFoundException() throws ServiceConfigurationError {
            if (this.ignoreOnClassNotFound) {
                if (LOGGER.isLoggable(Level.CONFIG)) {
                    LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
                }
                this.nextName = null;
            } else {
                ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
            }
        }
    }

    private static final class LazyClassIterator<T>
    extends AbstractLazyIterator<T>
    implements Iterator<Class<T>> {
        private LazyClassIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            super(service, serviceName, loader, ignoreOnClassNotFound);
        }

        @Override
        public Class<T> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            String cn = this.nextName;
            this.nextName = null;
            try {
                Class tClass = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(cn, this.loader));
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Loading next class: " + tClass.getName());
                }
                return tClass;
            }
            catch (ClassNotFoundException ex) {
                ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(cn, this.service));
            }
            catch (PrivilegedActionException pae) {
                Throwable thrown = pae.getCause();
                if (thrown instanceof ClassNotFoundException) {
                    ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(cn, this.service));
                }
                if (thrown instanceof NoClassDefFoundError) {
                    ServiceFinder.fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), cn, this.service));
                }
                if (thrown instanceof ClassFormatError) {
                    ServiceFinder.fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), cn, this.service));
                }
                ServiceFinder.fail(this.serviceName, SpiMessages.PROVIDER_CLASS_COULD_NOT_BE_LOADED(cn, this.service, thrown.getLocalizedMessage()), thrown);
            }
            return null;
        }
    }

    private static class AbstractLazyIterator<T> {
        final Class<T> service;
        final String serviceName;
        final ClassLoader loader;
        final boolean ignoreOnClassNotFound;
        Enumeration<URL> configs = null;
        Iterator<String> pending = null;
        Set<String> returned = new TreeSet<String>();
        String nextName = null;

        private AbstractLazyIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            this.service = service;
            this.serviceName = serviceName;
            this.loader = loader;
            this.ignoreOnClassNotFound = ignoreOnClassNotFound;
        }

        protected final void setConfigs() {
            if (this.configs == null) {
                try {
                    String fullName = ServiceFinder.PREFIX + this.serviceName;
                    this.configs = ServiceFinder.filterServiceURLsWithVersion(fullName, ServiceFinder.getResources(this.loader, fullName));
                }
                catch (IOException x) {
                    ServiceFinder.fail(this.serviceName, ": " + x);
                }
            }
        }

        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            this.setConfigs();
            while (this.nextName == null) {
                while (this.pending == null || !this.pending.hasNext()) {
                    if (!this.configs.hasMoreElements()) {
                        return false;
                    }
                    this.pending = ServiceFinder.parse(this.serviceName, this.configs.nextElement(), this.returned);
                }
                this.nextName = this.pending.next();
                if (!this.ignoreOnClassNotFound) continue;
                try {
                    AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader));
                }
                catch (ClassNotFoundException ex) {
                    this.handleClassNotFoundException();
                }
                catch (PrivilegedActionException pae) {
                    Exception thrown = pae.getException();
                    if (thrown instanceof ClassNotFoundException) {
                        this.handleClassNotFoundException();
                        continue;
                    }
                    if (thrown instanceof NoClassDefFoundError) {
                        if (LOGGER.isLoggable(Level.CONFIG)) {
                            LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), this.nextName, this.service));
                        }
                        this.nextName = null;
                        continue;
                    }
                    if (thrown instanceof ClassFormatError) {
                        if (LOGGER.isLoggable(Level.CONFIG)) {
                            LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), this.nextName, this.service));
                        }
                        this.nextName = null;
                        continue;
                    }
                    if (thrown instanceof RuntimeException) {
                        throw (RuntimeException)thrown;
                    }
                    throw new IllegalStateException(thrown);
                }
            }
            return true;
        }

        private void handleClassNotFoundException() {
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
            }
            this.nextName = null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

