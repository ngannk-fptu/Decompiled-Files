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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FinderFromClass
extends RuleFinder {
    public static String DFLT_RULECLASS_ATTR = "ruleclass";
    public static String DFLT_METHOD_ATTR = "method";
    public static String DFLT_METHOD_NAME = "addRules";
    private String ruleClassAttr;
    private String methodAttr;
    private String dfltMethodName;

    public FinderFromClass() {
        this(DFLT_RULECLASS_ATTR, DFLT_METHOD_ATTR, DFLT_METHOD_NAME);
    }

    public FinderFromClass(String ruleClassAttr, String methodAttr, String dfltMethodName) {
        this.ruleClassAttr = ruleClassAttr;
        this.methodAttr = methodAttr;
        this.dfltMethodName = dfltMethodName;
    }

    @Override
    public RuleLoader findLoader(Digester digester, Class<?> pluginClass, Properties p) throws PluginException {
        Class<?> ruleClass;
        String ruleClassName = p.getProperty(this.ruleClassAttr);
        if (ruleClassName == null) {
            return null;
        }
        String methodName = null;
        if (this.methodAttr != null) {
            methodName = p.getProperty(this.methodAttr);
        }
        if (methodName == null) {
            methodName = this.dfltMethodName;
        }
        if (methodName == null) {
            methodName = DFLT_METHOD_NAME;
        }
        try {
            ruleClass = digester.getClassLoader().loadClass(ruleClassName);
        }
        catch (ClassNotFoundException cnfe) {
            throw new PluginException("Unable to load class " + ruleClassName, cnfe);
        }
        return new LoaderFromClass(ruleClass, methodName);
    }
}

