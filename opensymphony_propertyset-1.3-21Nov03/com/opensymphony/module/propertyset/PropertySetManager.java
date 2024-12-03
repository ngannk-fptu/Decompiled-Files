/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetCloner;
import com.opensymphony.module.propertyset.config.PropertySetConfig;
import java.util.Map;

public class PropertySetManager {
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$PropertySetManager;

    public static PropertySet getInstance(String name, Map args) {
        PropertySet ps = PropertySetManager.getInstance(name, args, (class$com$opensymphony$module$propertyset$PropertySetManager == null ? (class$com$opensymphony$module$propertyset$PropertySetManager = PropertySetManager.class$("com.opensymphony.module.propertyset.PropertySetManager")) : class$com$opensymphony$module$propertyset$PropertySetManager).getClassLoader());
        if (ps == null) {
            ps = PropertySetManager.getInstance(name, args, Thread.currentThread().getContextClassLoader());
        }
        return ps;
    }

    public static PropertySet getInstance(String name, Map args, ClassLoader loader) {
        Class<?> psClass;
        PropertySetConfig psc = PropertySetConfig.getConfig();
        String clazz = psc.getClassName(name);
        Map config = psc.getArgs(name);
        try {
            psClass = loader.loadClass(clazz);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
        try {
            PropertySet ps = (PropertySet)psClass.newInstance();
            ps.init(config, args);
            return ps;
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clone(PropertySet src, PropertySet dest) {
        PropertySetCloner cloner = new PropertySetCloner();
        cloner.setSource(src);
        cloner.setDestination(dest);
        cloner.cloneProperties();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

