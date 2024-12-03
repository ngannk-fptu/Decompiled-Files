/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.util.Map;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.Content;

public abstract class BundleArchiveRevision {
    private final Logger m_logger;
    private final Map m_configMap;
    private final File m_revisionRootDir;
    private final String m_location;

    public BundleArchiveRevision(Logger logger, Map configMap, File revisionRootDir, String location) throws Exception {
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_revisionRootDir = revisionRootDir;
        this.m_location = location;
    }

    public Logger getLogger() {
        return this.m_logger;
    }

    public Map getConfig() {
        return this.m_configMap;
    }

    public File getRevisionRootDir() {
        return this.m_revisionRootDir;
    }

    public String getLocation() {
        return this.m_location;
    }

    public abstract Map<String, Object> getManifestHeader() throws Exception;

    public abstract Content getContent() throws Exception;

    protected abstract void close() throws Exception;
}

