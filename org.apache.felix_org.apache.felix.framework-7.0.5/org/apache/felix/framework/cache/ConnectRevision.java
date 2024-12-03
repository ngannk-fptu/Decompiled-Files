/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchiveRevision;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.ConnectContentContent;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.WeakZipFileFactory;
import org.osgi.framework.connect.ConnectContent;
import org.osgi.framework.connect.ConnectModule;

public class ConnectRevision
extends BundleArchiveRevision {
    private final WeakZipFileFactory m_zipFactory;
    private final ConnectContent m_module;

    public ConnectRevision(Logger logger, Map configMap, WeakZipFileFactory zipFactory, File revisionRootDir, String location, ConnectModule module) throws Exception {
        super(logger, configMap, revisionRootDir, location);
        this.m_zipFactory = zipFactory;
        this.m_module = module.getContent();
        this.m_module.open();
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
        return this.m_module.getHeaders().orElseGet(() -> this.m_module.getEntry("META-INF/MANIFEST.MF").flatMap(entry -> {
            try {
                byte[] manifest = entry.getBytes();
                return Optional.of(BundleCache.getMainAttributes((Map<String, Object>)new StringMap(), (InputStream)new ByteArrayInputStream(manifest), manifest.length));
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).orElse(null));
    }

    @Override
    public Content getContent() throws Exception {
        return new ConnectContentContent(this.getLogger(), this.m_zipFactory, this.getConfig(), "connect", this.getRevisionRootDir(), this, this.m_module);
    }

    @Override
    protected void close() throws Exception {
        this.m_module.close();
    }
}

