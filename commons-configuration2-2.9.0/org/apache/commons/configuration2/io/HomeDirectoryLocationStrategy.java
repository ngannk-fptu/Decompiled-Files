/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.lang3.StringUtils;

public class HomeDirectoryLocationStrategy
implements FileLocationStrategy {
    private static final String PROP_HOME = "user.home";
    private final String homeDirectory;
    private final boolean evaluateBasePath;

    public HomeDirectoryLocationStrategy(String homeDir, boolean withBasePath) {
        this.homeDirectory = HomeDirectoryLocationStrategy.fetchHomeDirectory(homeDir);
        this.evaluateBasePath = withBasePath;
    }

    public HomeDirectoryLocationStrategy(boolean withBasePath) {
        this(null, withBasePath);
    }

    public HomeDirectoryLocationStrategy() {
        this(false);
    }

    public String getHomeDirectory() {
        return this.homeDirectory;
    }

    public boolean isEvaluateBasePath() {
        return this.evaluateBasePath;
    }

    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        String basePath;
        File file;
        if (StringUtils.isNotEmpty((CharSequence)locator.getFileName()) && (file = FileLocatorUtils.constructFile(basePath = this.fetchBasePath(locator), locator.getFileName())).isFile()) {
            return FileLocatorUtils.convertFileToURL(file);
        }
        return null;
    }

    private String fetchBasePath(FileLocator locator) {
        if (this.isEvaluateBasePath() && StringUtils.isNotEmpty((CharSequence)locator.getBasePath())) {
            return FileLocatorUtils.appendPath(this.getHomeDirectory(), locator.getBasePath());
        }
        return this.getHomeDirectory();
    }

    private static String fetchHomeDirectory(String homeDir) {
        return homeDir != null ? homeDir : System.getProperty(PROP_HOME);
    }
}

