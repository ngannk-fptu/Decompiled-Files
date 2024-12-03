/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import java.lang.reflect.Constructor;

public class LegacyModuleFactory
implements ModuleFactory {
    @Override
    public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) {
        throw new UnsupportedOperationException(" create Module not supported by LegacyModuleFactory. Use PrefixDelegatingModuleFactory instead.");
    }

    public <T> Class<T> getModuleClass(String name, ModuleDescriptor<T> moduleDescriptor) {
        try {
            Class loadedClass = moduleDescriptor.getPlugin().loadClass(name, null);
            try {
                Constructor noargConstructor = loadedClass.getConstructor(new Class[0]);
                if (noargConstructor != null) {
                    noargConstructor.newInstance(new Object[0]);
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            return loadedClass;
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException("Could not load class: " + name, e);
        }
        catch (NoClassDefFoundError e) {
            throw new PluginParseException("Error retrieving dependency of class: " + name + ". Missing class: " + e.getMessage(), e);
        }
        catch (UnsupportedClassVersionError e) {
            throw new PluginParseException("Class version is incompatible with current JVM: " + name, e);
        }
        catch (Throwable t) {
            throw new PluginParseException(t);
        }
    }
}

