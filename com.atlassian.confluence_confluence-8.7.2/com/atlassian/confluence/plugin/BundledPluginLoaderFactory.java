/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.BundledPluginLoader
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.util.zip.UrlUnzipper
 *  javax.servlet.ServletContext
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.BundledPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.util.zip.UrlUnzipper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

public class BundledPluginLoaderFactory
implements FactoryBean,
ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(BundledPluginLoaderFactory.class);
    private static final String ZIP_PATH = "/WEB-INF/classes/com/atlassian/confluence/setup/";
    private static final String WEB_INF = "/WEB-INF/";
    private static final String EXTRA_BUNDLED_PLUGIN_ZIP = "atlassian-bundled-plugins.zip";
    private final List<PluginFactory> pluginFactories;
    private final PluginEventManager eventManager;
    private final PluginDirectoryProvider pluginDirectoryProvider;
    private final String directoryName;
    private final String legacyZipName;
    private ServletContext servletContext;

    private BundledPluginLoaderFactory(String directoryName, PluginDirectoryProvider pluginDirectoryProvider, List<PluginFactory> pluginFactories, PluginEventManager eventManager, String legacyZipName) {
        this.directoryName = directoryName;
        this.pluginFactories = pluginFactories;
        this.eventManager = eventManager;
        this.pluginDirectoryProvider = pluginDirectoryProvider;
        this.legacyZipName = legacyZipName;
    }

    public BundledPluginLoaderFactory(String directoryName, PluginDirectoryProvider pluginDirectoryProvider, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        this(directoryName, pluginDirectoryProvider, pluginFactories, eventManager, null);
    }

    public BundledPluginLoaderFactory(PluginDirectoryProvider pluginDirectoryProvider, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        this("atlassian-bundled-plugins", pluginDirectoryProvider, pluginFactories, eventManager, EXTRA_BUNDLED_PLUGIN_ZIP);
    }

    public Object getObject() throws Exception {
        URL bundledPluginDirectoryUrl = null;
        String webappBundledPluginDirectoryPath = this.servletContext.getRealPath(WEB_INF + this.directoryName);
        if (webappBundledPluginDirectoryPath == null) {
            throw new IllegalStateException("Running Confluence from a WAR is not supported. Make sure to configure your Servlet container to unpack the WAR before running it.");
        }
        File legacyBundledPluginDirectory = this.pluginDirectoryProvider.getBundledPluginDirectory();
        boolean sourcePluginsFromLegacyBundledPluginDirectory = this.explodePluginsZipFile(this.legacyZipName, legacyBundledPluginDirectory);
        if (sourcePluginsFromLegacyBundledPluginDirectory) {
            bundledPluginDirectoryUrl = this.copyPluginsToHomeDirectory(legacyBundledPluginDirectory);
        } else {
            bundledPluginDirectoryUrl = new File(webappBundledPluginDirectoryPath).toURI().toURL();
            if (legacyBundledPluginDirectory.isDirectory()) {
                this.purgeDirectory(legacyBundledPluginDirectory);
            }
        }
        if (bundledPluginDirectoryUrl == null) {
            throw new IllegalStateException("Can't find bundled plugins ZIP file or directory at classpath:" + this.directoryName);
        }
        log.info("Bundled plugins are loaded from '{}'", (Object)bundledPluginDirectoryUrl);
        return new BundledPluginLoader(bundledPluginDirectoryUrl, legacyBundledPluginDirectory, this.pluginFactories, this.eventManager);
    }

    private URL copyPluginsToHomeDirectory(File bundledPluginDirectory) throws IOException {
        Set resourcePaths = this.servletContext.getResourcePaths(WEB_INF + this.directoryName);
        for (String resourcePath : resourcePaths) {
            URL resource = this.servletContext.getResource(resourcePath);
            File destination = new File(bundledPluginDirectory, StringUtils.substringAfterLast((String)resource.getPath(), (String)"/"));
            FileUtils.copyURLToFile((URL)resource, (File)destination);
        }
        return bundledPluginDirectory.toURI().toURL();
    }

    private boolean explodePluginsZipFile(String zipFileName, File destinationDirectory) throws IOException {
        URL zipUrl;
        UrlUnzipper unzipper;
        ZipEntry[] entries;
        if (zipFileName != null && (entries = (unzipper = new UrlUnzipper(zipUrl = this.servletContext.getResource(ZIP_PATH + zipFileName), destinationDirectory)).entries()).length > 1) {
            if (!ConfluenceSystemProperties.isDevMode()) {
                log.warn("Non-empty atlassian-bundled-plugins.zip file '{}' detected. This should only be the case when testing plugins", (Object)zipUrl);
            } else {
                log.info("Loading AMPS-bundled plugins from '{}'", (Object)zipUrl);
            }
            String unexpectedPlugin = null;
            for (ZipEntry entry : entries) {
                log.info("Bundled plugin zip entry found: {}", (Object)entry.getName());
                if (!entry.getName().startsWith("confluence-keyboard-shortcuts")) continue;
                unexpectedPlugin = entry.getName();
            }
            if (unexpectedPlugin != null) {
                throw new IllegalStateException(zipFileName + " contains " + unexpectedPlugin + ", which it should not.  You have an atlassian-bundled-plugins.zip left over from a Confluence version older than 5.0.");
            }
            log.info("Unzipping extra plugins to {}", (Object)destinationDirectory);
            unzipper.unzip();
            FileUtils.deleteQuietly((File)new File(destinationDirectory, "README.TXT"));
            return true;
        }
        return false;
    }

    private void purgeDirectory(File directory) {
        if (directory.isDirectory()) {
            try {
                FileUtils.cleanDirectory((File)directory);
            }
            catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.warn(e.getMessage(), (Throwable)e);
                }
                log.warn(e.getMessage());
            }
        }
    }

    public Class<PluginLoader> getObjectType() {
        return PluginLoader.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

