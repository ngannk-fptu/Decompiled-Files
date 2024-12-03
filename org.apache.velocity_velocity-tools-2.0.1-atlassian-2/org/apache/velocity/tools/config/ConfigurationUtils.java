/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.exception.ResourceNotFoundException
 */
package org.apache.velocity.tools.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationCleaner;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.PropertiesFactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;

public class ConfigurationUtils {
    public static final String GENERIC_DEFAULTS_PATH = "/org/apache/velocity/tools/generic/tools.xml";
    public static final String VIEW_DEFAULTS_PATH = "/org/apache/velocity/tools/view/tools.xml";
    public static final String STRUTS_DEFAULTS_PATH = "/org/apache/velocity/tools/struts/tools.xml";
    public static final String AUTOLOADED_XML_PATH = "tools.xml";
    public static final String AUTOLOADED_PROPS_PATH = "tools.properties";
    public static final String SYSTEM_PROPERTY_KEY = "org.apache.velocity.tools";
    public static final ConfigurationUtils INSTANCE = new ConfigurationUtils();
    public static final String CONFIG_FACTORY_METHOD = "getConfiguration";

    private ConfigurationUtils() {
    }

    public ConfigurationUtils getInstance() {
        return INSTANCE;
    }

    public static FactoryConfiguration getDefaultTools() {
        XmlFactoryConfiguration config = new XmlFactoryConfiguration("ConfigurationUtils.getDefaultTools()");
        config.read(GENERIC_DEFAULTS_PATH);
        config.read(VIEW_DEFAULTS_PATH, false);
        config.read(STRUTS_DEFAULTS_PATH, false);
        ConfigurationUtils.clean(config);
        return config;
    }

    public static FactoryConfiguration getGenericTools() {
        XmlFactoryConfiguration config = new XmlFactoryConfiguration("ConfigurationUtils.getGenericTools()");
        config.read(GENERIC_DEFAULTS_PATH);
        ConfigurationUtils.clean(config);
        return config;
    }

    public static FactoryConfiguration getVelocityView() {
        XmlFactoryConfiguration config = new XmlFactoryConfiguration("ConfigurationUtils.getVelocityView()");
        config.read(GENERIC_DEFAULTS_PATH);
        config.read(VIEW_DEFAULTS_PATH);
        ConfigurationUtils.clean(config);
        return config;
    }

    public static FactoryConfiguration getVelocityStruts() {
        XmlFactoryConfiguration config = new XmlFactoryConfiguration("ConfigurationUtils.getVelocityStruts()");
        config.read(GENERIC_DEFAULTS_PATH);
        config.read(VIEW_DEFAULTS_PATH);
        config.read(STRUTS_DEFAULTS_PATH);
        ConfigurationUtils.clean(config);
        return config;
    }

    public static FactoryConfiguration getAutoLoaded() {
        return ConfigurationUtils.getAutoLoaded(true);
    }

    public static FactoryConfiguration getAutoLoaded(boolean includeDefaults) {
        FactoryConfiguration fsProps;
        FactoryConfiguration fsXml;
        FactoryConfiguration cpProps;
        FactoryConfiguration auto = includeDefaults ? ConfigurationUtils.getDefaultTools() : new FactoryConfiguration("ConfigurationUtils.getAutoLoaded(false)");
        FactoryConfiguration cpXml = ConfigurationUtils.findInClasspath(AUTOLOADED_XML_PATH);
        if (cpXml != null) {
            auto.addConfiguration(cpXml);
        }
        if ((cpProps = ConfigurationUtils.findInClasspath(AUTOLOADED_PROPS_PATH)) != null) {
            auto.addConfiguration(cpProps);
        }
        if ((fsXml = ConfigurationUtils.findInFileSystem(AUTOLOADED_XML_PATH)) != null) {
            auto.addConfiguration(fsXml);
        }
        if ((fsProps = ConfigurationUtils.findInFileSystem(AUTOLOADED_PROPS_PATH)) != null) {
            auto.addConfiguration(fsProps);
        }
        return auto;
    }

    public static FactoryConfiguration findFromSystemProperty() {
        String path = System.getProperty(SYSTEM_PROPERTY_KEY);
        if (path == null || path.length() == 0) {
            return null;
        }
        return ConfigurationUtils.load(path);
    }

    public static ToolboxFactory createFactory() {
        FactoryConfiguration auto = ConfigurationUtils.getAutoLoaded();
        FactoryConfiguration sys = ConfigurationUtils.findFromSystemProperty();
        if (sys != null) {
            auto.addConfiguration(sys);
        }
        ToolboxFactory factory = new ToolboxFactory();
        factory.configure(auto);
        return factory;
    }

