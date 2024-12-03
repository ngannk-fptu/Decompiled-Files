/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.management.DevModeClassLoader;
import net.sf.ehcache.management.ManagementServer;
import net.sf.ehcache.management.ResourceClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementServerLoader {
    static final Map<String, ManagementServerHolder> MGMT_SVR_BY_BIND = new HashMap<String, ManagementServerHolder>();
    private static final String PRIVATE_CLASSPATH = "rest-management-private-classpath";
    private static final Class<?> MANAGEMENT_SERVER_CLASS;
    private static final ClassLoader RESOURCE_CLASS_LOADER;
    private static final Logger LOG;

    public static boolean isManagementAvailable() {
        try {
            ServiceLoader<ManagementServer> loader = ServiceLoader.load(ManagementServer.class, RESOURCE_CLASS_LOADER);
            Iterator<ManagementServer> loaderIterator = loader.iterator();
            if (loaderIterator.hasNext()) {
                return true;
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to load META-INF/services/net.sf.ehcache.management.ManagementServer ; the management agent won't be available");
        }
        return false;
    }

    public static void register(CacheManager cacheManager, String clientUUID, ManagementRESTServiceConfiguration managementRESTServiceConfiguration) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(RESOURCE_CLASS_LOADER);
            if (!MGMT_SVR_BY_BIND.containsKey(managementRESTServiceConfiguration.getBind())) {
                if (!MGMT_SVR_BY_BIND.isEmpty()) {
                    String alreadyBound = MGMT_SVR_BY_BIND.keySet().iterator().next();
                    managementRESTServiceConfiguration.setBind(alreadyBound);
                    LOG.warn("You cannot have several Ehcache management rest agents running in the same ClassLoader; CacheManager " + cacheManager.getName() + " will be registered to the already running Ehcache management rest agent " + (String)("".equals(managementRESTServiceConfiguration.getBind()) ? "reachable through the TSA agent" : "listening on port " + alreadyBound) + ", the configuration will not be changed for " + cacheManager.getName());
                } else {
                    new ManagementServerHolder(ManagementServerLoader.loadOSorEEManagementServer()).start(managementRESTServiceConfiguration);
                }
            } else {
                LOG.warn("Another CacheManager already instantiated the Ehcache Management rest agent" + (String)("".equals(managementRESTServiceConfiguration.getBind()) ? ", reachable through the TSA agent" : ", on port " + managementRESTServiceConfiguration.getBind()) + ", the configuration will not be changed for " + cacheManager.getName());
            }
            ManagementServerHolder managementServerHolder = MGMT_SVR_BY_BIND.get(managementRESTServiceConfiguration.getBind());
            managementServerHolder.register(cacheManager, clientUUID);
        }
        catch (Exception e) {
            if (e.getCause() instanceof ClassNotFoundException) {
                throw new RuntimeException("Failed to initialize the ManagementRESTService - Did you include ehcache-rest-agent on the classpath?", e);
            }
            throw new CacheException("Failed to instantiate ManagementServer.", e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private static Object loadOSorEEManagementServer() throws Exception {
        ManagementServer managementServerImpl;
        ServiceLoader<ManagementServer> loader = ServiceLoader.load(ManagementServer.class, RESOURCE_CLASS_LOADER);
        Iterator<ManagementServer> loaderIterator = loader.iterator();
        if (!loaderIterator.hasNext()) {
            LOG.info("Could not find any META-INF/services/net.sf.ehcache.management.ManagementServer using the ResourceClassLoader; choosing the default OS implementation : net.sf.ehcache.management.ManagementServerImpl");
            Class<?> managementServerImplClass = RESOURCE_CLASS_LOADER.loadClass("net.sf.ehcache.management.ManagementServerImpl");
            Constructor<?> managementServerImplClassConstructor = managementServerImplClass.getConstructor(new Class[0]);
            managementServerImpl = managementServerImplClassConstructor.newInstance(new Object[0]);
        } else {
            managementServerImpl = loaderIterator.next();
            if (loaderIterator.hasNext()) {
                throw new RuntimeException("Several META-INF/services/net.sf.ehcache.management.ManagementServer found in the classpath, aborting agent start up");
            }
            LOG.info("The ManagementServer implementation that is going to be used is {} .", (Object)managementServerImpl.getClass().toString());
        }
        return managementServerImpl;
    }

    public static void unregister(String registeredMgmtSvrBind, CacheManager cacheManager) {
        ManagementServerHolder managementServerHolder = MGMT_SVR_BY_BIND.get(registeredMgmtSvrBind);
        try {
            managementServerHolder.unregister(cacheManager);
            if (!managementServerHolder.hasRegistered()) {
                managementServerHolder.stop(registeredMgmtSvrBind);
            }
        }
        catch (Exception e) {
            LOG.warn("Failed to shutdown the ManagementRESTService", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        LOG = LoggerFactory.getLogger(ManagementServerLoader.class);
        URL depsResource = DevModeClassLoader.devModeResource();
        RESOURCE_CLASS_LOADER = depsResource != null ? new DevModeClassLoader(depsResource, CacheManager.class.getClassLoader()) : new ResourceClassLoader(PRIVATE_CLASSPATH, CacheManager.class.getClassLoader());
        LOG.info("XXX: using classloader: " + RESOURCE_CLASS_LOADER);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Class<?> managementServerClass = null;
        try {
            Thread.currentThread().setContextClassLoader(RESOURCE_CLASS_LOADER);
            managementServerClass = RESOURCE_CLASS_LOADER.loadClass("net.sf.ehcache.management.ManagementServer");
        }
        catch (Exception e) {
            managementServerClass = null;
            if (e.getCause() instanceof ClassNotFoundException) {
                LOG.warn("Failed to initialize the ManagementRESTService - Did you include ehcache-rest-agent on the classpath?", (Throwable)e);
            } else {
                LOG.warn("Failed to load ManagementServer class. Management agent will not be available.", (Throwable)e);
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            MANAGEMENT_SERVER_CLASS = managementServerClass;
        }
    }

    static final class ManagementServerHolder {
        private Object managementServer;
        private final Map<String, String> clientUUIDs = new HashMap<String, String>();

        ManagementServerHolder(Object managementServer) {
            this.managementServer = managementServer;
        }

        void start(ManagementRESTServiceConfiguration managementRESTServiceConfiguration) throws Exception {
            Method initializeMethod = MANAGEMENT_SERVER_CLASS.getMethod("initialize", ManagementRESTServiceConfiguration.class);
            initializeMethod.invoke(this.managementServer, managementRESTServiceConfiguration);
            Method startMethod = MANAGEMENT_SERVER_CLASS.getMethod("start", new Class[0]);
            startMethod.invoke(this.managementServer, new Object[0]);
            MGMT_SVR_BY_BIND.put(managementRESTServiceConfiguration.getBind(), this);
        }

        void register(CacheManager cacheManager, String clientUUID) throws Exception {
            Method registerMethod = MANAGEMENT_SERVER_CLASS.getMethod("register", CacheManager.class);
            registerMethod.invoke(this.managementServer, cacheManager);
            if (clientUUID != null) {
                Method registerClusterRemoteEndpoint = MANAGEMENT_SERVER_CLASS.getMethod("registerClusterRemoteEndpoint", String.class);
                registerClusterRemoteEndpoint.invoke(this.managementServer, clientUUID);
                this.clientUUIDs.put(cacheManager.getName(), clientUUID);
            }
        }

        void unregister(CacheManager cacheManager) throws Exception {
            Method unregisterMethod = MANAGEMENT_SERVER_CLASS.getMethod("unregister", CacheManager.class);
            unregisterMethod.invoke(this.managementServer, cacheManager);
            String unregisteredClientUUID = this.clientUUIDs.remove(cacheManager.getName());
            if (unregisteredClientUUID != null) {
                Method unregisterClusterRemoteEndpoint = MANAGEMENT_SERVER_CLASS.getMethod("unregisterClusterRemoteEndpoint", String.class);
                unregisterClusterRemoteEndpoint.invoke(this.managementServer, unregisteredClientUUID);
            }
        }

        boolean hasRegistered() throws Exception {
            Method hasRegisteredMethod = MANAGEMENT_SERVER_CLASS.getMethod("hasRegistered", new Class[0]);
            return (Boolean)hasRegisteredMethod.invoke(this.managementServer, new Object[0]);
        }

        void stop(String bind) throws Exception {
            try {
                Method stopMethod = MANAGEMENT_SERVER_CLASS.getMethod("stop", new Class[0]);
                stopMethod.invoke(this.managementServer, new Object[0]);
            }
            finally {
                MGMT_SVR_BY_BIND.remove(bind);
            }
        }

        public Object getManagementServer() {
            return this.managementServer;
        }
    }
}

