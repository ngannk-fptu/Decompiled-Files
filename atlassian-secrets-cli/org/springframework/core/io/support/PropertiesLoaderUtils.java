/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.ResourceUtils;

public abstract class PropertiesLoaderUtils {
    private static final String XML_FILE_EXTENSION = ".xml";

    public static Properties loadProperties(EncodedResource resource) throws IOException {
        Properties props = new Properties();
        PropertiesLoaderUtils.fillProperties(props, resource);
        return props;
    }

    public static void fillProperties(Properties props, EncodedResource resource) throws IOException {
        PropertiesLoaderUtils.fillProperties(props, resource, new DefaultPropertiesPersister());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void fillProperties(Properties props, EncodedResource resource, PropertiesPersister persister) throws IOException {
        InputStream stream = null;
        Reader reader = null;
        try {
            String filename = resource.getResource().getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                stream = resource.getInputStream();
                persister.loadFromXml(props, stream);
            } else if (resource.requiresReader()) {
                reader = resource.getReader();
                persister.load(props, reader);
            } else {
                stream = resource.getInputStream();
                persister.load(props, stream);
            }
        }
        finally {
            if (stream != null) {
                stream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static Properties loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        PropertiesLoaderUtils.fillProperties(props, resource);
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void fillProperties(Properties props, Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream();){
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
        }
    }

    public static Properties loadAllProperties(String resourceName) throws IOException {
        return PropertiesLoaderUtils.loadAllProperties(resourceName, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties loadAllProperties(String resourceName, @Nullable ClassLoader classLoader) throws IOException {
        Assert.notNull((Object)resourceName, "Resource name must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = ClassUtils.getDefaultClassLoader();
        }
        Enumeration<URL> urls = classLoaderToUse != null ? classLoaderToUse.getResources(resourceName) : ClassLoader.getSystemResources(resourceName);
        Properties props = new Properties();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            URLConnection con = url.openConnection();
            ResourceUtils.useCachesIfNecessary(con);
            try (InputStream is = con.getInputStream();){
                if (resourceName.endsWith(XML_FILE_EXTENSION)) {
                    props.loadFromXML(is);
                    continue;
                }
                props.load(is);
            }
        }
        return props;
    }
}

