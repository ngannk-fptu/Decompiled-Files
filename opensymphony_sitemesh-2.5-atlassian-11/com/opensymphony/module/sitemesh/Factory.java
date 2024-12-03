/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.rmi.PortableRemoteObject
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.util.ClassLoaderUtil;
import com.opensymphony.module.sitemesh.util.Container;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

public abstract class Factory
implements PageParserSelector {
    private static final String SITEMESH_FACTORY = "sitemesh.factory";

    public static Factory getInstance(Config config) {
        Factory instance = (Factory)config.getServletContext().getAttribute(SITEMESH_FACTORY);
        if (instance == null) {
            String factoryClass = Factory.getEnvEntry(SITEMESH_FACTORY, "com.opensymphony.module.sitemesh.factory.DefaultFactory");
            try {
                Class cls = ClassLoaderUtil.loadClass(factoryClass, config.getClass());
                Constructor con = cls.getConstructor(Config.class);
                instance = (Factory)con.newInstance(config);
                config.getServletContext().setAttribute(SITEMESH_FACTORY, (Object)instance);
            }
            catch (InvocationTargetException e) {
                throw new FactoryException("Cannot construct Factory : " + factoryClass, e.getTargetException());
            }
            catch (Exception e) {
                throw new FactoryException("Cannot construct Factory : " + factoryClass, e);
            }
        }
        instance.refresh();
        return instance;
    }

    public abstract void refresh();

    public abstract DecoratorMapper getDecoratorMapper();

    public abstract PageParser getPageParser(String var1);

    public abstract boolean shouldParsePage(String var1);

    public abstract boolean isPathExcluded(String var1);

    private static String getEnvEntry(String envEntry, String defaultValue) {
        String result = null;
        try {
            if (Container.get() != 6) {
                InitialContext ctx = new InitialContext();
                Object o = ctx.lookup("java:comp/env/" + envEntry);
                ctx.close();
                result = (String)PortableRemoteObject.narrow((Object)o, String.class);
            }
        }
        catch (Exception e) {
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        return result == null || result.trim().length() == 0 ? defaultValue : result;
    }
}

