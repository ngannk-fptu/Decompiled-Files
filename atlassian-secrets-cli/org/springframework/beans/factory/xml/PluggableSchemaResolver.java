/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class PluggableSchemaResolver
implements EntityResolver {
    public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";
    private static final Log logger = LogFactory.getLog(PluggableSchemaResolver.class);
    @Nullable
    private final ClassLoader classLoader;
    private final String schemaMappingsLocation;
    @Nullable
    private volatile Map<String, String> schemaMappings;

    public PluggableSchemaResolver(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
    }

    public PluggableSchemaResolver(@Nullable ClassLoader classLoader, String schemaMappingsLocation) {
        Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
        this.classLoader = classLoader;
        this.schemaMappingsLocation = schemaMappingsLocation;
    }

    @Override
    @Nullable
    public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws IOException {
        block7: {
            if (logger.isTraceEnabled()) {
                logger.trace("Trying to resolve XML entity with public id [" + publicId + "] and system id [" + systemId + "]");
            }
            if (systemId != null) {
                String resourceLocation = this.getSchemaMappings().get(systemId);
                if (resourceLocation == null && systemId.startsWith("https:")) {
                    resourceLocation = this.getSchemaMappings().get("http:" + systemId.substring(6));
                }
                if (resourceLocation != null) {
                    ClassPathResource resource = new ClassPathResource(resourceLocation, this.classLoader);
                    try {
                        InputSource source = new InputSource(resource.getInputStream());
                        source.setPublicId(publicId);
                        source.setSystemId(systemId);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
                        }
                        return source;
                    }
                    catch (FileNotFoundException ex) {
                        if (!logger.isDebugEnabled()) break block7;
                        logger.debug("Could not find XML schema [" + systemId + "]: " + resource, ex);
                    }
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, String> getSchemaMappings() {
        Map<String, String> schemaMappings = this.schemaMappings;
        if (schemaMappings == null) {
            PluggableSchemaResolver pluggableSchemaResolver = this;
            synchronized (pluggableSchemaResolver) {
                schemaMappings = this.schemaMappings;
                if (schemaMappings == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Loading schema mappings from [" + this.schemaMappingsLocation + "]");
                    }
                    try {
                        Properties mappings = PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, this.classLoader);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Loaded schema mappings: " + mappings);
                        }
                        schemaMappings = new ConcurrentHashMap<String, String>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
                        this.schemaMappings = schemaMappings;
                    }
                    catch (IOException ex) {
                        throw new IllegalStateException("Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return schemaMappings;
    }

    public String toString() {
        return "EntityResolver using schema mappings " + this.getSchemaMappings();
    }
}

