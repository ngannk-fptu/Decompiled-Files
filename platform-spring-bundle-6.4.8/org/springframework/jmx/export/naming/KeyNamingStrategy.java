/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.jmx.export.naming;

import java.io.IOException;
import java.util.Properties;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class KeyNamingStrategy
implements ObjectNamingStrategy,
InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Properties mappings;
    @Nullable
    private Resource[] mappingLocations;
    @Nullable
    private Properties mergedMappings;

    public void setMappings(Properties mappings) {
        this.mappings = mappings;
    }

    public void setMappingLocation(Resource location) {
        this.mappingLocations = new Resource[]{location};
    }

    public void setMappingLocations(Resource ... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        this.mergedMappings = new Properties();
        CollectionUtils.mergePropertiesIntoMap(this.mappings, this.mergedMappings);
        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Loading JMX object name mappings file from " + location));
                }
                PropertiesLoaderUtils.fillProperties(this.mergedMappings, location);
            }
        }
    }

    @Override
    public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
        Assert.notNull((Object)beanKey, "KeyNamingStrategy requires bean key");
        String objectName = null;
        if (this.mergedMappings != null) {
            objectName = this.mergedMappings.getProperty(beanKey);
        }
        if (objectName == null) {
            objectName = beanKey;
        }
        return ObjectNameManager.getInstance(objectName);
    }
}

