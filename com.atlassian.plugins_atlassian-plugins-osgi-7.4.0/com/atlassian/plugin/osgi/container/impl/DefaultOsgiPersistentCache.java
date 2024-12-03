/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Preconditions
 *  com.google.common.hash.Hashing
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.container.impl;

import com.atlassian.plugin.osgi.container.OsgiContainerException;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOsgiPersistentCache
implements OsgiPersistentCache {
    private final File osgiBundleCache;
    private final File frameworkBundleCache;
    private final File transformedPluginCache;
    private final Logger log = LoggerFactory.getLogger(DefaultOsgiPersistentCache.class);

    public DefaultOsgiPersistentCache(File baseDir) {
        Preconditions.checkState((boolean)((File)Preconditions.checkNotNull((Object)baseDir)).exists(), (String)"The base directory for OSGi persistent caches should exist, %s", (Object)baseDir);
        this.osgiBundleCache = new File(baseDir, "felix");
        this.frameworkBundleCache = new File(baseDir, "framework-bundles");
        this.transformedPluginCache = new File(baseDir, "transformed-plugins");
        this.validate(null);
    }

    @Override
    public File getFrameworkBundleCache() {
        return this.frameworkBundleCache;
    }

    @Override
    public File getOsgiBundleCache() {
        return this.osgiBundleCache;
    }

    @Override
    public File getTransformedPluginCache() {
        return this.transformedPluginCache;
    }

    @Override
    public void clear() {
        try {
            FileUtils.cleanDirectory((File)this.frameworkBundleCache);
            FileUtils.cleanDirectory((File)this.osgiBundleCache);
            FileUtils.cleanDirectory((File)this.transformedPluginCache);
        }
        catch (IOException e) {
            throw new OsgiContainerException("Unable to clear OSGi caches", e);
        }
    }

    @Override
    public void validate(String cacheValidationKey) {
        this.ensureDirectoryExists(this.frameworkBundleCache);
        this.ensureDirectoryExists(this.osgiBundleCache);
        this.ensureDirectoryExists(this.transformedPluginCache);
        try {
            FileUtils.cleanDirectory((File)this.osgiBundleCache);
        }
        catch (IOException e) {
            throw new OsgiContainerException("Unable to clean the cache directory: " + this.osgiBundleCache, e);
        }
        if (cacheValidationKey != null) {
            String newHash = Hashing.sha1().hashString((CharSequence)cacheValidationKey, Charsets.UTF_8).toString();
            File versionFile = new File(this.transformedPluginCache, "cache.key");
            if (versionFile.exists()) {
                String oldVersion = null;
                try {
                    oldVersion = FileUtils.readFileToString((File)versionFile);
                }
                catch (IOException e) {
                    this.log.debug("Unable to read cache key file", (Throwable)e);
                }
                if (!newHash.equals(oldVersion)) {
                    this.log.info("Application upgrade detected, clearing OSGi cache directories");
                    this.clear();
                } else {
                    return;
                }
            }
            try {
                FileUtils.writeStringToFile((File)versionFile, (String)newHash);
            }
            catch (IOException e) {
                this.log.warn("Unable to write cache key file, so will be unable to detect upgrades", (Throwable)e);
            }
        }
    }

    private void ensureDirectoryExists(File dir) {
        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalArgumentException("'" + dir + "' is not a directory");
        }
        if (!dir.exists() && !dir.mkdir()) {
            throw new IllegalArgumentException("Directory '" + dir + "' cannot be created");
        }
    }
}

