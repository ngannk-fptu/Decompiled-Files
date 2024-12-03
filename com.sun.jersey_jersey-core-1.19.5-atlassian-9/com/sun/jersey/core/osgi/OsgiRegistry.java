/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.ext.RuntimeDelegate
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.BundleReference
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.SynchronousBundleListener
 */
package com.sun.jersey.core.osgi;

import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.impl.SpiMessages;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import com.sun.jersey.spi.service.ServiceFinder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ext.RuntimeDelegate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.SynchronousBundleListener;

public final class OsgiRegistry
implements SynchronousBundleListener {
    private static final String CoreBundleSymbolicNAME = "com.sun.jersey.core";
    private static final Logger LOGGER = Logger.getLogger(OsgiRegistry.class.getName());
    private final BundleContext bundleContext;
    private final Map<Long, Map<String, Callable<List<Class<?>>>>> factories = new HashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static OsgiRegistry instance;
    private Map<String, Bundle> classToBundleMapping = new HashMap<String, Bundle>();

    public static synchronized OsgiRegistry getInstance() {
        BundleContext context;
        ClassLoader classLoader;
        if (instance == null && (classLoader = ReflectionHelper.class.getClassLoader()) instanceof BundleReference && (context = FrameworkUtil.getBundle(OsgiRegistry.class).getBundleContext()) != null) {
            instance = new OsgiRegistry(context);
            instance.hookUp();
        }
        return instance;
    }

    public void bundleChanged(BundleEvent event) {
        if (event.getType() == 32) {
            this.register(event.getBundle());
        } else if (event.getType() == 64 || event.getType() == 16) {
            Bundle unregisteredBundle = event.getBundle();
            this.lock.writeLock().lock();
            try {
                this.factories.remove(unregisteredBundle.getBundleId());
                this.classToBundleMapping.values().removeAll(Collections.singleton(unregisteredBundle));
                if (unregisteredBundle.getSymbolicName().equals(CoreBundleSymbolicNAME)) {
                    this.bundleContext.removeBundleListener((BundleListener)this);
                    this.factories.clear();
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    private void setOSGiPackageScannerResourceProvider() {
        PackageNamesScanner.setResourcesProvider(new PackageNamesScanner.ResourcesProvider(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Enumeration<URL> getResources(String packagePath, ClassLoader classLoader) throws IOException {
                LinkedList<URL> result = new LinkedList<URL>();
                for (Bundle bundle : OsgiRegistry.this.bundleContext.getBundles()) {
                    for (String bundlePackagePath : new String[]{packagePath, "WEB-INF/classes/" + packagePath}) {
                        Enumeration enumeration = bundle.findEntries(bundlePackagePath, "*", false);
                        if (enumeration == null) continue;
                        while (enumeration.hasMoreElements()) {
                            URL url = (URL)enumeration.nextElement();
                            String path = url.getPath();
                            String className = (packagePath + path.substring(path.lastIndexOf(47))).replace('/', '.').replace(".class", "");
                            OsgiRegistry.this.classToBundleMapping.put(className, bundle);
                            result.add(url);
                        }
                    }
                    Enumeration jars = bundle.findEntries("/", "*.jar", true);
                    if (jars == null) continue;
                    while (jars.hasMoreElements()) {
                        JarInputStream jarInputStream;
                        URL jar = (URL)jars.nextElement();
                        InputStream inputStream = classLoader.getResourceAsStream(jar.getPath());
                        if (inputStream == null) {
                            LOGGER.config(SpiMessages.OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(jar));
                            continue;
                        }
                        try {
                            jarInputStream = new JarInputStream(inputStream);
                        }
                        catch (IOException ex) {
                            LOGGER.log(Level.CONFIG, SpiMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex);
                            try {
                                inputStream.close();
                            }
                            catch (IOException url) {}
                            continue;
                        }
                        try {
                            JarEntry jarEntry;
                            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                                String jarEntryName = jarEntry.getName();
                                if (!jarEntryName.endsWith(".class") || !jarEntryName.contains(packagePath)) continue;
                                OsgiRegistry.this.classToBundleMapping.put(jarEntryName.replace(".class", "").replace('/', '.'), bundle);
                                result.add(bundle.getResource(jarEntryName));
                            }
                        }
                        catch (Exception ex) {
                            LOGGER.log(Level.CONFIG, SpiMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex);
                        }
                        finally {
                            try {
                                jarInputStream.close();
                            }
                            catch (IOException iOException) {}
                        }
                    }
                }
                return Collections.enumeration(result);
            }
        });
    }

    public Class<?> classForNameWithException(String className) throws ClassNotFoundException {
        Bundle bundle = this.classToBundleMapping.get(className);
        if (bundle == null) {
            throw new ClassNotFoundException(className);
        }
        return bundle.loadClass(className);
    }

    private OsgiRegistry(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private void hookUp() {
        this.setOSGiPackageScannerResourceProvider();
        this.setOSGiServiceFinderIteratorProvider();
        this.bundleContext.addBundleListener((BundleListener)this);
        this.registerExistingBundles();
        Bundle jerseyServerBundle = this.getJerseyServerBundle(this.bundleContext);
        RuntimeDelegate runtimeDelegate = null;
        try {
            if (jerseyServerBundle == null) {
                LOGGER.config("jersey-client bundle registers JAX-RS RuntimeDelegate");
                runtimeDelegate = (RuntimeDelegate)this.getClass().getClassLoader().loadClass("com.sun.ws.rs.ext.RuntimeDelegateImpl").newInstance();
            } else {
                LOGGER.config("jersey-server bundle activator registers JAX-RS RuntimeDelegate instance");
                runtimeDelegate = (RuntimeDelegate)this.getClass().getClassLoader().loadClass("com.sun.jersey.server.impl.provider.RuntimeDelegateImpl").newInstance();
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to create RuntimeDelegate instance.", e);
        }
        RuntimeDelegate.setInstance(runtimeDelegate);
    }

    private Bundle getJerseyServerBundle(BundleContext bc) {
        for (Bundle b : bc.getBundles()) {
            String symbolicName = b.getSymbolicName();
            if (symbolicName == null || !symbolicName.endsWith("jersey-server") && !symbolicName.endsWith("jersey-gf-server")) continue;
            return b;
        }
        return null;
    }

    private void registerExistingBundles() {
        for (Bundle bundle : this.bundleContext.getBundles()) {
            if (bundle.getState() != 4 && bundle.getState() != 8 && bundle.getState() != 32 && bundle.getState() != 16) continue;
            this.register(bundle);
        }
    }

    private void setOSGiServiceFinderIteratorProvider() {
        ServiceFinder.setIteratorProvider(new OsgiServiceFinder());
    }

    private void register(Bundle bundle) {
        Map<String, Callable<List<Class<?>>>> map;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "checking bundle {0}", bundle.getBundleId());
        }
        this.lock.writeLock().lock();
        try {
            map = this.factories.get(bundle.getBundleId());
            if (map == null) {
                map = new ConcurrentHashMap();
                this.factories.put(bundle.getBundleId(), map);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        Enumeration e = bundle.findEntries("META-INF/services/", "*", false);
        if (e != null) {
            while (e.hasMoreElements()) {
                URL u = (URL)e.nextElement();
                String url = u.toString();
                if (url.endsWith("/")) continue;
                String factoryId = url.substring(url.lastIndexOf("/") + 1);
                map.put(factoryId, new BundleSpiProvidersLoader(factoryId, u, bundle));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<Class<?>> locateAllProviders(String serviceName) {
        this.lock.readLock().lock();
        try {
            LinkedList result = new LinkedList();
            for (Map<String, Callable<List<Class<?>>>> value : this.factories.values()) {
                if (!value.containsKey(serviceName)) continue;
                try {
                    result.addAll(value.get(serviceName).call());
                }
                catch (Exception exception) {}
            }
            LinkedList linkedList = result;
            return linkedList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    private static class BundleSpiProvidersLoader
    implements Callable<List<Class<?>>> {
        private final String spi;
        private final URL spiRegistryUrl;
        private final String spiRegistryUrlString;
        private final Bundle bundle;

        BundleSpiProvidersLoader(String spi, URL spiRegistryUrl, Bundle bundle) {
            this.spi = spi;
            this.spiRegistryUrl = spiRegistryUrl;
            this.spiRegistryUrlString = spiRegistryUrl.toExternalForm();
            this.bundle = bundle;
        }

        @Override
        public List<Class<?>> call() throws Exception {
            BufferedReader reader = null;
            try {
                String providerClassName;
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Loading providers for SPI: {0}", this.spi);
                }
                reader = new BufferedReader(new InputStreamReader(this.spiRegistryUrl.openStream(), "UTF-8"));
                ArrayList providerClasses = new ArrayList();
                while ((providerClassName = reader.readLine()) != null) {
                    if (providerClassName.trim().length() == 0) continue;
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "SPI provider: {0}", providerClassName);
                    }
                    providerClasses.add(this.bundle.loadClass(providerClassName));
                }
                ArrayList arrayList = providerClasses;
                return arrayList;
            }
            catch (Exception e) {
                LOGGER.log(Level.WARNING, "exception caught while creating factories: " + e);
                throw e;
            }
            catch (Error e) {
                LOGGER.log(Level.WARNING, "error caught while creating factories: " + e);
                throw e;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException ioe) {
                        LOGGER.log(Level.FINE, "Error closing SPI registry stream:" + this.spiRegistryUrl, ioe);
                    }
                }
            }
        }

        public String toString() {
            return this.spiRegistryUrlString;
        }

        public int hashCode() {
            return this.spiRegistryUrlString.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof BundleSpiProvidersLoader) {
                return this.spiRegistryUrlString.equals(((BundleSpiProvidersLoader)obj).spiRegistryUrlString);
            }
            return false;
        }
    }

    private final class OsgiServiceFinder<T>
    extends ServiceFinder.ServiceIteratorProvider<T> {
        final ServiceFinder.ServiceIteratorProvider defaultIterator = new ServiceFinder.DefaultServiceIteratorProvider();

        private OsgiServiceFinder() {
        }

        @Override
        public Iterator<T> createIterator(final Class<T> serviceClass, final String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            final List providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<T>(){
                    Iterator<Class<?>> it;
                    {
                        this.it = providerClasses.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public T next() {
                        Class<?> nextClass = this.it.next();
                        try {
                            return serviceClass.cast(nextClass.newInstance());
                        }
                        catch (Exception ex) {
                            ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(nextClass.getName(), serviceClass, ex.getLocalizedMessage()));
                            sce.initCause(ex);
                            throw sce;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createIterator(serviceClass, serviceName, loader, ignoreOnClassNotFound);
        }

        @Override
        public Iterator<Class<T>> createClassIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
            final List providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<Class<T>>(){
                    Iterator<Class<?>> it;
                    {
                        this.it = providerClasses.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public Class<T> next() {
                        return this.it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createClassIterator(service, serviceName, loader, ignoreOnClassNotFound);
        }
    }
}

