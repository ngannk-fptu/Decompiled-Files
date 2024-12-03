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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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

    @Override
    public RuleLoader findLoader(Digester d, Class<?> pluginClass, Properties p) throws PluginException {
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

    public static RuleLoader loadRules(Digester d, Class<?> pluginClass, InputStream is, String resourceName) throws PluginException {
        try {
            LoaderFromStream loader;
            LoaderFromStream loaderFromStream = loader = new LoaderFromStream(is);
            return loaderFromStream;
        }
        catch (Exception e) {
            throw new PluginException("Unable to load xmlrules from resource [" + resourceName + "]", e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ioe) {
                throw new PluginException("Unable to close stream for resource [" + resourceName + "]", ioe);
            }
        }
    }
}

