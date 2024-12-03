/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import groovy.lang.GroovyRuntimeException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.m12n.SimpleExtensionModule;

public class MetaInfExtensionModule
extends SimpleExtensionModule {
    private static final Logger LOG = Logger.getLogger(MetaInfExtensionModule.class.getName());
    public static final String MODULE_INSTANCE_CLASSES_KEY = "extensionClasses";
    public static final String MODULE_STATIC_CLASSES_KEY = "staticExtensionClasses";
    private final List<Class> instanceExtensionClasses;
    private final List<Class> staticExtensionClasses;

    @Override
    public List<Class> getInstanceMethodsExtensionClasses() {
        return this.instanceExtensionClasses;
    }

    @Override
    public List<Class> getStaticMethodsExtensionClasses() {
        return this.staticExtensionClasses;
    }

    private MetaInfExtensionModule(String moduleName, String moduleVersion, List<Class> instanceExtensionClasses, List<Class> staticExtensionClasses) {
        super(moduleName, moduleVersion);
        this.instanceExtensionClasses = instanceExtensionClasses;
        this.staticExtensionClasses = staticExtensionClasses;
    }

    public static MetaInfExtensionModule newModule(Properties properties, ClassLoader loader) {
        String name = properties.getProperty("moduleName");
        if (name == null) {
            throw new GroovyRuntimeException("Module file hasn't set the module name using key [moduleName]");
        }
        String version = properties.getProperty("moduleVersion");
        if (version == null) {
            throw new GroovyRuntimeException("Module file hasn't set the module version using key [moduleVersion]");
        }
        String[] extensionClasses = properties.getProperty(MODULE_INSTANCE_CLASSES_KEY, "").trim().split("[,; ]");
        String[] staticExtensionClasses = properties.getProperty(MODULE_STATIC_CLASSES_KEY, "").trim().split("[,; ]");
        ArrayList<Class> instanceClasses = new ArrayList<Class>(extensionClasses.length);
        ArrayList<Class> staticClasses = new ArrayList<Class>(staticExtensionClasses.length);
        LinkedList<String> errors = new LinkedList<String>();
        for (String extensionClass : extensionClasses) {
            try {
                extensionClass = extensionClass.trim();
                if (extensionClass.length() <= 0) continue;
                instanceClasses.add(loader.loadClass(extensionClass));
            }
            catch (ClassNotFoundException e) {
                errors.add(extensionClass);
            }
            catch (NoClassDefFoundError e) {
                errors.add(extensionClass);
            }
            catch (UnsupportedClassVersionError e) {
                errors.add(extensionClass);
            }
        }
        for (String extensionClass : staticExtensionClasses) {
            try {
                extensionClass = extensionClass.trim();
                if (extensionClass.length() <= 0) continue;
                staticClasses.add(loader.loadClass(extensionClass));
            }
            catch (ClassNotFoundException e) {
                errors.add(extensionClass);
            }
            catch (NoClassDefFoundError e) {
                errors.add(extensionClass);
            }
            catch (UnsupportedClassVersionError e) {
                errors.add(extensionClass);
            }
        }
        if (!errors.isEmpty()) {
            for (String error : errors) {
                LOG.warning("Module [" + name + "] - Unable to load extension class [" + error + "]");
            }
        }
        return new MetaInfExtensionModule(name, version, instanceClasses, staticClasses);
    }
}

