/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins.strategies;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleFinder;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.digester.plugins.strategies.LoaderFromStream;

public class FinderFromFile
extends RuleFinder {
    public static String DFLT_FILENAME_ATTR = "file";
    private String filenameAttr;

    public FinderFromFile() {
        this(DFLT_FILENAME_ATTR);
    }

    public FinderFromFile(String filenameAttr) {
        this.filenameAttr = filenameAttr;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public RuleLoader findLoader(Digester d, Class pluginClass, Properties p) throws PluginException {
        LoaderFromStream loaderFromStream;
        String rulesFileName = p.getProperty(this.filenameAttr);
        if (rulesFileName == null) {
            return null;
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(rulesFileName);
        }
        catch (IOException ioe) {
            throw new PluginException("Unable to process file [" + rulesFileName + "]", ioe);
        }
        try {
            try {
                LoaderFromStream loader;
                loaderFromStream = loader = new LoaderFromStream(is);
                Object var9_10 = null;
            }
            catch (Exception e) {
                throw new PluginException("Unable to load xmlrules from file [" + rulesFileName + "]", e);
            }
        }
        catch (Throwable throwable) {
            Object var9_11 = null;
            try {
                ((InputStream)is).close();
                throw throwable;
            }
            catch (IOException ioe) {
                throw new PluginException("Unable to close stream for file [" + rulesFileName + "]", ioe);
            }
        }
        try {}
        catch (IOException ioe) {
            throw new PluginException("Unable to close stream for file [" + rulesFileName + "]", ioe);
        }
        ((InputStream)is).close();
        return loaderFromStream;
    }
}

