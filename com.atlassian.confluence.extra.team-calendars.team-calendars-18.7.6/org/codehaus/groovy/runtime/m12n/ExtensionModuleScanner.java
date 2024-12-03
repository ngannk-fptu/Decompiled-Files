/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import groovy.lang.GroovyRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.m12n.StandardPropertiesModuleFactory;

public class ExtensionModuleScanner {
    public static final String MODULE_META_INF_FILE = "META-INF/services/org.codehaus.groovy.runtime.ExtensionModule";
    private final ExtensionModuleListener listener;
    private final ClassLoader classLoader;

    public ExtensionModuleScanner(ExtensionModuleListener listener, ClassLoader loader) {
        this.listener = listener;
        this.classLoader = loader;
    }

    public void scanClasspathModules() {
        try {
            Enumeration<URL> resources = this.classLoader.getResources(MODULE_META_INF_FILE);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                this.scanExtensionModuleFromMetaInf(url);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void scanExtensionModuleFromMetaInf(URL metadata) {
        Properties properties = new Properties();
        InputStream inStream = null;
        try {
            inStream = metadata.openStream();
            properties.load(inStream);
        }
        catch (IOException e) {
            throw new GroovyRuntimeException("Unable to load module META-INF descriptor", e);
        }
        finally {
            DefaultGroovyMethodsSupport.closeQuietly(inStream);
        }
        this.scanExtensionModuleFromProperties(properties);
    }

    public void scanExtensionModuleFromProperties(Properties properties) {
        StandardPropertiesModuleFactory factory = new StandardPropertiesModuleFactory();
        ExtensionModule module = factory.newModule(properties, this.classLoader);
        this.listener.onModule(module);
    }

    public static interface ExtensionModuleListener {
        public void onModule(ExtensionModule var1);
    }
}

