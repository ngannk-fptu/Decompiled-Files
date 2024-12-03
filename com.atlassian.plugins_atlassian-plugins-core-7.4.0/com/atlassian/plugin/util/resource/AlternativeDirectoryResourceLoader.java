/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util.resource;

import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Splitter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlternativeDirectoryResourceLoader
implements AlternativeResourceLoader {
    private static final Logger log = LoggerFactory.getLogger(AlternativeDirectoryResourceLoader.class);
    private static final Splitter splitter = Splitter.on((char)',').trimResults().omitEmptyStrings();
    public static final String PLUGIN_RESOURCE_DIRECTORIES = "plugin.resource.directories";
    private volatile String pluginResourceSystemProperty = "";
    private volatile List<File> resourceDirectories = Collections.emptyList();

    public AlternativeDirectoryResourceLoader() {
        this.getPluginResourceDirs();
    }

    private List<File> getPluginResourceDirs() {
        String dirs = System.getProperty(PLUGIN_RESOURCE_DIRECTORIES, "");
        if (!dirs.equals(this.pluginResourceSystemProperty)) {
            ArrayList<File> tmp = new ArrayList<File>();
            for (String dir : splitter.split((CharSequence)dirs)) {
                File file = new File(dir);
                if (file.exists()) {
                    log.debug("Found alternative resource directory {}", (Object)dir);
                    tmp.add(file);
                    continue;
                }
                log.warn("Resource directory {}, which resolves to {} does not exist", (Object)dir, (Object)file.getAbsolutePath());
            }
            this.pluginResourceSystemProperty = dirs;
            this.resourceDirectories = tmp;
        }
        return this.resourceDirectories;
    }

    @Override
    public URL getResource(String path) {
        for (File dir : this.getPluginResourceDirs()) {
            File file = new File(dir, path);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                }
                catch (MalformedURLException e) {
                    log.error("Malformed URL: " + file.toString(), (Throwable)e);
                    continue;
                }
            }
            log.debug("File {} not found, ignoring", (Object)file);
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = this.getResource(name);
        if (url != null) {
            try {
                return url.openStream();
            }
            catch (IOException e) {
                log.error("Unable to open URL " + url, (Throwable)e);
            }
        }
        return null;
    }

    public List<File> getResourceDirectories() {
        return this.getPluginResourceDirs();
    }
}

