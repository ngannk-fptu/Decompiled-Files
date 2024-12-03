/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.fs.FileRevision;
import com.opensymphony.xwork2.util.fs.JarEntryRevision;
import com.opensymphony.xwork2.util.fs.Revision;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultFileManager
implements FileManager {
    private static Logger LOG = LogManager.getLogger(DefaultFileManager.class);
    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|\\.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;
    protected static final Map<String, Revision> files = Collections.synchronizedMap(new HashMap());
    private static final List<URL> lazyMonitoredFilesCache = Collections.synchronizedList(new ArrayList());
    protected boolean reloadingConfigs = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setReloadingConfigs(boolean reloadingConfigs) {
        if (reloadingConfigs && !this.reloadingConfigs) {
            this.reloadingConfigs = true;
            List<URL> list = lazyMonitoredFilesCache;
            synchronized (list) {
                for (URL fileUrl : lazyMonitoredFilesCache) {
                    this.monitorFile(fileUrl);
                }
                lazyMonitoredFilesCache.clear();
            }
        }
        this.reloadingConfigs = reloadingConfigs;
    }

    @Override
    public boolean fileNeedsReloading(URL fileUrl) {
        return fileUrl != null && this.fileNeedsReloading(fileUrl.toString());
    }

    @Override
    public boolean fileNeedsReloading(String fileName) {
        Revision revision = files.get(fileName);
        if (revision == null) {
            return this.reloadingConfigs;
        }
        return revision.needsReloading();
    }

    @Override
    public InputStream loadFile(URL fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        InputStream is = this.openFile(fileUrl);
        this.monitorFile(fileUrl);
        return is;
    }

    private InputStream openFile(URL fileUrl) {
        try {
            InputStream is = fileUrl.openStream();
            if (is == null) {
                throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
            }
            return is;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
        }
    }

    @Override
    public void monitorFile(URL fileUrl) {
        String fileName = fileUrl.toString();
        if (!this.reloadingConfigs) {
            files.remove(fileName);
            lazyMonitoredFilesCache.add(fileUrl);
            return;
        }
        LOG.debug("Creating revision for URL: {}", (Object)fileName);
        Revision revision = this.isJarURL(fileUrl) ? JarEntryRevision.build(fileUrl, this) : FileRevision.build(fileUrl);
        if (revision == null) {
            files.put(fileName, Revision.build(fileUrl));
        } else {
            files.put(fileName, revision);
        }
    }

    protected boolean isJarURL(URL fileUrl) {
        Matcher jarMatcher = JAR_PATTERN.matcher(fileUrl.getPath());
        return jarMatcher.matches();
    }

    @Override
    public URL normalizeToFileProtocol(URL url) {
        String fileName = url.toExternalForm();
        Matcher jarMatcher = JAR_PATTERN.matcher(fileName);
        try {
            if (jarMatcher.matches()) {
                String path = jarMatcher.group(3);
                return new URL("file", "", path);
            }
            if ("file".equals(url.getProtocol())) {
                return url;
            }
            LOG.warn("Could not normalize URL [{}] to file protocol!", (Object)url);
            return null;
        }
        catch (MalformedURLException e) {
            LOG.warn("Error normalizing URL [{}] to file protocol!", (Object)url, (Object)e);
            return null;
        }
    }

    @Override
    public boolean support() {
        return false;
    }

    @Override
    public boolean internal() {
        return true;
    }

    @Override
    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        return Arrays.asList(url);
    }
}

