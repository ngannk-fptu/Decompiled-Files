/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.util.Properties;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.ResourceUtils;
import org.apache.commons.discovery.tools.SPInterface;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PropertiesHolder {
    private Properties properties;
    private final String propertiesFileName;

    public PropertiesHolder(Properties properties) {
        this.properties = properties;
        this.propertiesFileName = null;
    }

    public PropertiesHolder(String propertiesFileName) {
        this.properties = null;
        this.propertiesFileName = propertiesFileName;
    }

    public Properties getProperties(SPInterface<?> spi, ClassLoaders loaders) {
        if (this.properties == null) {
            this.properties = ResourceUtils.loadProperties(spi.getSPClass(), this.getPropertiesFileName(), loaders);
        }
        return this.properties;
    }

    public String getPropertiesFileName() {
        return this.propertiesFileName;
    }
}

