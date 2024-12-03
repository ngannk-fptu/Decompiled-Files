/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins.strategies;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleFinder;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.digester.plugins.strategies.LoaderFromStream;

public class FinderFromResource
extends RuleFinder {
    public static String DFLT_RESOURCE_ATTR = "resource";
    private String resourceAttr;

    public FinderFromResource() {
        this(DFLT_RESOURCE_ATTR);
    }

    public FinderFromResource(String resourceAttr) {
        this.resourceAttr = resourceAttr;
    }

    public RuleLoader findLoader(Digester d, Class pluginClass, Properties p) throws PluginException {
        String resourceName = p.getProperty(this.resourceAttr);
        if (resourceName == null) {
            return null;
        }
        InputStream is = pluginClass.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new PluginException("Resource " + resourceName + " not found.");
        }
        return FinderFromResource.loadRules(d, pluginClass, is, resourceName);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static RuleLoader loadRules(Digester d, Class pluginClass, InputStream is, String resourceName) throws PluginException {
        LoaderFromStream loaderFromStream;
        try {
            try {
                LoaderFromStream loader;
                loaderFromStream = loader = new LoaderFromStream(is);
                Object var7_7 = null;
            }
            catch (Exception e) {
                throw new PluginException("Unable to load xmlrules from resource [" + resourceName + "]", e);
            }
        }
        catch (Throwable throwable) {
            Object var7_8 = null;
            try {
                is.close();
                throw throwable;
            }
            catch (IOException ioe) {
                throw new PluginException("Unable to close stream for resource [" + resourceName + "]", ioe);
            }
        }
        try {}
        catch (IOException ioe) {
            throw new PluginException("Unable to close stream for resource [" + resourceName + "]", ioe);
        }
        is.close();
        return loaderFromStream;
    }
}

