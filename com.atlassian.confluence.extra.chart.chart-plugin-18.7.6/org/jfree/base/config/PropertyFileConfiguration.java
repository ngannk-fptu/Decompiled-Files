/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;

public class PropertyFileConfiguration
extends HierarchicalConfiguration {
    static /* synthetic */ Class class$org$jfree$base$config$PropertyFileConfiguration;

    public void load(String resourceName) {
        this.load(resourceName, class$org$jfree$base$config$PropertyFileConfiguration == null ? (class$org$jfree$base$config$PropertyFileConfiguration = PropertyFileConfiguration.class$("org.jfree.base.config.PropertyFileConfiguration")) : class$org$jfree$base$config$PropertyFileConfiguration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(String resourceName, Class resourceSource) {
        InputStream in = ObjectUtilities.getResourceRelativeAsStream(resourceName, resourceSource);
        if (in != null) {
            try {
                this.load(in);
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
        Log.debug("Configuration file not found in the classpath: " + resourceName);
    }

    public void load(InputStream in) {
        if (in == null) {
            throw new NullPointerException();
        }
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            Properties p = new Properties();
            p.load(bin);
            this.getConfiguration().putAll((Map<?, ?>)p);
            bin.close();
        }
        catch (IOException ioe) {
            Log.warn("Unable to read configuration", ioe);
        }
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

