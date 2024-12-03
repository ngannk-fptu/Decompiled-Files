/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.Log
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.ResourceCache
 *  org.apache.velocity.runtime.resource.ResourceCacheImpl
 *  org.apache.velocity.runtime.resource.ResourceFactory
 *  org.apache.velocity.runtime.resource.ResourceManager
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.apache.velocity.runtime.resource.loader.ResourceLoaderFactory
 *  org.apache.velocity.util.ClassUtils
 *  org.apache.velocity.util.StringUtils
 */
package com.atlassian.confluence.util.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoaderFactory;
import org.apache.velocity.util.ClassUtils;

@Deprecated(forRemoval=true)
public class ConfigurableResourceManager
implements ResourceManager {
    public static final int RESOURCE_TEMPLATE = 1;
    public static final int RESOURCE_CONTENT = 2;
    private static final String RESOURCE_LOADER_IDENTIFIER = "_RESOURCE_LOADER_IDENTIFIER_";
    private final List<String> allowedResourceTypes = List.of(".vm", ".vmd", ".xml", ".css");
    protected ResourceCache globalCache = null;
    protected final List<ResourceLoader> resourceLoaders = new ArrayList<ResourceLoader>();
    private final List<ExtendedProperties> sourceInitializerList = new ArrayList<ExtendedProperties>();
    private boolean isInit = false;
    private boolean logWhenFound = true;
    protected RuntimeServices rsvc = null;
    protected Log log = null;

    public synchronized void initialize(RuntimeServices rsvc) throws Exception {
        if (this.isInit) {
            this.log.warn((Object)"Re-initialization of ResourceLoader attempted!");
            return;
        }
        ResourceLoader resourceLoader = null;
        this.rsvc = rsvc;
        this.log = rsvc.getLog();
        this.log.debug((Object)("Default ResourceManager initializing. (" + this.getClass() + ")"));
        this.assembleResourceLoaderInitializers();
        for (ExtendedProperties configuration : this.sourceInitializerList) {
            String loaderClass = org.apache.velocity.util.StringUtils.nullTrim((String)configuration.getString("class"));
            ResourceLoader loaderInstance = (ResourceLoader)configuration.get((Object)"instance");
            if (loaderInstance != null) {
                resourceLoader = loaderInstance;
            } else if (loaderClass != null) {
                resourceLoader = ResourceLoaderFactory.getLoader((RuntimeServices)rsvc, (String)loaderClass);
            } else {
                this.log.error((Object)("Unable to find '" + configuration.getString(RESOURCE_LOADER_IDENTIFIER) + ".resource.loader.class' specification in configuration. This is a critical value.  Please adjust configuration."));
                continue;
            }
            resourceLoader = this.postProcessLoader(resourceLoader, configuration);
            resourceLoader.commonInit(rsvc, configuration);
            resourceLoader.init(configuration);
            this.resourceLoaders.add(resourceLoader);
        }
        this.logWhenFound = rsvc.getBoolean("resource.manager.logwhenfound", true);
        String cacheClassName = rsvc.getString("resource.manager.cache.class");
        Object cacheObject = null;
        if (StringUtils.isNotEmpty((CharSequence)cacheClassName)) {
            try {
                cacheObject = ClassUtils.getNewInstance((String)cacheClassName);
            }
            catch (ClassNotFoundException cnfe) {
                this.log.error((Object)("The specified class for ResourceCache (" + cacheClassName + ") does not exist or is not accessible to the current classloader."));
                cacheObject = null;
            }
            if (!(cacheObject instanceof ResourceCache)) {
                this.log.error((Object)("The specified class for ResourceCache (" + cacheClassName + ") does not implement " + ResourceCache.class.getName() + " ResourceManager. Using default ResourceCache implementation."));
                cacheObject = null;
            }
        }
        if (cacheObject == null) {
            cacheObject = new ResourceCacheImpl();
        }
        this.globalCache = (ResourceCache)cacheObject;
        this.globalCache.initialize(rsvc);
        this.log.trace((Object)"Default ResourceManager initialization complete.");
    }

    private void assembleResourceLoaderInitializers() {
        Vector resourceLoaderNames = this.rsvc.getConfiguration().getVector("resource.loader");
        org.apache.velocity.util.StringUtils.trimStrings((List)resourceLoaderNames);
        for (String loaderName : resourceLoaderNames) {
            StringBuilder loaderID = new StringBuilder(loaderName);
            loaderID.append(".").append("resource.loader");
            ExtendedProperties loaderConfiguration = this.rsvc.getConfiguration().subset(loaderID.toString());
            if (loaderConfiguration == null) {
                this.log.warn((Object)("ResourceManager : No configuration information for resource loader named '" + loaderName + "'. Skipping."));
                continue;
            }
            loaderConfiguration.setProperty(RESOURCE_LOADER_IDENTIFIER, (Object)loaderName);
            this.sourceInitializerList.add(loaderConfiguration);
        }
    }

    public Resource getResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        String resourceKey = resourceType + resourceName;
        Resource resource = this.globalCache.get((Object)resourceKey);
        if (resource != null) {
            try {
                this.refreshResource(resource, encoding);
            }
            catch (ResourceNotFoundException rnfe) {
                this.globalCache.remove((Object)resourceKey);
                return this.getResource(resourceName, resourceType, encoding);
            }
        } else {
            resource = this.loadResource(resourceName, resourceType, encoding);
            if (resource.getResourceLoader().isCachingOn()) {
                this.globalCache.put((Object)resourceKey, resource);
            }
        }
        return resource;
    }

    protected Resource loadResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        if (!this.isValidResourceName(resourceName)) {
            throw new ResourceNotFoundException("Resource name must end with .vm, .vmd, .css or .xml");
        }
        Resource resource = this.getResourceFactory().getResource(resourceName, resourceType);
        resource.setRuntimeServices(this.rsvc);
        resource.setName(resourceName);
        resource.setEncoding(encoding);
        long howOldItWas = 0L;
        for (ResourceLoader resourceLoader : this.resourceLoaders) {
            resource.setResourceLoader(resourceLoader);
            try {
                InputStream resourceStream = resourceLoader.getResourceStream(resource.getName());
                try {
                    if (resourceStream == null || !resource.process()) continue;
                    if (this.logWhenFound && this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Found " + resourceName + " with loader " + resourceLoader.getClassName()));
                    }
                    howOldItWas = resourceLoader.getLastModified(resource);
                    break;
                }
                finally {
                    if (resourceStream == null) continue;
                    resourceStream.close();
                }
            }
            catch (ResourceNotFoundException resourceNotFoundException) {}
        }
        if (resource.getData() == null) {
            throw new ResourceNotFoundException("Unable to find resource '" + resourceName + "'");
        }
        resource.setLastModified(howOldItWas);
        resource.setModificationCheckInterval(resource.getResourceLoader().getModificationCheckInterval());
        resource.touch();
        return resource;
    }

    protected void refreshResource(Resource resource, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        if (resource.requiresChecking()) {
            resource.touch();
            if (resource.isSourceModified()) {
                if (!StringUtils.equals((CharSequence)resource.getEncoding(), (CharSequence)encoding)) {
                    this.log.warn((Object)("Declared encoding for template '" + resource.getName() + "' is different on reload. Old = '" + resource.getEncoding() + "' New = '" + encoding));
                    resource.setEncoding(encoding);
                }
                long howOldItWas = resource.getResourceLoader().getLastModified(resource);
                resource.process();
                resource.setLastModified(howOldItWas);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String getLoaderNameForResource(String resourceName) {
        int index = 0;
        Iterator<ResourceLoader> iterator = this.resourceLoaders.iterator();
        while (iterator.hasNext()) {
            ResourceLoader resourceLoader = iterator.next();
            try (InputStream is = resourceLoader.getResourceStream(resourceName);){
                if (is != null) {
                    String string = this.sourceInitializerList.get(index).getString(RESOURCE_LOADER_IDENTIFIER);
                    return string;
                }
            }
            catch (IOException | ResourceNotFoundException throwable) {
                // empty catch block
            }
            ++index;
        }
        return null;
    }

    protected ResourceLoader postProcessLoader(ResourceLoader loader, ExtendedProperties properties) {
        return loader;
    }

    protected ResourceFactory getResourceFactory() {
        return DefaultResourceFactory.getInstance();
    }

    private boolean isValidResourceName(String resourceName) {
        String lowerCaseResourceName = resourceName.toLowerCase();
        return this.allowedResourceTypes.stream().anyMatch(lowerCaseResourceName::endsWith);
    }

    private static class DefaultResourceFactory
    implements ResourceFactory {
        private static final ResourceFactory INSTANCE = new DefaultResourceFactory();

        private DefaultResourceFactory() {
        }

        @Override
        public Resource getResource(String resourceName, int resourceType) {
            return org.apache.velocity.runtime.resource.ResourceFactory.getResource((String)resourceName, (int)resourceType);
        }

        public static ResourceFactory getInstance() {
            return INSTANCE;
        }
    }

    protected static interface ResourceFactory {
        public Resource getResource(String var1, int var2);
    }
}

