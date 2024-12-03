/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin;

import org.codehaus.groovy.vmplugin.VMPlugin;

public class VMPluginFactory {
    private static final String JDK5_CLASSNAME_CHECK = "java.lang.annotation.Annotation";
    private static final String JDK6_CLASSNAME_CHECK = "javax.script.ScriptEngine";
    private static final String JDK7_CLASSNAME_CHECK = "java.util.Objects";
    private static final String JDK8_CLASSNAME_CHECK = "java.util.Optional";
    private static final String JDK5_PLUGIN_NAME = "org.codehaus.groovy.vmplugin.v5.Java5";
    private static final String JDK6_PLUGIN_NAME = "org.codehaus.groovy.vmplugin.v6.Java6";
    private static final String JDK7_PLUGIN_NAME = "org.codehaus.groovy.vmplugin.v7.Java7";
    private static final String JDK8_PLUGIN_NAME = "org.codehaus.groovy.vmplugin.v8.Java8";
    private static VMPlugin plugin = VMPluginFactory.createPlugin("java.util.Optional", "org.codehaus.groovy.vmplugin.v8.Java8");

    public static VMPlugin getPlugin() {
        return plugin;
    }

    private static VMPlugin createPlugin(String classNameCheck, String pluginName) {
        try {
            ClassLoader loader = VMPluginFactory.class.getClassLoader();
            loader.loadClass(classNameCheck);
            return (VMPlugin)loader.loadClass(pluginName).newInstance();
        }
        catch (Throwable ex) {
            return null;
        }
    }

    static {
        if (plugin == null) {
            plugin = VMPluginFactory.createPlugin(JDK7_CLASSNAME_CHECK, JDK7_PLUGIN_NAME);
        }
        if (plugin == null) {
            plugin = VMPluginFactory.createPlugin(JDK6_CLASSNAME_CHECK, JDK6_PLUGIN_NAME);
        }
        if (plugin == null) {
            plugin = VMPluginFactory.createPlugin(JDK5_CLASSNAME_CHECK, JDK5_PLUGIN_NAME);
        }
    }
}

