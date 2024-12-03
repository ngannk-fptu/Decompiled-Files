/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.io.FileOptionsProvider;
import org.apache.commons.configuration2.io.URLConnectionOptions;

public abstract class FileSystem {
    private static final ConfigurationLogger DEFAULT_LOG = ConfigurationLogger.newDummyLogger();
    private volatile ConfigurationLogger log;
    private volatile FileOptionsProvider optionsProvider;

    public abstract String getBasePath(String var1);

    public abstract String getFileName(String var1);

    public FileOptionsProvider getFileOptionsProvider() {
        return this.optionsProvider;
    }

    public abstract InputStream getInputStream(URL var1) throws ConfigurationException;

    public InputStream getInputStream(URL url, URLConnectionOptions urlConnectionOptions) throws ConfigurationException {
        return this.getInputStream(url);
    }

    public ConfigurationLogger getLogger() {
        ConfigurationLogger result = this.log;
        return result != null ? result : DEFAULT_LOG;
    }

    public abstract OutputStream getOutputStream(File var1) throws ConfigurationException;

    public abstract OutputStream getOutputStream(URL var1) throws ConfigurationException;

    public abstract String getPath(File var1, URL var2, String var3, String var4);

    public abstract URL getURL(String var1, String var2) throws MalformedURLException;

    public abstract URL locateFromURL(String var1, String var2);

    public void setFileOptionsProvider(FileOptionsProvider provider) {
        this.optionsProvider = provider;
    }

    public void setLogger(ConfigurationLogger log) {
        this.log = log;
    }
}