    public static void clean(Configuration config) {
        ConfigurationCleaner cleaner = new ConfigurationCleaner();
        cleaner.clean(config);
    }

    public static FactoryConfiguration load(String path) {
        FactoryConfiguration config = ConfigurationUtils.find(path);
        if (config == null) {
            throw new ResourceNotFoundException("Could not find configuration at " + path);
        }
        return config;
    }

    public static FactoryConfiguration find(String path) {
        FactoryConfiguration cp = ConfigurationUtils.findInClasspath(path);
        FactoryConfiguration fs = ConfigurationUtils.findInFileSystem(path);
        if (cp != null) {
            if (fs != null) {
                cp.addConfiguration(fs);
            }
            return cp;
        }
        return fs;
    }

    public static FactoryConfiguration findInFileSystem(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                return ConfigurationUtils.read(file.toURL());
            }
            catch (MalformedURLException mue) {
                throw new IllegalStateException("Could not convert existing file path \"" + path + "\" to URL", mue);
            }
        }
        return null;
    }

    public static FactoryConfiguration findInClasspath(String path) {
        return ConfigurationUtils.findInClasspath(path, new ConfigurationUtils());
    }

    public static FactoryConfiguration findInClasspath(String path, Object caller) {
        List<URL> found = ClassUtils.getResources(path, caller);
        if (found.isEmpty()) {
            return null;
        }
        if (found.size() == 1) {
            return ConfigurationUtils.read(found.get(0));
        }
        FactoryConfiguration config = new FactoryConfiguration("ConfigurationUtils.findInClassPath(" + path + "," + caller + ")");
        boolean readAConfig = false;
        for (URL resource : found) {
            FactoryConfiguration c = ConfigurationUtils.read(resource);
            if (c == null) continue;
            readAConfig = true;
            config.addConfiguration(c);
        }
        if (readAConfig) {
            return config;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static FactoryConfiguration read(URL url) {
        FileFactoryConfiguration config = null;
        String path = url.toString();
        String source = "ConfigurationUtils.read(" + url.toString() + ")";
        if (path.endsWith(".xml")) {
            config = new XmlFactoryConfiguration(source);
        } else if (path.endsWith(".properties")) {
            config = new PropertiesFactoryConfiguration(source);
        } else {
            if (path.endsWith(".class")) {
                String fqn = path.substring(0, path.indexOf(46)).replace('/', '.');
                return ConfigurationUtils.getFromClass(fqn);
            }
            String msg = "Unknown configuration file type: " + url.toString() + "\nOnly .xml and .properties configuration files are supported at this time.";
            throw new UnsupportedOperationException(msg);
        }
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            config.read(inputStream);
        }
        catch (IOException ioe) {
            FactoryConfiguration factoryConfiguration = null;
            return factoryConfiguration;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ioe) {
                    throw new RuntimeException("Could not close input stream for " + path, ioe);
                }
            }
        }
        return config;
    }

    public static FactoryConfiguration getFromClass(String classname) {
        try {
            Class configFactory = ClassUtils.getClass(classname);
            return ConfigurationUtils.getFromClass(configFactory);
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Could not find class " + classname, cnfe);
        }
    }

    public static FactoryConfiguration getFromClass(Class factory) {
        Method getConf = null;
        try {
            getConf = factory.getMethod(CONFIG_FACTORY_METHOD, null);
        }
        catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("Could not find getConfiguration in class " + factory.getName(), nsme);
        }
        Object instance = null;
        if (!Modifier.isStatic(getConf.getModifiers())) {
            try {
                instance = factory.newInstance();
            }
            catch (Exception e) {
                throw new IllegalArgumentException(factory.getName() + " must have usable default constructor or else " + CONFIG_FACTORY_METHOD + " must be declared static", e);
            }
        }
        try {
            FactoryConfiguration result = (FactoryConfiguration)getConf.invoke(instance, (Object[])null);
            if (result == null) {
                throw new IllegalArgumentException("Method getConfiguration in class " + factory.getName() + " should not return null or void");
            }
            return result;
        }
        catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Failed to invoke getConfiguration in class " + factory.getName(), iae);
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (InvocationTargetException ite) {
            throw new IllegalArgumentException("There was an exception while executing getConfiguration in class " + factory.getName(), ite.getCause());
        }
    }
}

