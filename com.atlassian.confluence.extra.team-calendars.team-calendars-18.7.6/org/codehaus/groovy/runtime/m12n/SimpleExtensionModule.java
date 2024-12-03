/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import groovy.lang.MetaMethod;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;

public abstract class SimpleExtensionModule
extends ExtensionModule {
    private static final Logger LOG = Logger.getLogger(SimpleExtensionModule.class.getName());

    public SimpleExtensionModule(String moduleName, String moduleVersion) {
        super(moduleName, moduleVersion);
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        LinkedList<MetaMethod> metaMethods = new LinkedList<MetaMethod>();
        List<Class> extensionClasses = this.getInstanceMethodsExtensionClasses();
        for (Class extensionClass : extensionClasses) {
            try {
                SimpleExtensionModule.createMetaMethods(extensionClass, metaMethods, false);
            }
            catch (LinkageError e) {
                LOG.warning("Module [" + this.getName() + "] - Unable to load extension class [" + extensionClass + "] due to [" + e.getMessage() + "]. Maybe this module is not supported by your JVM version.");
            }
        }
        extensionClasses = this.getStaticMethodsExtensionClasses();
        for (Class extensionClass : extensionClasses) {
            try {
                SimpleExtensionModule.createMetaMethods(extensionClass, metaMethods, true);
            }
            catch (LinkageError e) {
                LOG.warning("Module [" + this.getName() + "] - Unable to load extension class [" + extensionClass + "] due to [" + e.getMessage() + "]. Maybe this module is not supported by your JVM version.");
            }
        }
        return metaMethods;
    }

    private static void createMetaMethods(Class extensionClass, List<MetaMethod> metaMethods, boolean isStatic) {
        CachedMethod[] methods;
        CachedClass cachedClass = ReflectionCache.getCachedClass(extensionClass);
        for (CachedMethod method : methods = cachedClass.getMethods()) {
            if (!method.isStatic() || !method.isPublic() || method.getParamsCount() <= 0) continue;
            metaMethods.add(isStatic ? new NewStaticMetaMethod(method) : new NewInstanceMetaMethod(method));
        }
    }

    public abstract List<Class> getInstanceMethodsExtensionClasses();

    public abstract List<Class> getStaticMethodsExtensionClasses();
}

