/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import java.util.Properties;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;

public abstract class PropertiesModuleFactory {
    public static final String MODULE_NAME_KEY = "moduleName";
    public static final String MODULE_VERSION_KEY = "moduleVersion";

    public abstract ExtensionModule newModule(Properties var1, ClassLoader var2);
}

