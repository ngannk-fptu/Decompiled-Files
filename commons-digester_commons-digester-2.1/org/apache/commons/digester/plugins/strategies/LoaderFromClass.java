/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.MethodUtils
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins.strategies;

import java.lang.reflect.Method;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LoaderFromClass
extends RuleLoader {
    private Class<?> rulesClass;
    private Method rulesMethod;

    public LoaderFromClass(Class<?> rulesClass, Method rulesMethod) {
        this.rulesClass = rulesClass;
        this.rulesMethod = rulesMethod;
    }

    public LoaderFromClass(Class<?> rulesClass, String methodName) throws PluginException {
        Method method = LoaderFromClass.locateMethod(rulesClass, methodName);
        if (method == null) {
            throw new PluginException("rule class " + rulesClass.getName() + " does not have method " + methodName + " or that method has an invalid signature.");
        }
        this.rulesClass = rulesClass;
        this.rulesMethod = method;
    }

    @Override
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

    public static Method locateMethod(Class<?> rulesClass, String methodName) throws PluginException {
        Class[] paramSpec = new Class[]{Digester.class, String.class};
        Method rulesMethod = MethodUtils.getAccessibleMethod(rulesClass, (String)methodName, (Class[])paramSpec);
        return rulesMethod;
    }
}

