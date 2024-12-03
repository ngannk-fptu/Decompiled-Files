/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import groovy.lang.GroovyRuntimeException;
import java.util.Properties;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule;
import org.codehaus.groovy.runtime.m12n.PropertiesModuleFactory;

public class StandardPropertiesModuleFactory
extends PropertiesModuleFactory {
    public static final String MODULE_FACTORY_KEY = "moduleFactory";

    @Override
    public ExtensionModule newModule(Properties properties, ClassLoader classLoader) {
        String factoryName = properties.getProperty(MODULE_FACTORY_KEY);
        if (factoryName != null) {
            try {
                Class<?> factoryClass = classLoader.loadClass(factoryName);
                PropertiesModuleFactory delegate = (PropertiesModuleFactory)factoryClass.newInstance();
                return delegate.newModule(properties, classLoader);
            }
            catch (ClassNotFoundException e) {
                throw new GroovyRuntimeException("Unable to load module factory [" + factoryName + "]", e);
            }
            catch (InstantiationException e) {
                throw new GroovyRuntimeException("Unable to instantiate module factory [" + factoryName + "]", e);
            }
            catch (IllegalAccessException e) {
                throw new GroovyRuntimeException("Unable to instantiate module factory [" + factoryName + "]", e);
            }
        }
        return MetaInfExtensionModule.newModule(properties, classLoader);
    }
}

