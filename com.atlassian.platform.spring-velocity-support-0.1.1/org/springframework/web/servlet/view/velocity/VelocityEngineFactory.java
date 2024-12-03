/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.exception.VelocityException
 *  org.apache.velocity.runtime.log.CommonsLogLogChute
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.view.velocity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.velocity.SpringResourceLoader;

public class VelocityEngineFactory {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private Resource configLocation;
    private final Map<String, Object> velocityProperties = new HashMap<String, Object>();
    private String resourceLoaderPath;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private boolean preferFileSystemAccess = true;
    private boolean overrideLogging = true;

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public void setVelocityProperties(Properties velocityProperties) {
        CollectionUtils.mergePropertiesIntoMap((Properties)velocityProperties, this.velocityProperties);
    }

    public void setVelocityPropertiesMap(Map<String, Object> velocityPropertiesMap) {
        if (velocityPropertiesMap != null) {
            this.velocityProperties.putAll(velocityPropertiesMap);
        }
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        this.resourceLoaderPath = resourceLoaderPath;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
        this.preferFileSystemAccess = preferFileSystemAccess;
    }

    private boolean isPreferFileSystemAccess() {
        return this.preferFileSystemAccess;
    }

    public void setOverrideLogging(boolean overrideLogging) {
        this.overrideLogging = overrideLogging;
    }

    public VelocityEngine createVelocityEngine() throws IOException, VelocityException {
        VelocityEngine velocityEngine = this.newVelocityEngine();
        HashMap<String, Object> props = new HashMap<String, Object>();
        if (this.configLocation != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Loading Velocity config from [" + this.configLocation + "]"));
            }
            CollectionUtils.mergePropertiesIntoMap((Properties)PropertiesLoaderUtils.loadProperties((Resource)this.configLocation), props);
        }
        if (!this.velocityProperties.isEmpty()) {
            props.putAll(this.velocityProperties);
        }
        if (this.resourceLoaderPath != null) {
            this.initVelocityResourceLoader(velocityEngine, this.resourceLoaderPath);
        }
        if (this.overrideLogging) {
            velocityEngine.setProperty("runtime.log.logsystem", (Object)new CommonsLogLogChute());
        }
        for (Map.Entry entry : props.entrySet()) {
            velocityEngine.setProperty((String)entry.getKey(), entry.getValue());
        }
        this.postProcessVelocityEngine(velocityEngine);
        velocityEngine.init();
        return velocityEngine;
    }

    private VelocityEngine newVelocityEngine() throws IOException, VelocityException {
        return new VelocityEngine();
    }

    private void initVelocityResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
        if (this.isPreferFileSystemAccess()) {
            try {
                StringBuilder resolvedPath = new StringBuilder();
                String[] paths = StringUtils.commaDelimitedListToStringArray((String)resourceLoaderPath);
                for (int i = 0; i < paths.length; ++i) {
                    String path = paths[i];
                    Resource resource = this.getResourceLoader().getResource(path);
                    File file = resource.getFile();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Resource loader path [" + path + "] resolved to file [" + file.getAbsolutePath() + "]"));
                    }
                    resolvedPath.append(file.getAbsolutePath());
                    if (i >= paths.length - 1) continue;
                    resolvedPath.append(',');
                }
                velocityEngine.setProperty("resource.loader", (Object)"file");
                velocityEngine.setProperty("file.resource.loader.cache", (Object)"true");
                velocityEngine.setProperty("file.resource.loader.path", (Object)resolvedPath.toString());
            }
            catch (IOException ex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Cannot resolve resource loader path [" + resourceLoaderPath + "] to [java.io.File]: using SpringResourceLoader"), (Throwable)ex);
                }
                this.initSpringResourceLoader(velocityEngine, resourceLoaderPath);
            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"File system access not preferred: using SpringResourceLoader");
            }
            this.initSpringResourceLoader(velocityEngine, resourceLoaderPath);
        }
    }

    private void initSpringResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
        velocityEngine.setProperty("resource.loader", (Object)"spring");
        velocityEngine.setProperty("spring.resource.loader.class", (Object)SpringResourceLoader.class.getName());
        velocityEngine.setProperty("spring.resource.loader.cache", (Object)"true");
        velocityEngine.setApplicationAttribute((Object)"spring.resource.loader", (Object)this.getResourceLoader());
        velocityEngine.setApplicationAttribute((Object)"spring.resource.loader.path", (Object)resourceLoaderPath);
    }

    protected void postProcessVelocityEngine(VelocityEngine velocityEngine) throws VelocityException {
    }
}

