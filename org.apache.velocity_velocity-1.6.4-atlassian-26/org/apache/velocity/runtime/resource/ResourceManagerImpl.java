/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;
import org.apache.velocity.runtime.resource.ResourceCacheImpl;
import org.apache.velocity.runtime.resource.ResourceFactory;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoaderFactory;
import org.apache.velocity.util.ClassUtils;

public class ResourceManagerImpl
implements ResourceManager {
    public static final int RESOURCE_TEMPLATE = 1;
    public static final int RESOURCE_CONTENT = 2;
    private static final String RESOURCE_LOADER_IDENTIFIER = "_RESOURCE_LOADER_IDENTIFIER_";
    protected ResourceCache globalCache = null;
    protected final List resourceLoaders = new ArrayList();
    private final List sourceInitializerList = new ArrayList();
    private boolean isInit = false;
    private boolean logWhenFound = true;
    protected RuntimeServices rsvc = null;
    protected Log log = null;

    @Override
    public synchronized void initialize(RuntimeServices rsvc) throws Exception {
        if (this.isInit) {
            this.log.debug("Re-initialization of ResourceLoader attempted and ignored.");
            return;
        }
        ResourceLoader resourceLoader = null;
        this.rsvc = rsvc;
        this.log = rsvc.getLog();
        this.log.trace("Default ResourceManager initializing. (" + this.getClass() + ")");
        this.assembleResourceLoaderInitializers();
        for (ExtendedProperties configuration : this.sourceInitializerList) {
            String loaderClass = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("class"));
            ResourceLoader loaderInstance = (ResourceLoader)configuration.get((Object)"instance");
            if (loaderInstance != null) {
                resourceLoader = loaderInstance;
            } else if (loaderClass != null) {
                resourceLoader = ResourceLoaderFactory.getLoader(rsvc, loaderClass);
            } else {
                String msg = "Unable to find '" + configuration.getString(RESOURCE_LOADER_IDENTIFIER) + ".resource.loader.class' specification in configuration. This is a critical value.  Please adjust configuration.";
                this.log.error(msg);
                throw new Exception(msg);
            }
            resourceLoader.commonInit(rsvc, configuration);
            resourceLoader.init(configuration);
            this.resourceLoaders.add(resourceLoader);
        }
        this.logWhenFound = rsvc.getBoolean("resource.manager.logwhenfound", true);
        String cacheClassName = rsvc.getString("resource.manager.cache.class");
        Object cacheObject = null;
        if (StringUtils.isNotEmpty((CharSequence)cacheClassName)) {
            try {
                cacheObject = ClassUtils.getNewInstance(cacheClassName);
            }
            catch (ClassNotFoundException cnfe) {
                String msg = "The specified class for ResourceCache (" + cacheClassName + ") does not exist or is not accessible to the current classloader.";
                this.log.error(msg, cnfe);
                throw cnfe;
            }
            if (!(cacheObject instanceof ResourceCache)) {
                String msg = "The specified resource cache class (" + cacheClassName + ") must implement " + ResourceCache.class.getName();
                this.log.error(msg);
                throw new RuntimeException(msg);
            }
        }
        if (cacheObject == null) {
            cacheObject = new ResourceCacheImpl();
        }
        this.globalCache = (ResourceCache)cacheObject;
        this.globalCache.initialize(rsvc);
        this.log.trace("Default ResourceManager initialization complete.");
    }

    private void assembleResourceLoaderInitializers() {
        Vector resourceLoaderNames = this.rsvc.getConfiguration().getVector("resource.loader");
        org.apache.velocity.util.StringUtils.trimStrings(resourceLoaderNames);
        for (String loaderName : resourceLoaderNames) {
            StringBuffer loaderID = new StringBuffer(loaderName);
            loaderID.append(".").append("resource.loader");
            ExtendedProperties loaderConfiguration = this.rsvc.getConfiguration().subset(loaderID.toString());
            if (loaderConfiguration == null) {
                this.log.debug("ResourceManager : No configuration information found for resource loader named '" + loaderName + "' (id is " + loaderID + "). Skipping it...");
                continue;
            }
            loaderConfiguration.setProperty(RESOURCE_LOADER_IDENTIFIER, (Object)loaderName);
            this.sourceInitializerList.add(loaderConfiguration);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Resource getResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        String resourceKey = resourceType + resourceName;
        Resource resource = this.globalCache.get(resourceKey);
        if (resource != null) {
            try {
                if (!resource.requiresChecking()) return resource;
                return this.refreshResource(resource, encoding);
            }
            catch (ResourceNotFoundException rnfe) {
                this.globalCache.remove(resourceKey);
                return this.getResource(resourceName, resourceType, encoding);
            }
            catch (ParseErrorException pee) {
                this.log.error("ResourceManager.getResource() exception", pee);
                throw pee;
            }
            catch (RuntimeException re) {
                this.log.error("ResourceManager.getResource() exception", re);
                throw re;
            }
            catch (Exception e) {
                this.log.error("ResourceManager.getResource() exception", e);
                throw e;
            }
        }
        try {
            resource = this.loadResource(resourceName, resourceType, encoding);
            if (!resource.getResourceLoader().isCachingOn()) return resource;
            this.globalCache.put(resourceKey, resource);
            return resource;
        }
        catch (ResourceNotFoundException rnfe) {
            this.log.error("ResourceManager : unable to find resource '" + resourceName + "' in any resource loader.");
            throw rnfe;
        }
        catch (ParseErrorException pee) {
            this.log.error("ResourceManager.getResource() parse exception", pee);
            throw pee;
        }
        catch (RuntimeException re) {
            this.log.error("ResourceManager.getResource() load exception", re);
            throw re;
        }
        catch (Exception e) {
            this.log.error("ResourceManager.getResource() exception new", e);
            throw e;
        }
    }

    protected Resource createResource(String resourceName, int resourceType) {
        return ResourceFactory.getResource(resourceName, resourceType);
    }

    protected Resource loadResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        Resource resource = this.createResource(resourceName, resourceType);
        resource.setRuntimeServices(this.rsvc);
        resource.setName(resourceName);
        resource.setEncoding(encoding);
        long howOldItWas = 0L;
        for (ResourceLoader resourceLoader : this.resourceLoaders) {
            resource.setResourceLoader(resourceLoader);
            try {
                if (!resource.process()) continue;
                if (this.logWhenFound && this.log.isDebugEnabled()) {
                    this.log.debug("ResourceManager : found " + resourceName + " with loader " + resourceLoader.getClassName());
                }
                howOldItWas = resourceLoader.getLastModified(resource);
                break;
            }
            catch (ResourceNotFoundException resourceNotFoundException) {
            }
        }
        if (resource.getData() == null) {
            throw new ResourceNotFoundException("Unable to find resource '" + resourceName + "'");
        }
        resource.setLastModified(howOldItWas);
        resource.setModificationCheckInterval(resource.getResourceLoader().getModificationCheckInterval());
        resource.touch();
        return resource;
    }

    protected Resource refreshResource(Resource resource, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        String name;
        resource.touch();
        ResourceLoader loader = resource.getResourceLoader();
        if (this.resourceLoaders.size() > 0 && this.resourceLoaders.indexOf(loader) > 0 && loader != this.getLoaderForResource(name = resource.getName())) {
            return this.loadResource(name, resource.getType(), encoding);
        }
        if (resource.isSourceModified()) {
            if (!StringUtils.equals((CharSequence)resource.getEncoding(), (CharSequence)encoding)) {
                this.log.warn("Declared encoding for template '" + resource.getName() + "' is different on reload. Old = '" + resource.getEncoding() + "' New = '" + encoding);
                resource.setEncoding(encoding);
            }
            long howOldItWas = loader.getLastModified(resource);
            String resourceKey = resource.getType() + resource.getName();
            Resource newResource = ResourceFactory.getResource(resource.getName(), resource.getType());
            newResource.setRuntimeServices(this.rsvc);
            newResource.setName(resource.getName());
            newResource.setEncoding(resource.getEncoding());
            newResource.setResourceLoader(loader);
            newResource.setModificationCheckInterval(loader.getModificationCheckInterval());
            newResource.process();
            newResource.setLastModified(howOldItWas);
            resource = newResource;
            this.globalCache.put(resourceKey, newResource);
        }
        return resource;
    }

    public Resource getResource(String resourceName, int resourceType) throws ResourceNotFoundException, ParseErrorException, Exception {
        return this.getResource(resourceName, resourceType, "ISO-8859-1");
    }

    @Override
    public String getLoaderNameForResource(String resourceName) {
        ResourceLoader loader = this.getLoaderForResource(resourceName);
        if (loader == null) {
            return null;
        }
        return loader.getClass().toString();
    }

    private ResourceLoader getLoaderForResource(String resourceName) {
        for (ResourceLoader loader : this.resourceLoaders) {
            if (!loader.resourceExists(resourceName)) continue;
            return loader;
        }
        return null;
    }
}

