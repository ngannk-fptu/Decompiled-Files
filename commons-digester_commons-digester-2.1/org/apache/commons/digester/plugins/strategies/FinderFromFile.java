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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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

    @Override
    public RuleLoader findLoader(Digester d, Class<?> pluginClass, Properties p) throws PluginException {
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
            LoaderFromStream loader;
            LoaderFromStream loaderFromStream = loader = new LoaderFromStream(is);
            return loaderFromStream;
        }
        catch (Exception e) {
            throw new PluginException("Unable to load xmlrules from file [" + rulesFileName + "]", e);
        }
        finally {
            try {
                ((InputStream)is).close();
            }
            catch (IOException ioe) {
                throw new PluginException("Unable to close stream for file [" + rulesFileName + "]", ioe);
            }
        }
    }
}

