/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchiveRevision;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.DirectoryContent;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.WeakZipFileFactory;

class DirectoryRevision
extends BundleArchiveRevision {
    private final WeakZipFileFactory m_zipFactory;
    private final File m_refDir;

    public DirectoryRevision(Logger logger, Map configMap, WeakZipFileFactory zipFactory, File revisionRootDir, String location) throws Exception {
        super(logger, configMap, revisionRootDir, location);
        this.m_zipFactory = zipFactory;
        this.m_refDir = new File(location.substring(location.indexOf("file:") + "file:".length()));
        if (BundleCache.getSecureAction().fileExists(this.getRevisionRootDir())) {
            return;
        }
        if (!BundleCache.getSecureAction().mkdir(this.getRevisionRootDir())) {
            this.getLogger().log(1, this.getClass().getName() + ": Unable to create revision directory.");
            throw new IOException("Unable to create archive directory.");
        }
    }

    @Override
    public Map<String, Object> getManifestHeader() throws Exception {
        File manifest = new File(this.m_refDir, "META-INF/MANIFEST.MF");
        return BundleCache.getSecureAction().isFile(manifest) ? BundleCache.getMainAttributes((Map<String, Object>)new StringMap(), BundleCache.getSecureAction().getInputStream(manifest), manifest.length()) : null;
    }

    @Override
    public Content getContent() throws Exception {
        return new DirectoryContent(this.getLogger(), this.getConfig(), this.m_zipFactory, this, this.getRevisionRootDir(), this.m_refDir);
    }

    @Override
    protected void close() throws Exception {
    }
}

