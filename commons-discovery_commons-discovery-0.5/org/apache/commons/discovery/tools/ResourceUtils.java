/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.DiscoverResources;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResourceUtils {
    public static String getPackageName(Class<?> clazz) {
        String packageName;
        Package clazzPackage = clazz.getPackage();
        if (clazzPackage != null) {
            packageName = clazzPackage.getName();
        } else {
            String clazzName = clazz.getName();
            packageName = new String(clazzName.toCharArray(), 0, clazzName.lastIndexOf(46));
        }
        return packageName;
    }

    public static Resource getResource(Class<?> spi, String resourceName, ClassLoaders loaders) throws DiscoveryException {
        DiscoverResources explorer = new DiscoverResources(loaders);
        ResourceIterator resources = explorer.findResources(resourceName);
        if (spi != null && !resources.hasNext() && resourceName.charAt(0) != '/') {
            resourceName = ResourceUtils.getPackageName(spi).replace('.', '/') + "/" + resourceName;
            resources = explorer.findResources(resourceName);
        }
        return resources.hasNext() ? resources.nextResource() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties loadProperties(Class<?> spi, String propertiesFileName, ClassLoaders classLoaders) throws DiscoveryException {
        Properties properties;
        block7: {
            properties = null;
            if (propertiesFileName != null) {
                try {
                    InputStream stream;
                    Resource resource = ResourceUtils.getResource(spi, propertiesFileName, classLoaders);
                    if (resource == null || (stream = resource.getResourceAsStream()) == null) break block7;
                    properties = new Properties();
                    try {
                        properties.load(stream);
                    }
                    finally {
                        stream.close();
                    }
                }
                catch (IOException e) {
                }
                catch (SecurityException e) {
                    // empty catch block
                }
            }
        }
        return properties;
    }
}

