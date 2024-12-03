/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins.strategies;

import java.lang.reflect.Method;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;

public class LoaderFromClass
extends RuleLoader {
    private Class rulesClass;
    private Method rulesMethod;
    static /* synthetic */ Class class$org$apache$commons$digester$Digester;
    static /* synthetic */ Class class$java$lang$String;

    public LoaderFromClass(Class rulesClass, Method rulesMethod) {
        this.rulesClass = rulesClass;
        this.rulesMethod = rulesMethod;
    }

    public LoaderFromClass(Class rulesClass, String methodName) throws PluginException {
        Method method = LoaderFromClass.locateMethod(rulesClass, methodName);
        if (method == null) {
            throw new PluginException("rule class " + rulesClass.getName() + " does not have method " + methodName + " or that method has an invalid signature.");
        }
        this.rulesClass = rulesClass;
        this.rulesMethod = method;
    }

    public void addRules(Digester d, String path) throws PluginException {
        Log log = d.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("LoaderFromClass loading rules for plugin at path [" + path + "]"));
        }
        try {
            Object[] params = new Object[]{d, path};
            this.rulesMethod.invoke(null, params);
        }
        catch (Exception e) {
            throw new PluginException("Unable to invoke rules method " + this.rulesMethod + " on rules class " + this.rulesClass, e);
        }
    }

    public static Method locateMethod(Class rulesClass, String methodName) throws PluginException {
        Class[] paramSpec = new Class[]{class$org$apache$commons$digester$Digester == null ? (class$org$apache$commons$digester$Digester = LoaderFromClass.class$("org.apache.commons.digester.Digester")) : class$org$apache$commons$digester$Digester, class$java$lang$String == null ? (class$java$lang$String = LoaderFromClass.class$("java.lang.String")) : class$java$lang$String};
        Method rulesMethod = MethodUtils.getAccessibleMethod(rulesClass, methodName, paramSpec);
        return rulesMethod;
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

