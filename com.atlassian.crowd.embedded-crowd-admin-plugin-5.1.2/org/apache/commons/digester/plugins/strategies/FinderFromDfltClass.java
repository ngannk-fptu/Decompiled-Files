/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins.strategies;

import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleFinder;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.digester.plugins.strategies.LoaderFromClass;

public class FinderFromDfltClass
extends RuleFinder {
    public static String DFLT_RULECLASS_SUFFIX = "RuleInfo";
    public static String DFLT_METHOD_NAME = "addRules";
    private String rulesClassSuffix;
    private String methodName;

    public FinderFromDfltClass() {
        this(DFLT_RULECLASS_SUFFIX, DFLT_METHOD_NAME);
    }

    public FinderFromDfltClass(String rulesClassSuffix, String methodName) {
        this.rulesClassSuffix = rulesClassSuffix;
        this.methodName = methodName;
    }

    public RuleLoader findLoader(Digester digester, Class pluginClass, Properties p) throws PluginException {
        String rulesClassName = pluginClass.getName() + this.rulesClassSuffix;
        Class<?> rulesClass = null;
        try {
            rulesClass = digester.getClassLoader().loadClass(rulesClassName);
        }
        catch (ClassNotFoundException cnfe) {
            // empty catch block
        }
        if (rulesClass == null) {
            return null;
        }
        if (this.methodName == null) {
            this.methodName = DFLT_METHOD_NAME;
        }
        return new LoaderFromClass(rulesClass, this.methodName);
    }
}

